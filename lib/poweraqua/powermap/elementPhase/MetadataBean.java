package poweraqua.powermap.elementPhase;

import java.util.ArrayList;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.indexingService.manager.IndexManager;
import poweraqua.powermap.mappingModel.MappingSession;

public class MetadataBean
{
  private RDFEntityList superclasses;
  private RDFEntityList directSuperclasses;
  private RDFEntityList directClasses;
  private RDFEntityList directSubclasses;
  private RDFEntityList subclasses;
  private RDFEntityList equivalentEntity;
  private IndexManager indexManager;
  private RDFEntity entity;
  
  public MetadataBean()
  {
    this.superclasses = new RDFEntityList();
    this.subclasses = new RDFEntityList();
    this.directSuperclasses = new RDFEntityList();
    this.directSubclasses = new RDFEntityList();
    this.equivalentEntity = new RDFEntityList();
    this.directClasses = new RDFEntityList();
  }
  
  public MetadataBean(IndexManager indexManager, RDFEntity entity)
    throws Exception
  {
    this.superclasses = null;
    this.subclasses = null;
    this.directSuperclasses = null;
    this.directSubclasses = null;
    this.equivalentEntity = null;
    this.directClasses = null;
    if (indexManager == null) {
      throw new Exception("Initialize Index Manager !!! ");
    }
    this.indexManager = indexManager;
    this.entity = entity;
  }
  
  public void setSuperclasses(RDFEntityList superclasses)
  {
    this.superclasses = superclasses;
  }
  
  public void addSuperclasses(RDFEntityList superclasses)
  {
    if (this.superclasses == null) {
      this.superclasses = new RDFEntityList();
    }
    this.superclasses.addNewRDFEntities(superclasses);
  }
  
  public void addDirectSuperclasses(RDFEntityList superclasses)
  {
    if (this.directSuperclasses == null) {
      this.directSubclasses = new RDFEntityList();
    }
    this.directSuperclasses.addNewRDFEntities(superclasses);
  }
  
  protected RDFEntityList getDirectSuperclasses()
  {
    if (this.directSuperclasses == null) {
      setDirectSuperclasses(this.indexManager.searchDirectSuperClasses(this.entity));
    }
    return this.directSuperclasses;
  }
  
  public void setDirectSuperclasses(RDFEntityList directSuperclasses)
  {
    this.directSuperclasses = directSuperclasses;
  }
  
  protected RDFEntityList getDirectSubclasses()
  {
    if (this.directSubclasses == null)
    {
      MappingSession.getLog_poweraqua().log(Level.INFO, "adding direct subclasses metadata for class " + this.entity.getURI());
      setDirectSubclasses(this.indexManager.searchDirectSubClasses(this.entity));
    }
    return this.directSubclasses;
  }
  
  public void setDirectSubclasses(RDFEntityList directSubclasses)
  {
    this.directSubclasses = directSubclasses;
  }
  
  protected RDFEntityList getSubclasses()
  {
    if (this.subclasses == null) {
      if (!getDirectSubclasses().isEmpty())
      {
        RDFEntityList res = this.indexManager.searchAllSubClasses(this.entity);
        if (res.isEmpty()) {
          setSubclasses(getDirectSubclasses());
        } else {
          setSubclasses(res);
        }
      }
      else
      {
        this.subclasses = new RDFEntityList();
      }
    }
    return this.subclasses;
  }
  
  public void setSubclasses(RDFEntityList subclasses)
  {
    this.subclasses = subclasses;
  }
  
  protected RDFEntityList getSuperclasses()
  {
    if (this.superclasses == null) {
      if (!getDirectSuperclasses().isEmpty()) {
        setSuperclasses(this.indexManager.searchAllSuperClasses(this.entity));
      } else {
        this.superclasses = new RDFEntityList();
      }
    }
    return this.superclasses;
  }
  
  public RDFEntityList getEquivalentEntity()
  {
    if (this.equivalentEntity == null) {
      this.equivalentEntity = this.indexManager.searchEquivalentEntities(this.entity);
    }
    return this.equivalentEntity;
  }
  
  public void setEquivalentEntity(RDFEntityList equivalentEntity)
  {
    for (RDFEntity eq : equivalentEntity.getAllRDFEntities()) {
      if (!eq.getURI().startsWith("node")) {
        this.equivalentEntity.addRDFEntity(eq);
      }
    }
  }
  
  protected RDFEntityList getDirectClasses()
  {
    if (this.directClasses == null)
    {
      setDirectClasses(this.indexManager.searchDirectClassOfInstance(this.entity));
      for (RDFEntity entityParent : getDirectClasses().getAllRDFEntities())
      {
        addDirectSuperclasses(this.indexManager.searchDirectSuperClasses(entityParent));
        if ((!getDirectSuperclasses().isEmpty()) && (!getDirectClasses().getUris().contains(getDirectSuperclasses().getUris().get(0)))) {
          addSuperclasses(this.indexManager.searchAllSuperClasses(entityParent));
        } else {
          this.superclasses = new RDFEntityList();
        }
      }
    }
    return this.directClasses;
  }
  
  public void setDirectClasses(RDFEntityList directClasses)
  {
    this.directClasses = directClasses;
  }
}

