package poweraqua.core.model.myocmlmodel;

import java.io.Serializable;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;

public class OcmlProperty
  implements Serializable
{
  private RDFEntity property;
  private RDFEntityList domain;
  private RDFEntityList range;
  private RDFEntityList directSubProperties;
  private RDFEntityList directSuperProperties;
  private RDFEntityList subProperties;
  private RDFEntityList superProperties;
  private RDFEntityList equivalentProperties;
  
  public OcmlProperty(RDFEntity property)
  {
    this.property = property;
    setSubProperties(new RDFEntityList());
    setSuperProperties(new RDFEntityList());
    setDirectSubProperties(new RDFEntityList());
    setDirectSuperProperties(new RDFEntityList());
    setEquivalentProperties(new RDFEntityList());
  }
  
  public String getPropertyName()
  {
    return this.property.getLocalName();
  }
  
  public String getPrettyName()
  {
    return this.property.getLabel();
  }
  
  public RDFEntityList listDomain()
  {
    return this.domain;
  }
  
  public RDFEntityList listEquivalentProperties()
  {
    return this.equivalentProperties;
  }
  
  public RDFEntityList listRange()
  {
    return this.range;
  }
  
  public RDFEntityList listAllSubProperties()
  {
    return this.subProperties;
  }
  
  public RDFEntityList listDirectSubProperties()
  {
    return this.directSubProperties;
  }
  
  public RDFEntityList listAllSuperProperties()
  {
    return this.superProperties;
  }
  
  public RDFEntityList listDirectSuperProperties()
  {
    return this.directSuperProperties;
  }
  
  public void setDomain(RDFEntityList domain)
  {
    this.domain = domain;
  }
  
  public void setRange(RDFEntityList range)
  {
    this.range = range;
  }
  
  public void setDirectSubProperties(RDFEntityList directSubProperties)
  {
    this.directSubProperties = directSubProperties;
  }
  
  public void setDirectSuperProperties(RDFEntityList directSuperProperties)
  {
    this.directSuperProperties = directSuperProperties;
  }
  
  public void setSubProperties(RDFEntityList subProperties)
  {
    this.subProperties = subProperties;
  }
  
  public void setSuperProperties(RDFEntityList superProperties)
  {
    this.superProperties = superProperties;
  }
  
  public void setEquivalentProperties(RDFEntityList equivalentProperties)
  {
    this.equivalentProperties = equivalentProperties;
  }
}

