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
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author connor-woodson
 */
public class RserveConnector implements RConnector {

    public void execfile(InputStream scriptStream, String path) throws RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> lsVariables() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> lsFunctions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean init() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void terminate() throws com.cwoodson.pigaddons.rpig.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> ls() throws com.cwoodson.pigaddons.rpig.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RType eval(String expr) throws com.cwoodson.pigaddons.rpig.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void voidEval(String expr) throws com.cwoodson.pigaddons.rpig.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execFile(InputStream input, String path) throws com.cwoodson.pigaddons.rpig.rutils.RException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
