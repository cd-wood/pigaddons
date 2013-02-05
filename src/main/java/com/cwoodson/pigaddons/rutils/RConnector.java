/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rutils;

import java.io.InputStream;
import java.util.List;
import org.nuiton.j2r.RException;

/**
 *
 * @author connor-woodson
 */
public interface RConnector
{
    boolean init();
    
    void terminate() throws RException;
    
    void execfile(InputStream scriptStream, String path) throws RException;
    
    List<String> lsVariables();
    
    List<String> lsFunctions();
    
    Object eval(String expr) throws RException;
    
    void voidEval(String expr) throws RException;
}
