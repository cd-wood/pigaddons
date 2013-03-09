/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nuiton.j2r.REngine;
import org.nuiton.j2r.RException;
import org.nuiton.j2r.RInstructions;
import org.nuiton.j2r.jni.RJniEngine;
import org.nuiton.j2r.types.RDataFrame;
import org.nuiton.j2r.types.RList;
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
                String[] args = {"--no-save", "--no-environ"};

                //Set the property so that rJava does not make a System.exit(1)
                //System.setProperty("jri.ignore.ule", "yes");

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
            } catch (Exception eee) {
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
    private Object convertResult(REXP rexp) {
        if (rexp == null) {
            log.debug("Null returned");
            return null;
        }
        log.debug("Converting : " + rexp.toString());
        int type = rexp.getType();
        Object result = null;
        switch (type) {
            case REXP.XT_STR:
                //If string return the r expression as string
                result = rexp.asString();
                break;
            case REXP.XT_INT:
                //if integer, return the rexp as integer
                result = (Integer) rexp.asInt();
                break;
            case REXP.XT_ARRAY_INT:
                int[] array = rexp.asIntArray();
                Integer[] bigArray = new Integer[array.length];
                for (int i = 0; i < array.length; i++) {
                    bigArray[i] = (Integer) array[i];
                }
                result = bigArray;
                //Check if only one integer, return an integer.
                if (array.length == 1) {
                    result = (Integer) array[0];
                }
                break;
            case REXP.XT_ARRAY_DOUBLE:
                //if double array, return the rexp as double array.
                double[] doublearray = rexp.asDoubleArray();
                Double[] bigdoublearray = new Double[doublearray.length];
                for (int i = 0; i < doublearray.length; i++) {
                    bigdoublearray[i] = (Double) doublearray[i];
                }
                result = bigdoublearray;
                //Check if only one double, return a double.
                if (doublearray.length == 1) {
                    result = doublearray[0];
                }
                break;
            case REXP.XT_BOOL:
                //if boolean, return rexp as boolean
                result = rexp.asBool().isTRUE();
                break;
            case REXP.XT_DOUBLE:
                //if double, return rexp as double
                //Get a double array
                result = rexp.asDoubleArray();
                //return only the first element.
                result = (Double) ((double[]) result)[0];
                break;
            case REXP.XT_NULL:
                //if null return null
                result = null;
                break;
            case REXP.XT_ARRAY_BOOL_INT:
                //if boolean array, get the rexp as integer array (full of 0 and 1)
                result = rexp.asIntArray();
                int[] integers = ((int[]) result);
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
                    result = booleanArray[0];
                } else {
                    result = booleanArray;
                }
                //return the boolean array
                break;
            case REXP.XT_ARRAY_STR:
                //if is a string array, return as a string array.
                result = rexp.asStringArray();
                break;
            case REXP.XT_VECTOR:
                //dataframes, lists and vectors are recognized as vectors.
                //get the class of the vector (to successfully detect data.frames)
                String klass = "";
                REXP klassAttribute = rexp.getAttribute(
                    RInstructions.ATTRIBUTE_CLASS);
                if (klassAttribute != null) {
                    klass = klassAttribute.asString();
                }
                //get REXP asList to successfully detect lists.
                org.rosuda.JRI.RList list = rexp.asList();
                if (klass.equals(RInstructions.CLASS_DATAFRAME)) {

                    //if rexp is a data.frame
                    RDataFrame temp = new RDataFrame((REngine) this);

                    //create the data list.
                    List<List<? extends Object>> data =
                        new ArrayList<List<? extends Object>>();
                    //get rexp as a list (data.frame is a list of vectors)
                    org.rosuda.JRI.RList dataList = rexp.asList();
                    for (int i = 0; i < dataList.keys().length; i++) {
                        //for each vector, create a list and fill it with the
                        //content of the vector.
                        List<Object> templist = new ArrayList<Object>();
                        REXP tempREXP = dataList.at(i);
                        Object[] convertedREXP = (Object[]) convertResult(
                            tempREXP);
                        templist = Arrays.asList(convertedREXP);
                        //add this list to the data list.
                        data.add(templist);

                    }
                    //Create a new dataframe with the names, row.names and data
                    //gotten from rexp. It has no variable name so throws a
                    //RException.
                    temp = new RDataFrame((REngine) this, rexp.getAttribute(
                        RInstructions.ATTRIBUTE_NAMES).asStringArray(),
                        rexp.getAttribute(RInstructions.ATTRIBUTE_ROWNAMES).asStringArray(),
                        data, "");
                    result = temp;
                } else if (list != null) {
                    RList temp = new RList((REngine) this);
                    List<Object> data = new ArrayList<Object>();
                    org.rosuda.JRI.RList dataList = rexp.asList();
                    for (int i = 0; i < dataList.keys().length; i++) {
                        //for each object of the list, convert it to java.
                        REXP tempREXP = dataList.at(i);
                        Object convertedREXP = convertResult(
                            tempREXP);
                        //add this object to the data list.
                        data.add(convertedREXP);

                    }
                    //Create a new list with the names and data
                    //gotten from rexp. It has no variable name so throws a
                    //RException.
                    try {
                        temp = new RList(
                            rexp.getAttribute(RInstructions.ATTRIBUTE_NAMES).asStringArray(),
                            data, (REngine) this, "");
                    } catch (RException re) {
                        //don't propagate the error as it is normal. Log it for debug.
                        log.debug(
                                "Converting REXP to RList. Creating list without variable name");
                    }
                    result = temp;
                } else {
                    return vectorToList(rexp.asVector());
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
        log.debug(String.format(RInstructions.RTRY, expr));
        REXP r = engine.eval(String.format(RInstructions.RTRY, expr));
        if ((null != r) && (null != r.getAttribute(RInstructions.ATTRIBUTE_CLASS))) {
            //if the "class" attribute of the R expression is "try-error"
            //throw a new exception with the error message from R.
            String classe = r.getAttribute(RInstructions.ATTRIBUTE_CLASS).asString();
            if (classe.equals(RInstructions.CLASS_ERROR)) {
                throw new RException(r.asString());
            }
        }
    }
    
    @Override
    public Object eval(String expr) throws RException
    {
        REXP result = null;
        log.debug(String.format(RInstructions.RTRY, expr));
        
        //encapsulate the R expression in a try method/object to get the R error
        //message if thrown
        result = engine.eval(String.format(RInstructions.RTRY, expr));
        if (result.getAttribute(RInstructions.ATTRIBUTE_CLASS) != null) {
            //if the "class" attribute of the R expression is "try-error"
            //throw a new exception with the error message from R.
            String klass =
                result.getAttribute(RInstructions.ATTRIBUTE_CLASS).asString();
            if (klass.equals(RInstructions.CLASS_ERROR)) {
                throw new RException(result.asString());
            }
        }
        return convertResult(result);
    }
    
    private RList vectorToList(RVector vec)
    {
        List<String> names = vec.getNames();
        List<Object> data = new ArrayList<Object>(names.size());
        for(String name : names)
        {
            REXP rVal = vec.at(name);
            data.add(convertResult(rVal));
        }
        RList list = null;
        try {
            list = new RList(names.toArray(new String[0]), data, (REngine) this, "");
        } catch(RException re) {
            log.warn("Try-Catch in RJriConnector.vectorToList unexpectedly reached", re);
        }
        return list;
    }

    public void execfile(InputStream scriptStream, String path) throws RException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(scriptStream));
        
        try {
            String line = null;
            while((line = in.readLine()) != null)
            {
                voidEval(line);
            }
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
    
    public String[] ls() throws RException
    {
        return (String[]) eval("ls()");
    }
    
    public List<String> lsVariables()
    {
        List<String> result = new ArrayList<String>();
        try {
            String[] names = ls();
            for(String s : names)
            {
                Object clazzO = eval("class(" + s + ")");
                if(clazzO instanceof String)
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
    
    public List<String> lsFunctions()
    {
        List<String> result = new ArrayList<String>();
        try {
            String[] names = ls();
            for(String s : names)
            {
                Object clazzO = eval("class(" + s + ")");
                if(clazzO instanceof String)
                {
                    String clazz = (String) clazzO;
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
