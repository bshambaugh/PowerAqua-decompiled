package poweraquaDB;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConector
{
  public static DataBaseManager openDbConexion(String dbName, String login, String password)
  {
    DataBaseManager dbManager = null;
    try
    {
      System.out.println("creating new Conexion for the database index ontology " + dbName + " ************ ");
      String className = "com.mysql.jdbc.Driver";
      Class.forName(className);
      Connection conn = DriverManager.getConnection(dbName, login, password);
      dbManager = new DataBaseManager(conn);
    }
    catch (ClassNotFoundException ex)
    {
      ex.printStackTrace();
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
    return dbManager;
  }
}

