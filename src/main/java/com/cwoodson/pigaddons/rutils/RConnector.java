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
public interface RConnector
{
    boolean init();
    
    void terminate() throws RException;
    
    String[] ls() throws RException;
    
    List<String> lsVariables();
    
    List<String> lsFunctions();
    
    RType eval(String expr) throws RException;
    
    void voidEval(String expr) throws RException;
    
    void execFile(InputStream input, String path) throws RException;
}
