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
import org.nuiton.j2r.RException;
import org.nuiton.j2r.jni.RJniEngine;
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
    
    static
    {
        try {
            System.loadLibrary("jri");
        } catch(UnsatisfiedLinkError ule) {
            try {
                LibraryLoader.extractNativeLibraries(LIB_BIN, LIB_PATH, OVERWRITE, LIBS);
                System.loadLibrary("jri");
            } catch(Throwable t) {}
        }
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
}
