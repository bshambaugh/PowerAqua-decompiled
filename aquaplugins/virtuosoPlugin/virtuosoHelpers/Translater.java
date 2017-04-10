package virtuosoPlugin.virtuosoHelpers;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;

public class Translater
{
  static String instance_uri = "\" + instance_uri + \"";
  static String e = "\" + e + \"";
  static String p = "\" + p + \"";
  static String entity = "\" + entity + \"";
  static String property_uri = "\" + property_uri + \"";
  static String node = "\" + node + \"";
  static String entity_uri = "\" + entity_uri + \"";
  static String classGeneric_uri = "\" + classGeneric_uri + \"";
  static String literal = "\" + literal + \"";
  static String class_URI = "\" + class_URI + \"";
  static String instance1_uri = "\" + instance1_uri + \"";
  static String instance2_uri = "\" + instance2_uri + \"";
  static String slot_value = "\" + slot_value + \"";
  static String classURI = "\" + classURI + \"";
  static String class_uri = "\" + class_uri + \"";
  static String class1_uri = "\" + class1_uri + \"";
  static String class2_uri = "\" + class2_uri + \"";
  static String limit = "\" + limit + \"";
  static String offset = "\" + offset + \"";
  static String NOT_RDF_INSTANCES = "NOT_RDF_INSTANCES";
  static String NOT_RDF_CLASSES = "NOT_RDF_CLASSES";
  static String NOT_RDF_PROPERTIES = "NOT_RDF_PROPERTIES";
  static String NOT_RDF_PROPERTIES1 = "NOT_RDF_PROPERTIES1";
  static String NOT_RDF_PROPERTIES2 = "NOT_RDF_PROPERTIES2";
  static HashSet<String> usedNOTS = new HashSet();
  static String[] NOTS = { NOT_RDF_INSTANCES, NOT_RDF_CLASSES, NOT_RDF_PROPERTIES, NOT_RDF_PROPERTIES1, NOT_RDF_PROPERTIES2 };
  private static String start;
  private static boolean distinct;
  private static String select = "";
  private static String filter = "";
  private static HashSet<String> filterStatementExtraHash = new HashSet();
  private static LinkedList<String> filterStatementExtra = new LinkedList();
  private static String ending;
  static boolean bOrder;
  static boolean bLimit;
  static boolean bOffset;
  static String sOrder;
  static String sLimit;
  static String sOffset;
  private static HashSet<String> allVariables = new HashSet();
  private static String SPARQL;
  private static String SPARQLhelp;
  private static boolean bFilterStatementExtra;
  
  public static void main(String[] args)
  {
    String inputString = " select distinct i, l, title from {i} rdf:type {c}, {c} rdf:type {rdfs:Class}, [{i}  <http://purl.org/dc/elements/1.1/title> {title}], [{i} rdfs:label {l}] where isURI(i) " + NOT_RDF_CLASSES + NOT_RDF_INSTANCES + "limit " + limit + " offset " + offset;
    
    System.out.println(inputString);
    System.out.println(translateSERQLtoSPARQL(inputString));
  }
  
  private static String translateSERQLtoSPARQL(String string)
  {
    String outputstring = "";
    split(string);
    return outputstring;
  }
  
