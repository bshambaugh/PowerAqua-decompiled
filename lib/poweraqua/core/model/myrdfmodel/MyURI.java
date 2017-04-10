package poweraqua.core.model.myrdfmodel;

import java.io.Serializable;

public class MyURI
  implements Serializable
{
  private String local_name;
  private String namespace;
  
  public MyURI(String uri)
  {
    handlingString(uri);
  }
  
  public MyURI(String namespace, String local_name)
  {
    this.namespace = namespace;
    this.local_name = local_name;
  }
  
  public String getLocalName()
  {
    if (this.local_name.equals("")) {
      return this.namespace;
    }
    return this.local_name;
  }
  
  public String getNamespace()
  {
    return this.namespace;
  }
  
  public String toString()
  {
    return this.namespace + this.local_name;
  }
  
  private void handlingString(String uri)
  {
    if (uri == null) {
      return;
    }
    int k = uri.lastIndexOf("#");
    if (k > -1)
    {
      this.namespace = uri.substring(0, k + 1);
      this.local_name = uri.substring(k + 1);
    }
    else
    {
      k = uri.lastIndexOf("/");
      this.namespace = uri.substring(0, k + 1);
      this.local_name = uri.substring(k + 1);
    }
  }
  
  public static boolean isURIValid(String uri)
  {
    int k = uri.lastIndexOf("#");
    int k1 = uri.lastIndexOf("/");
    if ((k > -1) || (k1 > -1)) {
      return true;
    }
    if (uri.startsWith("node")) {
      return true;
    }
    return false;
  }
  
  public static String getLocalName(String uri)
  {
    int k = uri.lastIndexOf("#");
    if (k <= -1)
    {
      k = uri.lastIndexOf("/");
      if (k <= -1) {
        return null;
      }
    }
    return uri.substring(k + 1);
  }
  
  public static String getNamespace(String uri)
  {
    int k = uri.lastIndexOf("#");
    if (k <= -1)
    {
      k = uri.lastIndexOf("/");
      if (k <= -1) {
        return null;
      }
    }
    return uri.substring(0, k + 1);
  }
}

