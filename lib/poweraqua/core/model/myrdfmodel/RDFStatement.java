package poweraqua.core.model.myrdfmodel;

public class RDFStatement
{
  private RDFEntity subject;
  private RDFProperty property;
  private RDFEntity object;
  
  public RDFStatement()
  {
    this.subject = null;
    this.object = null;
    this.property = null;
  }
  
  public RDFStatement(RDFEntity subject, RDFProperty property, RDFEntity object)
  {
    this.subject = subject;
    this.object = object;
    this.property = property;
  }
  
  public RDFEntity getSubject()
  {
    return this.subject;
  }
  
  public void setSubject(RDFEntity subject)
  {
    this.subject = subject;
  }
  
  public RDFProperty getProperty()
  {
    return this.property;
  }
  
  public void setProperty(RDFProperty property)
  {
    this.property = property;
  }
  
  public RDFEntity getObject()
  {
    return this.object;
  }
  
  public void setObject(RDFEntity object)
  {
    this.object = object;
  }
}

