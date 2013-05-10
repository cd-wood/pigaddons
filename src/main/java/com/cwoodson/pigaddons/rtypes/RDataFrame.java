package com.cwoodson.pigaddons.rtypes;

import com.cwoodson.pigaddons.rutils.RConnector;
import com.cwoodson.pigaddons.rutils.RException;
import com.cwoodson.pigaddons.rutils.RHelpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Based off of RDataFrame include in org.nuiton.j2r
 * 
 * @author Connor
 *
 */
public class RDataFrame extends RType
{
	protected List<String> names;
	protected List<String> rowNames;
	protected List<List<? extends Object>> data;
	
	/**
     * Constructor
     *
     */
    public RDataFrame()
    {
        this.names = new ArrayList<String>();
        this.rowNames = new ArrayList<String>();
        this.data = new ArrayList<List<? extends Object>>();
    }

    /**
     * Constructor
     *
     * @param datatypes a table object determining the type of each column of
     * the data.frame.
     * @param y the length of each vector that compound the data.frame.
     */
    public RDataFrame(Object[] datatypes, int y) throws RException
    {
        this.names = new ArrayList<String>();
        this.rowNames = new ArrayList<String>();
        this.data = new ArrayList<List<? extends Object>>();
        for (int i = 0; i < datatypes.length; i++) {
            //create one column for each datatype element
            //check if type is supported
            checkType(datatypes[i]);
            //create the list of objects
            List<Object> column = new ArrayList<Object>();
            //Fill it with instances of the datatype
            for (int j = 0; j < y; j++) {
                if (datatypes[i] instanceof Double) {
                    column.add(0.0);
                } else if (datatypes[i] instanceof Integer) {
                    column.add(0);
                } else if (datatypes[i] instanceof Boolean) {
                    column.add(true);
                } else if (datatypes[i] instanceof String) {
                    column.add("");
                }
            }
            //add the column to the dataframe.
            data.add(column);
        }
    }

    /**
     * Constructor
     *
     * @param names names of the the data.frame columns.
     * @param rowNames names of the data.frame rows.
     * @param data the data of the data.frame
     * @throws org.nuiton.j2r.RException if an error occur while trying to
     * initialize 
     */
    public RDataFrame(List<String> names,
            List<String> rowNames, List<List<? extends Object>> data) throws RException
    {
        this.names = names;
        this.rowNames = rowNames;
        this.data = data;
    }

    public RDataFrame(String[] asStringArray,
			String[] asStringArray2, List<List<? extends Object>> data2) throws RException {
		this(Arrays.asList(asStringArray), Arrays.asList(asStringArray2), data2);
	}

	/**
     * Method to get the names of the rows of the R data.frame
     * 
     * @return a ArrayList of strings containing the names of each row of the R
     *         data.frame
     * @throws RException
     *             if an error occurs while getting back the names from R.
     * @throws IndexOutOfBoundsException
     *             when the row.names size get from R is bigger than the local
     *             data size.
     */
    public List<String> getRowNames()
    {
        return rowNames;
    }

    /**
     * Method to get the row name of the row index y+1 of the R data.frame.
     * 
     * @param y
     *            index of the row (0 to n-1)
     * 
     * @return the name of the ArrayList
     * 
     * @throws RException
     *             if an error occurs while getting back the row name from R.
     */
    public String getRowName(int y)
    {
        return this.rowNames.get(y);

    }
    /**
     * 
     * @return A list of all data appended into a single list
     */
    @Override
    public List<Object> asList() {
        List<Object> result = new ArrayList<Object>();
        for(List<? extends Object> list : data) {
            result.addAll(list);
        }
        return result;
    }

    /**
     * Method to export the data.frame in a String for evaluation in R.
     * 
     * @return a R string representation of the data.frame to create it in R.
     * @throws RException
     *             If no variable name is given
     */
    @Override
    public String toRString() throws RException
    {
        String returnString = "data.frame(";
        if (!(this.data.isEmpty())) {
            for (int i = 0; i < data.size(); i++) {
                if (!(this.names.isEmpty())) {
                    returnString += this.names.get(i) + "=c(";
                } else {
                    returnString += "c(";
                }
                if (data.get(i).get(0) instanceof String) {
                    for (int j = 0; j < data.get(i).size(); j++) {
                        returnString += "\"" + data.get(i).get(j) + "\",";
                    }
                } else if (data.get(i).get(0) instanceof Boolean) {
                    for (int j = 0; j < data.get(i).size(); j++) {
                        if ((Boolean) data.get(i).get(j)) {
                            returnString += RHelpers.TRUE + ",";
                        } else {
                            returnString += RHelpers.FALSE + ",";
                        }

                    }
                } else if (data.get(i).get(0) instanceof Integer) {
                    for (int j = 0; j < data.get(i).size(); j++) {
                        returnString += RHelpers.asInteger((Integer)
                                data.get(i).get(j)) + ",";
                    }
                } else {
                    for (int j = 0; j < data.get(i).size(); j++) {
                        returnString += data.get(i).get(j) + ",";
                    }
                }
                returnString = returnString.substring(0,
                        returnString.length() - 1);
                returnString = returnString + "),";

            }
        }
        if (!(this.rowNames.isEmpty())) {
            returnString += "row.names=c(";
            for (int i = 0; i < rowNames.size(); i++) {
                returnString += "\"" + rowNames.get(i) + "\",";
            }
            returnString =
                    returnString.substring(0, returnString.length() - 1) +
                    "),stringsAsFactors=FALSE)";

        } else if (this.data.isEmpty()) {
            returnString += ")";
        } else {
            returnString += "stringsAsFactors=FALSE)";
        }
        return returnString;
    }
    
    @Override
    public String toString() {
        return "";
    }
    
    private void checkType(Object o) throws RException {
        if (!(o instanceof String) && !(o instanceof Double) &&
                !(o instanceof Integer) && !(o instanceof Boolean)) {
            throw new RException("Not supported type");
        }
    }
}