/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rpig;

import com.cwoodson.pigaddons.rpig.rutils.RConnector;
import com.cwoodson.pigaddons.rpig.rutils.RException;
import com.cwoodson.pigaddons.rpig.rutils.RJriConnector;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.pig.FuncSpec;
import org.apache.pig.PigServer;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.impl.PigContext;
import org.apache.pig.scripting.ScriptEngine;
import org.apache.pig.tools.pigstats.PigStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author connor-woodson
 */
public class RScriptEngine extends ScriptEngine
{
    private static final Logger log = LoggerFactory.getLogger(RScriptEngine.class);
    
    private static class Interpreter
    {
        static final RConnector rEngine;
        static final Set<String> internalNames = new HashSet<String>();
        static
        {
            Runtime.getRuntime().addShutdownHook(new RShutdown());
            try {
                rEngine = RJriConnector.create();
                if(rEngine == null)
                {
                    log.error("Rengine failed to be created. Exiting program.");
                    System.exit(1);
                }
                rEngine.voidEval("if('rJava' %in% rownames(installed.packages()) == FALSE) { install.packages('rJava', dependencies=TRUE, repos='http://cran.us.r-project.org') }");
                rEngine.voidEval("library('rJava')");
                rEngine.voidEval(".jinit()");
                
                // set up helpful functions
                rEngine.voidEval("Utils.logError <<- function(error) {.jcall('com/cwoodson/pigaddons/rfunctions/Utils', 'LogError', error) }");
                rEngine.voidEval("Utils.installPackage <<- function(name) { if(!is.character(name)) { Utils.logError('Utils.installPackage not called on a string'); return(FALSE) }; if(name %in% rownames(installed.packages()) == FALSE) { install.packages(name, dependencies=TRUE, repos='http://cran.us.r-project.org') }; return(TRUE) }");
                
                // set up JavaGD
                //rEngine.voidEval("Sys.setenv('JAVAGD_USE_RJAVA'=TRUE)");
                //rEngine.voidEval("Sys.setenv('JAVAGD_CLASS_NAME'='com.cwoodson.pigaddons.rfunctions.RGraphics')");
                //rEngine.voidEval("Utils.installPackage('JavaGD')");
                //rEngine.voidEval("library('JavaGD')");
                
                String width_str = System.getProperty("rpig.gfx.width");
                Integer width;
                try {
                    width = width_str == null ? 640 : Integer.parseInt(width_str);
                } catch(NumberFormatException nfe) {
                    width = 640;
                }
                
                String height_str = System.getProperty("rpig.gfx.height");
                Integer height;
                try {
                    height = height_str == null ? 480 : Integer.parseInt(height_str);
                } catch(NumberFormatException nfe) {
                    height = 480;
                }
                
                String ps_str = System.getProperty("rpig.gfx.ps");
                Integer ps;
                try {
                    ps = ps_str == null ? 12 : Integer.parseInt(ps_str);
                } catch(NumberFormatException nfe) {
                    ps = 12;
                }
                //rEngine.voidEval("JavaGD('rPig GFX', width=" + width.toString()
                //        + ", height=" + height.toString() + ", ps=" + ps.toString() + ")");
                        
                // set up graphics functions
                  // save - can save internally by adding to a map<String, Image>
                  // save to file
                  // flume?
                  // email?
                
                // set up Pig functions
                  // compile/bind
            } catch(RException re) {
                log.error("RException thrown", re);
                throw new RuntimeException("Unable to initialize R", re);
            }
        }
        
        static void init(String path, PigContext pigContext) throws IOException
        {
            // rengine execute its stuff
            
            InputStream is = getScriptAsStream(path);
            try {
                execFile(is, path, pigContext);
            } finally {
                is.close();
            }
        }
        
        static void execFile(InputStream script, String path, PigContext pigContext) throws ExecException
        {
            try {
                rEngine.execFile(script, path);
            } catch(RException re) {
                throw new ExecException(re);
            }
        }
    }
    
    @Override
    public void registerFunctions(String path, String namespace, PigContext context) throws IOException {
        Interpreter.init(path, context);
        namespace = (namespace == null) ? "" : namespace + NAMESPACE_SEPARATOR;
        for(String name : Interpreter.rEngine.lsFunctions())
        {
            if(!Interpreter.internalNames.contains(name))
            {
                FuncSpec funcspec = new FuncSpec(RFunction.class.getCanonicalName() + "('" + name + "')");
                context.registerFunction(namespace + name, funcspec);
                log.info("Registered Function: " + namespace + name);
            }
        }
        context.addScriptFile(path);
    }

    @Override
    protected Map<String, List<PigStats>> main(PigContext pigContext, String scriptFile) throws IOException {
        PigServer pigServer = new PigServer(pigContext, false);
        
        String thisJarPath = getJarPath(RScriptEngine.class);
        if(thisJarPath != null)
        {
            pigServer.registerJar(thisJarPath);
        }
        
        File f = new File(scriptFile);
        
        if(!f.canRead())
        {
            log.error("Unable to open specified file");
            throw new IOException("Can't read file: " + scriptFile);
        }
        
        try {
            Interpreter.init(scriptFile, pigServer.getPigContext());
        } finally {
            
        }
        
        return getPigStatsMap();
    }

    @Override
    protected String getScriptingLang() {
        return "rPig";
    }

    @Override
    protected Map<String, Object> getParamsFromVariables() throws IOException {
        RConnector rEngine = Interpreter.rEngine;
        
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> variables = rEngine.lsVariables();
        for(String v : variables)
        {
            if(!Interpreter.internalNames.contains(v))
            {
                try {
                    result.put(v, rEngine.eval(v));
                } catch(RException re) {
                    log.error("Error evaluating variable " + v, re);
                    throw new IOException("Error evaluating variable " + v, re);
                }
            }
        }
        return result;
    }
    
    public static RConnector getEngine()
    {
        return Interpreter.rEngine;
    }
    
    private static class RShutdown extends Thread
    {
        @Override
        public void run()
        {
            try {
                if(Interpreter.rEngine != null)
                {
                    Interpreter.rEngine.terminate();
                }
            } catch(Exception e) {
                
            }
        }
    }
}