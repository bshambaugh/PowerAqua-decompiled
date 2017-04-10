package poweraqua.core.model.myrdfmodel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;

public class RDFEntityList
  implements Serializable
{
  private ArrayList<RDFEntity> entities;
  private boolean affirmativeNegative = false;
  
  public RDFEntityList()
  {
    this.entities = new ArrayList();
  }
  
  public RDFEntityList(int size)
  {
    this.entities = new ArrayList(size);
  }
  
  public boolean isEmpty()
  {
    return this.entities.isEmpty();
  }
  
  public int size()
  {
    return this.entities.size();
  }
  
  public void addRDFEntity(RDFEntity e)
  {
    if (!this.entities.contains(e)) {
      this.entities.add(e);
    }
  }
  
  public void addAllRDFEntity(RDFEntityList list)
  {
    for (RDFEntity ent : list.getAllRDFEntities()) {
      this.entities.add(ent);
    }
  }
  
  public void addRDFEntity(RDFProperty e)
  {
    if (!this.entities.contains(e)) {
      this.entities.add(e);
    }
  }
  
  public void addRefersToInstance(RDFEntity refers_to_instance)
  {
    for (RDFEntity ent : getAllRDFEntities()) {
      ent.setRefers_to(refers_to_instance);
    }
  }
  
  public void removeRDFEntity(RDFEntity e)
  {
    this.entities.remove(e);
  }
  
  public void removeRDFEntity(String ent_uri)
  {
    for (int i = 0; i < this.entities.size(); i++)
    {
      RDFEntity e = (RDFEntity)this.entities.get(i);
      if (e.getURI().equals(ent_uri))
      {
        this.entities.remove(e);
        return;
      }
    }
  }
  
  public void removeRDFEntity(int i)
  {
    if ((i >= 0) && (i < size())) {
      this.entities.remove(i);
    }
  }
  
  public void addRDFEntities(RDFEntityList entities)
  {
    for (RDFEntity entity : entities.getAllRDFEntities()) {
      addRDFEntity(entity);
    }
  }
  
  public void addNewRDFEntities(RDFEntityList entities)
  {
    for (RDFEntity entity : entities.getAllRDFEntities()) {
      addRDFEntity(entity);
    }
  }
  
  public RDFEntityList cloneEntityList()
  {
    RDFEntityList newClone = new RDFEntityList();
    for (RDFEntity entity : this.entities) {
      newClone.addRDFEntity(entity.clone());
    }
    return newClone;
  }
  
  public RDFEntityList clonePropertyList()
  {
    RDFEntityList newClone = new RDFEntityList();
    for (RDFEntity entity : this.entities) {
      newClone.addRDFEntity((RDFProperty)entity.clone());
    }
    return newClone;
  }
  
  public ArrayList<String> getUris()
  {
    if (this.entities.isEmpty()) {
      return new ArrayList();
    }
    ArrayList<String> uris = new ArrayList(size());
    for (RDFEntity entity : this.entities) {
      uris.add(entity.getURI());
    }
    return uris;
  }
  
  public ArrayList<String> getLabels()
  {
    if (this.entities.isEmpty()) {
      return new ArrayList();
    }
    ArrayList<String> labels = new ArrayList(size());
    for (RDFEntity entity : this.entities) {
      labels.add(entity.getLabel());
    }
    return labels;
  }
  
  public ArrayList<RDFEntity> getAllRDFEntities()
  {
    return this.entities;
  }
  
  public void setPlugingID(String pluginID)
  {
    for (RDFEntity entity : this.entities) {
      entity.setIdPlugin(pluginID);
    }
  }
  
  public String toString()
  {
    StringBuffer buffer = new StringBuffer("EntityList: \n");
    for (RDFEntity entity : this.entities)
    {
      buffer.append(entity.toString());
      buffer.append("\n");
    }
    return buffer.toString();
  }
  
  public boolean isExactRDFEntityContained(RDFEntity rdfEntity)
  {
    return this.entities.contains(rdfEntity);
  }
  
  public boolean isRDFEntityContained(String rdfEntity_uri)
  {
    for (RDFEntity entity : this.entities) {
      if (entity.getURI().equals(rdfEntity_uri)) {
        return true;
      }
    }
    return false;
  }
  
  public static void main(String[] args)
  {
    RDFEntityList list = new RDFEntityList();
    RDFEntity et1 = new RDFProperty("http:\\peipe", "peipe", "idiP");
    RDFEntity et2 = new RDFEntity("literal", "http:\\pepe", "pepe", "idP");
    
    RDFProperty pp = new RDFProperty("http:\\pepe", "pepe", "idP");
    
    list.addRDFEntity(et1);
    list.addRDFEntity(pp);
    System.out.println(list);
    
    RDFEntityList list2 = new RDFEntityList();
    list2.addNewRDFEntities(list);
    for (RDFEntity propertyEnt : list2.getAllRDFEntities())
    {
      RDFProperty property = (RDFProperty)propertyEnt;
      System.out.println(property);
    }
  }
  
  public boolean isAffirmativeNegative()
  {
    return this.affirmativeNegative;
  }
  
  public void setAffirmativeNegative(boolean affirmativeNegative)
  {
    this.affirmativeNegative = affirmativeNegative;
  }
}

