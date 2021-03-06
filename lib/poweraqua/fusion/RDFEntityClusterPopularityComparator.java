package poweraqua.fusion;

import java.util.Comparator;
import java.util.List;
import poweraqua.core.model.myrdfmodel.RDFEntity;

public class RDFEntityClusterPopularityComparator
  implements Comparator<RDFEntityCluster>
{
  public int compare(RDFEntityCluster c1, RDFEntityCluster c2)
    throws ClassCastException
  {
    if (c1.getEntries().size() > c2.getEntries().size()) {
      return -1;
    }
    if (c1.getEntries().size() < c2.getEntries().size()) {
      return 1;
    }
    String[] tmp = new String[2];
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
