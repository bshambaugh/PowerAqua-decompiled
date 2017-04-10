package poweraqua.indexingService.manager;

import java.io.IOException;
import poweraqua.WordNetJWNL.WNSynsetSetBean;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.powermap.elementPhase.EntityMappingTable;

public abstract interface IndexManager
{
  public static final int STANDARD_SEARCH = 2;
  public static final int FUZZY_SEARCH = 4;
  public static final int SPELL_SEARCH = 8;
  public static final int INDEX_LUCENE = 2;
  public static final int INDEX_WATSON = 4;
  public static final int INDEX_VIRTUOSO = 6;
  
  public abstract void openIndexForCreation(boolean paramBoolean);
  
  public abstract void openIndexForUpload(boolean paramBoolean);
  
  public abstract int getIndexType();
  
  public abstract String getId();
  
  public abstract void closeIndex()
    throws Exception;
  
  public abstract void addRDFEntitiesToOntologyIndex(RDFEntityList paramRDFEntityList)
    throws IOException;
  
  public abstract void addRDFEntitiesToKnowledgeBaseIndex(RDFEntityList paramRDFEntityList)
    throws IOException;
  
  public abstract void addLiteralsToKnowledgeBaseIndex(RDFEntity paramRDFEntity, RDFEntityList paramRDFEntityList)
    throws IOException;
  
  public abstract void addSuperClassesToIndex(RDFEntity paramRDFEntity, RDFEntityList paramRDFEntityList);
  
  public abstract void addSubClassesToIndex(RDFEntity paramRDFEntity, RDFEntityList paramRDFEntityList);
  
  public abstract void addDirectSuperClassesToIndex(RDFEntity paramRDFEntity, RDFEntityList paramRDFEntityList);
  
  public abstract void addDirectSubClassesToIndex(RDFEntity paramRDFEntity, RDFEntityList paramRDFEntityList);
  
  public abstract void addDirectClassesToIndex(RDFEntity paramRDFEntity, RDFEntityList paramRDFEntityList);
  
  public abstract void addEquivalentEntitiesToIndex(RDFEntity paramRDFEntity, RDFEntityList paramRDFEntityList);
  
  public abstract void addWNSynsetsToIndex(RDFEntity paramRDFEntity, WNSynsetSetBean paramWNSynsetSetBean);
  
  public abstract void addOntologyToIndex(String paramString);
  
  public abstract EntityMappingTable searchEntityMappingsOnOntology(String paramString1, String paramString2, float paramFloat, int paramInt)
    throws Exception;
  
  public abstract EntityMappingTable searchEntityMappingsOnKnowledgeBase(String paramString1, String paramString2, float paramFloat, int paramInt)
    throws Exception;
  
  public abstract RDFEntityList searchAllSuperClasses(RDFEntity paramRDFEntity);
  
  public abstract RDFEntityList searchAllSubClasses(RDFEntity paramRDFEntity);
  
  public abstract RDFEntityList searchDirectSuperClasses(RDFEntity paramRDFEntity);
  
  public abstract RDFEntityList searchDirectSubClasses(RDFEntity paramRDFEntity);
  
  public abstract RDFEntityList searchDirectClassOfInstance(RDFEntity paramRDFEntity);
  
  public abstract RDFEntityList searchEquivalentEntities(RDFEntity paramRDFEntity);
  
  public abstract boolean isSynsetIndex();
  
  public abstract WNSynsetSetBean searchSynsets(RDFEntity paramRDFEntity);
}

