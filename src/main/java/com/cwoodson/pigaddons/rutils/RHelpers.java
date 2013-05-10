package com.cwoodson.pigaddons.rutils;

import com.cwoodson.pigaddons.rtypes.RType;
import org.rosuda.JRI.REXP;

public class RHelpers
{
	private RHelpers() {}
	
	public static String safeCall(String code)
	{
		return "try(" + code + ", silent=TRUE)";
	}
	
	public static String asInteger(Integer n)
	{
		return "as.integer(" + n.toString() + ")";
	}
	
	public static String printObject(RType rt) throws RException
	{
		return "print(" + rt.toRString() + ")";
	}
        
        public static String getClass(REXP rexp) {
            REXP klassAttribute = rexp.getAttribute("class");
            return klassAttribute == null ? "" :  klassAttribute.asString();
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
	
	public static final String TRUE = "TRUE";
	public static final String FALSE = "FALSE";
}