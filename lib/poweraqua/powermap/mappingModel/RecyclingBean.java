package poweraqua.powermap.mappingModel;

import java.util.ArrayList;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraqua.powermap.elementPhase.SearchSemanticResult;

public class RecyclingBean
{
  private EntityMappingTable taxonomyRecyclingBean;
  private EntityMappingTable stringRecyclingBean;
  private EntityMappingTable synsetRecyclingBean;
  
  public RecyclingBean(String keyword)
  {
    this.taxonomyRecyclingBean = new EntityMappingTable(keyword);
    this.stringRecyclingBean = new EntityMappingTable(keyword);
    this.synsetRecyclingBean = new EntityMappingTable(keyword);
  }
  
  public void addTaxonomyRecyclingMapping(EntityMappingTable ent)
  {
    this.taxonomyRecyclingBean.merge(ent);
  }
  
  public void addStringRecyclingMapping(EntityMappingTable ent)
  {
    this.stringRecyclingBean.merge(ent);
  }
  
  public void addStringRecyclingMapping(ArrayList<SearchSemanticResult> SSRs)
  {
    this.stringRecyclingBean.addMappingList(SSRs);
  }
  
  public void addSynsetRecyclingMapping(EntityMappingTable ent)
  {
    this.synsetRecyclingBean.merge(ent);
  }
  
  public EntityMappingTable getTaxonomyRecyclingBean()
  {
    return this.taxonomyRecyclingBean;
  }
  
  public EntityMappingTable getStringRecyclingBean()
  {
    return this.stringRecyclingBean;
  }
  
  public EntityMappingTable getSynsetRecyclingBean()
  {
    return this.synsetRecyclingBean;
  }
}

