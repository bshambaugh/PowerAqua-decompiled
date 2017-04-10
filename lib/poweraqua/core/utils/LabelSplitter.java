package poweraqua.core.utils;

import java.io.PrintStream;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelSplitter
{
  public String splitLabel(String label)
  {
    String result = "";
    
    int firstSeparator = containsSeparator(label);
    if (firstSeparator > -1) {
      result = splitOnSeparators(label);
    } else if (toSplit(label)) {
      result = splitOnCaps(label);
    } else {
      result = label;
    }
    return result;
  }
  
  public static String splitOnCaps(String label)
  {
    if (label.length() < 3) {
      return label;
    }
    if (label.contains(" ")) {
      return label;
    }
    String result = "";
    
    Character curr = null;
    Character next = null;
    String element = "";
    for (int i = 0; i < label.length() - 1; i++)
    {
      Character prev = curr;curr = new Character(label.charAt(i));
      next = new Character(label.charAt(i + 1));
      if (Character.isUpperCase(curr.charValue()))
      {
        if (prev == null)
        {
          element = element + curr.toString();
        }
        else if (Character.isUpperCase(prev.charValue()))
        {
          if (Character.isUpperCase(next.charValue()))
          {
            element = element + curr.toString();
          }
          else
          {
            result = result + element + " ";
            
            element = curr.toString();
          }
        }
        else
        {
          result = result + element + " ";
          
          element = curr.toString();
        }
      }
      else {
        element = element + curr.toString();
      }
    }
    element = element + next.toString();
    
    result = result + element + " ";
    
    return result.trim();
  }
  
  public String splitOnSeparators(String label)
  {
    String result = "";
    
    int firstSeparator = containsSeparator(label);
    while (firstSeparator > -1)
    {
      String element = label.substring(0, firstSeparator);
      if (toSplit(element)) {
        element = splitOnCaps(element);
      }
      result = result + element + "/";
      label = label.substring(firstSeparator + 1, label.length());
      firstSeparator = containsSeparator(label);
    }
    if (toSplit(label)) {
      label = splitOnCaps(label);
    }
    result = result + label + "/";
    
    return result;
  }
  
  public int containsSeparator(String label)
  {
    int result = -1;
    
    int indexOf_ = label.indexOf("_");
    if (indexOf_ > -1) {
      result = indexOf_;
    }
    int indexOfMinus = label.indexOf("-");
    if (indexOfMinus > -1) {
      if (result != -1)
      {
        if (indexOfMinus < result) {
          result = indexOfMinus;
        }
      }
      else {
        result = indexOfMinus;
      }
    }
    int indexOfDot = label.indexOf(".");
    if (indexOfDot > -1) {
      if (result != -1)
      {
        if (indexOfDot < result) {
          result = indexOfDot;
        }
      }
      else {
        result = indexOfDot;
      }
    }
    int indexOfSpace = label.indexOf(" ");
    if (indexOfSpace > -1) {
      if (result != -1)
      {
        if (indexOfSpace < result) {
          result = indexOfSpace;
        }
      }
      else {
        result = indexOfSpace;
      }
    }
    int indexOfPlus = label.indexOf("+");
    if (indexOfPlus > -1) {
      if (result != -1)
      {
        if (indexOfPlus < result) {
          result = indexOfPlus;
        }
      }
      else {
        result = indexOfPlus;
      }
    }
    return result;
  }
  
  public boolean toSplit(String label)
  {
    Pattern oneLowerCase = Pattern.compile("[a-z]+");
    Matcher m1 = oneLowerCase.matcher(label);
    Pattern upperCase = Pattern.compile(".+[A-Z]+");
    Matcher m2 = upperCase.matcher(label);
    
    return (m1.find()) && (m2.find());
  }
  
  public boolean isPartOfLabel(String term, String label)
  {
    boolean result = false;
    while (label.indexOf("/") > -1)
    {
      String element = label.substring(0, label.indexOf("/"));
      label = label.substring(label.indexOf("/") + 1, label.length());
      if (term.compareToIgnoreCase(element) == 0) {
        result = true;
      }
    }
    if (term.compareToIgnoreCase(label) == 0) {
      result = true;
    }
    return result;
  }
  
  public Vector toVector(String a)
  {
    Vector result = new Vector();
    while (a.indexOf("/") > -1)
    {
      String part = a.substring(0, a.indexOf("/"));
      a = a.substring(a.indexOf("/") + 1, a.length());
      result.add(part);
    }
    return result;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    String word = "romanCityWalls";
    System.out.println(splitOnCaps(word));
  }
}

