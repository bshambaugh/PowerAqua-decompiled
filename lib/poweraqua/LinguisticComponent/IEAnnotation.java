package poweraqua.LinguisticComponent;

import java.io.PrintStream;
import java.util.Map;

public class IEAnnotation
{
  private int offset_begin;
  private int offset_end;
  private String sentence;
  private Map features;
  
  public IEAnnotation(int offset_begin, int offset_end, String sentence, Map features)
  {
    this.offset_begin = offset_begin;
    this.offset_end = offset_end;
    this.sentence = sentence.trim();
    this.features = features;
  }
  
  public String getSentence()
  {
    return this.sentence;
  }
  
  public String getFeature(String name_feature)
  {
    Object obj = new String(name_feature);
    String rule = this.features.get(obj).toString();
    System.out.println(name_feature + " = " + rule);
    return rule;
  }
  
  public String getRule()
  {
    String rule = getFeature("rule");
    return rule;
  }
  
  public void setFeatures(Map features)
  {
    this.features = features;
  }
  
  public Map getFeatures()
  {
    return this.features;
  }
  
  public void setSentence(String sentence)
  {
    this.sentence = sentence.trim();
  }
  
  public int getOffset_begin()
  {
    return this.offset_begin;
  }
  
  public int getOffset_end()
  {
    return this.offset_end;
  }
  
  public void setOffset(int a, int b)
  {
    this.offset_begin = a;
    this.offset_end = b;
  }
  
  public boolean validateAnnotation(int offsetbegin, int offsetend)
  {
    int temp_begin = getOffset_begin();
    int temp_end = getOffset_end();
    if ((temp_begin < offsetbegin) || (temp_end > offsetend)) {
      return false;
    }
    return true;
  }
  
  public IEAnnotation DeleteOverlaps(IEAnnotation[] X_ann, int pattern_begin, int pattern_end)
  {
    boolean overlap = false;
    int n1 = getOffset_begin();
    int n2 = getOffset_end();
    int indexquotes_ini = getSentence().indexOf("\"");
    int indexquotes_end = getSentence().lastIndexOf("\"");
    if (indexquotes_ini == -1)
    {
      indexquotes_ini = getSentence().indexOf("'");
      indexquotes_end = getSentence().lastIndexOf("'");
    }
    for (int i = 0; i < X_ann.length; i++) {
      if (X_ann[i].validateAnnotation(pattern_begin, pattern_end))
      {
        int r1 = X_ann[i].getOffset_begin();
        int r2 = X_ann[i].getOffset_end();
        if ((r1 > indexquotes_ini) && (r2 < indexquotes_end)) {
          return this;
        }
        if ((n1 >= r1) && (n2 <= r2)) {
          return null;
        }
        if ((r1 > n1) && (r1 < n2))
        {
          setSentence(getSentence().substring(0, r1 - (n1 + 1)).trim());
          setOffset(n1, r1 - 1);
          return this;
        }
        if ((n1 > r1) && (n1 < r2) && (n2 > r2))
        {
          setSentence(getSentence().substring(r2 + 1 - n1, n2 - n1).trim());
          setOffset(r2 + 1, n2);
          return this;
        }
      }
    }
    return this;
  }
}

