/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Connor Woodson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cwoodson.pigaddons.rpig.rtypes;

import com.cwoodson.pigaddons.rpig.rutils.RConnector;
import com.cwoodson.pigaddons.rpig.rutils.RException;
import com.cwoodson.pigaddons.rpig.rutils.RHelpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Based off of RDataFrame include in org.nuiton.j2r
 *
 * @author Connor
 *
 */
public class RDataFrame extends RType {

    protected List<String> names;
    protected List<String> rowNames;
    protected List<List<? extends Object>> data;

    /**
     * Constructor
     *
     */
    public RDataFrame() {
        this.names = new ArrayList<String>();
        this.rowNames = new ArrayList<String>();
        this.data = new ArrayList<List<? extends Object>>();
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
            List<String> rowNames, List<List<? extends Object>> data) {
        this.names = names;
        this.rowNames = rowNames;
        this.data = data;
    }

    public RDataFrame(String[] asStringArray,
            String[] asStringArray2, List<List<? extends Object>> data2) {
        this(Arrays.asList(asStringArray), Arrays.asList(asStringArray2), data2);
    }

    /**
     * Method to get the names of the rows of the R data.frame
     *
     * @return a ArrayList of strings containing the names of each row of the R
     * data.frame
     * @throws RException if an error occurs while getting back the names from
     * R.
     * @throws IndexOutOfBoundsException when the row.names size get from R is
     * bigger than the local data size.
     */
    public List<String> getRowNames() {
        return rowNames;
    }

    /**
     * Method to get the row name of the row index y+1 of the R data.frame.
     *
     * @param y index of the row (0 to n-1)
     *
     * @return the name of the ArrayList
     *
     * @throws RException if an error occurs while getting back the row name
     * from R.
     */
    public String getRowName(int y) {
        return this.rowNames.get(y);

    }

    /**
     *
     * @return A list of all data appended into a single list
     */
    @Override
    public List<Object> asList() {
        List<Object> result = new ArrayList<Object>();
        for (List<? extends Object> list : data) {
            result.addAll(list);
        }
        return result;
    }

    /**
     * Method to export the data.frame in a String for evaluation in R.
     *
     * @return a R string representation of the data.frame to create it in R.
     * @throws RException If no variable name is given
     */
    @Override
    public String toRString() {
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
                        returnString += RHelpers.asInteger((Integer) data.get(i).get(j)) + ",";
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
                    returnString.substring(0, returnString.length() - 1)
                    + "),stringsAsFactors=FALSE)";

        } else if (this.data.isEmpty()) {
            returnString += ")";
        } else {
            returnString += "stringsAsFactors=FALSE)";
        }
        return returnString;
    }

    @Override
    public String toString() {
        // I don't know what to put here
        return "";
    }

    @Override
    public String toDecoratedString() {
        // This will take some work
        return "";
    }
}