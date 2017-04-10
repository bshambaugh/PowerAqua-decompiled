package poweraqua.WordNetJWNL;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipList;

public class WordNetSemantics
{
  private WordNet WN;
  
  public WordNetSemantics()
    throws Exception
  {
    this.WN = new WordNet();
  }
  
  public WordNetSemantics(String realpath)
    throws Exception
  {
    this.WN = new WordNet(realpath);
  }
  
  public ArrayList<RelationshipList> getHyperTermSense(String term1, String term2)
    throws Exception
  {
    getWN().Initialize(term1);
    int synsets1 = getWN().num_senses;
    int pos1 = getWN().pos;
    IndexWord indexWord1 = getWN().termIndex;
    System.out.println("IndexWord1 " + indexWord1);
    IndexWord indexWord2;
    IndexWord indexWord2;
    if (term1.equalsIgnoreCase(term2))
    {
      int synsets2 = synsets1;
      int pos2 = pos1;
      indexWord2 = indexWord1;
    }
    else
    {
      getWN().Initialize(term2);
      int synsets2 = getWN().num_senses;
      int pos2 = getWN().pos;
      indexWord2 = getWN().termIndex;
    }
    System.out.println("IndexWord2 " + indexWord2);
    return getHyperTermSense(indexWord1, indexWord2);
  }
  
  public ArrayList<RelationshipList> getHyperTermSense(IndexWord indexWord1, IndexWord indexWord2)
    throws Exception
  {
    ArrayList<RelationshipList> common = new ArrayList();
    for (int i = 0; i < indexWord1.getSenses().size(); i++) {
      for (int j = 0; j < indexWord2.getSenses().size(); j++)
      {
        RelationshipList list = getWN().demonstrateAsymmetricRelationshipOperation(indexWord1, indexWord2, i, j);
        if (((list != null ? 1 : 0) & (list.size() > 0 ? 1 : 0)) != 0)
        {
          RelationshipList aux = new RelationshipList();
          if (indexWord1.getLemma().equals(indexWord2.getLemma())) {
            aux.add((Relationship)list.get(0));
          } else {
            for (int lenght = 0; lenght < list.size(); lenght++) {
              if (((Relationship)list.get(lenght)).getDepth() < 10) {
                aux.add((Relationship)list.get(lenght));
              }
            }
          }
          common.add(aux);
        }
      }
    }
    return common;
  }
  
  public WNSynsetSimilarity getWNTaxonomySynsets(String term1, String term2)
    throws Exception
  {
    WNSynsetSimilarity WNSynsets = new WNSynsetSimilarity();
    
    ArrayList<RelationshipList> common = getHyperTermSense(term1, term2);
    for (int u = 0; u < common.size(); u++) {
      WNSynsets.addRelationShipList((RelationshipList)common.get(u));
    }
    System.out.println("The number of common results for " + term1 + " and " + term2 + " are : " + WNSynsets.getRelationshipList().size());
    
    WNSynsets.calculateBestSimilarity();
    return WNSynsets;
  }
  
  public WNSynsetSimilarity getWNTaxonomySynsets(IndexWord term1, IndexWord term2)
    throws Exception
  {
    WNSynsetSimilarity WNSynsets = new WNSynsetSimilarity(term1.getLemma(), term2.getLemma());
    
    ArrayList<RelationshipList> common = getHyperTermSense(term1, term2);
    for (int u = 0; u < common.size(); u++) {
      WNSynsets.addRelationShipList((RelationshipList)common.get(u));
    }
    WNSynsets.calculateBestSimilarity();
    return WNSynsets;
  }
  
  public static WNSynsetSimilarity getWNMappingSynsets(Synset start, Synset end)
    throws Exception
  {
    WNSynsetSimilarity WNSynsets = new WNSynsetSimilarity();
    RelationshipList common = WordNet.demonstrateAsymmetricRelationshipOperation(start, end);
    WNSynsets.addRelationShipList(common);
    WNSynsets.calculateBestSimilarity();
    return WNSynsets;
  }
  
  public void getSimilarTermSense(String term1, String term2)
    throws Exception
  {
    getWN().Initialize(term1);
    int synsets1 = getWN().num_senses;
    getWN().Initialize(term2);
    int synsets2 = getWN().num_senses;
    
    Vector common = new Vector();
    for (int i = 0; i < synsets1; i++) {
      for (int j = 0; j < synsets2; j++)
      {
        RelationshipList list = getWN().demonstrateSymmetricRelationshipOperation(term1, term2, i, j);
        if (((list != null ? 1 : 0) & (list.size() > 0 ? 1 : 0)) != 0) {
          common.add(list);
        }
      }
    }
    System.out.println("The number of common results for " + term1 + " and " + term2 + " are : " + common.size());
    for (int u = 0; u < common.size(); u++) {
      getWN().printRelationShipList((RelationshipList)common.get(u));
    }
  }
  
