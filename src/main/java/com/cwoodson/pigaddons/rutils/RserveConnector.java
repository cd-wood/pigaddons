/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rutils;

import java.io.InputStream;
import java.util.List;
import org.nuiton.j2r.RException;
import org.nuiton.j2r.net.RNetEngine;

/**
 *
 * @author connor-woodson
 */
public class RserveConnector extends RNetEngine implements RConnector
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
    
}
