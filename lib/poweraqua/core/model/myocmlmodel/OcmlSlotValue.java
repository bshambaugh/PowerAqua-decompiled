package poweraqua.core.model.myocmlmodel;

import java.io.Serializable;
import java.util.ArrayList;

public class OcmlSlotValue
  implements Serializable
{
  private String slotName;
  private ArrayList slotValues;
  private boolean isValueString = true;
  
  public OcmlSlotValue(String slotName)
  {
    this.slotName = slotName;
    this.slotValues = new ArrayList();
  }
  
  public void setValueStringStatus(boolean isString)
  {
    this.isValueString = isString;
  }
  
  public boolean isValueString()
  {
    return this.isValueString;
  }
  
  public int valueCount()
  {
    return this.slotValues.size();
  }
  
  public String[] getValues()
  {
    int count = valueCount();
    String[] values = new String[count];
    for (int i = 0; i < count; i++) {
      values[i] = ((String)this.slotValues.get(i));
    }
    return values;
  }
  
  public void addSlotValue(String s)
  {
    if (s == null) {
      return;
    }
    s = StringUtility.lrTrim(s);
    if (s == null) {
      return;
    }
    s = s.replace('(', '-');
    s = s.replace(')', '-');
    s = s.replace('"', '\'');
    s = removeMarkupSymbols(s);
    
    s = s.replaceAll("\r\n", " ");
    s = s.replace('\n', ' ');
    s = s.replace('\024', ' ');
    s = s.replace('\031', ' ');
    s = s.replaceAll("#39", "'");
    if (this.slotName.indexOf("has-web-address") <= -1)
    {
      s = s.replaceAll("#39s", " ");
      s = s.replaceAll("39s", " ");
      s = s.replaceAll("39t", " ");
      s = s.replaceAll("39 ", " ");
      
      s = s.replaceAll("&amp;", "&");
      s = s.replaceAll("&", "");
    }
    if (this.slotValues.indexOf(s) <= -1) {
      this.slotValues.add(s);
    }
  }
  
  public void setSlotValue(String[] s)
  {
    this.slotValues.clear();
    if (s == null) {
      return;
    }
    for (int i = 0; i < s.length; i++) {
      this.slotValues.add(s[i]);
    }
  }
  
  public void setSlotName(String s)
  {
    this.slotName = s;
  }
  
  public String getSlotName()
  {
    return this.slotName;
  }
  
  public boolean isValuesForSlot(String sname)
  {
    if ((this.slotName.equals(sname)) || (this.slotName.endsWith("#" + sname)) || (this.slotName.startsWith(sname + "-of-")) || (this.slotName.indexOf("#" + sname + "-of-") > -1)) {
      return true;
    }
    return false;
  }
  
  public boolean hasValue(String value)
  {
    for (int i = 0; i < this.slotValues.size(); i++)
    {
      String v = (String)this.slotValues.get(i);
      if ((v.trim().toLowerCase().equals(value.trim().toLowerCase())) || (v.trim().toLowerCase().endsWith('#' + value.trim().toLowerCase()))) {
        return true;
      }
    }
    return false;
  }
  
  private String removeMarkupSymbols(String s)
  {
    if ((s == null) || (s.length() <= 0)) {
      return s;
    }
    if ((s == "<") || (s == "<>")) {
      return null;
    }
    while ((s.indexOf("<") > -1) && (s.indexOf(">") > -1))
    {
      int k = s.indexOf("<");
      int j = s.indexOf(">");
      if (j > k) {
        if ((k == 0) && (j == s.length() - 1)) {
          s = "";
        } else if ((k == 0) && (j < s.length() - 1)) {
          s = s.substring(j + 1);
        } else {
          s = s.substring(0, k - 1) + " " + s.substring(j + 1);
        }
      }
    }
    return s;
  }
}

