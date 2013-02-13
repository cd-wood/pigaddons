/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cwoodson.pigaddons.rutils;

import java.util.ArrayList;
import java.util.HashMap;
import org.nuiton.j2r.REngine;
import org.nuiton.j2r.RException;
import org.nuiton.j2r.RInstructions;
import org.nuiton.j2r.types.REXP;
import org.nuiton.j2r.types.REXPAbstract;

/**
 *
 * @author connor-woodson
 */
public class RPrimitive extends REXPAbstract
{
    protected Object data;
    
    public RPrimitive()
    {
        super();
        this.names = new ArrayList<String>();
        this.data = null;
        this.variable = "";
        this.engine = engine;
        this.attributes = new HashMap<String, Object>();
    }
    
    public RPrimitive(Object data, REngine engine, String variable) throws RException
    {
        super();
        this.names = new ArrayList<String>(0);
        this.data = data;
        this.variable = variable;
        this.engine = engine;
        this.attributes = new HashMap<String, Object>(0);
        try {
            engine.eval(toRString());
        } catch(RException re) {
            throw new RException("Cannot create RPrimitive in R", re);
        }
    }
    
    public String toRString() throws RException
    {
        String output = "";
        if(!variable.isEmpty())
        {
            output += variable + "<-";
        }
        if(data == null)
        {
            output += "NULL";
        } else
        {
            if (data instanceof String) {
                output += "'" + data.toString() + "'";
            } else if ((data instanceof Boolean) &&
                    ((Boolean) data)) {
                output += RInstructions.TRUE;
            } else if ((data instanceof Boolean) &&
                    (!(Boolean) data)) {
                output += RInstructions.FALSE;
            } else if (data instanceof Integer) {
                output += String.format(RInstructions.AS_INTEGER,
                        data);
            } else if (data instanceof REXP) {
                output += ((REXP) (data)).toRString();
            } else {
                output += data;
            }
        }
        return output;
    }

    public void getFrom(String variable) throws RException
    {
        this.variable = variable;
        if(names == null)
        {
            names = new ArrayList<String>(0);
        } else
        {
            names.clear();
        }
        if(attributes == null)
        {
            attributes = new HashMap<String, Object>(0);
        } else
        {
            attributes.clear();
        }
        
        data = engine.eval(variable);
        
        //update attributes
        Integer attributeslength = (Integer) engine.eval(String.format(
                RInstructions.LENGTH_ATTRIBUTES, this.variable));

        for (int i = 0; i < attributeslength; i++) {
            String key = (String) engine.eval(String.format(
                    RInstructions.GET_ATTRIBUTE_NAME, this.variable, i + 1));

            Object attribute = engine.eval(String.format(
                    RInstructions.GET_ATTRIBUTE, this.variable, key));
            attributes.put(key, attribute);
        }
    }

    public void checkX(int x) {
        return;
    }
    
}
