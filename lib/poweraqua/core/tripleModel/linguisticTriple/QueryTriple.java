package poweraqua.core.tripleModel.linguisticTriple;

import java.io.PrintStream;
import java.util.ArrayList;

public class QueryTriple
{
  private int typeQuestion;
  private ArrayList<String> queryTerm;
  private String secondTerm;
  private String thirdTerm;
  private String relation;
  public String relationCategory;
  private String relationFeature;
  private boolean relationPassive;
  private boolean coverage_criteria = true;
  private boolean queryTermCoverage = false;
  private boolean splitting = false;
  
  public QueryTriple(int typeQuestion, ArrayList<String> queryTerm, String secondTerm, String thirdTerm, String relation, String relationCategory, String relationFeature, boolean relationPassive)
    throws Exception
  {
    setTypeQuestion(typeQuestion);
    setQueryTerm(queryTerm);
    setSecondTerm(secondTerm);
    setThirdTerm(thirdTerm);
    setRelation(relation);
    this.relationCategory = relationCategory;
    this.relationFeature = relationFeature;
    this.relationPassive = relationPassive;
  }
  
  public QueryTriple(int typeQuestion, ArrayList<String> queryTerm, String secondTerm, String thirdTerm, String relation)
    throws Exception
  {
    setTypeQuestion(typeQuestion);
    setQueryTerm(queryTerm);
    setSecondTerm(secondTerm);
    setThirdTerm(thirdTerm);
    setRelation(relation);
    this.relationPassive = false;
  }
  
  public QueryTriple(int typeQuestion, String queryTerm, String secondTerm, String thirdTerm, String relation)
    throws Exception
  {
    setTypeQuestion(typeQuestion);
    setQueryTerm(queryTerm);
    setSecondTerm(secondTerm);
    setThirdTerm(thirdTerm);
    setRelation(relation);
    this.relationPassive = false;
  }
  
  public QueryTriple(int typeQuestion, String queryTerm, String secondTerm, String thirdTerm, String relation, String relationCategory, String relationFeature, boolean relationPassive)
    throws Exception
  {
    setTypeQuestion(typeQuestion);
    setQueryTerm(queryTerm);
    setSecondTerm(secondTerm);
    setThirdTerm(thirdTerm);
    setRelation(relation);
    this.relationCategory = relationCategory;
    this.relationFeature = relationFeature;
    this.relationPassive = relationPassive;
  }
  
  public QueryTriple(int typeQuestion, ArrayList<String> queryTerm, String relation, String secondTerm)
    throws Exception
  {
    setTypeQuestion(typeQuestion);
    setQueryTerm(queryTerm);
    setSecondTerm(secondTerm);
    setRelation(relation);
    this.relationPassive = false;
  }
  
  public QueryTriple(int typeQuestion, String queryTerm, String relation, String secondTerm)
    throws Exception
  {
    setTypeQuestion(typeQuestion);
    setQueryTerm(queryTerm);
    setSecondTerm(secondTerm);
    setRelation(relation);
    this.relationPassive = false;
  }
  
  public QueryTriple cloneTriple()
    throws Exception
  {
    return new QueryTriple(this.typeQuestion, this.queryTerm, this.secondTerm, this.thirdTerm, this.relation, this.relationCategory, this.relationFeature, this.relationPassive);
  }
  
  public int getTypeQuestion()
  {
    return this.typeQuestion;
  }
  
  public ArrayList<String> getQueryTerm()
  {
    return this.queryTerm;
  }
  
  public String getSecondTerm()
  {
    return this.secondTerm;
  }
  
  public String getThirdTerm()
  {
    return this.thirdTerm;
  }
  
  public String getRelation()
  {
    return this.relation;
  }
  
  public boolean isRelationPassive()
  {
    return this.relationPassive;
  }
  
  public void setTypeQuestion(int typeQuestion)
  {
    this.typeQuestion = typeQuestion;
  }
  
  public void setQueryTerm(String queryTerm)
  {
    ArrayList<String> aux = new ArrayList();
    aux.add(queryTerm);
    this.queryTerm = aux;
  }
  
  public void setQueryTerm(ArrayList<String> queryTerm)
  {
    this.queryTerm = queryTerm;
  }
  
  public void setSecondTerm(String secondTerm)
  {
    this.secondTerm = secondTerm;
  }
  
  public void setThirdTerm(String thirdTerm)
  {
    this.thirdTerm = thirdTerm;
  }
  
  public void setRelation(String relation)
  {
    this.relation = relation;
  }
  
  public void print()
  {
    System.out.print("Query term (" + getTypeQuestion() + ") : ");
    for (String queryTerm : getQueryTerm()) {
      System.out.print(queryTerm + " -- ");
    }
    System.out.print(" Relation: " + getRelation());
    System.out.print(" Second term: " + getSecondTerm() + "\n");
  }
  
  public boolean isCoverage_criteria()
  {
    return this.coverage_criteria;
  }
  
  public void setCoverage_criteria(boolean coverage_criteria)
  {
    this.coverage_criteria = coverage_criteria;
  }
  
  public String getRelationFeature()
  {
    return this.relationFeature;
  }
  
  public void setRelationFeature(String relationFeature)
  {
    this.relationFeature = relationFeature;
  }
  
  public boolean isQueryTermCoverage()
  {
    return this.queryTermCoverage;
  }
  
  public void setQueryTermCoverage(boolean queryTermCoverage)
  {
    this.queryTermCoverage = queryTermCoverage;
  }
  
  public boolean isSplitting()
  {
    return this.splitting;
  }
  
  public void setSplitting(boolean splitting)
  {
    this.splitting = splitting;
  }
}

