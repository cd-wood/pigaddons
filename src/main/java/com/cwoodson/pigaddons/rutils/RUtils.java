/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.logicalLayer.schema.Schema.FieldSchema;
import org.nuiton.j2r.REngine;
import org.nuiton.j2r.RException;
import org.nuiton.j2r.types.REXP;
import org.nuiton.j2r.types.RList;

/**
 *
 * @author connor-woodson
 */
public class RUtils {

    private RUtils() {
    }

    public static List<REXPStr> pigTupleToR(Tuple tuple, Schema schema, int depth, REngine engine) throws FrontendException, ExecException, RException {
        List<REXPStr> result = new ArrayList<REXPStr>();
        if (tuple != null) {
            for (int i = 0; i < schema.size(); i++) {
                FieldSchema field = schema.getField(i);
                REXP value = null;
                if (field.type == DataType.BAG) {
                    value = pigBagToR((DataBag) tuple.get(i), field.schema, depth + 1, engine);
                } else if (field.type == DataType.TUPLE) {
                    List<REXPStr> recursed = pigTupleToR((Tuple) tuple.get(i), field.schema, depth + 1, engine);
                    value = REXPStr.toRList(recursed, engine);
                } else if (field.type == DataType.MAP) {
                    value = pigMapToR((Map<String, Object>) tuple.get(i), field.schema, depth + 1, engine);
                } else {
                    Object thing = tuple.get(i);
                    if (thing instanceof Object[]) {
                        value = new RPrimitiveArray((Object[]) thing, engine, "");
                    } else {
                        value = new RPrimitive(thing, engine, "");
                    }
                }
                REXPStr rs = new REXPStr(value, field.alias);
                result.add(rs);
            }
        }
        return result;
    }
    
    public static RList pigMapToR(Map<String, Object> map, Schema schema, int depth, REngine engine) throws RException {
        List<String> names = new ArrayList<String>(map.size());
        List<Object> data = new ArrayList<Object>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            names.add(entry.getKey());
            data.add(entry.getValue());
        }
        return new RList(names.toArray(new String[0]), data, engine, "");
    }

    public static RList pigBagToR(DataBag bag, Schema schema, int depth, REngine engine) throws FrontendException, ExecException, RException {
        if (schema.size() == 1 && schema.getField(0).type == DataType.TUPLE) {
            schema = schema.getField(0).schema;
        }

        if (bag.size() > Integer.MAX_VALUE) {
            throw new ExecException("Bag Size is too large for R: " + bag.size());
        }

        Integer bagSize = Integer.valueOf(Long.toString(bag.size()));

        List<String> names = new ArrayList<String>(bagSize);
        List<Object> data = new ArrayList<Object>(bagSize);

        int index = 0;
        for (Tuple t : bag) {
            names.add(Integer.toString(index));
            index++;
            List<REXPStr> list = pigTupleToR(t, schema, depth + 1, engine);
            data.add(REXPStr.toRList(list, engine));
        }
        
        return new RList(names.toArray(new String[0]), data, engine, "");
    }

    public static Tuple rToPigTuple(RList object, Schema schema, int depth) throws FrontendException, ExecException, RException
    {
        Tuple t = TupleFactory.getInstance().newTuple(schema.size());
        for(int i = 0; i < schema.size(); i++)
        {
            FieldSchema field = schema.getField(i);
            byte type = field.type;
            Object value = null;
            if(object.getData().size() <= i)
            {
                return null;
            }
            
            Object data = object.get(i);
            
            
            
            if(type == DataType.BAG)
            {
                
            }
            t.set(i, value);
        }
        return t;
    }

    public static String arrayToRString(Object[] array) {
        return "";
    }

    public static Tuple arrayToPigTuple(Object[] array) {
        return null;
    }

    public static class REXPStr {

        public final REXP rexp;
        public final String string;

        public REXPStr(REXP rexp, String string) {
            this.rexp = rexp;
            this.string = string;
        }

        public static RList toRList(List<REXPStr> list, REngine engine) throws RException
        {
            List<String> names = new ArrayList<String>(list.size());
            List<Object> data = new ArrayList<Object>(list.size());
            for (int i = 0; i < list.size(); i++) {
                names.add(list.get(i).string);
                data.add(list.get(i).rexp);
            }
            return new RList(names.toArray(new String[0]), data, engine, "");
        }
    }
}
