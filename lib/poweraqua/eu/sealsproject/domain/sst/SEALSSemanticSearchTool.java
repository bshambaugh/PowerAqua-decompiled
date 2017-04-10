package eu.sealsproject.domain.sst;

import java.net.URI;

public abstract interface SEALSSemanticSearchTool
{
  public abstract boolean loadOntology(URI paramURI, String paramString1, String paramString2);
  
  public abstract boolean isResultSetReady();
  
  public abstract String getResults();
  
  public abstract boolean executeQuery(String paramString);
  
  public abstract boolean isUserInputComplete();
  
  public abstract String getUserQuery();
  
  public abstract String getInternalQuery();
  
  public abstract void setToolInstallationPath(String paramString);
  
  public abstract void showGUI(boolean paramBoolean);
  
  public abstract boolean isRankedList();
}

