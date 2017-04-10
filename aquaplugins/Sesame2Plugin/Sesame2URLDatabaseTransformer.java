package Sesame2Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.URL;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;

public class Sesame2URLDatabaseTransformer
{
  public static void createDBFromURI(String sesameServerPath, String repositoryName, String filePath, String format)
  {
    try
    {
      System.out.println("Uploading into repository " + sesameServerPath + repositoryName);
      Repository myRepository = new HTTPRepository(sesameServerPath, repositoryName);
      
      myRepository.initialize();
      
      RepositoryConnection con = myRepository.getConnection();
      try
      {
        System.out.println("Adding... " + filePath);
        File file = new File(filePath);
        String baseURI = getBaseURI(file);
        if (format.equals("RDF")) {
          con.add(file, baseURI, RDFFormat.RDFXML, new Resource[0]);
        } else if (format.equals("N3")) {
          con.add(file, baseURI, RDFFormat.N3, new Resource[0]);
        } else {
          con.add(file, baseURI, RDFFormat.NTRIPLES, new Resource[0]);
        }
      }
      finally
      {
        con.close();
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static void createDBFromURI(String sesameServerPath, String repositoryName, String filePath, String format, String baseURI)
  {
    try
    {
      System.out.println("Uploading into repository " + sesameServerPath + repositoryName);
      Repository myRepository = new HTTPRepository(sesameServerPath, repositoryName);
      
      myRepository.initialize();
      
      RepositoryConnection con = myRepository.getConnection();
      try
      {
        System.out.println("Adding... " + filePath);
        
        URL file = new URL(filePath);
        if (format.equals("RDF")) {
          con.add(file, baseURI, RDFFormat.RDFXML, new Resource[0]);
        } else if (format.equals("N3")) {
          con.add(file, baseURI, RDFFormat.N3, new Resource[0]);
        } else {
          con.add(file, baseURI, RDFFormat.NTRIPLES, new Resource[0]);
        }
      }
      finally
      {
        con.close();
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private static String getBaseURI(File file)
  {
    String baseURI = "";
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      
      String line = null;
      while ((line = reader.readLine()) != null)
      {
        int pos = line.indexOf("xml:base=\"");
        if (pos >= 0)
        {
          baseURI = line.substring(pos + 10);
          
          pos = baseURI.indexOf("\"");
          if (pos >= 0) {}
          return baseURI.substring(0, pos);
        }
        pos = line.indexOf("xml:base='");
        if (pos >= 0)
        {
          baseURI = line.substring(pos + 10);
          
          pos = baseURI.indexOf("'");
          if (pos >= 0) {}
          return baseURI.substring(0, pos);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return baseURI;
  }
  
  public static void main(String[] args)
  {
    String sesameServerPath = "http://kmi-web03:8080/openrdf-sesame/";
    String repositoryName = "evoont-seals_10000K_final";
    
    String path = "http://kmi.open.ac.uk/technologies/poweraqua/evoont-seals_10000K_final.owl";
    String baseURI = "";
    createDBFromURI(sesameServerPath, repositoryName, path, "RDF", baseURI);
  }
}

