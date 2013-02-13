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
public class RPrimitiveArray extends REXPAbstract
{
    protected Object[] data;
    
    public RPrimitiveArray()
    {
        super();
        this.names = new ArrayList<String>();
        this.data = null;
        this.variable = "";
        this.engine = engine;
        this.attributes = new HashMap<String, Object>();
    }
    
    public RPrimitiveArray(Object[] data, REngine engine, String variable) throws RException
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
            output += variable + "<-c(";
        }
        if(data == null)
        {
            output += "NULL";
        } else
        {
            for(int i = 0; i < data.length; i++)
            {
                if (data[i] instanceof String) {
                    output += "'" + data[i].toString() + "',";
                } else if ((data[i] instanceof Boolean) &&
                        ((Boolean) data[i])) {
                    output += RInstructions.TRUE + ",";
                } else if ((data[i] instanceof Boolean) &&
                        (!(Boolean) data[i])) {
                    output += RInstructions.FALSE + ",";
                } else if (data[i] instanceof Integer) {
                    output += String.format(RInstructions.AS_INTEGER,
                            data[i]) + ",";
                } else if (data[i] instanceof REXP) {
                    output += ((REXP) data[i]).toRString() + ",";
                } else {
                    output += data[i] + ",";
                }
            }
        }
        output = output.substring(0, output.length() - 1);
        output += ")";
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
        
        Object result = engine.eval(variable);
        if(result instanceof Object[])
        {
            data = (Object[]) result;
        } else
        {
            data = null;
        }
        
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
