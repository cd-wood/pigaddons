package com.cwoodson.pigaddons.rpig.rtypes;

import com.cwoodson.pigaddons.rpig.rutils.RException;
import com.cwoodson.pigaddons.rpig.rutils.RHelpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Based off of RList included in org.nuiton.j2r
 * 
 * @author Connor
 *
 */
public class RList extends RType
{
	//Content of the list (eg elements of the list in R)
    protected List<String> names;
	protected List<Object> data;
    /**
     * Create a default RList linked to a R engine (the List is not initialized
     * in R.)
     *
     * @param engine the R engine in which to assign the RList.
     */
    public RList()
    {
        this.names = new ArrayList<String>();
        this.data = new ArrayList<Object>();
    }

    /**
     *  Create the RList with parameters and initialize it in R.
     *
     * @param names the names of each object of the list.
     * @param data the list of the objects that compound the RList.
     * @throws org.nuiton.j2r.RException if an error occur while initializing
     * the list in R.
     */
    public RList(List<String> names, List<Object> data)
    {
        this.names = names;
        this.data = data;
    }
    
    public RList(String[] asStringArray, List<Object> data2) {
    	this(Arrays.asList(asStringArray), data2);
	}

    
    @Override
    public List<Object> asList() {
        return data;
    }
    
    public int contains(String name) {
        int result = -1;
        for(int i = 0; i < names.size(); i++) {
            if(names.get(i).equals(name)) {
                result = i;
                break;
            }
        }
        return result;
    }
    
    public Object get(int id) {
        if(id < 0 || id > data.size()) {
            return null;
        }
        return data.get(id);
    }
    
    public List<String> getNames() {
        return names;
    }
    
    /**
     * Unwrap the elements of the RList
     * into a list of RTypes
     * 
     * @return 
     */
    public List<RType> expand() {
       List<RType> result = new ArrayList<RType>(data.size());
       for(int i = 0; i < data.size(); i++) {
           Object o = data.get(i);
           if(o instanceof RType) {
               result.add((RType)o);
           } else if(o instanceof Object[]) {
               result.add(new RPrimitiveArray((Object[])o));
           } else {
               result.add(new RPrimitive(o));
           }
       }
       return result;
    }
    
	/**
     * Method to export the list in a String for evaluation in R.
     * 
     * @return a R string representation of the list to create it in R.
     * @throws RException
     *             If no variable name is given
     */
    @Override
    public String toRString()
    {
        StringBuilder returnString = new StringBuilder();
        returnString.append("list(");
        if ((this.data != null) && (!(this.data.isEmpty()))) {
            for (int i = 0; i < data.size(); i++) {
                returnString.append(toRString(i));
            }
            returnString = new StringBuilder(returnString.substring(0, returnString.length() - 1));
        }
        returnString.append(")");

        return returnString.toString();
    }

    /**
     * Create the R instruction for element at index i
     * @param i index
     * @return the corresponding R instruction
     * @throws RException if an error occur creating R instruction for REXPs
     */
    protected String toRString(int i)
    {
        String returnString="";

        Object obj = data.get(i);

        if (!(this.names.isEmpty())) {
        	if(this.names.get(i) != null && this.names.get(i).length() > 0)
        	{
        		returnString += this.names.get(i) + "=";
        	}
        }
        
        if (obj instanceof String) {
            returnString += "'" + obj + "',";
        } else if ((obj instanceof Boolean) && ((Boolean) obj)) {
            returnString += RHelpers.TRUE + ",";
        } else if ((obj instanceof Boolean) && (!(Boolean)obj)) {
            returnString += RHelpers.FALSE + ",";
        } else if (obj instanceof Integer) {
            returnString += RHelpers.asInteger((Integer)obj) + ",";
        } else if (obj instanceof RType) {
            returnString += ((RType)obj).toRString() + ",";
        } else {
            returnString += obj + ",";
        }

        return returnString;
    }
    
    @Override
    public String toString()
    {
    	if(data == null)
    	{
    		return "NULL";
    	}
    	String result = "";
    	for(int i = 0; i < data.size(); i++)
    	{
    		if(names != null && i < names.size() && names.get(i) != null && names.get(i).length() > 0)
    		{
    			result += "$" + names.get(i);
    		} else
    		{
    			result += "[[" + Integer.toString(i+1) + "]]";
    		}
    		result += "\n";
    		result += data.get(i).toString();
    		if(i + 1 < data.size())
			{
    			result += "\n\n";
			}
    	}
    	return result;
    }
}