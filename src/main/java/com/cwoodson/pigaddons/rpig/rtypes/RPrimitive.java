package com.cwoodson.pigaddons.rpig.rtypes;

import com.cwoodson.pigaddons.rpig.rutils.RException;
import com.cwoodson.pigaddons.rpig.rutils.RHelpers;
import java.util.ArrayList;
import java.util.List;

public class RPrimitive extends RType
{
	protected Object data;
    
	public RPrimitive()
	{
		this.data = null;
	}
	
    public RPrimitive(Object data)
    {
        this.data = data;
    }
    
    @Override
    public List<Object> asList() {
        List<Object> result = new ArrayList<Object>(1);
        result.add(data);
        return result;
    }
    
    public Object getValue() {
        return data;
    }
    
    @Override
    public String toRString()
    {
        String output = "";
        if(data == null)
        {
            output += "NULL";
        } else
        {
            if (data instanceof String) {
                output += "'" + data.toString() + "'";
            } else if ((data instanceof Boolean) &&
                    ((Boolean) data)) {
                output += RHelpers.TRUE;
            } else if ((data instanceof Boolean) &&
                    (!(Boolean) data)) {
                output += RHelpers.FALSE;
            } else if (data instanceof Integer) {
                output += RHelpers.asInteger((Integer)data);
            } else if (data instanceof RType) {
                output += ((RType) (data)).toRString();
            } else {
                output += data;
            }
        }
        return output;
    }
    
    @Override
    public String toString() {
        return (data == null) ? "" : data.toString();
    }
    
    @Override
    public String toDecoratedString()
    {
    	if(data == null)
    	{
    		return "NULL";
    	}
    	
    	return "[1]\t" + data.toString();
    }
}