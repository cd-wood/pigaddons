/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rutils;

import java.util.List;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.nuiton.j2r.types.REXP;

/**
 *
 * @author connor-woodson
 */
public class RUtils
{
    private RUtils() {}
    
    public static List<REXP> pigTupleToRList(Tuple tuple, Schema schema, int depth)
    {
        return null;
    }
    
    public static Tuple rObjectToPigTuple(Object data, Schema schema, int depth)
    {
        return null;
    }
    
    public static String arrayToRString(Object[] array)
    {
        return "";
    }
    
    public static Tuple arrayToPigTuple(Object[] array)
    {
        return null;
    }
}
