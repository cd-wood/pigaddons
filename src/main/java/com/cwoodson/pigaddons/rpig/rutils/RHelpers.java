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
package com.cwoodson.pigaddons.rpig.rutils;

import com.cwoodson.pigaddons.rpig.rtypes.RType;
import org.rosuda.JRI.REXP;

public class RHelpers {

    private RHelpers() {
    }

    public static String safeCall(String code) {
        return "try(" + code + ", silent=TRUE)";
    }

    public static String asInteger(Integer n) {
        return "as.integer(" + n.toString() + ")";
    }

    public static String printObject(RType rt) throws RException {
        return "print(" + rt.toRString() + ")";
    }

    public static String getClass(REXP rexp) {
        REXP klassAttribute = rexp.getAttribute("class");
        return klassAttribute == null ? "" : klassAttribute.asString();
    }

    public static boolean isDataframe(String klass) {
        return klass.equals("data.frame");
    }

    public static boolean isDataframe(REXP rexp) {
        return isDataframe(getClass(rexp));
    }

    public static boolean isError(String klass) {
        return klass.equals("try-error");
    }

    public static boolean isError(REXP rexp) {
        return isError(getClass(rexp));
    }

    public static REXP getNamesAttribute(REXP rexp) {
        return rexp.getAttribute("names");
    }
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
}