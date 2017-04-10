package poweraquaDB;

import java.io.PrintStream;
import java.sql.Connection;

public class ConnectionMetadataBean
{
  private long id;
  private Connection connection;
  private int numOpenReaders;
  private DataBaseManager dbManager;
  
  public ConnectionMetadataBean(Connection connection, long idConnection, DataBaseManager dbManager)
  {
    this.connection = connection;
    this.id = idConnection;
    this.numOpenReaders = 1;
    this.dbManager = dbManager;
  }
  
  public boolean equals(Object obj)
  {
    if ((obj.getClass().equals(getClass())) && 
      (((ConnectionBean)obj).getId() == getId())) {
      return true;
    }
    return false;
  }
  
  public long getId()
  {
    return this.id;
  }
  
  public Connection getConnection()
  {
    return this.connection;
  }
  
  public void increaseOpenReaders()
  {
    this.numOpenReaders += 1;
  }
  
  public void decreaseOpenReaders()
  {
    this.numOpenReaders -= 1;
  }
  
  public boolean isEraseable()
  {
    if (this.numOpenReaders <= 0) {
      return true;
    }
    return false;
  }
  
  public void close()
  {
    try
    {
      this.connection.close();
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

