package poweraqua.core.model.myocmlmodel;

public class StringUtility
{
  public static boolean isStringEmpty(String s)
  {
    if ((s == null) || (s.trim().length() <= 0)) {
      return true;
    }
    return false;
  }
  
  public static String lrTrim(String s)
  {
    if ((s == null) || (s.trim().length() <= 0)) {
      return null;
    }
    s = s.trim();
    while (s.charAt(0) == ' ') {
      s = s.substring(1);
    }
    return s;
  }
}
