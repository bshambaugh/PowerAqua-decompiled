package poweraqua.WordNetJWNL;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTree;
import net.sf.extjwnl.data.relationship.AsymmetricRelationship;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipList;

public class WNSynsetSimilarity
{
  private String source_lemma = "";
  private String target_lemma = "";
  private Hashtable<Relationship, SimilaritySem> relationshipList;
  private ArrayList<Relationship> bestRelationships;
  private static Float thresh = new Float(0.58D);
  private ArrayList<Relationship> overThresholdList;
  
  public class SimilaritySem
  {
    private double similarity;
    private boolean gloss_similarity;
    
    public SimilaritySem(double similarity, boolean gloss_similarity)
    {
      this.similarity = similarity;
      this.gloss_similarity = gloss_similarity;
    }
    
    public double getSimilarity()
    {
      return this.similarity;
    }
    
    public boolean isGloss_similarity()
    {
      return this.gloss_similarity;
    }
  }
  
  public WNSynsetSimilarity()
    throws Exception
  {
    this.relationshipList = new Hashtable();
    this.bestRelationships = new ArrayList();
  }
  
  public WNSynsetSimilarity(String source_lemma, String target_lemma)
    throws Exception
  {
    this.relationshipList = new Hashtable();
    this.bestRelationships = new ArrayList();
    this.source_lemma = source_lemma;
    this.target_lemma = target_lemma;
  }
  
  public void addRelationShipList(RelationshipList relationshipList)
    throws Exception
  {
    for (int i = 0; i < relationshipList.size(); i++)
    {
      Relationship relationship = (Relationship)relationshipList.get(i);
      SimilaritySem similarity = calculateSimilarity(relationship);
      if (similarity.getSimilarity() > 0.22D) {
        getRelationshipList().put(relationship, similarity);
      }
    }
  }
  
  public void calculateBestSimilarity()
    throws Exception
  {
    SimilaritySem sim_previous = new SimilaritySem(0.0D, false);
    boolean gloss_previous = false;
    ArrayList<Relationship> previous = new ArrayList();
    
    this.overThresholdList = new ArrayList();
    for (Relationship relationship : getRelationshipList().keySet())
    {
      SimilaritySem sim_new = (SimilaritySem)getRelationshipList().get(relationship);
      if (sim_new.getSimilarity() > thresh.floatValue()) {
        this.overThresholdList.add(relationship);
      }
      if (sim_new.getSimilarity() > sim_previous.getSimilarity())
      {
        sim_previous = sim_new;
        gloss_previous = sim_new.isGloss_similarity();
        this.bestRelationships = new ArrayList();
        getBestRelationships().removeAll(previous);
        getBestRelationships().add(relationship);
        previous = new ArrayList();
        previous.add(relationship);
      }
      else if (sim_new.getSimilarity() == sim_previous.getSimilarity())
      {
        boolean gloss_new = sim_new.isGloss_similarity();
        if (!gloss_previous)
        {
          if (!gloss_new)
          {
            getBestRelationships().add(relationship);
            previous.add(relationship);
          }
          else
          {
            this.bestRelationships = new ArrayList();
            getBestRelationships().removeAll(previous);
            getBestRelationships().add(relationship);
            previous = new ArrayList();
            previous.add(relationship);
            gloss_previous = true;
            sim_previous = sim_new;
          }
        }
        else if (gloss_new)
        {
          getBestRelationships().add(relationship);
          previous.add(relationship);
        }
      }
    }
    for (Relationship overthresh : this.overThresholdList) {
      if (!getBestRelationships().contains(overthresh)) {
        getBestRelationships().add(overthresh);
      }
    }
  }
  
