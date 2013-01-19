/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.pig.impl.PigContext;
import org.apache.pig.scripting.ScriptEngine;
import org.apache.pig.tools.pigstats.PigStats;

/**
 *
 * @author connor-woodson
 */
public class RScriptEngine extends ScriptEngine
{

    @Override
    public void registerFunctions(String string, String string1, PigContext pc) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, List<PigStats>> main(PigContext pc, String string) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String getScriptingLang() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Map<String, Object> getParamsFromVariables() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
