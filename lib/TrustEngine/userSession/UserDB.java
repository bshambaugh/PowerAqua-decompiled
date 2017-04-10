package TrustEngine.userSession;

import it.essepuntato.xml.handler.XMLDocumentHandler;
import it.essepuntato.xml.handler.exceptions.MakeXMLDocumentHandlerException;
import it.essepuntato.xml.handler.exceptions.SerializeXMLDocumentException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class UserDB
{
  private File base = null;
  private XMLDocumentHandler h = null;
  private Document users = null;
  private File usersFile = null;
  
  public UserDB()
  {
    try
    {
      File base = new File("./logindb");
      generateDB(base);
    }
    catch (Exception e)
    {
      System.out.println("Exception generating database for users " + e);
    }
  }
  
  public UserDB(String real_path)
  {
    try
    {
      File base = new File(real_path + "/logindb");
      generateDB(base);
    }
    catch (Exception e)
    {
      System.out.println("Exception generating database for users " + e);
    }
  }
  
  public void generateDB(File base)
    throws Exception
  {
    try
    {
      this.h = XMLDocumentHandler.getInstance();
      if (!base.exists())
      {
        System.out.println("Making a new database in '" + base.getAbsolutePath() + "'");
        base.mkdirs();
        Document usersDocument = this.h.newDom();
        usersDocument.appendChild(usersDocument.createElement("users"));
        this.usersFile = new File(base.getAbsolutePath() + File.separator + "users.xml");
        this.h.save(usersDocument, this.usersFile);
        this.users = usersDocument;
        this.base = base;
      }
      else
      {
        if ((base.exists()) && (!base.isDirectory())) {
          throw new IOException("[ERROR - TrustDB - generateDB] The base specified is not a directory.");
        }
        if ((base.exists()) && (!new File(base.getAbsolutePath() + File.separator + "users.xml").exists())) {
          throw new IOException("[ERROR - TrustDB - generateDB] The users' index does not exist.");
        }
        System.out.println("Loading the database in '" + base.getAbsolutePath() + "'");
        this.base = base;
        this.usersFile = new File(base.getAbsolutePath() + File.separator + "users.xml");
        this.users = this.h.load(this.usersFile);
      }
    }
    catch (UnsupportedEncodingException ex)
    {
      throw new Exception("[ERROR - TrustDB - generateDB] Problems to save the users' index (Encoding problem)." + ex.getMessage(), ex);
    }
    catch (SerializeXMLDocumentException ex)
    {
      throw new Exception("[ERROR - TrustDB - generateDB] Problems to save the users' index (Serialization problem)." + ex.getMessage(), ex);
    }
    catch (SAXException ex)
    {
      throw new Exception("[ERROR - TrustDB - generateDB] Problems to load the users' index." + ex.getMessage(), ex);
    }
    catch (MakeXMLDocumentHandlerException ex)
    {
      throw new Exception("[ERROR - TrustDB - generateDB] Problems in the XML Document Handler initialization." + ex.getMessage(), ex);
    }
  }
  
  public boolean setUser(String username, String password, String name, String lastName, String company, String email)
    throws Exception
  {
    if (this.base == null) {
      throw new Exception("[ERROR - UserDB - setUser] You must generate a new database");
    }
    try
    {
      List<Node> userNode = this.h.query(this.users, "//user[@id = '" + username + "']");
      if (!userNode.isEmpty()) {
        return false;
      }
      if ((username == null) || (password == null)) {
        return false;
      }
      if (name == null) {
        name = "";
      }
      if (lastName == null) {
        lastName = "";
      }
      if (company == null) {
        company = "";
      }
      if (email == null) {
        email = "";
      }
      Element userElement = this.users.createElement("user");
      userElement.setAttribute("id", username);
      userElement.setAttribute("password", password);
      userElement.setAttribute("name", name);
      userElement.setAttribute("lastname", lastName);
      userElement.setAttribute("company", company);
      userElement.setAttribute("email", email);
      this.users.getDocumentElement().appendChild(userElement);
      this.h.save(this.users, this.usersFile);
    }
    catch (Exception e)
    {
      throw new Exception("[ERROR - UserDB - setUser ]" + e);
    }
    return true;
  }
  
  public boolean existUser(String username, String password)
    throws Exception
  {
    if (this.base == null) {
      throw new Exception("[ERROR - UserDB - setUser] You must generate a new database");
    }
    try
    {
      List<Node> userNodes = this.h.query(this.users, "//user[@id = '" + username + "']");
      if (userNodes.isEmpty()) {
        return false;
      }
      Node curUser = (Node)userNodes.get(0);
      String curUserPassword = this.h.getAttributeValue(curUser, "password");
      if (password.equals(curUserPassword)) {
        return true;
      }
      return false;
    }
    catch (Exception e)
    {
      throw new Exception("[ERROR - UserDB - setUser ]" + e);
    }
  }
  
  public static void main(String[] args)
    throws Exception
  {
    UserDB userDB = new UserDB();
    userDB.setUser("enrico", "enrico", "vanessa", null, null, "v.lopez@open.ac.uk");
    boolean exist = userDB.existUser("vanessa", "pepe");
    exist = userDB.existUser("vanessa", "vanessa");
    exist = userDB.existUser("ana", "");
  }
}

