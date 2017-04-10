package poweraqua.core.tripleModel.ontologyTriple;

import java.io.PrintStream;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.powermap.elementPhase.SearchSemanticResult;

public class OntoTriple
{
  private int typeQuestion = -1;
  private SearchSemanticResult firstTerm;
  private SearchSemanticResult relation;
  private SearchSemanticResult secondTerm;
  private boolean IS_A_RELATION = false;
  private boolean isPassive;
  
  public OntoTriple(int typeQuestion)
  {
    this.typeQuestion = typeQuestion;
  }
  
  public OntoTriple(SearchSemanticResult firstTerm, SearchSemanticResult relation, SearchSemanticResult secondTerm)
  {
    this.firstTerm = firstTerm;
    this.relation = relation;
    this.secondTerm = secondTerm;
  }
  
  public OntoTriple(SearchSemanticResult firstTerm, SearchSemanticResult secondTerm, boolean IS_A_RELATION)
  {
    this.firstTerm = firstTerm;
    this.IS_A_RELATION = IS_A_RELATION;
    this.secondTerm = secondTerm;
  }
  
  public int getTypeQuestion()
  {
    return this.typeQuestion;
  }
  
  public void setTypeQuestion(int typeQuestion)
  {
    this.typeQuestion = typeQuestion;
  }
  
  public SearchSemanticResult getFirstTerm()
  {
    return this.firstTerm;
  }
  
  public void setFirstTerm(SearchSemanticResult firstTerm)
  {
    this.firstTerm = firstTerm;
  }
  
  public SearchSemanticResult getRelation()
  {
    return this.relation;
  }
  
  public void setRelation(SearchSemanticResult relation)
  {
    this.relation = relation;
  }
  
  public SearchSemanticResult getSecondTerm()
  {
    return this.secondTerm;
  }
  
  public void setSecondTerm(SearchSemanticResult secondTerm)
  {
    this.secondTerm = secondTerm;
  }
  
  public boolean isIsPassive()
  {
    return this.isPassive;
  }
  
  public void setIsPassive(boolean isPassive)
  {
    this.isPassive = isPassive;
  }
  
  public void print()
  {
    if (getTypeQuestion() == 1)
    {
      System.out.println("Description of: " + getSecondTerm().getEntity().getURI() + " ( " + getSecondTerm().getSemanticRelation() + " ) ");
    }
    else
    {
      System.out.println("OntoTriple: < " + getFirstTerm().getEntity().getURI() + "(" + getFirstTerm().getEntity().getLabel() + ", " + getFirstTerm().getEntity().getType() + ")" + " , ");
      
      System.out.print(((isIS_A_RELATION() == true) && (getRelation() == null) ? "IS_A" : getRelation().getEntity().getURI()) + " , ");
      System.out.print(getSecondTerm().getEntity().getURI());
      System.out.print("(" + getSecondTerm().getEntity().getLabel() + ", " + getSecondTerm().getEntity().getType() + ", " + getSecondTerm().getSemanticRelation() + ">)");
    }
  }
  
  public void printLabel()
  {
    if (getTypeQuestion() == 1)
    {
      System.out.println("Description of: " + getSecondTerm().getEntity().getLabel());
    }
    else
    {
      System.out.println("" + getFirstTerm().getEntity().getLabel() + " | ");
      System.out.println(getRelation() == null ? "IS_A" : getRelation().getEntity().getLabel());
      System.out.println("| " + getSecondTerm().getEntity().getLabel());
    }
  }
  
  public boolean isIS_A_RELATION()
  {
    return this.IS_A_RELATION;
  }
}

