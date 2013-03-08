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
import java.util.List;
import org.nuiton.j2r.REngine;
import org.nuiton.j2r.RException;
import org.nuiton.j2r.jni.RJniEngine;
import org.nuiton.j2r.types.RList;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author connor-woodson
 */
public class RJriConnector extends RJniEngine implements RConnector
{
    
    private static final Logger log = LoggerFactory.getLogger(RJriConnector.class);
    
    private static final String LIB_PATH = "." + System.getProperty("file.separator");
    private static final String LIB_BIN = "/lib-bin/";
    private static final String[] LIBS = new String[]
    {
        "Rzlib.dll", "Riconv.dll", "R.dll", "Rblas.dll",
        "Rgraphapp.dll", "Rlapack.dll", "jri.dll"
    };
    private static final boolean OVERWRITE = false;
    
    @Override
    public boolean init()
    {
        try {
            System.loadLibrary("jri");
            log.info("JRI successfully loaded");
        } catch(Throwable t) {
            log.error("Unable to load JRI. Exception follows.", t);
        }
        return super.init();
    }
    
    @Override
    public Object eval(String code) throws RException
    {
        Object result = super.eval(code);
        
        // Turn RVector into RList
        if(result instanceof RVector)
        {
            return vectorToList((RVector) result);
        } else {
            return result;
        }
    }
    
    protected Object convert(REXP val)
    {
        Object result = super.convertResult(val);
        
        // Turn RVector into RList
        if(result instanceof RVector)
        {
            return vectorToList((RVector) result);
        } else {
            return result;
        }
    }
    
    private RList vectorToList(RVector vec)
    {
        List<String> names = vec.getNames();
        List<Object> data = new ArrayList<Object>(names.size());
        for(String name : names)
        {
            REXP rVal = vec.at(name);
            data.add(convert(rVal));
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
