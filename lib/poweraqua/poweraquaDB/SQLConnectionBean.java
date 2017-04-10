package poweraquaDB;

import java.io.PrintStream;

public class SQLConnectionBean
  extends ConnectionBean
{
  private DataBaseManager dbManager;
  
  public SQLConnectionBean(long idConnection, DataBaseManager dbManager)
  {
    super(idConnection);
    this.dbManager = dbManager;
  }
  
  public void close()
  {
    try
    {
      this.dbManager.finalice();
    }
    catch (Exception e)
    {
      System.out.println("Error closing the conexion");
    }
  }
  
  public DataBaseManager getDbManager()
  {
    return this.dbManager;
  }
}

