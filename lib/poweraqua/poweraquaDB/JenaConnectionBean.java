package poweraquaDB;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import java.io.PrintStream;

public class JenaConnectionBean
  extends ConnectionBean
{
  private DBConnection connection;
  private ModelMaker modelMaker;
  
  public JenaConnectionBean(DBConnection connection, long idConnection, ModelMaker modelMaker)
  {
    super(idConnection);
    this.connection = connection;
    this.modelMaker = modelMaker;
  }
  
  public DBConnection getConnection()
  {
    return this.connection;
  }
  
  public ModelMaker getModelMaker()
  {
    return this.modelMaker;
  }
  
  public void close()
  {
    try
    {
      this.modelMaker.close();
      this.connection.close();
    }
    catch (Exception e)
    {
      System.out.println("Error closing the conexion");
    }
  }
}

