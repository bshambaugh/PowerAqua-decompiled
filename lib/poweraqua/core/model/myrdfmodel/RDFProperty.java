package poweraqua.core.model.myrdfmodel;

import java.io.Serializable;

public class RDFProperty
  extends RDFEntity
  implements Serializable
{
  private RDFEntityList domain;
  private RDFEntityList range;
  
  public RDFProperty(String uri, String label, String idPlugin)
  {
    super("property", uri, label, idPlugin);
    this.domain = new RDFEntityList();
    this.range = new RDFEntityList();
  }
  
  public RDFProperty(RDFEntity ent)
  {
    super("property", ent.getURI(), ent.getLabel(), ent.getIdPlugin());
    this.domain = new RDFEntityList();
    this.range = new RDFEntityList();
  }
  
  public RDFEntityList getDomain()
  {
    return this.domain;
  }
  
  public void setDomain(RDFEntityList domain)
  {
    if (domain == null) {
      this.domain = new RDFEntityList();
    } else {
      this.domain = domain;
    }
  }
  
  public void setDomain(RDFEntity domain)
  {
    if (domain == null) {
      this.domain = new RDFEntityList();
    }
    this.domain.addRDFEntity(domain);
  }
  
  public void setRange(RDFEntity range)
  {
    if (range == null) {
      this.range = new RDFEntityList();
    }
    this.range.addRDFEntity(range);
  }
  
  public RDFEntityList getRange()
  {
    return this.range;
  }
  
  public void setRange(RDFEntityList range)
  {
    if (range == null) {
      this.range = new RDFEntityList();
    } else {
      this.range = range;
    }
  }
  
  public void addDomain(RDFEntity domainClass)
  {
    this.domain.addRDFEntity(domainClass);
  }
  
  public void addRange(RDFEntity rangeEntity)
  {
    this.range.addRDFEntity(rangeEntity);
  }
  
  public RDFProperty clone()
  {
    RDFProperty prop = new RDFProperty(getURI(), getLabel(), getIdPlugin());
    prop.setRange(getRange());
    prop.setDomain(getDomain());
    return prop;
  }
}

