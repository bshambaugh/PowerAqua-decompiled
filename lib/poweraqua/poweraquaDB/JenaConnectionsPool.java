package poweraquaDB;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import java.io.PrintStream;
import java.util.Hashtable;

public class JenaConnectionsPool
  extends ConnectionsPool
{
  public JenaConnectionBean stablishConnexion(String URLServer, String login, String password)
    throws Exception
  {
    long connectionId = generateConnectionId(URLServer, login, password);
    if (this.connections.containsKey(Long.valueOf(connectionId)))
    {
      JenaConnectionBean connection = (JenaConnectionBean)this.connections.get(Long.valueOf(connectionId));
      connection.increaseOpenReaders();
      return connection;
    }
    try
    {
      System.out.println("creating new Conexion " + URLServer);
      String className = "com.mysql.jdbc.Driver";
      Class.forName(className);
      DBConnection connection = new DBConnection(URLServer, login, password, "MySQL");
      ModelMaker modelMaker = ModelFactory.createModelRDBMaker(connection);
      
      JenaConnectionBean newConnection = new JenaConnectionBean(connection, connectionId, modelMaker);
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

