package it.essepuntato.trust.engine;

import it.essepuntato.trust.engine.exception.TrustEngineException;

public abstract interface ITrustEngine
{
  public abstract void store()
    throws TrustEngineException;
  
  public abstract void setInitialTrust(String paramString1, String paramString2, Double paramDouble)
    throws TrustEngineException;
  
  public abstract double getInitialTrust(String paramString1, String paramString2)
    throws TrustEngineException;
  
  public abstract void evaluate(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, double paramDouble)
    throws TrustEngineException;
  
  public abstract double getEvaluation(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws TrustEngineException;
  
  public abstract double getEntityEvaluation(String paramString1, String paramString2, String paramString3)
    throws TrustEngineException;
  
  public abstract double getGlobalEvaluation(String paramString1, String paramString2, String paramString3, String paramString4)
    throws TrustEngineException;
  
  public abstract double getGlobalEntityEvaluation(String paramString1, String paramString2)
    throws TrustEngineException;
  
  public abstract double getOntologyEvaluation(String paramString1, String paramString2)
    throws TrustEngineException;
  
  public abstract double getGlobalOntologyEvaluation(String paramString1, String paramString2)
    throws TrustEngineException;
}

