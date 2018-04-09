/*
 * $Id: DataException.java 168967 2011-02-25 21:56:00Z cbotev $
 */
package com.melot.data.change.exception;


/**
 * Generic Databus exception
 */
public class DataException
    extends Exception
{
  private static final long serialVersionUID = 1L;

  public DataException()
  {
    super();
  }

  public DataException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public DataException(String message)
  {
    super(message);
  }

  public DataException(Throwable cause)
  {
    super(cause);
  }
}
