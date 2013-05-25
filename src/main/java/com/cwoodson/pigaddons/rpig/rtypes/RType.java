package com.cwoodson.pigaddons.rpig.rtypes;

import com.cwoodson.pigaddons.rpig.rutils.RConnector;
import com.cwoodson.pigaddons.rpig.rutils.RException;
import java.util.List;

public abstract class RType
{
	public abstract String toRString();
        
        public abstract List<Object> asList();
        
        public String toRString(String var) {
            return var + " <- " + toRString();
        }
        
        public void verify(RConnector rEngine) throws RException {
            rEngine.voidEval(toRString());
        }
        
        @Override
	public abstract String toString();
        
        // This will produce a string that can be
        // Viewed in a console
        public abstract String toDecoratedString();
}