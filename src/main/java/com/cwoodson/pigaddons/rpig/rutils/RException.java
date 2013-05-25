package com.cwoodson.pigaddons.rpig.rutils;

public class RException extends Exception
{
	private static final long serialVersionUID = 6129833069962783888L;

	public RException(String message)
	{
		super(message);
	}
	
	public RException(String message, Throwable cause)
	{
		super(message, cause);
	}
}