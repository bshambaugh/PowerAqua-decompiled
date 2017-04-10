package poweraqua.fusion;

import java.util.Comparator;
import poweraqua.core.model.myrdfmodel.RDFEntity;

public class RDFEntityEntryComparator
  implements Comparator<RDFEntityEntry>
{
  public int compare(RDFEntityEntry c1, RDFEntityEntry c2)
    throws ClassCastException
  {
    String[] tmp = new String[2];
    try
    {
      tmp[0] = c1.getValue().getLabel().toLowerCase();
      tmp[1] = c2.getValue().getLabel().toLowerCase();
      for (int i = 0; i < 2; i++) {
        while (((tmp[i].charAt(0) < 'a') || (tmp[i].charAt(0) > 'z')) && 
          (tmp[i].length() > 1)) {
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

