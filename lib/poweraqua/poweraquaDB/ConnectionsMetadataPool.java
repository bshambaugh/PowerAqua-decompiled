package poweraquaDB;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Hashtable;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class ConnectionsMetadataPool
{
  Hashtable<Long, ConnectionMetadataBean> connections;
  
  public ConnectionsMetadataPool()
  {
    this.connections = new Hashtable();
  }
  
  public ConnectionMetadataBean stablishConnexion(String URLServer, String login, String password)
    throws Exception
  {
    long connectionId = generateConnectionId(URLServer, login, password);
    if (this.connections.containsKey(Long.valueOf(connectionId)))
    {
      ConnectionMetadataBean connection = (ConnectionMetadataBean)this.connections.get(Long.valueOf(connectionId));
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
      
      ConnectionMetadataBean newConnection = new ConnectionMetadataBean(conn, connectionId, dbManager);
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
  
  public void eraseConnection(ConnectionBean connection)
  {
    connection.decreaseOpenReaders();
    if (connection.isEraseable())
    {
      connection.close();
      this.connections.remove(Long.valueOf(connection.getId()));
    }
  }
  
  public void eraseAllConnections()
  {
    for (ConnectionMetadataBean connection : this.connections.values())
    {
      connection.close();
      this.connections.remove(Long.valueOf(connection.getId()));
    }
  }
  
  private long generateConnectionId(String serverURL, String login, String password)
  {
    String id = serverURL + login + password;
    byte[] bytes = id.getBytes();
    Checksum checksumEngine = new CRC32();
    checksumEngine.update(bytes, 0, bytes.length);
    return checksumEngine.getValue();
  }
}

