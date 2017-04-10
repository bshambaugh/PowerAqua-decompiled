package poweraqua.ranking;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.elementPhase.SemanticComponent;
import poweraqua.powermap.triplePhase.OntoTripleBean;
import poweraqua.powermap.triplePhase.TripleMappingTable;

public class SynsetClusterRanking
{
  private Hashtable<QueryTriple, TripleMappingTable> ontoKBTripleMappings;
  private int maxPopularity = 0;
  private int countSynsetClusters = 0;
  private static int MAX_OntoTriples = 70;
  private Hashtable<Integer, SynsetCluster> ontoTripleSynsetClusterTable;
  private Hashtable<Integer, ArrayList<SynsetCluster>> sortedSynsetClusterTable;
  private String realPath = "";
  private static final double similarity_threshold = 0.45D;
  
  public SynsetClusterRanking(Hashtable<QueryTriple, TripleMappingTable> ontoKBTripleMappings)
  {
    this.ontoKBTripleMappings = ontoKBTripleMappings;
    this.maxPopularity = 0;
    this.ontoTripleSynsetClusterTable = new Hashtable();
    this.countSynsetClusters = 0;
    this.sortedSynsetClusterTable = new Hashtable();
  }
  
  public SynsetClusterRanking(String realPath, Hashtable<QueryTriple, TripleMappingTable> ontoKBTripleMappings)
  {
    this.ontoKBTripleMappings = ontoKBTripleMappings;
    this.realPath = realPath;
    this.maxPopularity = 0;
    this.ontoTripleSynsetClusterTable = new Hashtable();
    this.countSynsetClusters = 0;
    this.sortedSynsetClusterTable = new Hashtable();
  }
  
  public void Rank()
  {
    for (QueryTriple queryTriple : this.ontoKBTripleMappings.keySet()) {
      if (queryTriple.getTypeQuestion() != 1)
      {
        System.out.println("Synset Clustering of the triple mapping table for ");queryTriple.print();
        tmt = (TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple);
        for (String ontology : tmt.getMappingTable().keySet())
        {
          System.out.println("Ontology: " + ontology);
          if (((ArrayList)tmt.getMappingTable().get(ontology)).size() > MAX_OntoTriples) {
            System.out.println("Similarity not calcualted, too many onto-triples: " + ((ArrayList)tmt.getMappingTable().get(ontology)).size());
          } else {
            for (OntoTripleBean otb : (ArrayList)tmt.getMappingTable().get(ontology))
            {
              addSemanticInfo(otb);
              
              ArrayList<SynsetCluster> synsetClusters = new ArrayList();
              for (Iterator i$ = this.ontoTripleSynsetClusterTable.keySet().iterator(); i$.hasNext();)
              {
                int cluster = ((Integer)i$.next()).intValue();
                synsetClusters.add(this.ontoTripleSynsetClusterTable.get(Integer.valueOf(cluster)));
              }
              if (synsetClusters.isEmpty())
              {
                SynsetCluster sc = new SynsetCluster(otb);
                this.countSynsetClusters += 1;
                this.ontoTripleSynsetClusterTable.put(Integer.valueOf(getCountSynsetClusters()), sc);
              }
              boolean belongsToCluster = false;
              ArrayList<ArrayList<OntoTripleBean>> equivalentOTBsList = new ArrayList();
              for (SynsetCluster synsetCluster : synsetClusters)
              {
                ArrayList<OntoTripleBean> clusterOTBs = synsetCluster.getEquivalentOTBs();
                
                boolean equivalent = true;
                ArrayList<OntoTripleBean> newEquivalentOTBs = new ArrayList();
                for (OntoTripleBean clusterOTB : clusterOTBs) {
                  if (isEquivalent(clusterOTB, otb)) {
                    newEquivalentOTBs.add(clusterOTB);
                  } else {
                    equivalent = false;
                  }
                }
                if (equivalent)
                {
                  belongsToCluster = true;
                  synsetCluster.addEquivalentOTB(otb);
                }
                else
                {
                  System.out.println("The ontoTriple is not equivalent with the cluster");
                  newEquivalentOTBs.add(otb);
                  if (!equivalentOTBsList.contains(newEquivalentOTBs)) {
                    equivalentOTBsList.add(newEquivalentOTBs);
                  }
                }
              }
              if (!belongsToCluster) {
                for (ArrayList<OntoTripleBean> newEquivalentOTBs : equivalentOTBsList)
                {
                  SynsetCluster sc = new SynsetCluster(newEquivalentOTBs);
                  this.countSynsetClusters += 1;
                  this.ontoTripleSynsetClusterTable.put(Integer.valueOf(getCountSynsetClusters()), sc);
                }
              }
            }
          }
        }
      }
    }
    TripleMappingTable tmt;
    sortByPopularity();
  }
  
