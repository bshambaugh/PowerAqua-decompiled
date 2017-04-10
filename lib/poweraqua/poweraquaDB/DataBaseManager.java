package poweraquaDB;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBaseManager
{
  private Connection conexion;
  
  public DataBaseManager(Connection conexion)
  {
    this.conexion = conexion;
  }
  
  public void disconnect()
  {
    try
    {
      if (getConexion() != null) {
        getConexion().close();
      }
    }
    catch (Exception e) {}
  }
  
  public void finalice()
  {
    try
    {
      disconnect();
    }
    catch (Exception e) {}
  }
  
  public ResultSet executeSelect(String query)
  {
    Statement stm = null;
    ResultSet rs = null;
    try
    {
      stm = getConexion().createStatement();
      rs = stm.executeQuery(query);
    }
    catch (Exception e) {}
    return rs;
  }
  
  public int executeInsert(String query)
  {
    Statement stm = null;
    int numColumnas = 0;
    try
    {
      stm = getConexion().createStatement();
      numColumnas = stm.executeUpdate(query);
      stm.close();
    }
    catch (Exception e)
    {
      System.out.println("Error inserting " + query);
      e.printStackTrace();
    }
    return numColumnas;
  }
  
  public int executeDelete(String query)
  {
    Statement stm = null;
    int numColumnas = 0;
    try
    {
      stm = getConexion().createStatement();
      numColumnas = stm.executeUpdate(query);
      stm.close();
    }
    catch (Exception e) {}
    return numColumnas;
  }
  
  public int executeUpdate(String query)
  {
    Statement stm = null;
    int numColumnas = 0;
    try
    {
      stm = getConexion().createStatement();
      numColumnas = stm.executeUpdate(query);
      stm.close();
    }
    catch (Exception e) {}
    return numColumnas;
  }
  
  public void executeDropTable(String query)
  {
    Statement stm = null;
    try
    {
      stm = getConexion().createStatement();
      stm.executeUpdate(query);
      stm.close();
    }
    catch (Exception e)
    {
      System.out.println("Imposible to drop table ");
    }
  }
  
  public void executeCreateTable(String query)
  {
    Statement stm = null;
    try
    {
      stm = getConexion().createStatement();
      stm.executeUpdate(query);
      stm.close();
    }
    catch (Exception e)
    {
      System.out.println("Imposible to create table ");
    }
  }
  
  public void executeCreateIndex(String query)
  {
    Statement stm = null;
    try
    {
      stm = getConexion().createStatement();
      stm.executeUpdate(query);
      stm.close();
    }
    catch (Exception e)
    {
      System.out.println("Imposible to create Index ");
    }
  }
  
  public void executeDropIndex(String query)
  {
    Statement stm = null;
    try
    {
      stm = getConexion().createStatement();
      stm.executeUpdate(query);
      stm.close();
    }
    catch (Exception e)
    {
      System.out.println("Imposible to drop Index ");
      e.printStackTrace();
    }
  }
  
  public Connection getConexion()
  {
    return this.conexion;
  }
}

