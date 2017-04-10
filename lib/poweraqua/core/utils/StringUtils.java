package poweraqua.core.utils;

import com.ibm.icu.text.Normalizer;
import java.io.PrintStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class StringUtils
{
  public static long generateStringId(String idOntology)
  {
    byte[] bytes = idOntology.getBytes();
    Checksum checksumEngine = new CRC32();
    checksumEngine.update(bytes, 0, bytes.length);
    return checksumEngine.getValue();
  }
  
  public static boolean isCompound(String keyword)
  {
    if ((keyword.startsWith("\"")) && (keyword.endsWith("\""))) {
      return false;
    }
    if ((keyword.startsWith("'")) && (keyword.endsWith("'"))) {
      return false;
    }
    boolean compound = false;
    if ((keyword.indexOf(" ") > 0) || (keyword.indexOf("_") > 0) || (keyword.indexOf("-") > 0)) {
      compound = true;
    }
    return compound;
  }
  
  public static int countCompounds(String keyword)
  {
    if ((keyword.startsWith("\"")) && (keyword.endsWith("\""))) {
      return 0;
    }
    String split = new String(keyword.trim());
    split = split.replaceAll("_", " ");
    split = split.replaceAll("-", " ");
    int compound = 1;
    int index = split.indexOf(" ");
    while (index > 0)
    {
      compound++;
      split = split.substring(index, split.length()).trim();
      index = split.indexOf(" ");
    }
    return compound;
  }
  
  public static boolean isSingularPluralExactMapping(String word1, String word2)
  {
    word1 = word1.replace(".", "");
    word2 = word2.replace(".", "");
    word1 = word1.replace("'", "");
    word1 = word1.replace("\"", "");
    word2 = word2.replace("'", "");
    word2 = word2.replace("\"", "");
    word1 = word1.replace("@en", "");
    word2 = word2.replace("@en", "");
    if (word1.equalsIgnoreCase(word2)) {
      return true;
    }
    String word2plural = SingularToPlural(word2);
    if (word2plural.equalsIgnoreCase(word1)) {
      return true;
    }
    String word1plural = SingularToPlural(word1);
    if (word1plural.equalsIgnoreCase(word2)) {
      return true;
    }
    String plural;
    if (((word1.endsWith("s")) || (word1.endsWith("s\""))) && (!word2.endsWith("s")))
    {
      String singular = new String(word2);
      plural = new String(word1);
    }
    else
    {
      String plural;
      if (((word2.endsWith("s")) || (word2.endsWith("s\""))) && (!word1.endsWith("s")))
      {
        String singular = new String(word1.toLowerCase().trim());
        plural = new String(word2.toLowerCase().trim());
      }
      else
      {
        return false;
      }
    }
    String plural;
    String singular;
    if (plural.endsWith("s"))
    {
      plural = plural.substring(0, plural.length() - 1);
      if (plural.equalsIgnoreCase(singular)) {
        return true;
      }
    }
    else if (plural.endsWith("s\""))
    {
      plural = plural.substring(0, plural.length() - 2);
      plural = plural.concat("\"");
      if (plural.equalsIgnoreCase(singular)) {
        return true;
      }
    }
    if (plural.endsWith("e"))
    {
      plural = plural.substring(0, plural.length() - 1);
      if (plural.equalsIgnoreCase(singular)) {
        return true;
      }
    }
    else if (plural.endsWith("e\""))
    {
      plural = plural.substring(0, plural.length() - 2);
      plural = plural.concat("\"");
      if (plural.equalsIgnoreCase(singular)) {
        return true;
      }
    }
    return false;
  }
  
  public static String SingularToPlural(String noun)
  {
    String[] ExceptionWords_DirectAddS = { "canto", "solo", "piano", "lasso", "halo", "memento", "albino", "sirocco", "chief", "fife", "mischief", "hoof", "roof", "grief", "kerchief", "safe" };
    
    String[] ExceptionWords_IrregularInput = { "man", "foot", "mouse", "woman", "tooth", "louse", "child", "ox", "goose" };
    
    String[] ExceptionWords_IrregularOutput = { "men", "feet", "mice", "women", "teeth", "lice", "children", "oxen", "geese" };
    
    String[] ExceptionWords_NoPlural = { "gold", "silver", "wheat", "corn", "molasses", "copper", "sugar", "cotton", "USA" };
    
    noun = noun.trim();
    for (String str : ExceptionWords_DirectAddS) {
      if (noun.compareToIgnoreCase(str) == 0) {
        return noun + "s";
      }
    }
    int i = 0;
    for (String str : ExceptionWords_IrregularInput)
    {
      if (noun.compareToIgnoreCase(str) == 0) {
        return ExceptionWords_IrregularOutput[i];
      }
      i++;
    }
    for (String str : ExceptionWords_NoPlural) {
      if (noun.compareToIgnoreCase(str) == 0) {
        return noun;
      }
    }
    if ((noun.endsWith("s")) || (noun.endsWith("z")) || (noun.endsWith("x")) || (noun.endsWith("sh")) || (noun.endsWith("ch"))) {
      noun = noun + "es";
    } else if (noun.endsWith("y"))
    {
      if ((noun.endsWith("ay")) || (noun.endsWith("ey")) || (noun.endsWith("iy")) || (noun.endsWith("oy")) || (noun.endsWith("uy"))) {
        noun = noun + "s";
      } else {
        noun = noun.substring(0, noun.length() - 1) + "ies";
      }
    }
    else if (noun.endsWith("o"))
    {
      if ((noun.endsWith("ao")) || (noun.endsWith("eo")) || (noun.endsWith("io")) || (noun.endsWith("oo")) || (noun.endsWith("uo"))) {
        noun = noun + "s";
      } else {
        noun = noun + "es";
      }
    }
    else if ((noun.endsWith("f")) && (noun.length() >= 1)) {
      noun = noun.substring(0, noun.length() - 1) + "ves";
    } else if ((noun.endsWith("fe")) && (noun.length() >= 2)) {
      noun = noun.substring(0, noun.length() - 2) + "ves";
    } else if (!noun.endsWith("\"")) {
      noun = noun + "s";
    }
    return noun;
  }
  
  public static String RemoveDiacritics(String s)
  {
    String temp = Normalizer.normalize(s, Normalizer.NFD);
    
    return temp.replaceAll("[^\\p{ASCII}]", "");
  }
  
  public static void main(String[] args)
  {
    System.out.println(SingularToPlural("country"));
    System.out.println(SingularToPlural("loaf"));
    System.out.println(RemoveDiacritics("Ja��n"));
  }
}

