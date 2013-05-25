package com.cwoodson.pigaddons.rpig.rtypes;

import com.cwoodson.pigaddons.rpig.rutils.RException;
import com.cwoodson.pigaddons.rpig.rutils.RHelpers;
import java.util.Arrays;
import java.util.List;

public class RPrimitiveArray extends RType
{
	protected Object[] data;
    
	public RPrimitiveArray()
	{
		this.data = null;
	}
	
    public RPrimitiveArray(Object[] data)
    {
        this.data = data;
    }
    
    @Override
    public List<Object> asList() {
        if(data == null) {
            return null;
        } else {
            return Arrays.asList(data);
        }
    }
    
    public Object[] getValue() {
        return data;
    }
    
    @Override
    public String toRString()
    {
        String output = "c(";
        if(data == null || data.length == 0)
        {
            output += "NULL,";
        } else
        {
            for(int i = 0; i < data.length; i++)
            {
                if (data[i] instanceof String) {
                    output += "'" + data[i].toString() + "',";
                } else if ((data[i] instanceof Boolean) &&
                        ((Boolean) data[i])) {
                    output += RHelpers.TRUE + ",";
                } else if ((data[i] instanceof Boolean) &&
                        (!(Boolean) data[i])) {
                    output += RHelpers.FALSE + ",";
                } else if (data[i] instanceof Integer) {
                    output += RHelpers.asInteger((Integer) data[i]) + ",";
                } else if (data[i] instanceof RType) {
                    output += ((RType) data[i]).toRString() + ",";
                } else {
                    output += data[i] + ",";
                }
            }
        }
        output = output.substring(0, output.length() - 1);
        output += ")";
        return output;
    }
    
    @Override
    public String toString() {
        // I don't know what to put here
        return "";
    }
    
    @Override
    public String toDecoratedString()
    {
    	if(data == null)
    	{
    		return "NULL";
    	}
    	String result = "[1]";
    	for(int i = 0; i < data.length; i++)
		{
    		result += "\t";
    		result += data[i].toString();
		}
    	return result;
    }
}