  private static void split(String string)
  {
    System.out.println("string: " + string);
    string = string.replace("#intersect", "#interxsect");
    string = string.replace(":intersect", ":interxsect");
    string = string.replace("intersection", "interxsection");
    string = string.replace("intersect", "INTERSECT");
    string = string.replace("interxsect", "intersect");
    String[] sInter = string.split("INTERSECT");
    for (String s : sInter) {
      System.out.println(s);
    }
    for (int iInter = 0; iInter < sInter.length; iInter++)
    {
      System.out.println("Inter:" + iInter);
      sInter[iInter] = sInter[iInter].replace("minus", "MINUS");
      String[] sMinus = sInter[iInter].split("MINUS");
      for (String s : sMinus) {
        System.out.println(s);
      }
      for (int iMinus = 0; iMinus < sMinus.length; iMinus++)
      {
        System.out.println("Minus:" + iMinus);
        sMinus[iMinus] = sMinus[iMinus].replace("unionO", "unxionO");
        sMinus[iMinus] = sMinus[iMinus].replace("#union", "#unxion");
        sMinus[iMinus] = sMinus[iMinus].replace(":union", ":unxion");
        sMinus[iMinus] = sMinus[iMinus].replace("union", "UNION");
        String[] sUnion = sMinus[iMinus].split("UNION");
        for (String s : sUnion) {
          System.out.println(s);
        }
        for (int iUnion = 0; iUnion < sUnion.length; iUnion++)
        {
          sMinus[iMinus] = sMinus[iMinus].replace("unxion", "union");
          filterStatementExtra = new LinkedList();
          filterStatementExtraHash = new HashSet();
          bFilterStatementExtra = false;
          System.out.println("Union:" + iUnion);
          sUnion[iUnion] = sUnion[iUnion].replace("SELECT", "select");
          String[] xSelect = sUnion[iUnion].split("select ");
          
          System.out.println(xSelect[0]);
          System.out.println(xSelect[1]);
          if (xSelect[1].contains("distinct"))
          {
            xSelect[1] = xSelect[1].replace("distinct ", "");
            distinct = true;
          }
          if (xSelect[1].contains("DISTINCT"))
          {
            xSelect[1] = xSelect[1].replace("DISTINCT ", "");
            distinct = true;
          }
          String[] xFrom = xSelect[1].split(" from ");
          System.out.println(xFrom[0]);
          System.out.println(xFrom[1]);
          
          xFrom[0] = xFrom[0].replace(" ", "");
          xFrom[0] = xFrom[0].replace(" ", "");
          
          String[] variables = xFrom[0].split(",");
          select = "";
          for (int i = 0; i < variables.length; i++)
          {
            select = select + " ?" + variables[i];
            
            allVariables.add(variables[i]);
          }
          System.out.println(select);
          
          boolean bWhere = false;
          String[] xWhere = new String[2];
          if (xFrom[1].contains(" where "))
          {
            xWhere = xFrom[1].split(" where ");
            System.out.println(xWhere[0]);
            System.out.println(xWhere[1]);
            String from = xWhere[0];
            bWhere = true;
            xWhere[1] = xWhere[1].replace("\"en\"", "\\\"en\\\"");
            xWhere[1] = xWhere[1].replace("\"\"", "\\\"\\\"");
            xWhere[1] = xWhere[1].replace("\\\"\\\" +", "\\\"\" +");
            xWhere[1] = xWhere[1].replace("+ \\\"\\\"", "+ \"\\\"");
            
            xWhere[1] = xWhere[1].replace(" AND ", " and ");
            xWhere[1] = xWhere[1].replace(" and  ", " and ");
            xWhere[1] = xWhere[1].replace(" and  ", " and ");
            xWhere[1] = xWhere[1].replace("  and ", " and ");
            xWhere[1] = xWhere[1].replace("  and ", " and ");
            
            xWhere[1] = xWhere[1].replace(" OR ", " or ");
            xWhere[1] = xWhere[1].replace(" or  ", " or ");
            xWhere[1] = xWhere[1].replace(" or  ", " or ");
            xWhere[1] = xWhere[1].replace("  or ", " or ");
            xWhere[1] = xWhere[1].replace("  or ", " or ");
            
            System.out.println(xWhere[1]);
            xWhere[1] = xWhere[1].replace("order by", "ORDER BY");
            xWhere[1] = xWhere[1].replace("limit", "LIMIT");
            xWhere[1] = xWhere[1].replace("offset", "OFFSET");
            
            int breakPoint = xWhere[1].length();
            
            int posOrder = xWhere[1].indexOf("ORDER BY");
            if (posOrder >= 0)
            {
              bOrder = true;
              breakPoint = posOrder;
            }
            int posLimit = xWhere[1].indexOf("LIMIT");
            if (posLimit >= 0)
            {
              bLimit = true;
              breakPoint = Math.min(breakPoint, posLimit);
            }
            int posOffset = xWhere[1].indexOf("OFFSET");
            if (posOffset >= 0)
            {
              bOffset = true;
              breakPoint = Math.min(breakPoint, posOffset);
            }
            ending = xWhere[1].substring(breakPoint, xWhere[1].length());
            xWhere[1] = xWhere[1].substring(0, breakPoint);
            
            filter = "";
            xWhere[1] = xWhere[1].replace("=", " = ");
            xWhere[1] = xWhere[1].replace(" =  ", " = ");
            xWhere[1] = xWhere[1].replace(" =  ", " = ");
            xWhere[1] = xWhere[1].replace("  = ", " = ");
            xWhere[1] = xWhere[1].replace("  = ", " = ");
            for (String rep : allVariables)
            {
              xWhere[1] = xWhere[1].replace(" + " + rep + " + ", " + " + rep + "xzyzhsyshdnm + ");
              xWhere[1] = xWhere[1].replace(rep + " ", "str(?" + rep + ") ");
              xWhere[1] = xWhere[1].replace("(" + rep + ")", "(?" + rep + ")");
              xWhere[1] = xWhere[1].replace(" + " + rep + "xzyzhsyshdnm + ", " + " + rep + " + ");
            }
            for (String nots : NOTS) {
              if (xWhere[1].contains(nots))
              {
                usedNOTS.add(nots);
                xWhere[1] = xWhere[1].replace(nots, "");
              }
            }
            String[] splitAnd = xWhere[1].split(" and ");
            for (int sA = 0; sA < splitAnd.length; sA++)
            {
              String[] splitOr = splitAnd[sA].split(" or ");
              for (int sO = 0; sO < splitOr.length; sO++)
              {
                System.out.println("            this: " + splitOr[sO]);
                if (splitOr[sO].contains(" not "))
                {
                  splitOr[sO] = splitOr[sO].replace(" not ", " ");
                  splitOr[sO] = splitOr[sO].replace("=", "!=");
                }
                System.out.println(splitOr[sO]);
              }
              splitAnd[sA] = splitOr[0];
              for (int split = 1; split < splitOr.length; split++)
              {
                int tmp1726_1724 = sA; String[] tmp1726_1722 = splitAnd;tmp1726_1722[tmp1726_1724] = (tmp1726_1722[tmp1726_1724] + " || "); int 
                  tmp1751_1749 = sA; String[] tmp1751_1747 = splitAnd;tmp1751_1747[tmp1751_1749] = (tmp1751_1747[tmp1751_1749] + splitOr[split]);
              }
              System.out.println(splitAnd[sA]);
            }
            filter = splitAnd[0];
            for (int split = 1; split < splitAnd.length; split++)
            {
              filter += " && ";
              filter += splitAnd[split];
            }
            System.out.println(filter);
          }
          else
          {
            System.out.println(" no where ");
            xFrom[1] = xFrom[1].replace("order by", "ORDER BY");
            xFrom[1] = xFrom[1].replace("limit", "LIMIT");
            xFrom[1] = xFrom[1].replace("offset", "OFFSET");
            int breakPoint = xFrom[1].length();
            
            int posOrder = xFrom[1].indexOf("ORDER BY");
            if (posOrder >= 0)
            {
              bOrder = true;
              breakPoint = posOrder;
            }
            int posLimit = xFrom[1].indexOf("LIMIT");
            if (posLimit >= 0)
            {
              bLimit = true;
              breakPoint = Math.min(breakPoint, posLimit);
            }
            int posOffset = xFrom[1].indexOf("OFFSET");
            if (posOffset >= 0)
            {
              bOffset = true;
              breakPoint = Math.min(breakPoint, posOffset);
            }
            ending = xFrom[1].substring(breakPoint, xFrom[1].length());
            from = xFrom[1].substring(0, breakPoint);
          }
          System.out.println("from" + from);
          
          String from = from.replace(", ", ",");
          from = from.replace(", ", ",");
          from = from.replace(" ,", ",");
          from = from.replace(" ,", ",");
          int semicolon = from.length() - from.replaceAll(";", "").length();
          String[] statements = from.split(",");
          for (int i = 0; i < statements.length; i++) {
            System.out.println("statements[" + i + "]: " + statements[i]);
          }
          String[] seperatStatements = new String[statements.length + semicolon];
          int semicolonCounter = 0;
          for (int i = 0; i < statements.length; i++)
          {
            System.out.println(statements[i]);
            boolean optionalStatement = (statements[i].contains("[")) && (statements[i].contains("]"));
            if (optionalStatement)
            {
              statements[i] = statements[i].replace("[", "");
              statements[i] = statements[i].replace("]", "");
            }
            if (statements[i].contains(";"))
            {
              statements[i] = statements[i].replace("; ", ";");
              statements[i] = statements[i].replace("; ", ";");
              statements[i] = statements[i].replace(" ;", ";");
              statements[i] = statements[i].replace(" ;", ";");
              String[] semicolonStatements = statements[i].split(";");
              for (int j = 0; j < semicolonStatements.length; j++)
              {
                if (j != 0)
                {
                  semicolonStatements[j] = (start + " " + semicolonStatements[j]);
                  semicolonCounter++;
                }
                seperatStatements[(i + semicolonCounter)] = getSPARQLStatement(semicolonStatements[j], optionalStatement);
                System.out.println(seperatStatements[(i + semicolonCounter)]);
              }
            }
            else
            {
              seperatStatements[(i + semicolonCounter)] = getSPARQLStatement(statements[i], optionalStatement);
              System.out.println(seperatStatements[(i + semicolonCounter)]);
            }
          }
          System.out.println("________");
          for (String s : seperatStatements) {
            System.out.println(s);
          }
          System.out.println("________");
          
          SPARQLhelp = "";
          SPARQLhelp += "\"";
          SPARQLhelp += "SELECT";
          if (distinct) {
            SPARQLhelp += " DISTINCT";
          }
          SPARQLhelp += select;
          SPARQLhelp += "\" +\n\t\"";
          
          SPARQLhelp += "WHERE";
          SPARQLhelp += " { ";
          for (String statement : seperatStatements) {
            SPARQLhelp = SPARQLhelp + statement + " . ";
          }
          if ((filter.length() > 0) || (bFilterStatementExtra))
          {
            SPARQLhelp += "\" +\n\t\"";
            SPARQLhelp += "FILTER";
            SPARQLhelp += " (";
            if ((filter.length() > 0) && (bFilterStatementExtra)) {
              SPARQLhelp += " (";
            }
            if (filter.length() > 0) {
              SPARQLhelp += filter;
            }
            if ((filter.length() > 0) && (bFilterStatementExtra)) {
              SPARQLhelp += " )";
            }
            if (bFilterStatementExtra) {
              for (int extra = 0; extra < filterStatementExtra.size(); extra++)
              {
                if ((extra != 0) || (filter.length() > 0)) {
                  SPARQLhelp += " && ";
                }
                SPARQLhelp += (String)filterStatementExtra.get(extra);
              }
            }
            SPARQLhelp += ") ";
            SPARQLhelp += " . ";
          }
          for (String usedNot : usedNOTS)
          {
            SPARQLhelp += "\" +\n\t";
            SPARQLhelp += usedNot;
          }
          if (usedNOTS.isEmpty()) {
            SPARQLhelp += "}";
          } else {
            SPARQLhelp += " + \"}";
          }
          if (ending.length() == 0) {
            SPARQLhelp += "\"";
          } else {
            SPARQLhelp += "";
          }
          SPARQLhelp += " ";
          if (ending.length() != 0)
          {
            SPARQLhelp += ending;
            SPARQLhelp += "\"";
          }
          SPARQLhelp = SPARQLhelp.replace("  ", " ");
          SPARQLhelp = SPARQLhelp.replace("  ", " ");
          sUnion[iUnion] = SPARQLhelp;
          System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
          System.out.println(SPARQLhelp);
          System.out.println(">>>>>>>>>>>>>>>>>>>>>>");
        }
        sMinus[iMinus] = sUnion[0];
        for (int iUnion = 1; iUnion < sUnion.length; iUnion++)
        {
          int tmp3559_3557 = iMinus; String[] tmp3559_3556 = sMinus;tmp3559_3556[tmp3559_3557] = (tmp3559_3556[tmp3559_3557] + " + \n\t\"UNION \" + "); int 
            tmp3583_3581 = iMinus; String[] tmp3583_3580 = sMinus;tmp3583_3580[tmp3583_3581] = (tmp3583_3580[tmp3583_3581] + sUnion[iUnion]);
        }
      }
      sInter[iInter] = sMinus[0];
      for (int iMinus = 1; iMinus < sMinus.length; iMinus++)
      {
        int tmp3637_3636 = iInter; String[] tmp3637_3635 = sInter;tmp3637_3635[tmp3637_3636] = (tmp3637_3635[tmp3637_3636] + " + \n\t\"MINUS \" + "); int 
          tmp3660_3659 = iInter; String[] tmp3660_3658 = sInter;tmp3660_3658[tmp3660_3659] = (tmp3660_3658[tmp3660_3659] + sMinus[iMinus]);
      }
    }
    SPARQL = "String sparql = " + sInter[0];
    for (int iInter = 1; iInter < sInter.length; iInter++)
    {
      SPARQL += " + \n\t\"INTERSECT \" + ";
      SPARQL += sInter[iInter];
    }
    SPARQL += ";";
    System.out.println(SPARQL);
  }
  
