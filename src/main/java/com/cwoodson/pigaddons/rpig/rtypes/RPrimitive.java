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
import java.util.ArrayList;
import java.util.List;

public class RPrimitive extends RType {

    protected Object data;

    public RPrimitive() {
        this.data = null;
    }

    public RPrimitive(Object data) {
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
    public String toRString() {
        String output = "";
        if (data == null) {
            output += "NULL";
        } else {
            if (data instanceof String) {
                output += "'" + data.toString() + "'";
            } else if ((data instanceof Boolean)
                    && ((Boolean) data)) {
                output += RHelpers.TRUE;
            } else if ((data instanceof Boolean)
                    && (!(Boolean) data)) {
                output += RHelpers.FALSE;
            } else if (data instanceof Integer) {
                output += RHelpers.asInteger((Integer) data);
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
    public String toDecoratedString() {
        if (data == null) {
            return "NULL";
        }

        return "[1]\t" + data.toString();
    }
}