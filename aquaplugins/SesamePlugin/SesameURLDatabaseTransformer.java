package SesamePlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.openrdf.sesame.Sesame;
import org.openrdf.sesame.admin.AdminListener;
import org.openrdf.sesame.admin.StdOutAdminListener;
import org.openrdf.sesame.config.AccessDeniedException;
import org.openrdf.sesame.config.ConfigurationException;
import org.openrdf.sesame.config.UnknownRepositoryException;
import org.openrdf.sesame.constants.RDFFormat;
import org.openrdf.sesame.repository.SesameRepository;
import org.openrdf.sesame.repository.SesameService;

public class SesameURLDatabaseTransformer
{
  public static void createDBFromURI(String sesameServerPath, String repositoryName, String filePath, boolean verifyData, boolean RDFTriples)
  {
    try
    {
      System.out.println("Adding " + filePath);
      File file = new File(filePath);
      String baseURI = getBaseURI(file);
      
      URL sesameServerURL = new URL(sesameServerPath);
      SesameService service = Sesame.getService(sesameServerURL);
      SesameRepository myRepository = service.getRepository(repositoryName);
      
      File myRDFData = new File(filePath);
      
      AdminListener myListener = new StdOutAdminListener();
      if (RDFTriples) {
        myRepository.addData(myRDFData, baseURI, RDFFormat.RDFXML, verifyData, myListener);
      } else {
        myRepository.addData(myRDFData, baseURI, RDFFormat.NTRIPLES, verifyData, myListener);
      }
    }
    catch (MalformedURLException ex)
    {
      ex.printStackTrace();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    catch (UnknownRepositoryException ex)
    {
      ex.printStackTrace();
    }
    catch (ConfigurationException ex)
    {
      ex.printStackTrace();
    }
    catch (AccessDeniedException ex)
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
    String sesameServerPath = "http://kmi-web07.open.ac.uk:8080/sesame";
    String repositoryName = "dbpedia_infoboxes";
    
    String path = "/tmp/bparts2/2008-11-01infobox-rest.nt.5.1";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-infobox-part1.nt";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-11-01infobox-rest.nt.1.1";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-11-01infobox-rest.nt.1.2";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-11-01infobox-rest.nt.2.1";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-11-01infobox-rest.nt.2.2";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-11-01infobox-rest.nt.3.1";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-11-01infobox-rest.nt.3.2";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-11-01infobox-rest.nt.4.1";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-11-01infobox-rest.nt.4.2";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
    
    path = "/tmp/bparts2/2008-11-01infobox-rest.nt.5.1";
    createDBFromURI(sesameServerPath, repositoryName, path, false, false);
  }
}

