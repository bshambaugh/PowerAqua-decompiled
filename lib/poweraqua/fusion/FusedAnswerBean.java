package poweraqua.fusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import poweraqua.core.model.myrdfmodel.RDFEntity;

public class FusedAnswerBean
{
  List<RDFEntityCluster> answers;
  IFusionService fusionService;
  
  public FusedAnswerBean(IFusionService fusionService)
  {
    this.answers = new ArrayList();
    this.fusionService = fusionService;
  }
  
  public IFusionService getFusionService()
  {
    return this.fusionService;
  }
  
  public List<RDFEntityCluster> getAnswers()
  {
    return this.answers;
  }
  
  public void addAnswer(RDFEntityCluster cluster)
  {
    if ((cluster != null) && (!this.answers.contains(cluster))) {
      this.answers.add(cluster);
    }
  }
  
  public void sortAnswers()
  {
    sortAnswers(0);
  }
  
  public void sortAnswers(int criterion)
  {
    int i;
    switch (criterion)
    {
    case 1: 
      Collections.sort(this.answers, new RDFEntityClusterConfidenceComparator());
      for (RDFEntityCluster cluster : this.answers) {
        cluster.setRankingValue(cluster.getRankingScore());
      }
      break;
    case 2: 
      Collections.sort(this.answers, new RDFEntityClusterPopularityComparator());
      for (RDFEntityCluster cluster : this.answers) {
        cluster.setRankingValue(cluster.getEntries().size());
      }
      break;
    case 3: 
      Collections.sort(this.answers, new RDFEntityClusterSynsetComparator(this));
      break;
    case 4: 
      Collections.sort(this.answers, new RDFEntityClusterCombinedComparator());
      for (RDFEntityCluster cluster : this.answers) {
        cluster.setRankingValue(cluster.getCombinedRankingScore());
      }
      break;
    case 0: 
    default: 
      Collections.sort(this.answers, new RDFEntityClusterComparator());
      i = 1;
      for (RDFEntityCluster cluster : this.answers) {
        cluster.setRankingValue(++i);
      }
    }
  }
  
  public ArrayList<String> getHigherRankEncodeAnswers(int rank)
  {
    ArrayList<String> semanticData = new ArrayList();
    
    int n = 0;
    int high_score = 0;
    for (RDFEntityCluster cluster : getAnswers())
    {
      String label = (String)cluster.getEntriesEncodeLabels().get(0);
      if (n == 0)
      {
        high_score = cluster.getRankingValue();
        semanticData.add(label);
      }
      else if (cluster.getRankingValue() == high_score)
      {
        semanticData.add(label);
      }
      else
      {
        if (((rank != 2) || (cluster.getRankingValue() <= 1)) && ((rank != 4) || (cluster.getRankingValue() <= 2))) {
          break;
        }
        semanticData.add(label);
      }
      n++;
    }
    return semanticData;
  }
  
  public ArrayList<String> getHigherRankAnswers(int rank)
  {
    ArrayList<String> semanticData = new ArrayList();
    
    int n = 0;
    int high_score = 0;
    for (RDFEntityCluster cluster : getAnswers())
    {
      String label = ((RDFEntityEntry)cluster.getEntries().get(0)).getValue().getLabel();
      if (n == 0)
      {
        high_score = cluster.getRankingValue();
        semanticData.add(label);
      }
      else if (cluster.getRankingValue() == high_score)
      {
        semanticData.add(label);
      }
      else
      {
        if (((rank != 2) || (cluster.getRankingValue() <= 1)) && ((rank != 4) || (cluster.getRankingValue() <= 2))) {
          break;
        }
        semanticData.add(label);
      }
      n++;
    }
    return semanticData;
  }
  
  public void consolidate()
  {
    List<RDFEntityCluster> newAnswers = new ArrayList();
    List<RDFEntityCluster> toSkip = new ArrayList();
    
    Map<RDFEntityCluster, Set<RDFEntityCluster>> clusterSets = new HashMap();
    List<Set<RDFEntityCluster>> clusterSetsList = new ArrayList();
    for (int i = 0; i < this.answers.size(); i++)
    {
      RDFEntityCluster currentCluster = (RDFEntityCluster)this.answers.get(i);
      Set<RDFEntityCluster> currentClusterSet;
      Set<RDFEntityCluster> currentClusterSet;
      if (clusterSets.containsKey(currentCluster))
      {
        currentClusterSet = (Set)clusterSets.get(currentCluster);
      }
      else
      {
        currentClusterSet = new HashSet();
        clusterSets.put(currentCluster, currentClusterSet);
        currentClusterSet.add(currentCluster);
        clusterSetsList.add(currentClusterSet);
      }
      for (int j = i + 1; j < this.answers.size(); j++)
      {
        RDFEntityCluster toCompare = (RDFEntityCluster)this.answers.get(j);
        if (currentCluster.overlapsWith(toCompare))
        {
          currentClusterSet.add(toCompare);
          clusterSets.put(toCompare, currentClusterSet);
        }
      }
    }
    for (Set<RDFEntityCluster> clusterSet : clusterSetsList)
    {
      RDFEntityCluster jointCluster = null;
      for (RDFEntityCluster currentCluster : clusterSet) {
        if (jointCluster == null) {
          jointCluster = currentCluster;
        } else {
          jointCluster = jointCluster.merge(currentCluster);
        }
      }
      if (jointCluster != null) {
        newAnswers.add(jointCluster);
      }
    }
    this.answers = newAnswers;
  }
}

