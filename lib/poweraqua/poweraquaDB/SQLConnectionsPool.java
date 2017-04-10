package poweraquaDB;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Hashtable;

public class SQLConnectionsPool
  extends ConnectionsPool
{
  public SQLConnectionBean stablishConnexion(String URLServer, String login, String password)
    throws Exception
  {
    long connectionId = generateConnectionId(URLServer, login, password);
    if (this.connections.containsKey(Long.valueOf(connectionId)))
    {
      SQLConnectionBean connection = (SQLConnectionBean)this.connections.get(Long.valueOf(connectionId));
      connection.increaseOpenReaders();
      return connection;
    }
    try
    {
      System.out.println("creating new Conexion for the metadata " + URLServer + " ************ ");
      String className = "com.mysql.jdbc.Driver";
      Class.forName(className);
      
      Connection conn = DriverManager.getConnection(URLServer, login, password);
      DataBaseManager dbManager = new DataBaseManager(conn);
      
      SQLConnectionBean newConnection = new SQLConnectionBean(connectionId, dbManager);
      this.connections.put(Long.valueOf(connectionId), newConnection);
      return newConnection;
    }
    catch (ClassNotFoundException ex)
    {
      System.out.println("Imposible to stablish the conextion: Server: " + URLServer + " login: " + login + " password " + password);
      ex.printStackTrace();
    }
    return null;
  }
}

