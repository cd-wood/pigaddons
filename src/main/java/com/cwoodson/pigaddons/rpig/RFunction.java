/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rpig;

import com.cwoodson.pigaddons.rpig.rtypes.RDataFrame;
import com.cwoodson.pigaddons.rpig.rtypes.RList;
import com.cwoodson.pigaddons.rpig.rtypes.RPrimitive;
import com.cwoodson.pigaddons.rpig.rtypes.RType;
import com.cwoodson.pigaddons.rpig.rutils.RConnector;
import com.cwoodson.pigaddons.rpig.rutils.RException;
import com.cwoodson.pigaddons.rpig.rutils.RUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.logicalLayer.schema.Schema.FieldSchema;
import org.apache.pig.impl.util.UDFContext;
import org.apache.pig.impl.util.Utils;
import org.apache.pig.parser.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author connor-woodson
 */
public class RFunction extends EvalFunc<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RFunction.class);
    private final String functionName;
    private final RConnector rEngine;
    private Schema inputSchema;
    private Schema outputSchema;

    public RFunction(String functionName) {
        this.rEngine = RScriptEngine.getEngine();
        this.functionName = functionName;

        try {
            RType schemaObj = rEngine.eval("attributes(" + functionName + ")$outputSchema");
            if (schemaObj != null && schemaObj instanceof RPrimitive) {
                String outputSchemaStr = (String)((RPrimitive)schemaObj).getValue();
                logger.info("Output Schema Attribute for RFunction '" + functionName + "' detected: " + outputSchemaStr);
                try {
                    this.outputSchema = Utils.getSchemaFromString(outputSchemaStr);
                    logger.info("Output Schema created for RFunction '" + functionName + "'");
                } catch (ParserException pe) {
                    throw new IllegalArgumentException("RFunction " + functionName + " has invalid output schema: " + outputSchemaStr, pe);
                }
            } else {
                logger.warn("Output Schema Attribute for RFunction '" + functionName + "' missing or improperly declared");
                this.outputSchema = null;
            }
        } catch (RException re) {
            throw new IllegalArgumentException("Failed to access attributes of R function '" + functionName + "'", re);
        }
    }

    @Override
    public Object exec(Tuple tuple) throws IOException {
        getInputSchema();
        
        RList result_list;
        try {
            List<RType> params = RUtils.pigTupleToR(tuple, inputSchema, 0).expand();
            String paramStr = params.isEmpty() ? "" : params.get(0).toRString();
            for(int i = 1; i < params.size(); i++) {
                paramStr += ", " + params.get(i).toRString();
            }
            RType result = rEngine.eval(functionName + "(" + paramStr + ")");
            if(result instanceof RDataFrame) {
                throw new UnsupportedOperationException("rPig does not currently support DataFrames");
            } else if(!(result instanceof RList)) { // wrap any other RType
                result_list = new RList(getFieldNames(outputSchema.getFields()), result.asList());
            } else {
                result_list = (RList) result;
            }
        } catch (RException ex) {
            throw new IOException("R Function Execution failed", ex);
        }
        
        Schema out = outputSchema;
        if(out.size() == 1 && out.getField(0).type == DataType.TUPLE) {
            out = out.getField(0).schema;
        }
        
        Tuple evalTuple = RUtils.rToPigTuple(result_list, out, 0);
        Object eval = out.size() == 1 ? evalTuple.get(0) : evalTuple; // Not sure about this
        return eval;
    }

    @Override
    public Schema outputSchema(Schema input) {
        this.setInputSchema(input);
        return this.outputSchema;
    }

    /**
     * *****************************************
     */
    /* Required for compatibility prior to 0.11 */
    /**
     * *****************************************
     */
    private Schema getInputSchema() {
        if (inputSchema == null) {
            Properties properties = UDFContext.getUDFContext().getUDFProperties(this.getClass(), new String[]{functionName});
            inputSchema = (Schema) properties.get(functionName + ".inputSchema");
        }
        return inputSchema;
    }

    private void setInputSchema(Schema inputSchema) {
        this.inputSchema = inputSchema;
        Properties properties = UDFContext.getUDFContext().getUDFProperties(this.getClass(), new String[]{functionName});
        properties.put(functionName + ".inputSchema", inputSchema);
    }
    
    private List<String> getFieldNames(List<FieldSchema> fs) {
        List<String> result = new ArrayList<String>();
        for(int i = 0; i < fs.size(); i++) {
            result.add(fs.get(i).alias);
        }
        return result;
    }
}
