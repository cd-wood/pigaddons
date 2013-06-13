/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rpig.rutils;

import com.cwoodson.pigaddons.rpig.rtypes.RDataFrame;
import com.cwoodson.pigaddons.rpig.rtypes.RList;
import com.cwoodson.pigaddons.rpig.rtypes.RPrimitive;
import com.cwoodson.pigaddons.rpig.rtypes.RPrimitiveArray;
import com.cwoodson.pigaddons.rpig.rtypes.RType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Based off of the source code from org.nuiton.j2r.jni.RJniEngine
 * 
 * @author connor-woodson
 */
public class RJriConnector implements RConnector
{
    private static final Logger log = LoggerFactory.getLogger(RJriConnector.class);
 
    /**
     * Rengine is made to be static
     */
    protected static Rengine engine;
    
    @Override
    public boolean init()
    {
        if(engine == null)
        {
            try {
                System.loadLibrary("jri");
                log.info("JRI successfully loaded");
            } catch(Throwable t) {
                log.error("Unable to load JRI. Exception follows.", t);
            }
            
            try {
                String[] args = {"--no-save", "--vanilla"};

                //jriLoaded is false is rJava did not find jri library
                if (!Rengine.jriLoaded) {
                    log.error(
                            "Cannot find jri library, make sure it is correctly installed");
                    return false;
                }

                engine = new Rengine(args, false, null);
                
                if (!engine.waitForR()) {
                    log.error("Cannot load the R engine");
                    return false;
                }
                
                log.info("Rengine Started");
            } catch (Throwable eee) {
                log.error("An error occured during R/JNI initialization.",
                    eee);
                return false;
            }
        }
        return true;
    }

    /**
     * Convert the result from an R expression to a java object.
     *
     * @param rexp the R expression to convert.
     *
     * @return the java object corresponding to the R expression.
     */
    private RType convertResult(REXP rexp) throws RException {
        if (rexp == null) {
            log.info("Null returned");
            return null;
        }
        int type = rexp.getType();
        RType result = null;
        switch (type) {
            case REXP.XT_STR:
                //If string return the r expression as string
                result = new RPrimitive(rexp.asString());
                break;
            case REXP.XT_INT:
                //if integer, return the rexp as integer
                result = new RPrimitive((Integer) rexp.asInt());
                break;
            case REXP.XT_ARRAY_INT:
                int[] array = rexp.asIntArray();
                Integer[] bigArray = new Integer[array.length];
                for (int i = 0; i < array.length; i++) {
                    bigArray[i] = (Integer) array[i];
                }
                
                //Check if only one integer, return an integer.
                if (array.length == 1) {
                    result = new RPrimitive((Integer) bigArray[0]);
                } else {
                    result = new RPrimitiveArray(bigArray);
                }
                break;
            case REXP.XT_ARRAY_DOUBLE:
                //if double array, return the rexp as double array.
                double[] doublearray = rexp.asDoubleArray();
                Double[] bigdoublearray = new Double[doublearray.length];
                for (int i = 0; i < doublearray.length; i++) {
                    bigdoublearray[i] = (Double) doublearray[i];
                }
                
                //Check if only one double, return a double.
                if (doublearray.length == 1) {
                    result = new RPrimitive(bigdoublearray[0]);
                } else {
                    result = new RPrimitiveArray(bigdoublearray);
                }
                break;
            case REXP.XT_BOOL:
                //if boolean, return rexp as boolean
                result = new RPrimitive(rexp.asBool().isTRUE());
                break;
            case REXP.XT_DOUBLE:
                //if double, return rexp as double
                //Get a double array
                //return only the first element.
                result = new RPrimitive((Double) ((double[]) rexp.asDoubleArray())[0]);
                break;
            case REXP.XT_NULL:
                //if null return null
                result = new RPrimitive();
                break;
            case REXP.XT_ARRAY_BOOL_INT:
                //if boolean array, get the rexp as integer array (full of 0 and 1)
                int[] integers = ((int[]) rexp.asIntArray());
                Boolean[] booleanArray = new Boolean[integers.length];
                //transform the 0 and 1 in true and false in a boolean array
                for (int i = 0; i < integers.length; i++) {
                    if (integers[i] == 1) {
                        booleanArray[i] = Boolean.TRUE;
                    } else {
                        booleanArray[i] = Boolean.FALSE;
                    }
                }
                //check if there is only a boolean, return a boolean
                if (booleanArray.length == 1) {
                    result = new RPrimitive(booleanArray[0]);
                } else {
                    result = new RPrimitiveArray(booleanArray);
                }
                //return the boolean array
                break;
            case REXP.XT_ARRAY_STR:
                //if is a string array, return as a string array.
                result = new RPrimitiveArray(rexp.asStringArray());
                break;
            case REXP.XT_VECTOR:
                //dataframes, lists and vectors are recognized as vectors.
                
                //get REXP asList to successfully detect lists.
                if (RHelpers.isDataframe(rexp)) {
                    convertDataFrame(rexp);
                } else if (rexp.asList() != null) {
                    return convertList(rexp);
                } else {
                    return convertVector(rexp.asVector());
                }
                break;
            default:
                //if don't know the type, throw an exception.
                log.error("Unknown return type [" + type + "] " + "on : " +
                    rexp.toString());
                break;
        }
        return result;
    }

