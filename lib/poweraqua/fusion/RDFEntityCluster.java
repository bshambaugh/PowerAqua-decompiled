package poweraqua.fusion;

import java.io.PrintStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.powermap.triplePhase.OntoTripleBean;
import poweraqua.powermap.triplePhase.TripleSimilarityService;
import poweraqua.ranking.SynsetClusterRanking;

public class RDFEntityCluster
{
  IFusionService fusionService;
  Map<String, RDFEntityEntry> entryMap;
  List<RDFEntityEntry> entryList;
  Map<String, RDFEntityEntry> entryOntologiesMap;
  OntoTripleBean ontoTripleBean;
  int rankingValue;
  int combinedRankingScore = -1;
  
  public RDFEntityCluster(IFusionService fusionService)
  {
    this.entryMap = new HashMap();
    this.entryList = new ArrayList();
    this.entryOntologiesMap = new HashMap();
    this.ontoTripleBean = new OntoTripleBean();
    this.fusionService = fusionService;
  }
  
  public int getRankingValue()
  {
    return this.rankingValue;
  }
  
  public boolean getAffirmativeNegative()
  {
    boolean affirneg = false;
    try
    {
      affirneg = ((RDFEntityEntry)this.entryList.get(0)).getOntoTripleBean().getAnswer_instances().isAffirmativeNegative();
    }
    catch (Exception e)
    {
      System.out.println("Error in getAffirmativeNegative for a cluster");
      e.printStackTrace();
    }
    return affirneg;
  }
  
  public void setRankingValue(int rankingValue)
  {
    this.rankingValue = rankingValue;
  }
  
  public OntoTripleBean getOntoTripleBean()
  {
    return this.ontoTripleBean;
  }
  
  public boolean addEntry(RDFEntityEntry entry)
  {
    this.ontoTripleBean.addBeans(entry.getOntoTripleBean());
    RDFEntityEntry existingEntry;
    if (!this.entryMap.containsKey(entry.getValue().getURI()))
    {
      if (!this.entryOntologiesMap.containsKey(entry.getOntologyId()))
      {
        this.entryMap.put(entry.getValue().getURI(), entry);
        this.entryOntologiesMap.put(entry.getOntologyId(), entry);
        this.entryList.add(entry);
        return true;
      }
    }
    else
    {
      existingEntry = (RDFEntityEntry)this.entryMap.get(entry.getValue().getURI());
      if (entry.getValue().getRefers_to() != null)
      {
        if (existingEntry.getValue().getRefers_to() == null) {
          existingEntry.getValue().setRefers_to(entry.getValue().getRefers_to());
        }
        for (RDFEntity entity : entry.getRefersToValues()) {
          existingEntry.addRefersToValue(entity);
        }
      }
    }
    return false;
  }
  
  public Map<String, RDFEntityEntry> getEntryMap()
  {
    return this.entryMap;
  }
  
  public List<RDFEntityEntry> getEntries()
  {
    return this.entryList;
  }
  
  public ArrayList<String> getEntriesEncodeLabels()
  {
    HashSet noRepeats = new HashSet();
    ArrayList<String> semanticData = new ArrayList();
    try
    {
      for (RDFEntityEntry entry : this.entryList)
      {
        RDFEntity entity = entry.getValue();
        String label = entity.getLabel();
        noRepeats.add(URLEncoder.encode(label, "UTF-8"));
      }
      semanticData.addAll(noRepeats);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return semanticData;
  }
  
  public void removeEntry(RDFEntityEntry entry)
  {
    removeEntryByURI(entry.getValue().getURI());
  }
  
  public void removeEntryByURI(String uri)
  {
    if (!this.entryMap.containsKey(uri)) {
      return;
    }
    RDFEntityEntry entry = (RDFEntityEntry)this.entryMap.get(uri);
    this.entryList.remove(entry);
    this.entryMap.remove(uri);
  }
  
  public RDFEntityCluster merge(RDFEntityCluster cluster)
  {
    RDFEntityCluster result = new RDFEntityCluster(this.fusionService);
    for (RDFEntityEntry entry : getEntries()) {
      result.addEntry(entry);
    }
    for (RDFEntityEntry entry : cluster.getEntries()) {
      result.addEntry(entry);
    }
    result.getOntoTripleBean().addBeans(this.ontoTripleBean);
    result.getOntoTripleBean().addBeans(cluster.getOntoTripleBean());
    return result;
  }
  
  public int getRankingScore()
  {
    int min = -1;
    for (RDFEntityEntry entry : this.entryList) {
      if ((min == -1) || (min > entry.getRankingScore())) {
        min = entry.getRankingScore();
      }
    }
    return min;
  }
  
  public int getCombinedRankingScore()
  {
    if (this.combinedRankingScore == -1) {
      this.combinedRankingScore = calculateCombinedRankingScore();
    }
    return this.combinedRankingScore;
  }
  
  private int calculateCombinedRankingScore()
  {
    int totalScore = 0;
    this.ontoTripleBean.printShort();
    int curScore = getRankingScore();
    if (curScore == 1) {
      totalScore += 2;
    } else if (curScore == 2) {
      totalScore++;
    }
    if (getEntries().size() > 2) {
      totalScore += 2;
    } else if (getEntries().size() == 2) {
      totalScore++;
    }
    ArrayList<OntoTripleBean> beans = new ArrayList();
    for (RDFEntityEntry entry : getEntries()) {
      beans.add(entry.getOntoTripleBean());
    }
    curScore = this.fusionService.getTripleSimilarityService().getSynsetClusterRanking().getSynsetPopularity(beans).intValue();
    if (curScore > 1) {
      totalScore++;
    }
    return totalScore;
  }
  
  public boolean overlapsWith(RDFEntityCluster cluster)
  {
    for (String otherUri : cluster.getEntryMap().keySet()) {
      if (this.entryMap.containsKey(otherUri)) {
        return true;
      }
    }
    return false;
  }
}

