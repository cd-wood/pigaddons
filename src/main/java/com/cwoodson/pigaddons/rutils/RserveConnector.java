/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rutils;

import com.cwoodson.pigaddons.rtypes.RType;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author connor-woodson
 */
public class RserveConnector implements RConnector
{
    
    public void execfile(InputStream scriptStream, String path) throws RException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public List<String> lsVariables()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public List<String> lsFunctions()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean init() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void terminate() throws com.cwoodson.pigaddons.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] ls() throws com.cwoodson.pigaddons.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RType eval(String expr) throws com.cwoodson.pigaddons.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void voidEval(String expr) throws com.cwoodson.pigaddons.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execFile(InputStream input, String path) throws com.cwoodson.pigaddons.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
