/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons;

import com.cwoodson.pigaddons.rutils.*;
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
import org.nuiton.j2r.RException;

/**
 *
 * @author connor-woodson
 */
public class RScriptEngine extends ScriptEngine
{
    private static class Interpreter
    {
        static final RConnector rEngine;
        static final Set<String> internalNames = new HashSet<String>();
        static
        {
            rEngine = new RJriConnector();
            try {
                rEngine.init();
                rEngine.voidEval("install.packages('rJava', dependencies=TRUE, repos='http://cran.us.r-project.org')");
                rEngine.voidEval("library(rJava)");
                rEngine.voidEval(".jinit()");
            } catch(RException re) {
                throw new RuntimeException("Unable to initialize R", re);
            }
            Runtime.getRuntime().addShutdownHook(new RShutdown());
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
                rEngine.execfile(script, path);
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
                FuncSpec funcspec = new FuncSpec(RFunction.class.getCanonicalName() + "(" + name + ")");
                context.registerFunction(namespace + name, funcspec);
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
        return "rJava";
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
        public void run()
        {
            try {
                Interpreter.rEngine.terminate();
            } catch(Exception e) {
                
            }
        }
        
    }
}