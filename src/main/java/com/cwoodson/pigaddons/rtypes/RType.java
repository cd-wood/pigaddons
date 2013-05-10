package com.cwoodson.pigaddons.rtypes;

import com.cwoodson.pigaddons.rutils.RConnector;
import com.cwoodson.pigaddons.rutils.RException;
import java.util.List;

public abstract class RType
{
	public abstract String toRString() throws RException;
        
        public abstract List<Object> asList();
        
        public String toRString(String var) throws RException {
            return var + " <- " + toRString();
        }
        
        public void verify(RConnector rEngine) throws RException {
            rEngine.voidEval(toRString());
        }
        
        @Override
	public abstract String toString();
}