    /**
     * Terminate the R connection.
     * 
     * @see org.nuiton.j2r.REngine#terminate()
     */
    @Override
    public void terminate() {
        if (engine.isAlive()) {
            engine.end();
        }
    }

    /**
     * Evaluate a R expression in R without getting back the result. If in
     * non-autocommit mode, expression is stored and evaluated only when the
     * commit() method is called.
     *
     * @param expr the R expression to evaluate.
     * 
     * @see org.nuiton.j2r.REngine#voidEval(java.lang.String)
     * @see org.nuiton.j2r.jni.RJniEngine#commit() 
     */
    @Override
    public void voidEval(String expr) throws RException {
        // voidEval is not really supported by JRI, we just discard the result
        // conversion

        //encapsulate the R expression in a try method/object to get the R error
        //message if thrown
        String call = RHelpers.safeCall(expr);
        log.info(call);
        REXP r = engine.eval(call);
        if (r != null && RHelpers.isError(r)) {
            throw new RException(r.asString());
        }
    }
    
    @Override
    public RType eval(String expr) throws RException
    {
        String call = RHelpers.safeCall(expr);
        log.info(call);
        REXP r = engine.eval(call);
        if (r != null && RHelpers.isError(r)) {
            throw new RException(r.asString());
        }
        return convertResult(r);
    }
    
    private RDataFrame convertDataFrame(REXP rexp) throws RException {
        throw new RException("Data Frames are not currently supported");
    }
    
    private RList convertList(REXP rexp) throws RException {
        org.rosuda.JRI.RList list = rexp.asList();
        
        List<Object> data = new ArrayList<Object>();
        String[] names = {};
        
        REXP attrNames = RHelpers.getNamesAttribute(rexp);
        if(attrNames != null) {
            names = attrNames.asStringArray();
        }
        
        Boolean flag = true;
        int index = 0;
        
        while(flag) {
            REXP tempREXP = list.at(index);
            if(tempREXP == null) {
                flag = false;
            } else {
                Object converted = convertResult(tempREXP);
                data.add(converted);
                index++;
            }
        }
        
        return new RList(names, data);
    }
    
    private RList convertVector(RVector vec) throws RException
    {
        List<Object> data = new ArrayList<Object>();
        for(Object o : vec) {
            data.add(convertResult((REXP)o));
        }
        List<String> names = new ArrayList<String>(0);
        if(!vec.getNames().isEmpty()) {
            names = vec.getNames();
        }
        return new RList(names, data);
    }

    @Override
    public void execFile(InputStream scriptStream, String path) throws RException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(scriptStream));
        log.info("Executing R File: " + path);
        try {
            String line;
            String fullLine = "{\n";
            while((line = in.readLine()) != null)
            {
                fullLine += line + '\n';
            }
            fullLine += "}";
            voidEval(fullLine);
        } catch(IOException ioe) {
            log.error("Error while reading input stream", ioe);
            throw new RException("Error while reading input stream", ioe);
        } catch(RException re) {
            log.error("Error in script passed to execfile", re);
            throw re;
        } finally {
            try {
                in.close();
            } catch(IOException ignored) {}
        }
    }
    
    @Override
    public List<String> ls() throws RException
    {
        List<String> ls = new ArrayList<String>();
        List<Object> lsO =  eval("ls()").asList();
        if(lsO == null) {
            log.warn("ls() returned NULL");
        }
        else {
            for(Object o : lsO) {
                if(o instanceof String) {
                    ls.add((String)o);
                } else {
                    log.warn("ls element '" + lsO.toString() + "' not of type String");
                }
            }
        }
        return ls;
    }
    
    @Override
    public List<String> lsVariables()
    {
        List<String> result = new ArrayList<String>();
        try {
            for(String s : ls())
            {
                RType clazzRT = eval("class(" + s + ")");
                Object clazzO;
                if(clazzRT instanceof RPrimitive && (clazzO = ((RPrimitive)clazzRT).getValue()) instanceof String)
                {
                    String clazz = (String) clazzO;
                    if(!"function".equals(clazz))
                    {
                        result.add(s);
                    }
                } else
                {
                    log.warn("Object returns class that is not a String: " + s);
                }
            }
        } catch(RException re) {
            log.error("Error in lsVariables", re);
        }
        return result;
    }
    
    @Override
    public List<String> lsFunctions()
    {
        List<String> result = new ArrayList<String>();
        try {
            for(String s : ls())
            {
                RType clazzRT = eval("class(" + s + ")");
                Object clazzO;
                if(clazzRT instanceof RPrimitive && (clazzO = ((RPrimitive)clazzRT).getValue()) instanceof String)
                {
                    String clazz = (String) clazzO;
                    log.info("Class of '" + s + "' is '" + clazz + "'");
                    if("function".equals(clazz))
                    {
                        result.add(s);
                    }
                } else
                {
                    log.warn("Object returns class that is not a String: " + s);
                }
            }
        } catch(RException re) {
            log.error("Error in lsFunctions", re);
        }
        return result;
    }
    
    public static RJriConnector create()
    {
        RJriConnector rjc = new RJriConnector();
        if(rjc.init())
        {
            return rjc;
        } else {
            return null;
        }
    }
}
