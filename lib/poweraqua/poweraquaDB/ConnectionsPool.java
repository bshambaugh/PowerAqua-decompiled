package poweraquaDB;

import java.util.Hashtable;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public abstract class ConnectionsPool
{
  protected Hashtable<Long, ConnectionBean> connections;
  
  public ConnectionsPool()
  {
    this.connections = new Hashtable();
  }
  
  public abstract ConnectionBean stablishConnexion(String paramString1, String paramString2, String paramString3)
    throws Exception;
  
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
    for (ConnectionBean connection : this.connections.values())
    {
      connection.close();
      this.connections.remove(Long.valueOf(connection.getId()));
    }
  }
  
  protected long generateConnectionId(String serverURL, String login, String password)
  {
    String id = serverURL + login + password;
    byte[] bytes = id.getBytes();
    Checksum checksumEngine = new CRC32();
    checksumEngine.update(bytes, 0, bytes.length);
    return checksumEngine.getValue();
  }
}

