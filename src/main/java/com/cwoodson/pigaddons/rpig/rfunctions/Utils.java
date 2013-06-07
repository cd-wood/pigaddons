/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rpig.rfunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author connor-woodson
 */
public class Utils {
    private static Logger log = LoggerFactory.getLogger(Utils.class);
    private Utils() {}
    
    public static void LogError(String error) {
        log.error(error);
    }
}
