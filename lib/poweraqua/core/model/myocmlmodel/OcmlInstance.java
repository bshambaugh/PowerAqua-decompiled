package poweraqua.core.model.myocmlmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;

public class OcmlInstance
  implements Serializable
{
  private RDFEntity instance;
  private RDFEntityList directSuperClasses;
  private RDFEntityList superClasses;
  private RDFEntityList equivalentInstances;
  private Hashtable<RDFEntity, RDFEntityList> properties;
  
  public OcmlInstance(RDFEntity entity)
  {
    setInstance(entity);
    this.directSuperClasses = new RDFEntityList();
    this.superClasses = new RDFEntityList();
    setProperties(new Hashtable());
  }
  
  public RDFEntity getInstance()
  {
    return this.instance;
  }
  
  public void setInstance(RDFEntity instance)
  {
    this.instance = instance;
  }
  
  public RDFEntityList listEquivalentInstances()
  {
    return this.equivalentInstances;
  }
  
  public RDFEntityList getSuperClasses()
  {
    return this.superClasses;
  }
  
  public void addSuperClasses(RDFEntityList superClasses)
  {
    this.superClasses.addNewRDFEntities(superClasses);
  }
  
  public RDFEntityList getDirectSuperClasses()
  {
    return this.directSuperClasses;
  }
  
  public void addDirectSuperClasses(RDFEntityList directSuperClasses)
  {
    this.directSuperClasses.addNewRDFEntities(directSuperClasses);
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getProperties()
  {
    return this.properties;
  }
  
  public RDFEntityList getPropertiesWithValue(RDFEntity value)
  {
    RDFEntityList results = new RDFEntityList();
    for (RDFEntity prop : this.properties.keySet())
    {
      RDFEntityList values = (RDFEntityList)this.properties.get(prop);
      if (values.isRDFEntityContained(value.getURI())) {
        results.addRDFEntity(prop);
      }
    }
    return results;
  }
  
  public RDFEntityList getPropertiesWithValue(String value)
  {
    RDFEntityList results = new RDFEntityList();
    for (Iterator i$ = this.properties.keySet().iterator(); i$.hasNext();)
    {
      prop = (RDFEntity)i$.next();
      RDFEntityList values = (RDFEntityList)this.properties.get(prop);
      ArrayList<String> labels = values.getLabels();
      for (String label : labels) {
        if (label.equals(value))
        {
          results.addRDFEntity(prop);
        }
        else
        {
          label = label.replaceAll("\"", "");
          if (label.equals(value)) {
            results.addRDFEntity(prop);
          }
        }
      }
    }
    RDFEntity prop;
    return results;
  }
  
  public void setProperties(Hashtable<RDFEntity, RDFEntityList> properties)
  {
    this.properties = properties;
  }
  
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("URI: " + getInstance().getURI() + "\n" + "Properties: ");
    for (Iterator i$ = this.properties.keySet().iterator(); i$.hasNext();)
    {
      property = (RDFEntity)i$.next();
      for (RDFEntity value : ((RDFEntityList)this.properties.get(property)).getAllRDFEntities()) {
        buffer.append("Property: " + property.getURI() + " " + value.getLabel() + " " + value.getLocalName() + "\n");
      }
    }
    RDFEntity property;
    return buffer.toString();
  }
  
  public String toHTML()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("URI: " + getInstance().getURI() + "<br>" + "Properties: ");
    for (Iterator i$ = this.properties.keySet().iterator(); i$.hasNext();)
    {
      property = (RDFEntity)i$.next();
      for (RDFEntity value : ((RDFEntityList)this.properties.get(property)).getAllRDFEntities()) {
        buffer.append("Property: " + property.getURI() + " " + value.getLocalName() + " (" + value.getLabel() + ") <br>");
      }
    }
    RDFEntity property;
    return buffer.toString();
  }
  
  public void setEquivalentInstances(RDFEntityList equivalentInstances)
  {
    this.equivalentInstances = equivalentInstances;
  }
}

