package poweraqua.fusion;

import com.wcohen.secondstring.Jaro;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import poweraqua.core.model.myocmlmodel.OcmlInstance;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;

public abstract class BagOfWordsEntityComparator
{
  public static List<String> tokenize(String val)
  {
    List<String> res = new ArrayList();
    
    StringTokenizer tokenizer = new StringTokenizer(val, " \t\n\r\f:(),-.|@");
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (token != "") {
        res.add(token);
      }
    }
    return res;
  }
  
  public static double getDirectStringDistance(String s1, String s2)
  {
    Jaro matcher = new Jaro();
    return matcher.score(matcher.prepare(s1.toLowerCase()), matcher.prepare(s2.toLowerCase()));
  }
  
  public static double getBestScore(List<String> list1, List<String> list2)
  {
    Jaro matcher = new Jaro();
    List<List<Double>> scores = new ArrayList();
    List<String> small;
    List<String> big;
    List<String> small;
    if (list1.size() > list2.size())
    {
      List<String> big = list1;
      small = list2;
    }
    else
    {
      big = list2;
      small = list1;
    }
    for (int i = 0; i < small.size(); i++)
    {
      List<Double> row = new ArrayList(big.size());
      for (int j = 0; j < big.size(); j++) {
        row.add(Double.valueOf(matcher.score(matcher.prepare(((String)small.get(i)).toLowerCase()), matcher.prepare(((String)big.get(j)).toLowerCase()))));
      }
      scores.add(row);
    }
    double bestscore = 0.0D;
    
    double res = 0.0D;
    for (int k = 0; k < small.size(); k++)
    {
      bestscore = 0.0D;
      int maxi = 0;
      int maxj = 0;
      for (int i = 0; i < scores.size(); i++) {
        for (int j = 0; j < ((List)scores.get(i)).size(); j++) {
          if (((Double)((List)scores.get(i)).get(j)).doubleValue() >= bestscore)
          {
            bestscore = ((Double)((List)scores.get(i)).get(j)).doubleValue();
            maxi = i;
            maxj = j;
          }
        }
      }
      for (List<Double> tmp : scores) {
        tmp.remove(maxj);
      }
      scores.remove(maxi);
      res += bestscore;
    }
    res /= small.size();
    
    return res;
  }
  
  public static double getSimilarity(OcmlInstance instance1, OcmlInstance instance2)
  {
    if ((instance1 == null) || (instance2 == null)) {
      return 0.0D;
    }
    List<String> vector1 = getAllPropertyValues(instance1);
    List<String> vector2 = getAllPropertyValues(instance2);
    double res = getBestScore(vector1, vector2);
    
    return res;
  }
  
  private static List<String> getAllPropertyValues(OcmlInstance instance)
  {
    List<String> res = new ArrayList();
    
    Hashtable<RDFEntity, RDFEntityList> propertyValueTable = instance.getProperties();
    Set<RDFEntity> propertySet = propertyValueTable.keySet();
    for (RDFEntity property : propertySet)
    {
      RDFEntityList propertyValues = (RDFEntityList)propertyValueTable.get(property);
      for (RDFEntity value : propertyValues.getAllRDFEntities()) {
        res.addAll(tokenize(value.getLabel()));
      }
    }
    return res;
  }
}

