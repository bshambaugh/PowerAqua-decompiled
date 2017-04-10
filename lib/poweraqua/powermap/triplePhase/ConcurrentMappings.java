package poweraqua.powermap.triplePhase;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.utils.StringUtils;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.elementPhase.SemanticComponent;
import poweraqua.powermap.elementPhase.SyntacticComponent;
import poweraqua.powermap.mappingModel.MappingBean;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.powermap.mappingModel.RecyclingBean;
import poweraqua.powermap.mappingModel.WordNetBean;

public class ConcurrentMappings
  implements Runnable
{
  String queryTerm;
  boolean find_hypernyms;
  boolean is_queryTerm;
  ArrayList<String> restrictedKeywords;
  private MappingSession mapSession;
  private Hashtable<String, ArrayList<MappingBean>> ontoCompoundMappings;
  private Hashtable<String, MappingBean> ontoTermMappings;
  
  public ConcurrentMappings(String queryTerm, boolean find_hypernyms, boolean is_queryTerm, ArrayList<String> restrictedKeywords, MappingSession mapSession, Hashtable<String, ArrayList<MappingBean>> ontoCompoundMappings, Hashtable<String, MappingBean> ontoTermMappings)
  {
    this.queryTerm = queryTerm;
    this.find_hypernyms = find_hypernyms;
    this.is_queryTerm = is_queryTerm;
    this.restrictedKeywords = restrictedKeywords;
    this.mapSession = mapSession;
    this.ontoCompoundMappings = ontoCompoundMappings;
    this.ontoTermMappings = ontoTermMappings;
  }
  
  public void run()
  {
    try
    {
      System.out.println("Thread for " + this.queryTerm);
      if ((this.queryTerm != null) && (!this.queryTerm.equals("")) && (!this.ontoCompoundMappings.containsKey(this.queryTerm)))
      {
        ArrayList<MappingBean> mappingBeans = new ArrayList();
        boolean isCompound = StringUtils.isCompound(this.queryTerm);
        MappingBean mappingBean;
        MappingBean mappingBean;
        if (this.restrictedKeywords == null) {
          mappingBean = new MappingBean(this.queryTerm, getMapSession().getRealpath(), this.is_queryTerm);
        } else {
          mappingBean = new MappingBean(this.queryTerm, this.restrictedKeywords, getMapSession().getRealpath(), this.is_queryTerm);
        }
        if (!this.find_hypernyms) {
          mappingBean.setFind_hypernyms(false);
        } else if (mappingBean.getWordNetBean().getPOS() != 1) {
          mappingBean.setFind_hypernyms(false);
        }
        mappingBeans.add(mappingBean);
        
        SyntacticComponent map = new SyntacticComponent(getMapSession(), mappingBeans);
        
        map.match();
        
        map.matchOntologyBackground();
        if ((map.isEmptyMapping()) && (isCompound))
        {
          ArrayList<MappingBean> mappingBeanSemantics = splitQueryTermCompound(this.queryTerm, this.restrictedKeywords, this.is_queryTerm);
          getOntoCompoundMappings().put(this.queryTerm, mappingBeanSemantics);
        }
        else
        {
          SemanticComponent mapSemantic = new SemanticComponent(getMapSession().getRealpath(), map.getMappingBeans());
          mapSemantic.addSemanticInfo();
          mapSemantic.closeOpenFileDescriptors();
          getOntoCompoundMappings().put(this.queryTerm, mapSemantic.getMappingBeans());
        }
      }
    }
    catch (Exception e)
    {
      System.out.println("");e.printStackTrace();
    }
  }
  
  private ArrayList<MappingBean> splitQueryTermCompound(String termC, ArrayList<String> restrictedKeywords, boolean is_queryTerm)
    throws Exception
  {
    ArrayList<MappingBean> mappingBeans = new ArrayList();
    
    ArrayList<String> compoundTerms = splitCompound(termC);
    
    boolean secondIteration = false;
    if (getOntoTermMappings().get(termC) != null) {
      secondIteration = ((MappingBean)getOntoTermMappings().get(termC)).getEntityMappingTable().isIs_ISACompound();
    }
    if (compoundTerms.size() == 2)
    {
      MappingBean bean_compound = (MappingBean)getOntoTermMappings().get(compoundTerms.get(0));
      if (bean_compound == null)
      {
        SyntacticComponent sc_tmp = new SyntacticComponent(getMapSession());
        if (!restrictedKeywords.isEmpty())
        {
          restrictedKeywords.add(compoundTerms.get(1));
          restrictedKeywords.remove(compoundTerms.get(0));
        }
        bean_compound = sc_tmp.matchSplittedKeyword(new MappingBean((String)compoundTerms.get(0), restrictedKeywords, getMapSession().getRealpath(), is_queryTerm));
        SemanticComponent semantic_main = new SemanticComponent(getMapSession().getRealpath(), bean_compound);
        semantic_main.addSemanticInfo();
        bean_compound.getEntityMappingTable().setExactMappingsToFalse();
        getOntoTermMappings().put(compoundTerms.get(0), bean_compound);
        semantic_main.closeOpenFileDescriptors();
      }
      MappingBean bean_main = (MappingBean)getOntoTermMappings().get(compoundTerms.get(1));
      if (bean_main == null)
      {
        SyntacticComponent sc_tmp = new SyntacticComponent(getMapSession());
        if (!restrictedKeywords.isEmpty())
        {
          restrictedKeywords.add(compoundTerms.get(0));
          restrictedKeywords.remove(compoundTerms.get(1));
        }
        bean_main = sc_tmp.matchSplittedKeyword(new MappingBean((String)compoundTerms.get(1), restrictedKeywords, getMapSession().getRealpath(), is_queryTerm));
        SemanticComponent semantic_main = new SemanticComponent(getMapSession().getRealpath(), bean_main);
        semantic_main.addSemanticInfo();
        bean_main.getEntityMappingTable().setExactMappingsToFalse();
        getOntoTermMappings().put(compoundTerms.get(1), bean_main);
        semantic_main.closeOpenFileDescriptors();
      }
      if (!secondIteration)
      {
        EntityMappingTable entTable_compound = bean_compound.getEntityMappingTable();
        EntityMappingTable entTable_parents = bean_main.getEntityMappingTable();
        if ((!entTable_compound.isEmpty()) || (!entTable_parents.isEmpty()))
        {
          MappingBean mapISA = new MappingBean(termC, getMapSession().getRealpath(), is_queryTerm);
          ArrayList<String> ontologies_parent = entTable_parents.getOntologyIDMappings();
          for (Iterator i$ = entTable_compound.getOntologyIDMappings().iterator(); i$.hasNext();)
          {
            onto_comp = (String)i$.next();
            if (ontologies_parent.contains(onto_comp))
            {
              ArrayList<SearchSemanticResult> RSS_parents = entTable_parents.getOntologyMappings(onto_comp);
              for (SearchSemanticResult RSS_comp : entTable_compound.getOntologyMappings(onto_comp))
              {
                RDFEntity ent_comp = RSS_comp.getEntity();
                
                RDFEntityList parents = RSS_comp.getDirectParents();
                parents.addAllRDFEntity(RSS_comp.getSuperclasses());
                boolean son_father = false;
                for (SearchSemanticResult RSS_parent : entTable_parents.getOntologyMappings(onto_comp)) {
                  if (parents.isRDFEntityContained(RSS_parent.getEntity().getURI()))
                  {
                    son_father = true;
                    break;
                  }
                }
                if (son_father)
                {
                  mapISA.getEntityMappingTable().addMapping(RSS_comp);
                  if (!RSS_comp.isExact()) {
                    break;
                  }
                  ArrayList<SearchSemanticResult> eliminatedString = mapISA.getEntityMappingTable().filterExactMappings(RSS_comp.getEntity().getIdPlugin());
                  if (!eliminatedString.isEmpty()) {
                    mapISA.getRecyclingBean().addStringRecyclingMapping(eliminatedString);
                  }
                  break;
                }
              }
            }
          }
          String onto_comp;
          if (!mapISA.getEntityMappingTable().isEmpty())
          {
            mapISA.getEntityMappingTable().setIs_ISACompound(true);
            
            getOntoTermMappings().put(termC, mapISA);
            mappingBeans.add(mapISA);
            return mappingBeans;
          }
        }
      }
      mappingBeans.add(bean_compound);
      mappingBeans.add(bean_main);
    }
    else
    {
      mappingBeans = new ArrayList();
      int i = 0;
      SyntacticComponent sc_tmp = new SyntacticComponent(getMapSession());
      while (i < compoundTerms.size())
      {
        String compound = (String)compoundTerms.get(i);
        if (i == compoundTerms.size() - 1)
        {
          MappingBean mappingBean = sc_tmp.matchSplittedKeyword(new MappingBean(compound, getMapSession().getRealpath(), is_queryTerm));
          SemanticComponent semantic_main = new SemanticComponent(getMapSession().getRealpath(), mappingBean);
          semantic_main.addSemanticInfo();
          mappingBean.getEntityMappingTable().setExactMappingsToFalse();
          getOntoTermMappings().put(compound, mappingBean);
          semantic_main.closeOpenFileDescriptors();
          mappingBeans.add(mappingBean);
        }
        else
        {
          String nextCompound = (String)compoundTerms.get(i + 1);
          String mergeCompound = compound + " " + nextCompound;
          MappingBean mappingBean = sc_tmp.matchSplittedKeyword(new MappingBean(mergeCompound, getMapSession().getRealpath(), is_queryTerm));
          if (!mappingBean.getEntityMappingTable().isEmpty())
          {
            SemanticComponent semantic_main = new SemanticComponent(getMapSession().getRealpath(), mappingBean);
            semantic_main.addSemanticInfo();
            mappingBean.getEntityMappingTable().setExactMappingsToFalse();
            getOntoTermMappings().put(mergeCompound, mappingBean);
            semantic_main.closeOpenFileDescriptors();
            mappingBeans.add(mappingBean);
            i++;
          }
          else
          {
            mappingBean = sc_tmp.matchSplittedKeyword(new MappingBean(compound, getMapSession().getRealpath(), is_queryTerm));
            SemanticComponent semantic_main = new SemanticComponent(getMapSession().getRealpath(), mappingBean);
            semantic_main.addSemanticInfo();
            mappingBean.getEntityMappingTable().setExactMappingsToFalse();
            getOntoTermMappings().put(compound, mappingBean);
            semantic_main.closeOpenFileDescriptors();
            mappingBeans.add(mappingBean);
          }
        }
        i++;
      }
    }
    return mappingBeans;
  }
  
  public ArrayList<String> splitCompound(String queryTerm)
  {
    ArrayList<String> compounds = new ArrayList();
    String[] elements = queryTerm.split(" ");
    if (elements.length <= 1)
    {
      elements = queryTerm.split("-");
      if (elements.length <= 1) {
        elements = queryTerm.split("_");
      }
    }
    for (int i = 0; i < elements.length; i++) {
      compounds.add(elements[i].trim());
    }
    return compounds;
  }
  
  public MappingSession getMapSession()
  {
    return this.mapSession;
  }
  
  public Hashtable<String, ArrayList<MappingBean>> getOntoCompoundMappings()
  {
    return this.ontoCompoundMappings;
  }
  
  public Hashtable<String, MappingBean> getOntoTermMappings()
  {
    return this.ontoTermMappings;
  }
}