  public void getHypoTermSense(String term1, String term2)
    throws Exception
  {
    getWN().Initialize(term1);
    int synsets1 = getWN().num_senses;
    getWN().Initialize(term2);
    int synsets2 = getWN().num_senses;
    
    Vector common = new Vector();
    for (int i = 0; i < synsets1; i++) {
      for (int j = 0; j < synsets2; j++)
      {
        RelationshipList list = getWN().demonstrateHypoRelationshipOperation(term1, term2, i, j);
        if ((((list != null ? 1 : 0) & (list.size() > 0 ? 1 : 0)) != 0) && 
          (((Relationship)list.get(0)).getDepth() < 10)) {
          common.add(list);
        }
      }
    }
    System.out.println("The number of common results for " + term1 + " and " + term2 + " are : " + common.size());
    for (int u = 0; u < common.size(); u++) {
      getWN().printRelationShipList((RelationshipList)common.get(u));
    }
  }
  
  public ArrayList<String> isWordNetInput(String termino)
    throws Exception
  {
    return getWN().isWordNetInput(termino);
  }
  
  public static void main(String[] args)
    throws Exception
  {
    try
    {
      String termino1 = "journal";
      String termino2 = "antiques";
      
      WordNetSemantics WNS = new WordNetSemantics();
      WordNet.OpenDictionary();
      System.out.println("testing");
      
      WNS.isWordNetInput("religious_organizations");
      
      System.out.println("Calling demonstrateSymmetricRelationshipOperation for " + termino1 + " and " + termino2);
      
      synsetSimilarity = WNS.getWNTaxonomySynsets(termino1, termino2);
      
      System.out.println("Number of iterations or RelationShipList .. " + synsetSimilarity.getRelationshipList().keySet().size());
      relationshipList = synsetSimilarity.getRelationshipList();
      for (Relationship relationship : relationshipList.keySet())
      {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(" ***Obtaining one RelationShip on the RelationShipList ...... ");
        WNSynsetSimilarity.SimilaritySem sim_aux = (WNSynsetSimilarity.SimilaritySem)relationshipList.get(relationship);
        
        System.out.println(" The source synset and gloss " + synsetSimilarity.getSourceSynset(relationship) + synsetSimilarity.getSourceGloss(relationship));
        
        System.out.println(" The target synset and gloss " + synsetSimilarity.getTargetSynset(relationship) + synsetSimilarity.getTargetGloss(relationship));
        
        System.out.println("RelationShip depth " + synsetSimilarity.getDepth(relationship) + " and number of common subsummers " + synsetSimilarity.getMaxNumberCommonSubsumers(relationship) + " and the lowest common subsummer " + synsetSimilarity.getLowestCommonSubsumer(relationship));
        
        System.out.println("Print all common subsummers -------");
        synsetSimilarity.printCommonSubsummers(relationship);
        System.out.println("Print all the path -------");
        synsetSimilarity.printRelationShip(relationship);
        System.out.println("*** The Similarity results " + sim_aux.getSimilarity() + " -glosses?-" + sim_aux.isGloss_similarity());
      }
      System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
      System.out.println("OBTAINING THE BEST SOURCE SYNSETS ++++++++++++++++++++++++++++++++++++++++");
      ArrayList<Synset> sources = synsetSimilarity.getBestSourceSynsets();
      for (Synset synset : sources) {
        System.out.println(synset.getOffset() + " key : " + synset.getKey() + " words: " + synset.getWords() + " gloss: " + synset.getGloss());
      }
      System.out.println("OBTAINING THE BEST TARGET SYNSETS ++++++++++++++++++++++++++++++++++++++++");
      ArrayList<Synset> targets = synsetSimilarity.getBestTargetSynsets();
      for (Synset synset : targets) {
        System.out.println(synset);
      }
      System.out.println("The best relationships are ");
      ArrayList<Relationship> bestRelationships = synsetSimilarity.getBestRelationships();
      for (Relationship rel : bestRelationships)
      {
        WNSynsetSimilarity.SimilaritySem sim_aux = (WNSynsetSimilarity.SimilaritySem)relationshipList.get(rel);
        System.out.println("--------- " + sim_aux.getSimilarity() + " -glosses?-" + sim_aux.isGloss_similarity());
        
        System.out.println("best path");
        synsetSimilarity.printRelationShip(rel);
        System.out.println("common subsummers ");
        synsetSimilarity.printCommonSubsummers(rel);
      }
    }
    catch (Exception e)
    {
      WNSynsetSimilarity synsetSimilarity;
      Hashtable<Relationship, WNSynsetSimilarity.SimilaritySem> relationshipList;
      System.out.println("Error: " + e);
    }
  }
  
  public WordNet getWN()
  {
    return this.WN;
  }
}