  public SimilaritySem calculateSimilarity(Relationship relationship)
    throws Exception
  {
    double depth = relationship.getDepth();
    
    double num_common_subsummers = getMaxNumberCommonSubsumers(relationship);
    
    double sim_num = 2.0D * num_common_subsummers / (depth + 2.0D * num_common_subsummers);
    boolean sim_gloss = true;
    if (depth > 0.0D) {
      sim_gloss = getGlossSimilarity(relationship);
    }
    return new SimilaritySem(sim_num, sim_gloss);
  }
  
  public Synset getLowestCommonSubsumer(Relationship relationship)
    throws Exception
  {
    int cpi = getCommonParentIndexFromSource(relationship);
    return ((PointerTargetNode)relationship.getNodeList().get(cpi)).getSynset();
  }
  
  public int getDepth(Relationship relationship)
    throws Exception
  {
    return relationship.getDepth();
  }
  
  public int getCommonParentIndexFromSource(Relationship relationship)
    throws Exception
  {
    return ((AsymmetricRelationship)relationship).getCommonParentIndex();
  }
  
  public String printCommonSubsummers(Relationship relationship)
    throws Exception
  {
    Synset synsetterm = getLowestCommonSubsumer(relationship);
    
    PointerTargetTree hyperTree = PointerUtils.getHypernymTree(synsetterm);
    String res = new String();
    
    List<PointerTargetNodeList> nodelist = hyperTree.reverse();
    for (int x = 0; x < nodelist.size(); x++)
    {
      PointerTargetNodeList node = (PointerTargetNodeList)nodelist.get(x);
      Iterator it = node.iterator();
      while (it.hasNext())
      {
        PointerTargetNode pt = (PointerTargetNode)it.next();
        System.out.println(pt.toString());
        res.concat("\n" + pt.toString());
      }
    }
    return res;
  }
  
  public void printRelationShip(Relationship relationship)
  {
    relationship.getNodeList().print();
  }
  
  public int getMaxNumberCommonSubsumers(Relationship rs)
    throws Exception
  {
    int cpi = ((AsymmetricRelationship)rs).getCommonParentIndex();
    Synset synsetterm = ((PointerTargetNode)rs.getNodeList().get(cpi)).getSynset();
    PointerTargetTree hyperTree = PointerUtils.getHypernymTree(synsetterm);
    List<PointerTargetNodeList> nodelist = hyperTree.reverse();
    int res = 0;
    for (int i = 0; i < nodelist.size(); i++)
    {
      int aux = ((PointerTargetNodeList)nodelist.get(i)).size();
      if (aux > res) {
        res = aux;
      }
    }
    return res;
  }
  
  public Synset getSourceSynset(Relationship relationship)
    throws Exception
  {
    return relationship.getSourceSynset();
  }
  
  public String getSourceGloss(Relationship relationship)
    throws Exception
  {
    return relationship.getSourceSynset().getGloss();
  }
  
  public Synset getTargetSynset(Relationship relationship)
    throws Exception
  {
    return relationship.getTargetSynset();
  }
  
  public String getTargetGloss(Relationship relationship)
    throws Exception
  {
    return relationship.getTargetSynset().getGloss();
  }
  
  public String getSourceParentGloss(Relationship relationship)
    throws Exception
  {
    PointerTargetNode parent = (PointerTargetNode)relationship.getNodeList().get(1);
    return parent.getSynset().getGloss();
  }
  
  public String getTargetParentGloss(Relationship relationship)
    throws Exception
  {
    PointerTargetNode parent = (PointerTargetNode)relationship.getNodeList().get(relationship.getNodeList().size() - 2);
    return parent.getSynset().getGloss();
  }
  
  public Synset getSourceParentSynset(Relationship relationship)
    throws Exception
  {
    PointerTargetNode parent = (PointerTargetNode)relationship.getNodeList().get(1);
    return parent.getSynset();
  }
  
