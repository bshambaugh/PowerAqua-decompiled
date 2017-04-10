package it.essepuntato.trust.engine;

public class LogarithmicDefault
  implements IDefaultValue
{
  private int triplesInKB;
  private int triplesInDB;
  private double initialTrust;
  private double averageTrust;
  private static double K = 5.0D;
  
  public LogarithmicDefault(int triplesInKB, int triplesInDB, double initialTrust, double averageTrust)
  {
    this.triplesInKB = triplesInKB;
    this.triplesInDB = triplesInDB;
    this.initialTrust = initialTrust;
    this.averageTrust = averageTrust;
  }
  
  public double getDefault()
  {
    double pow = Math.pow(this.triplesInDB, 2.0D);
    double log = K * Math.log(this.triplesInKB);
    double denominator = 1.0D + pow + log;
    double initialPrefix = (1.0D + log) / denominator;
    double averagePrefix = pow / denominator;
    
    return initialPrefix * this.initialTrust + averagePrefix * this.averageTrust;
  }
}

