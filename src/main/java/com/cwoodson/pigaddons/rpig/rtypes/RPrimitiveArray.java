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

import com.cwoodson.pigaddons.rpig.rutils.RHelpers;
import java.util.Arrays;
import java.util.List;

public class RPrimitiveArray extends RType {

    protected Object[] data;

    public RPrimitiveArray() {
        this.data = null;
    }

    public RPrimitiveArray(Object[] data) {
        this.data = data;
    }

    @Override
    public List<Object> asList() {
        if (data == null) {
            return null;
        } else {
            return Arrays.asList(data);
        }
    }

    public Object[] getArray() {
        return data;
    }

    @Override
    public String toRString() {
        String output = "c(";
        if (data == null || data.length == 0) {
            output += "NULL,";
        } else {
            for (int i = 0; i < data.length; i++) {
                if (data[i] instanceof String) {
                    output += "'" + data[i].toString() + "',";
                } else if ((data[i] instanceof Boolean)
                        && ((Boolean) data[i])) {
                    output += RHelpers.TRUE + ",";
                } else if ((data[i] instanceof Boolean)
                        && (!(Boolean) data[i])) {
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
    public String toDecoratedString() {
        if (data == null) {
            return "NULL";
        }
        String result = "[1]";
        for (int i = 0; i < data.length; i++) {
            result += "\t";
            result += data[i].toString();
        }
        return result;
    }
}