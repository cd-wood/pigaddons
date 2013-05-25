/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rpig.rutils;

import com.cwoodson.pigaddons.rpig.rtypes.RList;
import com.cwoodson.pigaddons.rpig.rtypes.RPrimitive;
import com.cwoodson.pigaddons.rpig.rtypes.RPrimitiveArray;
import com.cwoodson.pigaddons.rpig.rtypes.RType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.BagFactory;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.logicalLayer.schema.Schema.FieldSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author connor-woodson
 */
public class RUtils {

    private static Logger logger = LoggerFactory.getLogger(RUtils.class);
    
    private RUtils() {
    }

    public static RList pigTupleToR(Tuple tuple, Schema schema, int depth) throws FrontendException, ExecException {
        List<Object> data = new ArrayList<Object>(schema.size());
        List<String> names = new ArrayList<String>(schema.size());
        if (tuple != null) {
            for (int i = 0; i < schema.size(); i++) {
                FieldSchema field = schema.getField(i);
                RType value;
                if (field.type == DataType.BAG) {
                    value = pigBagToR((DataBag) tuple.get(i), field.schema, depth + 1);
                } else if (field.type == DataType.TUPLE) {
                    value = pigTupleToR((Tuple) tuple.get(i), field.schema, depth + 1);
                } else if (field.type == DataType.MAP) {
                    value = pigMapToR((Map<String, Object>) tuple.get(i), field.schema, depth + 1);
                } else {
                    Object thing = tuple.get(i);
                    if (thing instanceof Object[]) {
                        value = new RPrimitiveArray((Object[]) thing);
                    } else {
                        value = new RPrimitive(thing);
                    }
                }
                data.add(value);
                names.add(field.alias);
            }
        }
        return new RList(names, data);
    }
    
    public static RList pigMapToR(Map<String, Object> map, Schema schema, int depth) {
        List<String> names = new ArrayList<String>(map.size());
        List<Object> data = new ArrayList<Object>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            names.add(entry.getKey());
            data.add(entry.getValue());
        }
        return new RList(names, data);
    }

    public static RList pigBagToR(DataBag bag, Schema schema, int depth) throws FrontendException, ExecException {
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
            data.add(pigTupleToR(t, schema, depth + 1));
        }
        
        return new RList(names, data);
    }

    public static Tuple rToPigTuple(RList object, Schema schema, int depth) throws FrontendException, ExecException
    {
        Tuple t = TupleFactory.getInstance().newTuple(schema.size());
        for(int i = 0; i < schema.size(); i++)
        {
            FieldSchema field = schema.getField(i);
            int index = object.contains(field.alias);
            Object data = null;
            if((index >= 0) && ((data = object.get(index)) != null)) {
                byte type = field.type;
                Object value;
                if(type == DataType.BAG)
                {
                    if(data instanceof RList) {
                        value = rToPigBag((RList)data, field.schema, depth + 1);
                    } else {
                        value = null;
                    }
                } else if(field.type == DataType.TUPLE) {
                    if(data instanceof RList) {
                        value = rToPigTuple((RList)data, field.schema, depth + 1);
                    } else {
                        value = null;
                    }
                } else if(field.type == DataType.MAP) {
                    if(data instanceof RList) {
                        value = rToPigMap((RList)data, field.schema, depth + 1);
                    } else {
                        value = null;
                    }
                } else if(data instanceof RPrimitive) {
                    value = ((RPrimitive)data).getValue();
                } else if(data instanceof RPrimitiveArray) {
                    value = ((RPrimitiveArray)data).getValue();
                } else {
                    value = data;
                }
                t.set(i, value);
            } else {
                logger.warn("RList does not contain name specified by schema: " + field.alias + "[" + object.toRString() + "]");
            }
        }
        return t;
    }
    
    private static DataBag rToPigBag(RList object, Schema schema, int depth) throws FrontendException, ExecException {
        if(schema.size() == 1 && schema.getField(0).type == DataType.TUPLE) {
            schema = schema.getField(0).schema;
        }
        List<Tuple> bag = new ArrayList<Tuple>();
        List<Object> data = object.asList();
        for(int i = 0; i < data.size(); i ++) {
            Object obj = data.get(i);
            if(obj instanceof RList) {
                bag.add(rToPigTuple((RList)obj, schema, depth + 1));
            } else {
                logger.warn("Interpreted RList as a DataBag, but list does contains something other than an RList {" + object.toRString() + "}");
            }
        }
        DataBag result = BagFactory.getInstance().newDefaultBag(bag);
        return result;
    }
    
    private static Map<String, Object> rToPigMap(RList object, Schema schema, int depth) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<String> names = object.getNames();
        for(int i = 0; i < names.size(); i++) {
            Object data = object.get(i);
            if(data != null) {
                map.put(names.get(i), data);
            }
        }
        return map;
    }
}