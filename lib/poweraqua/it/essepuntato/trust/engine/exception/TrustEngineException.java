package it.essepuntato.trust.engine.exception;

public class TrustEngineException
  extends Exception
{
  public TrustEngineException() {}
  
  public TrustEngineException(String message)
  {
    super(message);
  }
  
  public TrustEngineException(Throwable t)
  {
    super(t);
  }
  
  public TrustEngineException(String message, Throwable t)
  {
    super(message, t);
  }
}

