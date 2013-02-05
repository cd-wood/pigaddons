/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons;

import com.cwoodson.pigaddons.rutils.RJriConnector;
import java.io.*;

/**
 *
 * @author connor-woodson
 */
public class MainClass {
    public static void main(String[] args)
    {
        RJriConnector rc = null;
        try {
            rc = new RJriConnector();
            
            rc.init();
            rc.voidEval("install.packages('rJava', dependencies=TRUE, repos='http://cran.us.r-project.org')");
            rc.voidEval("library(rJava)");
            rc.voidEval(".jinit()");
            
            String str = "obj <- .jnew(\"com.cwoodson.pigaddons.TestClass\")\n.jcall(obj, \"V\", \"DoSomething\")";
            InputStream is = new ByteArrayInputStream(str.getBytes());
            rc.execfile(is, ".");
            rc.voidEval("a <- ls");
            rc.voidEval("class(a) <- 'string'");
        } catch(Exception e) {
            System.err.println(e);
            e.printStackTrace();
        } finally {
            if(rc != null)
            {
                rc.terminate();
            }
        }
    }
}