  public boolean getGlossSimilarity(Relationship relationship)
    throws Exception
  {
    List<Word> targetWords = relationship.getTargetSynset().getWords();
    String sourceGloss = getSourceGloss(relationship);
    String sourceParentGloss = getSourceParentGloss(relationship);
    for (int i = 0; i < targetWords.size(); i++) {
      if ((sourceGloss.indexOf(((Word)targetWords.get(i)).getLemma()) > -1) || (sourceParentGloss.indexOf(((Word)targetWords.get(i)).getLemma()) > -1)) {
        return true;
      }
    }
    List<Word> sourceWords = relationship.getSourceSynset().getWords();
    String targetGloss = getTargetGloss(relationship);
    String targetParentGloss = getTargetParentGloss(relationship);
    for (int i = 0; i < sourceWords.size(); i++) {
      if ((targetGloss.indexOf(((Word)sourceWords.get(i)).getLemma()) > -1) || (targetParentGloss.indexOf(((Word)sourceWords.get(i)).getLemma()) > -1)) {
        return true;
      }
    }
    return false;
  }
  
  public ArrayList<Synset> getBestSourceSynsets()
  {
    ArrayList<Synset> bestSources = new ArrayList();
    for (Relationship bestRelationship : this.bestRelationships) {
      if (!bestSources.contains(bestRelationship.getSourceSynset())) {
        bestSources.add(bestRelationship.getSourceSynset());
      }
    }
    return bestSources;
  }
  
  public SimilaritySem getBestSimilarity()
  {
    ArrayList<SimilaritySem> bestSim = new ArrayList();
    if (!this.bestRelationships.isEmpty())
    {
      Relationship bestRelationship = (Relationship)this.bestRelationships.get(0);
      SimilaritySem sim_new = (SimilaritySem)getRelationshipList().get(bestRelationship);
      return sim_new;
    }
    return null;
  }
  
  public ArrayList<Synset> getBestTargetSynsets()
  {
    ArrayList<Synset> bestTarget = new ArrayList();
    for (Relationship bestRelationship : this.bestRelationships) {
      if (!bestTarget.contains(bestRelationship.getTargetSynset())) {
        bestTarget.add(bestRelationship.getTargetSynset());
      }
    }
    return bestTarget;
  }
  
  public Hashtable<Relationship, SimilaritySem> getRelationshipList()
  {
    return this.relationshipList;
  }
  
  public ArrayList<Relationship> getBestRelationships()
  {
    return this.bestRelationships;
  }
  
  public void merge(WNSynsetSimilarity similaritySynsets2)
  {
    this.bestRelationships.addAll(similaritySynsets2.bestRelationships);
    
    Hashtable<Relationship, SimilaritySem> relationShipList2 = similaritySynsets2.getRelationshipList();
    Iterator iter = relationShipList2.keySet().iterator();
    while (iter.hasNext())
    {
      Relationship key = (Relationship)iter.next();
      SimilaritySem simSem = (SimilaritySem)relationShipList2.get(key);
      if (getRelationshipList().containsKey(key)) {
        System.out.println("Warning: Why are we merging two relationshipList with the same source and target?");
      } else {
        getRelationshipList().put(key, simSem);
      }
    }
  }
  
  public String getSource_lemma()
  {
    return this.source_lemma;
  }
  
  public String getTarget_lemma()
  {
    return this.target_lemma;
  }
  
  public static boolean isEmptyTaxonomySimilaritySynsets(ArrayList<WNSynsetSimilarity> taxonomySynsets)
  {
    ArrayList<Synset> aux = new ArrayList();
    for (WNSynsetSimilarity synsetSim : taxonomySynsets) {
      for (Synset syn : synsetSim.getBestSourceSynsets()) {
        aux.add(syn);
      }
    }
    if (aux.isEmpty() == true) {
      return true;
    }
    return false;
  }
  
  public boolean isEmpty()
  {
    ArrayList<Synset> aux = new ArrayList();
    for (Synset syn : getBestSourceSynsets()) {
      aux.add(syn);
    }
    if (aux.isEmpty() == true) {
      return true;
    }
    return false;
  }
}
