package poweraqua.core.model.myrdfmodel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;

public class RDFPath
  implements Serializable
{
  private RDFProperty RDFProperty1;
  private RDFEntity RDFEntityReference;
  private RDFProperty RDFProperty2;
  private RDFEntityList KBAnswers;
  
  public RDFPath(RDFProperty RDFProperty1, RDFEntity RDFEntityReference, RDFProperty RDFProperty2)
  {
    this.RDFProperty1 = RDFProperty1;
    this.RDFProperty2 = RDFProperty2;
    this.RDFEntityReference = RDFEntityReference;
    this.KBAnswers = new RDFEntityList();
  }
  
  public RDFProperty getRDFProperty1()
  {
    return this.RDFProperty1;
  }
  
  public RDFEntity getRDFEntityReference()
  {
    return this.RDFEntityReference;
  }
  
  public RDFProperty getRDFProperty2()
  {
    return this.RDFProperty2;
  }
  
  public boolean sameAs(RDFPath path2)
  {
    if (getRDFEntityReference() == null) {
      return false;
    }
    if (path2.getRDFEntityReference() == null) {
      return false;
    }
    String term_uri = getRDFEntityReference().getURI();
    String prop1_uri = getRDFProperty1().getURI();
    String prop2_uri = getRDFProperty2().getURI();
    if ((term_uri.equals(path2.getRDFEntityReference().getURI())) && (prop1_uri.equals(path2.getRDFProperty1().getURI())) && (prop2_uri.equals(path2.getRDFProperty2().getURI()))) {
      return true;
    }
    return false;
  }
  
  public RDFPath isContainedIn(ArrayList<RDFPath> pathList)
  {
    for (RDFPath path : pathList) {
      if (sameAs(path)) {
        return path;
      }
    }
    return null;
  }
  
  public static ArrayList<RDFPath> mergePathLists(ArrayList<RDFPath> path1, ArrayList<RDFPath> path2)
  {
    for (RDFPath p2 : path2)
    {
      RDFPath p1 = p2.isContainedIn(path1);
      if (p1 == null) {
        path1.add(p2);
      } else {
        p1.getKBAnswers().addAllRDFEntity(p2.getKBAnswers());
      }
    }
    return path1;
  }
  
  public static ArrayList<RDFPath> mergePathLists(ArrayList<RDFPath> path1, RDFPath path2)
  {
    RDFPath p1 = path2.isContainedIn(path1);
    if (p1 == null)
    {
      path1.add(path2);
      return path1;
    }
    p1.getKBAnswers().addAllRDFEntity(path2.getKBAnswers());
    return path1;
  }
  
  public void print()
  {
    System.out.println("Reference entity " + getRDFEntityReference().getURI());
    System.out.println("First property " + getRDFProperty1().getURI());
    System.out.println("Second property " + getRDFProperty2().getURI());
  }
  
  public RDFEntityList getKBAnswers()
  {
    return this.KBAnswers;
  }
  
  public void setKBAnswers(RDFEntityList KBAnswers)
  {
    this.KBAnswers = KBAnswers;
  }
  
  public void setKBAnswers(RDFEntity KBAnswer)
  {
    this.KBAnswers = new RDFEntityList();
    this.KBAnswers.addRDFEntity(KBAnswer);
  }
}

