package poweraqua.core.model.myocmlmodel;

import java.io.Serializable;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;

public class OcmlClass
  implements Serializable
{
  private RDFEntity entityClass;
  private RDFEntityList equivalentClasses;
  private RDFEntityList subClasses;
  private RDFEntityList superClasses;
  private RDFEntityList directSubclasses;
  private RDFEntityList directSuperClasses;
  private RDFEntityList properties;
  private RDFEntityList directProperties;
  
  public OcmlClass(RDFEntity entityClass)
  {
    this.entityClass = entityClass;
    this.equivalentClasses = new RDFEntityList();
    setSubClasses(new RDFEntityList());
    setSuperClasses(new RDFEntityList());
    setDirectSubclasses(new RDFEntityList());
    setDirectSuperClasses(new RDFEntityList());
    setProperties(new RDFEntityList());
  }
  
  public RDFEntityList listAllProperties()
  {
    return this.properties;
  }
  
  public RDFEntityList listDirectProperties()
  {
    return this.directProperties;
  }
  
  public RDFEntityList listEquivalentClasses()
  {
    return this.equivalentClasses;
  }
  
  public RDFEntityList listAllSubClasses()
  {
    return this.subClasses;
  }
  
  public RDFEntityList listDirectSubClasses()
  {
    return this.directSubclasses;
  }
  
  public RDFEntityList listAllSuperClasses()
  {
    return this.superClasses;
  }
  
  public RDFEntityList listDirectSuperClasses()
  {
    return this.directSuperClasses;
  }
  
  public String getPrettyName()
  {
    return this.entityClass.getLabel();
  }
  
  public String getClassName()
  {
    return this.entityClass.getLocalName();
  }
  
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    
    buffer.append("URI: " + this.entityClass.getURI() + "\n" + "Properties: ");
    for (RDFEntity property : listAllProperties().getAllRDFEntities()) {
      buffer.append("Property: " + property.getURI() + "\n");
    }
    buffer.append("Supeclasses: \n");
    for (RDFEntity property : listAllSuperClasses().getAllRDFEntities()) {
      buffer.append("Property: " + property.getURI() + "\n");
    }
    buffer.append("Subclasses: \n");
    for (RDFEntity property : listAllSubClasses().getAllRDFEntities()) {
      buffer.append("Property: " + property.getURI() + "\n");
    }
    return buffer.toString();
  }
  
  public String toHTML()
  {
    StringBuffer buffer = new StringBuffer();
    
    buffer.append("URI: " + this.entityClass.getURI() + "<br>" + "Properties: ");
    for (RDFEntity property : listAllProperties().getAllRDFEntities()) {
      buffer.append("Property: " + property.getURI() + "<br>");
    }
    buffer.append("Supeclasses: <br>");
    for (RDFEntity property : listAllSuperClasses().getAllRDFEntities()) {
      buffer.append("Property: " + property.getURI() + "<br>");
    }
    buffer.append("Subclasses: <br>");
    for (RDFEntity property : listAllSubClasses().getAllRDFEntities()) {
      buffer.append("Property: " + property.getURI() + "<br>");
    }
    return buffer.toString();
  }
  
  public void setSubClasses(RDFEntityList subClasses)
  {
    this.subClasses = subClasses;
  }
  
  public void setSuperClasses(RDFEntityList superClasses)
  {
    this.superClasses = superClasses;
  }
  
  public void setDirectSubclasses(RDFEntityList directSubclasses)
  {
    this.directSubclasses = directSubclasses;
  }
  
  public void setDirectSuperClasses(RDFEntityList directSuperClasses)
  {
    this.directSuperClasses = directSuperClasses;
  }
  
  public void setProperties(RDFEntityList properties)
  {
    this.properties = properties;
  }
  
  public void setEquivalentClasses(RDFEntityList equivalentClasses)
  {
    this.equivalentClasses = equivalentClasses;
  }
  
  public void setDirectProperties(RDFEntityList directProperties)
  {
    this.directProperties = directProperties;
  }
}

