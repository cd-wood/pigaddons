/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rutils;

import java.io.IOException;

/**
 *
 * @author connor-woodson
 */
public class RJriConnector extends RConnector
{
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
            } catch(Throwable t) {
                System.err.printf("Failed to load necessary DLLs for RJriConnector: %s", t.getMessage());
            }
        }

    }

    @Override
    public void connect() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
