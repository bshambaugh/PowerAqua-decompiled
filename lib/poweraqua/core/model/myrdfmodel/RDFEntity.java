package poweraqua.core.model.myrdfmodel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import poweraqua.core.utils.LabelSplitter;

public class RDFEntity
  implements Serializable
{
  private MyURI uri;
  private String label;
  private String type;
  private String idPlugin;
  private RDFEntity refers_to;
  private ArrayList<String> groupLiteralURIs;
  private RDFEntityList groupLiteralPropertiesURIs;
  
  public RDFEntity(String type, String uri, String label, String idPlugin)
  {
    this.uri = new MyURI(uri);
    this.label = label;
    this.type = type;
    setIdPlugin(idPlugin);
    this.groupLiteralURIs = new ArrayList();
  }
  
  public void setRDFEntityPlugin(String idPlugin)
  {
    setIdPlugin(idPlugin);
  }
  
  public void setLabel(String l)
  {
    this.label = l;
  }
  
  public String getLabel()
  {
    if (this.label == null) {
      return getLocalName();
    }
    return this.label;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public String getLocalName()
  {
    return this.uri == null ? null : this.uri.getLocalName();
  }
  
  public String getNamespace()
  {
    return this.uri.getNamespace();
  }
  
  public String getURI()
  {
    if (this.uri == null) {
      return null;
    }
    return this.uri.toString();
  }
  
  public String getURIorLabel()
  {
    if (isLiteral()) {
      return this.label;
    }
    return getURI();
  }
  
  public String getLocalNameorLabel()
  {
    if (isLiteral()) {
      return this.label;
    }
    return getLocalName();
  }
  
  public boolean isClass()
  {
    if (this.type.equalsIgnoreCase("class")) {
      return true;
    }
    return false;
  }
  
  public boolean isInstance()
  {
    if (this.type.equals("instance")) {
      return true;
    }
    return false;
  }
  
  public boolean isProperty()
  {
    if (this.type.equals("property")) {
      return true;
    }
    return false;
  }
  
  public boolean isLiteral()
  {
    if (this.type.equals("literal")) {
      return true;
    }
    return false;
  }
  
  public boolean isDataType()
  {
    if (this.type.equals("datatype")) {
      return true;
    }
    return false;
  }
  
  public boolean isIndexable()
  {
    if ((getLabel() != null) && (getLabel().length() > 0)) {
      return true;
    }
    if ((getLocalName() != null) && (getLocalName().length() > 0)) {
      return true;
    }
    return false;
  }
  
  public String getIndexLabels()
  {
    StringBuffer indexLabels = new StringBuffer();
    if ((getLabel() != null) && (getLabel().length() > 0))
    {
      String labelSplit = LabelSplitter.splitOnCaps(getLabel());
      indexLabels.append(labelSplit.toLowerCase().trim() + " ");
    }
    if ((getLocalName() != null) && (getLocalName().length() > 0)) {
      indexLabels.append(getLocalName().toLowerCase().trim() + " ");
    }
    return indexLabels.toString();
  }
  
  public String toString()
  {
    if (this.refers_to != null) {
      return new String("RDFEntity  uri\t " + this.uri + " \tlabel\t " + this.label + " \ttype\t " + this.type + " \trefers to\t " + this.refers_to.getURI());
    }
    return new String("RDFEntity  uri\t " + this.uri + " \tlabel\t " + this.label + " \ttype\t " + this.type);
  }
  
  public String getIdPlugin()
  {
    return this.idPlugin;
  }
  
  public void setIdPlugin(String idPlugin)
  {
    this.idPlugin = idPlugin;
  }
  
  public boolean equals(Object obj)
  {
    if (obj.getClass() != getClass()) {
      return false;
    }
    RDFEntity entity = (RDFEntity)obj;
    if (getURI() == null)
    {
      System.out.println("Error in Entity " + this);
      return false;
    }
    if (entity.getURI() == null)
    {
      System.out.println("Error in Entity " + entity);
      return false;
    }
    if ((getURI().equals(entity.getURI())) && (getType().equals(entity.getType())) && (getLabel() != null) && (getLabel().equalsIgnoreCase(entity.getLabel())) && (getIdPlugin().equals(entity.getIdPlugin()))) {
      return true;
    }
    return false;
  }
  
  public RDFEntity clone()
  {
    return new RDFEntity(getType(), getURI(), getLabel(), getIdPlugin());
  }
  
  public int hashCode()
  {
    String id = getURI() + getLabel() + getType() + this.idPlugin;
    byte[] bytes = id.getBytes();
    Checksum checksumEngine = new CRC32();
    checksumEngine.update(bytes, 0, bytes.length);
    return new Long(checksumEngine.getValue()).intValue();
  }
  
  public RDFEntity getRefers_to()
  {
    return this.refers_to;
  }
  
  public void setRefers_to(RDFEntity refers_to)
  {
    if (refers_to != null) {
      this.refers_to = refers_to;
    }
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public ArrayList<String> getGroupLiteralURIs()
  {
    return this.groupLiteralURIs;
  }
  
  public void setGroupLiteralURIs(ArrayList<String> groupLiteralURIs)
  {
    this.groupLiteralURIs = groupLiteralURIs;
  }
  
  public void addGroupLiteralURIs(String uriLiteral)
  {
    this.groupLiteralURIs.add(uriLiteral);
  }
  
  public void addGroupLiteralPropertiesURIs(RDFEntity uriLiteral)
  {
    if (this.groupLiteralPropertiesURIs == null) {
      this.groupLiteralPropertiesURIs = new RDFEntityList();
    }
    if (!this.groupLiteralPropertiesURIs.getAllRDFEntities().contains(uriLiteral)) {
      this.groupLiteralPropertiesURIs.addRDFEntity(uriLiteral);
    }
  }
  
  public RDFEntityList getGroupLiteralPropertiesURIs()
  {
    return this.groupLiteralPropertiesURIs;
  }
}
