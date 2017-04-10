package poweraqua.fusion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.powermap.triplePhase.OntoTripleBean;
import poweraqua.powermap.triplePhase.TripleSimilarityService;
import poweraqua.ranking.SynsetClusterRanking;

public class RDFEntityClusterSynsetComparator
  implements Comparator<RDFEntityCluster>
{
  FusedAnswerBean fusedAnswerBean;
  
  public RDFEntityClusterSynsetComparator(FusedAnswerBean bean)
  {
    this.fusedAnswerBean = bean;
  }
  
  public int compare(RDFEntityCluster c1, RDFEntityCluster c2)
    throws ClassCastException
  {
    String[] tmp = new String[2];
    
    ArrayList<OntoTripleBean> beans = new ArrayList();
    for (RDFEntityEntry entry : c1.getEntries()) {
      beans.add(entry.getOntoTripleBean());
    }
    int r1 = this.fusedAnswerBean.getFusionService().getTripleSimilarityService().getSynsetClusterRanking().getSynsetPopularity(beans).intValue();
    c1.setRankingValue(r1);
    beans.clear();
    for (RDFEntityEntry entry : c2.getEntries()) {
      beans.add(entry.getOntoTripleBean());
    }
    int r2 = this.fusedAnswerBean.getFusionService().getTripleSimilarityService().getSynsetClusterRanking().getSynsetPopularity(beans).intValue();
    c2.setRankingValue(r2);
    if (r1 != r2) {
      return r2 - r1;
    }
    try
    {
      tmp[0] = ((RDFEntityEntry)c1.getEntries().get(0)).getValue().getLabel().toLowerCase();
      tmp[1] = ((RDFEntityEntry)c2.getEntries().get(0)).getValue().getLabel().toLowerCase();
      for (int i = 0; i < 2; i++) {
        while ((tmp[i].charAt(0) < 'a') || (tmp[i].charAt(0) > 'z')) {
          tmp[i] = tmp[i].substring(1);
        }
      }
      return tmp[0].compareToIgnoreCase(tmp[1]);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return 0;
  }
}