  public void addSemanticInfo(OntoTripleBean otb1)
  {
    SearchSemanticResult subject1 = ((OntoTriple)otb1.getOntoTripleBean().get(0)).getFirstTerm();
    SearchSemanticResult object1 = ((OntoTriple)otb1.getOntoTripleBean().get(otb1.getOntoTripleBean().size() - 1)).getSecondTerm();
    try
    {
      SemanticComponent semanticComponent = new SemanticComponent(this.realPath);
      if ((subject1.getEntity() != null) && (subject1.getEntity().isClass())) {
        semanticComponent.addSemanticInfoClass(subject1);
      } else if (subject1.getEntity().isInstance()) {
        semanticComponent.addSemanticInfoInstance(subject1);
      }
      if (object1.getEntity().isClass()) {
        semanticComponent.addSemanticInfoClass(object1);
      } else if (object1.getEntity().isInstance()) {
        semanticComponent.addSemanticInfoInstance(object1);
      }
      if ((!subject1.isEmptyValidSynsets()) && (!object1.isEmptyValidSynsets())) {
        otb1.setSemantic_interpretation(true);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public boolean isEquivalent(OntoTripleBean otb1, OntoTripleBean otb2)
  {
    try
    {
      SearchSemanticResult subject1 = ((OntoTriple)otb1.getOntoTripleBean().get(0)).getFirstTerm();
      SearchSemanticResult subject2 = ((OntoTriple)otb2.getOntoTripleBean().get(0)).getFirstTerm();
      SearchSemanticResult object1 = ((OntoTriple)otb1.getOntoTripleBean().get(otb1.getOntoTripleBean().size() - 1)).getSecondTerm();
      SearchSemanticResult object2 = ((OntoTriple)otb2.getOntoTripleBean().get(otb2.getOntoTripleBean().size() - 1)).getSecondTerm();
      
      String subject_otb1 = subject1.getEmt_keyword();
      String subject_otb2 = subject2.getEmt_keyword();
      String object_otb1 = object1.getEmt_keyword();
      String object_otb2 = object2.getEmt_keyword();
      
      SemanticComponent semanticComponent = new SemanticComponent(this.realPath);
      if (((subject_otb1.equals(subject_otb2)) && (object_otb1.equals(object_otb2))) || ((subject_otb1.equals(object_otb2)) && (object_otb1.equals(subject_otb2))))
      {
        if (!semanticComponent.isEquivalentSynsets(subject1, subject2, 0.45D)) {
          return false;
        }
        if (semanticComponent.isEquivalentSynsets(object1, object2, 0.45D)) {
          return true;
        }
        return false;
      }
      if (subject_otb1.equals(subject_otb2))
      {
        if (semanticComponent.isEquivalentSynsets(subject1, subject2, 0.45D)) {
          return true;
        }
        return false;
      }
      if (object_otb1.equals(subject_otb2))
      {
        if (semanticComponent.isEquivalentSynsets(object1, subject2, 0.45D)) {
          return true;
        }
        return false;
      }
      if (object_otb2.equals(subject_otb1))
      {
        if (semanticComponent.isEquivalentSynsets(object2, subject1, 0.45D)) {
          return true;
        }
        return false;
      }
      return false;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      
      System.out.println("?????");
    }
    return false;
  }
  
  public void sortByPopularity()
  {
    for (Iterator i$ = this.ontoTripleSynsetClusterTable.keySet().iterator(); i$.hasNext();)
    {
      int cluster = ((Integer)i$.next()).intValue();
      SynsetCluster sc = (SynsetCluster)this.ontoTripleSynsetClusterTable.get(Integer.valueOf(cluster));
      int popularity = sc.getPopularity().intValue();
      if (popularity > getMaxPopularity()) {
        this.maxPopularity = popularity;
      }
      if (getSortedSynsetClusterTable().containsKey(Integer.valueOf(popularity)))
      {
        ArrayList<SynsetCluster> scList = (ArrayList)getSortedSynsetClusterTable().get(Integer.valueOf(popularity));
        scList.add(sc);
        getSortedSynsetClusterTable().put(Integer.valueOf(popularity), scList);
      }
      else
      {
        ArrayList<SynsetCluster> scList = new ArrayList();
        scList.add(sc);
        getSortedSynsetClusterTable().put(Integer.valueOf(popularity), scList);
      }
    }
  }
  
  public Integer getSynsetPopularity(ArrayList<OntoTripleBean> otbs)
  {
    for (Iterator i$ = this.sortedSynsetClusterTable.keySet().iterator(); i$.hasNext();)
    {
      pop = (Integer)i$.next();
      ArrayList<SynsetCluster> synsetClusters = (ArrayList)this.sortedSynsetClusterTable.get(pop);
      for (SynsetCluster sc : synsetClusters) {
        if (sc.isContained(otbs)) {
          return pop;
        }
      }
    }
    Integer pop;
    return Integer.valueOf(-1);
  }
  
  public Hashtable<Integer, ArrayList<SynsetCluster>> getSortedSynsetClusterTable()
  {
    return this.sortedSynsetClusterTable;
  }
  
  public int getCountSynsetClusters()
  {
    return this.countSynsetClusters;
  }
  
  public int getMaxPopularity()
  {
    return this.maxPopularity;
  }
}