  private static String getSPARQLStatement(String SERQLstatement, boolean optionalStatement)
  {
    System.out.println(SERQLstatement);
    LinkedList<String[]> replacers = new LinkedList();
    replacers.add(new String[] { new String("{\" + "), new String(" + \"}") });
    replacers.add(new String[] { new String("{<\" + "), new String(" + \">}") });
    for (String[] replace : replacers) {
      while ((SERQLstatement.contains(replace[0])) && (SERQLstatement.contains(replace[1])))
      {
        int begin = SERQLstatement.indexOf(replace[0]) + replace[0].length();
        int end = SERQLstatement.indexOf(replace[1]);
        String var = SERQLstatement.substring(begin, end);
        String oldText = SERQLstatement.substring(begin - replace[0].length(), end + replace[1].length());
        oldText = oldText.replace("{", "");
        oldText = oldText.replace("}", "");
        String newStatement = "?" + var + " = " + oldText;
        
        System.out.println(newStatement);
        if (filterStatementExtraHash.add(newStatement)) {
          filterStatementExtra.add(newStatement);
        }
        bFilterStatementExtra = true;
        SERQLstatement = SERQLstatement.replace(oldText, var);
      }
    }
    String[] partStatements = SERQLstatement.split(" ");
    start = partStatements[0];
    if (partStatements.length != 3) {
      return null;
    }
    for (int j = 0; j < partStatements.length; j++)
    {
      partStatements[j] = partStatements[j].replace("{", "");
      partStatements[j] = partStatements[j].replace("}", "");
      if (!partStatements[j].contains(":"))
      {
        allVariables.add(partStatements[j]);
        partStatements[j] = ("?" + partStatements[j]);
      }
    }
    if (optionalStatement) {
      return "\" +\n\t\"OPTIONAL{" + partStatements[0] + " " + partStatements[1] + " " + partStatements[2] + "}";
    }
    return partStatements[0] + " " + partStatements[1] + " " + partStatements[2];
  }
}

