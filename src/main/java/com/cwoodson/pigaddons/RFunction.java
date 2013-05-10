/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons;

import com.cwoodson.pigaddons.rtypes.RList;
import com.cwoodson.pigaddons.rutils.RConnector;
import com.cwoodson.pigaddons.rutils.RException;
import com.cwoodson.pigaddons.rutils.RUtils;
import com.cwoodson.pigaddons.rutils.RUtils.REXPStr;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;
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
            Object schemaObj = rEngine.eval("attributes(" + functionName + ")$outputSchema");
            if (schemaObj != null && schemaObj instanceof String && !((String) schemaObj).isEmpty()) {
                String outputSchemaStr = (String) schemaObj;
                logger.info("Output Schema Attribute for RFunction '" + functionName + "' detected: " + outputSchema);
                try {
                    this.outputSchema = Utils.getSchemaFromString(outputSchemaStr);
                    logger.info("Output Schema create for RFunction '" + functionName + "'");
                } catch (ParserException pe) {
                    throw new IllegalArgumentException("RFunction " + functionName + " has invalid output schema: " + outputSchemaStr, pe);
                }
            } else {
                this.outputSchema = null;
            }
        } catch (RException re) {
            throw new IllegalArgumentException("Failed to access attributes of R function '" + functionName + "'", re);
        }
    }

    @Override
    public Object exec(Tuple tuple) throws IOException {
        Schema localInputSchema = this.getInputSchema();

        if (inputSchema.size() == 1 && inputSchema.getField(0).type == DataType.TUPLE) {
            inputSchema = inputSchema.getField(0).schema;
        }
        Object functionResult = null;
        try {
            List<REXPStr> params = RUtils.pigTupleToR(tuple, inputSchema, 0, rEngine);
            String paramString = "";
            for (int i = 0; i < params.size(); i++) {

                String rStr = params.get(i).rexp.toRString();
                if (rStr.startsWith("<-")) {
                    rStr = rStr.substring(2);
                }
                paramString += rStr + ",";
            }
            paramString = paramString.substring(0, paramString.length() - 1);
            functionResult = rEngine.eval(functionName + "(" + paramString + ")");
        } catch (RException ex) {
            throw new IOException("R Function Execution failed", ex);
        }
        
        Object eval = null;
        try {
            RList resultList;
            if(functionResult instanceof RList)
            {
                resultList = (RList) functionResult;
            } else {
                List<Object> asList = new ArrayList<Object>(1);
                asList.add(functionResult);
                resultList = new RList(new String[] {""}, asList, rEngine, "");
            }
            Tuple evalTuple = RUtils.rToPigTuple(resultList, outputSchema, 0);
            eval = outputSchema.size() == 1 ? evalTuple.get(0) : evalTuple;
        } catch(RException ex) {
            throw new IOException("RList conversion failed", ex);
        }
        
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
}
