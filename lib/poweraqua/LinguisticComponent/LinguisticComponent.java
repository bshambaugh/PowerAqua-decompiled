package poweraqua.LinguisticComponent;

import gate.AnnotationSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import poweraqua.LinguisticComponent.QueryClassify.QueryClassifyConfig;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;

public class LinguisticComponent
  implements GateQueryTypes
{
  public GateChunker gateChunker;
  private String question;
  private ArrayList<QueryTriple> queryTriples;
  public int typeQuestion;
  public IEAnnotation[] PATTERN_ann;
  public IEAnnotation[] VG_ann;
  public IEAnnotation[] NP_ann;
  public IEAnnotation[] REL_ann;
  public IEAnnotation[] QU_ann;
  public IEAnnotation[] NP_ann_unprocessed;
  private String localdir;
  public static String[] non_informative_terms = { "main", "type", "types", "kind", "kinds" };
  
  public LinguisticComponent()
    throws Exception
  {
    this.gateChunker = new GateChunker();
    this.queryTriples = new ArrayList();
    System.out.println("LinguisticComponent initialized");
  }
  
  public LinguisticComponent(String localdir)
    throws Exception
  {
    this.gateChunker = new GateChunker(localdir);
    this.queryTriples = new ArrayList();
    this.localdir = localdir;
    System.out.println("LinguisticComponent initialized");
  }
  
  public String cleanQuestion(String question)
  {
    int indexquotes_ini = question.indexOf("\"");
    int indexquotes_end = question.lastIndexOf("\"");
    if (indexquotes_ini == -1)
    {
      indexquotes_ini = question.indexOf("'");
      indexquotes_end = question.lastIndexOf("'");
    }
    int index = question.indexOf(".");
    if ((index > 0) && ((index <= indexquotes_ini) || (index >= indexquotes_end)))
    {
      question = question.replaceAll("\\.", "");
      System.out.println("new question after cleaning it " + question);
    }
    index = question.indexOf("'");
    int indexCompound = question.lastIndexOf("'");
    if ((index > 0) && (index == indexCompound))
    {
      question = question.replaceAll("'", "");
      System.out.println("new question after cleaning it " + question);
    }
    index = question.indexOf("_");
    if ((index > 0) && ((index <= indexquotes_ini) || (index >= indexquotes_end)))
    {
      question = question.replaceAll("\\_", " ");
      System.out.println("new question after cleaning it " + question);
    }
    index = question.indexOf("-");
    if ((index > 0) && ((index <= indexquotes_ini) || (index >= indexquotes_end)))
    {
      question = question.replaceAll("\\-", " ");
      System.out.println("new question after cleaning it " + question);
    }
    return question.trim();
  }
  
  public void parseQuestion(String quest)
    throws Exception
  {
    this.question = cleanQuestion(quest);
    
    AnnotationSet ann = this.gateChunker.ParseQuestion(getQuestion());
    this.PATTERN_ann = this.gateChunker.GetAnnotationsType(getQuestion(), "PATTERN", ann);
    this.QU_ann = this.gateChunker.GetAnnotationsType(getQuestion(), "QU", ann);
    this.NP_ann = this.gateChunker.GetAnnotationsType(getQuestion(), "NP", ann);
    this.NP_ann_unprocessed = this.gateChunker.GetAnnotationsType(getQuestion(), "NP", ann);
    this.REL_ann = this.gateChunker.GetAnnotationsType(getQuestion(), "REL", ann);
    this.VG_ann = this.gateChunker.GetAnnotationsType(getQuestion(), "VG", ann);
    
    DeleteNPOverlaps();
    DeleteVGOutOfPatterns();
    CleaningRelation();
    if (this.PATTERN_ann != null)
    {
      if (this.PATTERN_ann.length == 1)
      {
        String category = this.PATTERN_ann[0].getFeature("category");
        this.typeQuestion = getCategory(category);
      }
      else if (this.PATTERN_ann.length > 1)
      {
        this.typeQuestion = 25;
      }
      CreateQueryTriples();
    }
  }
  
  private int getCategory(String category)
  {
    category = category.trim();
    int typeQuestion = -1;
    if (category.equals("description")) {
      typeQuestion = 1;
    }
    if (category.equals("wh-genericterm")) {
      typeQuestion = 2;
    }
    if (category.equals("wh-unknterm")) {
      typeQuestion = 3;
    }
    if (category.equals("wh-unknrel")) {
      typeQuestion = 4;
    }
    if (category.equals("affirm-neg")) {
      typeQuestion = 5;
    }
    if (category.equals("affirm-neg-pseudorel")) {
      typeQuestion = 6;
    }
    if (category.equals("how-long")) {
      typeQuestion = 7;
    }
    if (category.equals("QU-whe")) {
      typeQuestion = 8;
    }
    if (category.equals("affirm-neg-3term")) {
      typeQuestion = 9;
    }
    if (category.equals("affirm-neg-1termclause")) {
      typeQuestion = 10;
    }
    if (category.equals("wh-3unknrel")) {
      typeQuestion = 11;
    }
    if (category.equals("wh-generic-1termclause")) {
      typeQuestion = 12;
    }
    if (category.equals("wh-unknterm-2clause")) {
      typeQuestion = 13;
    }
    if (category.equals("wh-3term")) {
      typeQuestion = 14;
    }
    if (category.equals("wh-3termclause")) {
      typeQuestion = 15;
    }
    if (category.equals("wh-3term1clause")) {
      typeQuestion = 16;
    }
    if (category.equals("wh-4term")) {
      typeQuestion = 17;
    }
    if (category.equals("wh-comb-and")) {
      typeQuestion = 18;
    }
    if (category.equals("wh-comb-or")) {
      typeQuestion = 19;
    }
    if (category.equals("wh-comb-cond")) {
      typeQuestion = 20;
    }
    if (category.equals("wh-comb-cond-relNN")) {
      typeQuestion = 21;
    }
    if (category.equals("affirm-neg-whclause")) {
      typeQuestion = 22;
    }
    if (category.equals("wh-generic-whclause")) {
      typeQuestion = 23;
    }
    if (category.equals("wh-unknown")) {
      typeQuestion = 24;
    }
    return typeQuestion;
  }
  
  public void DeleteVGOutOfPatterns()
  {
    int pattern_begin = this.PATTERN_ann[0].getOffset_begin();
    int pattern_end = this.PATTERN_ann[(this.PATTERN_ann.length - 1)].getOffset_end();
    
    Vector v = new Vector();
    if (this.REL_ann != null)
    {
      for (int i = 0; i < this.REL_ann.length; i++)
      {
        boolean valid = this.REL_ann[i].validateAnnotation(pattern_begin, pattern_end);
        if (valid) {
          v.add(this.REL_ann[i]);
        }
      }
      IEAnnotation[] nuevo = new IEAnnotation[v.size()];
      v.copyInto(nuevo);
      this.REL_ann = nuevo;
    }
  }
  
  public void DeleteNPOverlaps()
  {
    Vector v = new Vector();
    
    int pattern_begin = this.PATTERN_ann[0].getOffset_begin();
    int pattern_end = this.PATTERN_ann[(this.PATTERN_ann.length - 1)].getOffset_end();
    if ((this.NP_ann != null) && (this.REL_ann != null))
    {
      for (int i = 0; i < this.NP_ann.length; i++)
      {
        IEAnnotation overlap = this.NP_ann[i].DeleteOverlaps(this.REL_ann, pattern_begin, pattern_end);
        if (overlap != null) {
          v.add(this.NP_ann[i]);
        }
      }
      if (v.size() == 0)
      {
        this.NP_ann = null;
      }
      else
      {
        IEAnnotation[] nuevo = new IEAnnotation[v.size()];
        v.copyInto(nuevo);
        this.NP_ann = nuevo;
      }
    }
    if ((this.QU_ann != null) && (this.NP_ann != null))
    {
      v = new Vector();
      for (int i = 0; i < this.NP_ann.length; i++)
      {
        IEAnnotation overlap = this.NP_ann[i].DeleteOverlaps(this.QU_ann, pattern_begin, pattern_end);
        if (overlap != null) {
          v.add(this.NP_ann[i]);
        }
      }
      if (v.size() == 0)
      {
        this.NP_ann = null;
      }
      else
      {
        IEAnnotation[] nuevo = new IEAnnotation[v.size()];
        v.copyInto(nuevo);
        this.NP_ann = nuevo;
      }
    }
    if ((this.QU_ann != null) && (this.REL_ann != null))
    {
      v = new Vector();
      for (int i = 0; i < this.REL_ann.length; i++)
      {
        IEAnnotation overlap = this.REL_ann[i].DeleteOverlaps(this.QU_ann, pattern_begin, pattern_end);
        if (overlap != null) {
          v.add(this.REL_ann[i]);
        }
      }
      if (v.size() == 0)
      {
        this.REL_ann = null;
      }
      else
      {
        IEAnnotation[] nuevo = new IEAnnotation[v.size()];
        v.copyInto(nuevo);
        this.REL_ann = nuevo;
      }
    }
  }
  
  public void CleaningAuxiliars()
  {
    if ((this.REL_ann != null) && (this.REL_ann.length > 0))
    {
      Vector DT = new Vector(2);
      DT.add("is");DT.add("are");DT.add("do");DT.add("does");DT.add("did");
      DT.add("was");DT.add("were");DT.add("have");DT.add("has");
      for (int i = 0; i < this.REL_ann.length; i++)
      {
        String aux = this.REL_ann[i].getSentence();
        int index_ini = aux.indexOf(" ");
        if ((index_ini > 0) && 
          (DT.contains(aux.substring(0, index_ini).toLowerCase().trim())))
        {
          this.REL_ann[i].setSentence(aux.substring(index_ini + 1, aux.length()));
          this.REL_ann[i].setOffset(this.REL_ann[i].getOffset_begin() + index_ini + 1, this.REL_ann[i].getOffset_end());
        }
        int index_end = aux.lastIndexOf(" ");
        if ((index_end > 0) && 
          (DT.contains(aux.substring(index_end, aux.length()).toLowerCase().trim())))
        {
          this.REL_ann[i].setSentence(aux.substring(0, index_end));
          this.REL_ann[i].setOffset(this.REL_ann[i].getOffset_begin(), this.REL_ann[i].getOffset_begin() + index_end);
        }
      }
    }
    CleaningRelation();
  }
  
  public void CleaningRelation()
  {
    ArrayList<IEAnnotation> new_REL_Ann = new ArrayList();
    if ((this.REL_ann != null) && (this.REL_ann.length > 0))
    {
      Vector IN = new Vector(10);
      IN.add("in");IN.add("about");IN.add("of");IN.add("on");
      IN.add("at");IN.add("for");IN.add("by");IN.add("with");
      IN.add("from");IN.add("to");IN.add("that");IN.add("into");
      Vector DT = new Vector(6);
      DT.add("the");DT.add("does");DT.add("do");DT.add("did");
      DT.add("a");DT.add("an");DT.add("been");
      for (int i = 0; i < this.REL_ann.length; i++)
      {
        String aux = this.REL_ann[i].getSentence();
        
        int index_ini = aux.indexOf(" ");
        if ((index_ini > 0) && 
          (DT.contains(aux.substring(0, index_ini).toLowerCase().trim())))
        {
          this.REL_ann[i].setSentence(aux.substring(index_ini + 1, aux.length()));
          this.REL_ann[i].setOffset(this.REL_ann[i].getOffset_begin() + index_ini + 1, this.REL_ann[i].getOffset_end());
        }
        aux = this.REL_ann[i].getSentence();
        
        int index_end = aux.lastIndexOf(" ");
        if ((index_end > 0) && 
          (IN.contains(aux.substring(index_end, aux.length()).toLowerCase().trim())))
        {
          this.REL_ann[i].setSentence(aux.substring(0, index_end));
          this.REL_ann[i].setOffset(this.REL_ann[i].getOffset_begin(), this.REL_ann[i].getOffset_end() - (aux.length() - index_end));
        }
      }
    }
  }
  
  public String CleaningAuxiliars(String relation)
  {
    Vector DT = new Vector(2);
    DT.add("is");DT.add("are");
    
    int index_ini = relation.indexOf(" ");
    if ((index_ini > 0) && 
      (DT.contains(relation.substring(0, index_ini).toLowerCase().trim()))) {
      relation = relation.substring(index_ini + 1, relation.length());
    }
    int index_end = relation.lastIndexOf(" ");
    if ((index_end > 0) && 
      (DT.contains(relation.substring(index_end, relation.length()).toLowerCase().trim()))) {
      relation = relation.substring(0, index_end);
    }
    relation = CleaningRelation(relation);
    return relation;
  }
  
  public String CleaningRelation(String relation)
  {
    Vector IN = new Vector(9);
    IN.add("in");IN.add("about");IN.add("of");IN.add("on");
    IN.add("at");IN.add("for");IN.add("by");IN.add("with");
    IN.add("from");IN.add("that");
    Vector DT = new Vector(6);
    DT.add("the");DT.add("does");DT.add("do");DT.add("did");
    DT.add("a");DT.add("an");
    int index_ini = relation.indexOf(" ");
    if ((index_ini > 0) && 
      (DT.contains(relation.substring(0, index_ini).toLowerCase().trim()))) {
      relation = relation.substring(index_ini + 1, relation.length());
    }
    int index_end = relation.lastIndexOf(" ");
    if ((index_end > 0) && 
      (IN.contains(relation.substring(index_end, relation.length()).toLowerCase().trim()))) {
      relation = relation.substring(0, index_end);
    }
    return relation;
  }
  
  public void PostTreatmentRel()
  {
    if (this.REL_ann != null)
    {
      if ((this.REL_ann.length > 1) && 
        (this.REL_ann.length == 2))
      {
        String aux_stand = this.REL_ann[0].getSentence().toLowerCase().trim();
        if ((aux_stand.equals("is")) || (aux_stand.equals("are")) || (aux_stand.equals("was")) || (aux_stand.equals("were")) || (aux_stand.equals("has")) || (aux_stand.equals("have")) || (aux_stand.equals("do")) || (aux_stand.equals("does")) || (aux_stand.equals("did")))
        {
          IEAnnotation[] aux = new IEAnnotation[1];
          String sentence = this.REL_ann[0].getSentence();
          
          sentence = sentence.concat(" " + this.REL_ann[1].getSentence());
          int begin = this.REL_ann[0].getOffset_begin();
          int fin = this.REL_ann[(this.REL_ann.length - 1)].getOffset_end();
          
          aux[0] = new IEAnnotation(begin, fin, sentence, this.REL_ann[1].getFeatures());
          this.REL_ann = aux;
        }
        else if ((aux_stand.equalsIgnoreCase("is anybody")) || (aux_stand.equalsIgnoreCase("does anybody")) || (aux_stand.equalsIgnoreCase("anybody")))
        {
          IEAnnotation[] aux = new IEAnnotation[1];
          aux[0] = this.REL_ann[1];
          this.REL_ann = aux;
        }
      }
      CleaningAuxiliars();
    }
  }
  
  public boolean IsPassiveVoice()
  {
    boolean voice = false;
    if (this.VG_ann != null) {
      for (int i = 0; i < this.VG_ann.length; i++)
      {
        String value = this.VG_ann[i].getFeature("voice");
        if (value.equals("passive"))
        {
          System.out.println("RULE VOICE " + value);
          voice = true;
        }
      }
    }
    return voice;
  }
  
  public boolean IsPassiveVoice(int begin, int end)
  {
    boolean voice = false;
    if (this.VG_ann != null) {
      for (int i = 0; i < this.VG_ann.length; i++) {
        if ((this.VG_ann[i].getOffset_begin() >= begin) && (this.VG_ann[i].getOffset_end() <= end))
        {
          String value = this.VG_ann[i].getFeature("voice");
          if (value.equals("passive"))
          {
            System.out.println("RULE VOICE " + value);
            voice = true;
          }
        }
      }
    }
    return voice;
  }
  
  public boolean IsMainVerb(int begin, int end)
  {
    boolean main = false;
    if (this.VG_ann != null) {
      for (int i = 0; i < this.VG_ann.length; i++) {
        if ((this.VG_ann[i].getOffset_end() > begin) && (this.VG_ann[i].getOffset_end() <= end))
        {
          String value = this.VG_ann[i].getFeature("type");
          if ((value.equalsIgnoreCase("FVG")) || (value.equalsIgnoreCase("MODAL")))
          {
            System.out.println(this.VG_ann[i].getSentence() + " is the MAIN verb");
            return true;
          }
        }
      }
    }
    return main;
  }
  
  public void CreateQueryTriples()
    throws Exception
  {
    IEAnnotation[] tmpNP_ann;
    IEAnnotation[] tmpNP_ann;
    if (this.NP_ann != null) {
      tmpNP_ann = (IEAnnotation[])this.NP_ann.clone();
    } else {
      tmpNP_ann = null;
    }
    IEAnnotation[] tmpREL_ann;
    IEAnnotation[] tmpREL_ann;
    if (this.REL_ann != null) {
      tmpREL_ann = (IEAnnotation[])this.REL_ann.clone();
    } else {
      tmpREL_ann = null;
    }
    IEAnnotation[] tmpQU_ann;
    IEAnnotation[] tmpQU_ann;
    if (this.QU_ann != null) {
      tmpQU_ann = (IEAnnotation[])this.QU_ann.clone();
    } else {
      tmpQU_ann = null;
    }
    IEAnnotation[] tmpVG_ann;
    IEAnnotation[] tmpVG_ann;
    if (this.VG_ann != null) {
      tmpVG_ann = (IEAnnotation[])this.VG_ann.clone();
    } else {
      tmpVG_ann = null;
    }
    ArrayList<QueryTriple> lista = new ArrayList();
    try
    {
      if (this.typeQuestion == 25)
      {
        int pattern_begin = this.PATTERN_ann[0].getOffset_begin();
        int pattern_end = this.PATTERN_ann[0].getOffset_end();
        this.NP_ann = GetAnnotationsforPattern(tmpNP_ann, pattern_begin, pattern_end);
        this.REL_ann = GetAnnotationsforPattern(tmpREL_ann, pattern_begin, pattern_end);
        this.VG_ann = GetAnnotationsforPattern(tmpVG_ann, pattern_begin, pattern_end);
        this.QU_ann = GetAnnotationsforPattern(tmpQU_ann, pattern_begin, pattern_end);
        String category1 = this.PATTERN_ann[0].getFeature("category");
        int typeQuestion1 = getCategory(category1);
        lista = CreateQueryTriples_Categories(typeQuestion1);
        String category2 = this.PATTERN_ann[1].getFeature("category");
        int typeQuestion2 = getCategory(category2);
        pattern_begin = this.PATTERN_ann[1].getOffset_begin();
        pattern_end = this.PATTERN_ann[1].getOffset_end();
        this.NP_ann = GetAnnotationsforPattern(tmpNP_ann, pattern_begin, pattern_end);
        
        this.REL_ann = GetAnnotationsforPattern(tmpREL_ann, pattern_begin, pattern_end);
        this.VG_ann = GetAnnotationsforPattern(tmpVG_ann, pattern_begin, pattern_end);
        this.QU_ann = GetAnnotationsforPattern(tmpQU_ann, pattern_begin, pattern_end);
        ArrayList<QueryTriple> temp = CreateQueryTriples_Categories(typeQuestion2);
        this.NP_ann = tmpNP_ann;
        this.REL_ann = tmpREL_ann;
        this.QU_ann = tmpQU_ann;
        this.VG_ann = tmpVG_ann;
        if (temp != null) {
          for (int i = 0; i < temp.size(); i++) {
            lista.add(temp.get(i));
          }
        }
        lista = AnalyzeQueryTriples_Pattern2(lista);
      }
      else
      {
        lista = CreateQueryTriples_Categories(this.typeQuestion);
      }
      this.queryTriples = lista;
    }
    catch (Exception ex)
    {
      System.out.println("Query not classified");
      ex.printStackTrace();
      this.queryTriples = CreateUnclassifiedQueryTriples();
    }
  }
  
  private ArrayList<QueryTriple> AnalyzeQueryTriples_Pattern2(ArrayList<QueryTriple> queryTriples)
  {
    ArrayList<QueryTriple> lista = new ArrayList();
    int category1 = ((QueryTriple)queryTriples.get(0)).getTypeQuestion();
    int category2 = ((QueryTriple)queryTriples.get(1)).getTypeQuestion();
    try
    {
      if ((category1 == 1) && (category2 == 1))
      {
        System.out.println("Merging 2 patterns into one category WH_UNKNREL");
        int category3 = 4;
        QueryTriple triple = new QueryTriple(category3, ((QueryTriple)queryTriples.get(0)).getSecondTerm(), ((QueryTriple)queryTriples.get(1)).getSecondTerm(), null, null);
        lista.add(triple);
        return lista;
      }
      if ((category1 == 1) && (category2 == 4))
      {
        System.out.println("Merging 2 patterns into one category WH_GENERICTERM");
        int category3 = 2;
        QueryTriple triple = new QueryTriple(category3, ((QueryTriple)queryTriples.get(0)).getSecondTerm(), ((QueryTriple)queryTriples.get(1)).getSecondTerm(), null, (String)((QueryTriple)queryTriples.get(1)).getQueryTerm().get(0));
        lista.add(triple);
        return lista;
      }
      if (((category1 == 2) || (category1 == 3)) && (category2 == 3))
      {
        System.out.println("Merging 2 patterns into a WH_COMB_COND");
        
        ((QueryTriple)queryTriples.get(0)).setTypeQuestion(20);
      }
      else if ((category1 == 4) && (category2 == 3))
      {
        ((QueryTriple)queryTriples.get(1)).setTypeQuestion(2);
        ((QueryTriple)queryTriples.get(1)).setQueryTerm(((QueryTriple)queryTriples.get(0)).getSecondTerm());
      }
      else if ((category2 == 4) || (category2 == 5))
      {
        String linkTerm;
        String linkTerm;
        if (category1 == 4) {
          linkTerm = (String)((QueryTriple)queryTriples.get(0)).getQueryTerm().get(0);
        } else {
          linkTerm = ((QueryTriple)queryTriples.get(0)).getSecondTerm();
        }
        ((QueryTriple)queryTriples.get(1)).setTypeQuestion(2);
        ((QueryTriple)queryTriples.get(1)).setRelation((String)((QueryTriple)queryTriples.get(1)).getQueryTerm().get(0));
        ((QueryTriple)queryTriples.get(1)).setQueryTerm(linkTerm);
      }
      else if (category2 == 1)
      {
        String linkTerm = ((QueryTriple)queryTriples.get(0)).getSecondTerm();
        ((QueryTriple)queryTriples.get(1)).setTypeQuestion(4);
        ((QueryTriple)queryTriples.get(1)).setQueryTerm(linkTerm);
      }
    }
    catch (Exception ex)
    {
      Logger.getLogger(LinguisticComponent.class.getName()).log(Level.SEVERE, null, ex);
    }
    return queryTriples;
  }
  
  private ArrayList<QueryTriple> CreateUnclassifiedQueryTriples()
    throws Exception
  {
    ArrayList<QueryTriple> triples = new ArrayList();
    System.out.println("UNCLASIFIED QUERY.");
    try
    {
      if ((this.QU_ann.length == 1) && (this.NP_ann_unprocessed.length == 2))
      {
        String rule = this.QU_ann[0].getRule();
        if ((rule.equals("QU-listClass")) || (rule.equals("QU-what-class")) || (rule.equals("QU-whichClass")) || (rule.equals("QU-howmanyClass")))
        {
          String firstTerm = PrepareWord(this.NP_ann_unprocessed[0].getSentence());
          String secondTerm = PrepareWord(this.NP_ann_unprocessed[1].getSentence());
          ArrayList<String> terms = GetQueryTerm(this.QU_ann[0]);
          String queryterm = (String)terms.get(0);
          if ((queryterm.equals(firstTerm)) || (firstTerm.contains(queryterm)))
          {
            System.out.println("Creating an unclassified query triple <" + queryterm + ", ?, " + this.NP_ann_unprocessed[1].getSentence() + ">");
            triples.add(new QueryTriple(27, queryterm, null, this.NP_ann_unprocessed[1].getSentence()));
            return triples;
          }
          if (queryterm.equals(secondTerm))
          {
            System.out.println("Creating an unclassified query triple <" + this.NP_ann_unprocessed[1].getSentence() + ", ?, " + this.NP_ann_unprocessed[0].getSentence() + ">");
            triples.add(new QueryTriple(27, this.NP_ann_unprocessed[1].getSentence(), null, this.NP_ann_unprocessed[0].getSentence()));
            return triples;
          }
        }
        else if ((rule.equals("QU-who-what")) && (this.QU_ann[0].getSentence().equalsIgnoreCase("who")))
        {
          ArrayList<String> terms = GetQueryTerm(this.QU_ann[0]);
          String relation = PrepareWord(this.NP_ann_unprocessed[0].getSentence());
          String secondTerm = PrepareWord(this.NP_ann_unprocessed[1].getSentence());
          System.out.println("Creating an unclassified query triple <" + terms + " , " + relation + ", " + secondTerm + ">");
          triples.add(new QueryTriple(27, terms, relation, secondTerm));
          return triples;
        }
      }
      if (this.NP_ann_unprocessed.length == 2)
      {
        System.out.println("Creating an unclassified query triple <" + this.NP_ann_unprocessed[0].getSentence() + ", ?, " + this.NP_ann_unprocessed[1].getSentence() + ">");
        triples.add(new QueryTriple(27, this.NP_ann_unprocessed[0].getSentence(), null, this.NP_ann_unprocessed[1].getSentence()));
      }
      else if (this.NP_ann_unprocessed.length == 3)
      {
        triples.add(new QueryTriple(28, this.NP_ann_unprocessed[0].getSentence(), this.NP_ann_unprocessed[1].getSentence(), this.NP_ann_unprocessed[2].getSentence(), null));
      }
      else if ((this.NP_ann_unprocessed.length == 1) && (this.VG_ann == null))
      {
        System.out.println("Creating an unclassified query triple <" + this.NP_ann_unprocessed[0].getSentence() + "" + ">");
        triples.add(new QueryTriple(1, "what_is", null, this.NP_ann_unprocessed[0].getSentence()));
      }
      else if ((this.NP_ann_unprocessed.length == 1) && (this.VG_ann.length == 2))
      {
        System.out.println("Creating an unclassified query triple <" + this.NP_ann_unprocessed[0].getSentence() + ", " + this.VG_ann[0].getSentence() + ", " + this.VG_ann[1].getSentence() + ">");
        triples.add(new QueryTriple(27, this.NP_ann_unprocessed[0].getSentence(), null, this.NP_ann_unprocessed[1].getSentence()));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return triples;
  }
  
  private ArrayList<QueryTriple> CreateQueryTriples_Categories(int typeQuestion)
    throws Exception
  {
    ArrayList v = new ArrayList();
    String secondTerm;
    ArrayList<String> queryTerm;
    String relationFeature;
    QueryTriple triple;
    String thirdTerm;
    String secondTerm;
    String relation;
    String relationCategory;
    boolean relationPassive;
    QueryTriple triple;
    String rule;
    String relation;
    QueryTriple triple;
    QueryTriple triple;
    String relation;
    ArrayList<String> queryTerm;
    String secondTerm;
    QueryTriple triple;
    QueryTriple triple;
    ArrayList<String> queryTerm;
    String secondTerm;
    String thirdTerm;
    String relationFeature;
    String relationCategory;
    boolean relationPassive;
    QueryTriple triple;
    String relation;
    String relationCategory;
    ArrayList<String> queryTerm;
    String secondTerm;
    String thirdTerm;
    QueryTriple triple;
    QueryTriple triple2;
    String relation;
    String relation2;
    String relationCategory;
    String relationCategory2;
    boolean relationPassive;
    boolean relationPassive2;
    ArrayList<String> queryTerm;
    String secondTerm;
    String thirdTerm;
    String relationFeature;
    QueryTriple triple;
    QueryTriple triple2;
    switch (typeQuestion)
    {
    case 1: 
      String relationFeature;
      if (this.QU_ann == null)
      {
        String secondTerm = PrepareWord(this.NP_ann[0].getSentence());
        ArrayList<String> queryTerm = new ArrayList();
        queryTerm.add("what_is");
        relationFeature = null;
      }
      else
      {
        String relationFeature;
        if (this.QU_ann.length == 0)
        {
          String secondTerm = PrepareWord(this.NP_ann[0].getSentence());
          ArrayList<String> queryTerm = new ArrayList();
          queryTerm.add("what_is");
          relationFeature = null;
        }
        else
        {
          String rule = this.QU_ann[0].getRule();
          String secondTerm;
          if ((rule.equals("QU-listClass")) || (rule.equals("QU-whichClass")) || (rule.equals("QU-howmanyClass")))
          {
            ArrayList<String> terms = GetQueryTerm(this.QU_ann[0]);
            secondTerm = (String)terms.get(0);
          }
          else
          {
            secondTerm = PrepareWord(this.NP_ann[0].getSentence());
          }
          queryTerm = new ArrayList();
          queryTerm.add("what_is");
          relationFeature = getRelationFeature(this.QU_ann[0]);
          if ((relationFeature != null) && 
            (relationFeature.equals("who"))) {
            queryTerm = GetQueryTerm(this.QU_ann[0]);
          }
        }
      }
      triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, null, null, relationFeature, false);
      
      v.add(triple);
      break;
    case 2: 
      PostTreatmentRel();
      thirdTerm = null;
      queryTerm = null;
      if ((this.NP_ann.length == 2) && (this.REL_ann != null))
      {
        queryTerm = new ArrayList();
        queryTerm.add(PrepareWord(this.NP_ann[0].getSentence()));
        secondTerm = PrepareWord(this.NP_ann[1].getSentence());
      }
      else
      {
        queryTerm = GetQueryTerm(this.QU_ann[0]);
        secondTerm = PrepareWord(this.NP_ann[0].getSentence());
      }
      relationFeature = getRelationFeature(this.QU_ann[0]);
      boolean relationPassive;
      if ((this.REL_ann == null) && (this.NP_ann.length == 2))
      {
        String relation = this.NP_ann[1].getSentence();
        String relationCategory = "REL_NN";
        relationPassive = false;
      }
      else
      {
        relation = this.REL_ann[0].getSentence();
        relationCategory = getRelationCategory(this.REL_ann[0]);
        relationPassive = IsPassiveVoice();
      }
      if ((relation.toLowerCase().equals("is")) || (relation.toLowerCase().equals("are"))) {
        relation = "IS_A_Relation";
      }
      if ((relation.toLowerCase().equals("have")) || (relation.toLowerCase().equals("has")))
      {
        triple = new QueryTriple(4, queryTerm, secondTerm, thirdTerm, null);
        v.add(triple);
      }
      else
      {
        boolean is_informative = is_InformativeQueryTerms(queryTerm);
        QueryTriple triple;
        if (is_informative)
        {
          triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, thirdTerm, relation, relationCategory, relationFeature, relationPassive);
        }
        else
        {
          System.out.println("Query term no informative, changing the category of the triple to a unknown term type of triple");
          queryTerm = new ArrayList();
          queryTerm.add("what_is");
          triple = new QueryTriple(3, queryTerm, secondTerm, thirdTerm, relation, relationCategory, relationFeature, relationPassive);
        }
        v.add(triple);
      }
      break;
    case 3: 
      secondTerm = PrepareWord(this.NP_ann[0].getSentence());
      PostTreatmentRel();
      queryTerm = new ArrayList();
      queryTerm.add("what_is");
      
      relationFeature = null;
      relationCategory = "REL_NN";
      relationPassive = IsPassiveVoice();
      rule = this.QU_ann[0].getRule();
      if (rule.equals("QU-listClass"))
      {
        ArrayList<String> aux = GetQueryTerm(this.QU_ann[0]);
        relation = (String)aux.get(0);
      }
      else
      {
        relation = this.REL_ann[0].getSentence();
        relationCategory = getRelationCategory(this.REL_ann[0]);
      }
      if ((relation.equals("is")) && (this.NP_ann.length > 1))
      {
        relation = secondTerm;
        secondTerm = PrepareWord(this.NP_ann[1].getSentence());
        triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
      }
      else
      {
        triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
      }
      v.add(triple);
      break;
    case 4: 
      queryTerm = GetQueryTerm(this.QU_ann[0]);
      if ((queryTerm == null) && (this.QU_ann.length == 2)) {
        queryTerm = GetQueryTerm(this.QU_ann[1]);
      }
      secondTerm = PrepareWord(this.NP_ann[0].getSentence());
      
      boolean is_info = is_InformativeQueryTerms(queryTerm);
      if (is_info)
      {
        triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, null);
      }
      else
      {
        System.out.println("Query term no informative, changing the category of the triple to a description type of triple");
        queryTerm = new ArrayList();
        queryTerm.add("what_is");
        triple = new QueryTriple(1, queryTerm, null, secondTerm);
      }
      v.add(triple);
      break;
    case 5: 
      PostTreatmentRel();
      
      queryTerm = new ArrayList();
      queryTerm.add(PrepareWord(this.NP_ann[0].getSentence()));
      secondTerm = PrepareWord(this.NP_ann[1].getSentence());
      relation = this.REL_ann[0].getSentence();
      relationCategory = getRelationCategory(this.REL_ann[0]);
      relationFeature = null;
      if ((relation.toLowerCase().equals("is")) || (relation.toLowerCase().equals("are"))) {
        relation = "IS_A_Relation";
      }
      relationPassive = IsPassiveVoice();
      triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
      
      v.add(triple);
      break;
    case 6: 
      if ((this.QU_ann != null) && (this.QU_ann.length > 0))
      {
        v.addAll(CreateUnclassifiedQueryTriples());
      }
      else
      {
        if (this.NP_ann.length == 3)
        {
          String relation;
          if (this.REL_ann != null)
          {
            if (this.REL_ann.length > 0) {
              relation = this.REL_ann[0].getSentence().concat(" " + this.NP_ann[1].getSentence());
            } else {
              relation = this.NP_ann[1].getSentence();
            }
          }
          else {
            relation = this.NP_ann[1].getSentence();
          }
          queryTerm = new ArrayList();
          queryTerm.add(PrepareWord(this.NP_ann[0].getSentence()));
          secondTerm = PrepareWord(this.NP_ann[2].getSentence());
          typeQuestion = 5;
          String relation = CleaningAuxiliars(relation);
          triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, relation, "REL_NN", null, false);
          
          v.add(triple);
        }
        if (this.NP_ann.length == 2)
        {
          int endindex = this.NP_ann[0].getSentence().lastIndexOf(" ");
          if (endindex == -1) {
            endindex = this.NP_ann[0].getSentence().length();
          }
          int beginindex = 0;
          String firstpart = this.NP_ann[0].getSentence().substring(beginindex, endindex).trim();
          String lastpart = this.NP_ann[0].getSentence().substring(endindex, this.NP_ann[0].getSentence().length()).trim();
          String relation;
          if (this.REL_ann != null)
          {
            String relation;
            if (this.REL_ann.length > 0) {
              relation = this.REL_ann[0].getSentence().concat(" " + lastpart);
            } else {
              relation = lastpart;
            }
          }
          else
          {
            relation = lastpart;
          }
          queryTerm = new ArrayList();
          queryTerm.add(PrepareWord(firstpart));
          secondTerm = PrepareWord(this.NP_ann[1].getSentence());
          typeQuestion = 5;
          relation = CleaningAuxiliars(relation);
          triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, relation, "REL_NN", null, false);
          
          v.add(triple);
        }
      }
      break;
    case 7: 
      queryTerm = GetQueryTerm(this.QU_ann[0]);
      secondTerm = PrepareWord(this.NP_ann[0].getSentence());
      if (this.NP_ann.length > 1)
      {
        thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
        triple = new QueryTriple(14, queryTerm, secondTerm, thirdTerm, null);
      }
      else
      {
        QueryTriple triple;
        if (this.REL_ann != null)
        {
          String thirdTerm = PrepareWord(this.REL_ann[0].getSentence());
          triple = new QueryTriple(11, queryTerm, thirdTerm, secondTerm, null);
        }
        else
        {
          triple = new QueryTriple(4, queryTerm, null, secondTerm);
        }
      }
      v.add(triple);
      break;
    case 8: 
      break;
    case 14: 
      relation = this.REL_ann[0].getSentence();
      if ((isBasicRelation(relation)) && (this.REL_ann.length == 2))
      {
        relation = this.REL_ann[1].getSentence();
        relationFeature = null;
        relationCategory = getRelationCategory(this.REL_ann[1]);
        ArrayList<String> aux = new ArrayList();
        aux.add(PrepareWord(this.NP_ann[0].getSentence()));
        queryTerm = aux;
        PostTreatmentRel();
        secondTerm = PrepareWord(this.NP_ann[1].getSentence());
        String thirdTerm = PrepareWord(this.NP_ann[2].getSentence());
        relationPassive = IsPassiveVoice();
        relation = CleaningAuxiliars(relation);
        triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, thirdTerm, relation, relationCategory, relationFeature, relationPassive);
        
        v.add(triple);
      }
      else if ((isBasicRelation(relation)) && (this.REL_ann.length == 1))
      {
        ArrayList<String> queryTerm = GetQueryTerm(this.QU_ann[0]);
        relation = relation.concat(" " + this.NP_ann[0].getSentence());
        PostTreatmentRel();
        String secondTerm = PrepareWord(this.NP_ann[1].getSentence());
        typeQuestion = 2;
        String relationFeature = getRelationFeature(this.QU_ann[0]);
        String relationCategory = "REL_NN";
        boolean relationPassive = IsPassiveVoice();
        relation = CleaningAuxiliars(relation);
        QueryTriple triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
        
        v.add(triple);
      }
      else
      {
        queryTerm = GetQueryTerm(this.QU_ann[0]);
        PostTreatmentRel();
        secondTerm = PrepareWord(this.NP_ann[0].getSentence());
        thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
        relationFeature = getRelationFeature(this.QU_ann[0]);
        relationCategory = getRelationCategory(this.REL_ann[0]);
        relationPassive = IsPassiveVoice();
        relation = CleaningAuxiliars(relation);
        triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, thirdTerm, relation, relationCategory, relationFeature, relationPassive);
        
        v.add(triple);
      }
      break;
    case 13: 
      PostTreatmentRel();
      queryTerm = new ArrayList();
      queryTerm.add("what_is");
      rule = this.QU_ann[0].getRule();
      if (rule.equals("QU-listClass"))
      {
        ArrayList<String> terms = GetQueryTerm(this.QU_ann[0]);
        relation = (String)terms.get(0);
        relationCategory = "REL_NN";
      }
      else
      {
        relation = this.REL_ann[0].getSentence();
        relationCategory = getRelationCategory(this.REL_ann[0]);
      }
      secondTerm = PrepareWord(this.NP_ann[0].getSentence());
      thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
      relationFeature = null;
      relationPassive = IsPassiveVoice();
      triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, thirdTerm, relation, relationCategory, relationFeature, relationPassive);
      
      v.add(triple);
      break;
    case 17: 
      relation = this.REL_ann[0].getSentence();
      queryTerm = GetQueryTerm(this.QU_ann[0]);
      if (isBasicRelation(relation))
      {
        relation = relation.concat(" " + this.NP_ann[0].getSentence());
        PostTreatmentRel();
        secondTerm = PrepareWord(this.NP_ann[1].getSentence());
        thirdTerm = PrepareWord(this.NP_ann[2].getSentence());
        typeQuestion = 14;
        relationFeature = getRelationFeature(this.QU_ann[0]);
        relationCategory = "REL_NN";
        relationPassive = IsPassiveVoice();
        relation = CleaningAuxiliars(relation);
        triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, thirdTerm, relation, relationCategory, relationFeature, relationPassive);
        
        v.add(triple);
      }
      break;
    case 12: 
      PostTreatmentRel();
      relation = this.REL_ann[0].getSentence();
      queryTerm = GetQueryTerm(this.QU_ann[0]);
      PostTreatmentRel();
      secondTerm = PrepareWord(this.NP_ann[0].getSentence());
      thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
      relationFeature = getRelationFeature(this.QU_ann[0]);
      relationCategory = getRelationCategory(this.REL_ann[0]);
      relationPassive = IsPassiveVoice();
      relation = CleaningAuxiliars(relation);
      triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, thirdTerm, relation, relationCategory, relationFeature, relationPassive);
      
      v.add(triple);
      break;
    case 18: 
      CleaningAuxiliars();
      if (this.NP_ann.length == 3)
      {
        queryTerm = new ArrayList();
        queryTerm.add(PrepareWord(this.NP_ann[0].getSentence()));
        secondTerm = PrepareWord(this.NP_ann[1].getSentence());
        thirdTerm = PrepareWord(this.NP_ann[2].getSentence());
      }
      else
      {
        queryTerm = GetQueryTerm(this.QU_ann[0]);
        secondTerm = PrepareWord(this.NP_ann[0].getSentence());
        thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
      }
      QueryTriple triple2;
      if (this.REL_ann == null)
      {
        triple = new QueryTriple(4, queryTerm, secondTerm, null, null);
        triple2 = new QueryTriple(4, queryTerm, thirdTerm, null, null);
      }
      else
      {
        QueryTriple triple2;
        if (this.REL_ann.length == 2)
        {
          relationFeature = getRelationFeature(this.QU_ann[0]);
          relation = this.REL_ann[0].getSentence();
          String relation2 = this.REL_ann[1].getSentence();
          relationCategory = getRelationCategory(this.REL_ann[0]);
          String relationCategory2 = getRelationCategory(this.REL_ann[1]);
          relationPassive = IsPassiveVoice(this.REL_ann[0].getOffset_begin(), this.REL_ann[0].getOffset_end());
          boolean relationPassive2 = IsPassiveVoice(this.REL_ann[1].getOffset_begin(), this.REL_ann[1].getOffset_end());
          QueryTriple triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
          
          triple2 = new QueryTriple(2, queryTerm, thirdTerm, null, relation2, relationCategory2, relationFeature, relationPassive2);
        }
        else
        {
          String relationFeature = getRelationFeature(this.QU_ann[0]);
          String relation = this.REL_ann[0].getSentence();
          String relationCategory = getRelationCategory(this.REL_ann[0]);
          boolean relationPassive = IsPassiveVoice();
          QueryTriple triple2;
          if (queryTerm == null)
          {
            queryTerm = new ArrayList();
            queryTerm.add(relation);
            QueryTriple triple = new QueryTriple(4, queryTerm, secondTerm, null, null);
            triple2 = new QueryTriple(4, queryTerm, thirdTerm, null, null);
          }
          else
          {
            triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
            
            triple2 = new QueryTriple(2, queryTerm, thirdTerm, null, relation, relationCategory, relationFeature, relationPassive);
          }
        }
      }
      v.add(triple);
      v.add(triple2);
      break;
    case 19: 
      int typeTEMP = 18;
      ArrayList<QueryTriple> res = CreateQueryTriples_Categories(typeTEMP);
      
      ((QueryTriple)res.get(0)).setTypeQuestion(typeQuestion);
      v.add(res.get(0));
      v.add(res.get(1));
      break;
    case 20: 
      rule = this.QU_ann[0].getRule();
      String patron = this.PATTERN_ann[0].getFeature("type");
      CleaningAuxiliars();
      boolean relationPassive2;
      if (this.REL_ann.length == 1)
      {
        ArrayList<String> aux = GetQueryTerm(this.QU_ann[0]);
        String relation = (String)aux.get(0);
        String relation2 = this.REL_ann[0].getSentence();
        String relationCategory = "REL_NN";
        String relationCategory2 = getRelationCategory(this.REL_ann[0]);
        boolean relationPassive = false;
        relationPassive2 = IsPassiveVoice(this.REL_ann[0].getOffset_begin(), this.REL_ann[0].getOffset_end());
      }
      else
      {
        relation = this.REL_ann[0].getSentence();
        relation2 = this.REL_ann[1].getSentence();
        relationCategory = getRelationCategory(this.REL_ann[0]);
        relationCategory2 = getRelationCategory(this.REL_ann[1]);
        relationPassive = IsPassiveVoice(this.REL_ann[0].getOffset_begin(), this.REL_ann[0].getOffset_end());
        relationPassive2 = IsPassiveVoice(this.REL_ann[1].getOffset_begin(), this.REL_ann[1].getOffset_end());
      }
      if (((rule.equalsIgnoreCase("QU-who-what")) && (patron.equalsIgnoreCase("normal")) && (!this.QU_ann[0].getSentence().equalsIgnoreCase("who"))) || ((rule.equalsIgnoreCase("QU-listClass")) && (this.REL_ann.length == 1)) || ((rule.equalsIgnoreCase("QU-which")) && (patron.equalsIgnoreCase("normal"))))
      {
        queryTerm = new ArrayList();
        queryTerm.add("what_is");
        ArrayList<String> queryTerm2 = new ArrayList();
        queryTerm2.add("what_is");
        secondTerm = PrepareWord(this.NP_ann[0].getSentence());
        thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
        String relationFeature = null;
        triple = new QueryTriple(20, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
        
        triple2 = new QueryTriple(3, queryTerm2, thirdTerm, null, relation2, relationCategory2, relationFeature, relationPassive2);
        
        v.add(triple);
        v.add(triple2);
      }
      else if ((isBasicRelation(relation)) && (patron.equalsIgnoreCase("normal")))
      {
        ArrayList<String> queryTerm = GetQueryTerm(this.QU_ann[0]);
        String secondTerm = PrepareWord(this.NP_ann[0].getSentence());
        String thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
        String relationFeature = getRelationFeature(this.QU_ann[0]);
        if (IsMainVerb(this.REL_ann[0].getOffset_begin(), this.REL_ann[0].getOffset_end()))
        {
          QueryTriple triple = new QueryTriple(4, queryTerm, secondTerm, null, null, relationCategory, relationFeature, relationPassive);
          
          QueryTriple triple2 = new QueryTriple(2, secondTerm, thirdTerm, null, relation2, relationCategory2, null, relationPassive2);
          
          v.add(triple);
          v.add(triple2);
        }
        else
        {
          System.out.println("is this possible????????????");
          QueryTriple triple = new QueryTriple(12, queryTerm, secondTerm, thirdTerm, relation2, relationCategory, relationFeature, relationPassive);
          
          v.add(triple);
        }
      }
      else
      {
        queryTerm = GetQueryTerm(this.QU_ann[0]);
        ArrayList<String> queryTerm2 = new ArrayList();
        queryTerm2.add("what_is");
        secondTerm = PrepareWord(this.NP_ann[0].getSentence());
        thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
        relationFeature = getRelationFeature(this.QU_ann[0]);
        
        String value = this.QU_ann[0].getFeature("rule");
        System.out.println("patron: " + patron + "---");
        QueryTriple triple2;
        if (patron.equalsIgnoreCase("extended"))
        {
          queryTerm = new ArrayList();
          queryTerm.add(secondTerm);
          secondTerm = thirdTerm;
          relation = relation2;
          relation2 = this.REL_ann[2].getSentence();
          thirdTerm = PrepareWord(this.NP_ann[2].getSentence());
          QueryTriple triple = new QueryTriple(20, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
          
          triple2 = new QueryTriple(3, queryTerm2, thirdTerm, null, relation2, relationCategory2, null, relationPassive2);
        }
        else
        {
          QueryTriple triple2;
          if (IsMainVerb(this.REL_ann[0].getOffset_begin(), this.REL_ann[0].getOffset_end()))
          {
            QueryTriple triple;
            QueryTriple triple;
            if (relation.equals(this.QU_ann[0].getSentence())) {
              triple = new QueryTriple(4, queryTerm, secondTerm, null, null, relationCategory, relationFeature, relationPassive);
            } else {
              triple = new QueryTriple(2, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
            }
            triple2 = new QueryTriple(2, secondTerm, thirdTerm, null, relation2, relationCategory2, null, relationPassive2);
          }
          else
          {
            QueryTriple triple2;
            if (IsMainVerb(this.REL_ann[1].getOffset_begin(), this.REL_ann[1].getOffset_end()))
            {
              QueryTriple triple = new QueryTriple(18, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
              
              triple2 = new QueryTriple(2, queryTerm, thirdTerm, null, relation2, relationCategory2, relationFeature, relationPassive2);
            }
            else
            {
              triple = new QueryTriple(20, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
              
              triple2 = new QueryTriple(3, queryTerm2, thirdTerm, null, relation2, relationCategory2, null, relationPassive2);
            }
          }
        }
        v.add(triple);
        v.add(triple2);
      }
      break;
    case 23: 
      CleaningAuxiliars();
      queryTerm = GetQueryTerm(this.QU_ann[0]);
      secondTerm = PrepareWord(this.NP_ann[0].getSentence());
      thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
      relation = this.REL_ann[0].getSentence();
      relation2 = this.REL_ann[1].getSentence();
      relationFeature = getRelationFeature(this.QU_ann[0]);
      relationCategory = getRelationCategory(this.REL_ann[0]);
      relationCategory2 = getRelationCategory(this.REL_ann[1]);
      relationPassive = IsPassiveVoice(this.REL_ann[0].getOffset_begin(), this.REL_ann[0].getOffset_end());
      relationPassive2 = IsPassiveVoice(this.REL_ann[1].getOffset_begin(), this.REL_ann[1].getOffset_end());
      triple = new QueryTriple(18, queryTerm, secondTerm, null, relation, relationCategory, relationFeature, relationPassive);
      
      triple2 = new QueryTriple(2, queryTerm, thirdTerm, null, relation2, relationCategory2, relationFeature, relationPassive2);
      
      v.add(triple);
      v.add(triple2);
      break;
    case 11: 
      queryTerm = GetQueryTerm(this.QU_ann[0]);
      secondTerm = PrepareWord(this.NP_ann[0].getSentence());
      thirdTerm = PrepareWord(this.NP_ann[1].getSentence());
      triple = new QueryTriple(typeQuestion, queryTerm, secondTerm, thirdTerm, null);
      v.add(triple);
      break;
    case 9: 
      break;
    case 10: 
      break;
    case 15: 
      break;
    case 16: 
      break;
    case 22: 
      break;
    case 24: 
      break;
    }
    return v;
  }
  
  public boolean is_InformativeQueryTerms(ArrayList<String> queryTerms)
  {
    for (String queryTerm : queryTerms) {
      if (is_InformativeQueryTerm(queryTerm)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean is_InformativeQueryTerm(String queryterm)
  {
    for (int i = 0; i < non_informative_terms.length; i++) {
      if (non_informative_terms[i].equalsIgnoreCase(queryterm)) {
        return false;
      }
    }
    return true;
  }
  
  private ArrayList<String> GetQueryTerm(IEAnnotation Qu_term)
    throws Exception
  {
    if (Qu_term == null) {
      return null;
    }
    String rule = Qu_term.getRule();
    if (rule.equals("QU-whichClass"))
    {
      int index = Qu_term.getSentence().toLowerCase().indexOf("which");
      if (index < 0) {
        index = Qu_term.getSentence().toLowerCase().indexOf("that");
      }
      String sentence = Qu_term.getSentence().substring(index + 5, Qu_term.getSentence().length()).trim();
      if (index < 0) {
        return null;
      }
    }
    else
    {
      String sentence;
      if (rule.equals("QU-what-class"))
      {
        int index = Qu_term.getSentence().toLowerCase().indexOf("what");
        if (index < 0) {
          return null;
        }
        sentence = Qu_term.getSentence().substring(index + 4, Qu_term.getSentence().length()).trim();
      }
      else if (rule.equals("QU-there"))
      {
        int index = Qu_term.getSentence().toLowerCase().indexOf("there");
        if (index < 0) {
          return null;
        }
        int index2 = Qu_term.getSentence().toLowerCase().indexOf("any");
        String sentence;
        String sentence;
        if (index2 < 0) {
          sentence = Qu_term.getSentence().substring(index + 5, Qu_term.getSentence().length()).trim();
        } else {
          sentence = Qu_term.getSentence().substring(index2 + 3, Qu_term.getSentence().length()).trim();
        }
        if (sentence.startsWith("a ")) {
          sentence = sentence.substring(2, sentence.length()).trim();
        }
        if (sentence.startsWith("an ")) {
          sentence = sentence.substring(3, sentence.length()).trim();
        }
      }
      else
      {
        String sentence;
        if (rule.equals("QU-howmanyClass"))
        {
          int index = Qu_term.getSentence().toLowerCase().indexOf("many");
          if (index < 0) {
            return null;
          }
          sentence = Qu_term.getSentence().substring(index + 4, Qu_term.getSentence().length()).trim();
        }
        else if (rule.equals("QU-listClass"))
        {
          int index = Qu_term.getSentence().toLowerCase().indexOf("tell ");
          int count_length = 4;
          if (index < 0) {
            index = Qu_term.getSentence().toLowerCase().indexOf("list ");
          }
          if (index < 0) {
            index = Qu_term.getSentence().toLowerCase().indexOf("show ");
          }
          if (index < 0) {
            index = Qu_term.getSentence().toLowerCase().indexOf("give ");
          }
          if (index < 0) {
            index = Qu_term.getSentence().toLowerCase().indexOf("name ");
          }
          if (index < 0) {
            index = Qu_term.getSentence().toLowerCase().indexOf("find ");
          }
          if (index < 0)
          {
            index = Qu_term.getSentence().toLowerCase().indexOf("provide ");
            count_length = 7;
          }
          if (index < 0)
          {
            index = Qu_term.getSentence().toLowerCase().indexOf("describe ");
            count_length = 8;
          }
          if (index < 0)
          {
            index = Qu_term.getSentence().toLowerCase().indexOf("identify ");
            count_length = 8;
          }
          if (index < 0)
          {
            index = Qu_term.getSentence().toLowerCase().indexOf("explain ");
            count_length = 7;
          }
          if (index < 0) {
            return null;
          }
          String sentence = Qu_term.getSentence().substring(index + count_length, Qu_term.getSentence().length());
          index = sentence.toLowerCase().indexOf(" me ");
          if (index > -1) {
            sentence = sentence.substring(index + 3, sentence.length());
          }
          index = sentence.toLowerCase().indexOf(" all ");
          if (index > -1) {
            sentence = sentence.substring(index + 4, sentence.length());
          }
          index = sentence.toLowerCase().indexOf(" a ");
          if (index > -1) {
            sentence = sentence.substring(index + 2, sentence.length());
          }
          index = sentence.toLowerCase().indexOf(" information ");
          if (index > -1) {
            sentence = sentence.substring(index + 12, sentence.length());
          }
          index = sentence.toLowerCase().indexOf(" list ");
          if (index > -1) {
            sentence = sentence.substring(index + 5, sentence.length());
          }
          index = sentence.toLowerCase().indexOf(" on ");
          if (index > -1) {
            sentence = sentence.substring(index + 3, sentence.length());
          }
          index = sentence.toLowerCase().indexOf(" off ");
          if (index > -1) {
            sentence = sentence.substring(index + 4, sentence.length());
          }
          index = sentence.toLowerCase().indexOf(" about ");
          if (index > -1) {
            sentence = sentence.substring(index + 6, sentence.length());
          }
          index = sentence.toLowerCase().indexOf(" the ");
          if (index > -1) {
            sentence = sentence.substring(index + 4, sentence.length());
          }
          sentence = sentence.trim();
        }
        else
        {
          if (((rule.equals("QU-who-what")) && (this.QU_ann[0].getSentence().equalsIgnoreCase("who"))) || (rule.equals("QU-anybody"))) {
            return TypeWho();
          }
          if ((rule.equals("QU-whe")) || (rule.equals("QU-howlong"))) {
            return TypeWhe(Qu_term.getSentence());
          }
          return null;
        }
      }
    }
    int index;
    String sentence;
    ArrayList<String> term = new ArrayList();
    term.add(sentence);
    return term;
  }
  
  private String getRelationCategory(IEAnnotation Rel)
  {
    String category = Rel.getFeature("category");
    return category;
  }
  
  private String getRelationFeature(IEAnnotation Rel)
  {
    String rule = Rel.getRule();
    if (rule.equals("QU-there")) {
      return "how-many";
    }
    if (Rel.getSentence().toLowerCase().trim().equals("who")) {
      return "who";
    }
    if (Rel.getSentence().toLowerCase().trim().equals("does anybody")) {
      return "who";
    }
    if (Rel.getSentence().toLowerCase().trim().equals("do anybody")) {
      return "who";
    }
    if (Rel.getSentence().toLowerCase().trim().equals("when")) {
      return "when";
    }
    if (Rel.getSentence().toLowerCase().trim().equals("where")) {
      return "where";
    }
    return null;
  }
  
  public ArrayList<String> TypeWho()
    throws Exception
  {
    QueryClassifyConfig context = new QueryClassifyConfig(this.localdir);
    String who = context.who.trim();
    ArrayList<String> v = new ArrayList();
    StringTokenizer st = new StringTokenizer(who, ",");
    while (st.hasMoreTokens()) {
      v.add(st.nextToken().trim());
    }
    return v;
  }
  
  public ArrayList<String> TypeWhe(String que)
    throws Exception
  {
    QueryClassifyConfig context = new QueryClassifyConfig(this.localdir);
    String when = context.when.trim();
    String where = context.where.trim();
    String howlong = context.howlong.trim();
    ArrayList<String> v = new ArrayList();
    StringTokenizer st;
    StringTokenizer st;
    if (que.trim().equalsIgnoreCase("when"))
    {
      st = new StringTokenizer(when, ",");
    }
    else
    {
      StringTokenizer st;
      if (que.trim().equalsIgnoreCase("where")) {
        st = new StringTokenizer(where, ",");
      } else {
        st = new StringTokenizer(howlong, ",");
      }
    }
    while (st.hasMoreTokens()) {
      v.add(st.nextToken().trim());
    }
    return v;
  }
  
  public static String PrepareWord(String instance)
  {
    Vector PREP = new Vector(3);
    PREP.add("a");PREP.add("an");PREP.add("the");PREP.add("all");
    
    String aux = instance.trim();
    int index_ini = aux.indexOf(" ");
    if ((index_ini > 0) && 
      (PREP.contains(aux.substring(0, index_ini).toLowerCase().trim()))) {
      instance = aux.substring(index_ini + 1, aux.length());
    }
    return instance.trim();
  }
  
  public int ValidateQuery()
  {
    if (this.PATTERN_ann == null) {
      return 5;
    }
    int pattern_begin = this.PATTERN_ann[0].getOffset_begin();
    int pattern_end = this.PATTERN_ann[(this.PATTERN_ann.length - 1)].getOffset_end();
    if (this.REL_ann != null) {
      for (int j = 0; j < this.REL_ann.length; j++)
      {
        int temp_begin = this.REL_ann[j].getOffset_begin();
        int temp_end = this.REL_ann[j].getOffset_end();
        if ((temp_begin < pattern_begin) || (temp_end > pattern_end))
        {
          System.out.println("Outside offsets " + this.REL_ann[j].getSentence() + " " + temp_begin + ":" + temp_end);
          return 1;
        }
      }
    }
    if (this.NP_ann != null) {
      for (int j = 0; j < this.NP_ann.length; j++)
      {
        int temp_begin = this.NP_ann[j].getOffset_begin();
        int temp_end = this.NP_ann[j].getOffset_end();
        if ((temp_begin < pattern_begin) || (temp_end > pattern_end))
        {
          System.out.println("Outside offsets " + this.NP_ann[j].getSentence() + " " + temp_begin + ":" + temp_end);
          return 2;
        }
      }
    }
    if (this.QU_ann != null) {
      for (int j = 0; j < this.QU_ann.length; j++)
      {
        int temp_begin = this.QU_ann[j].getOffset_begin();
        int temp_end = this.QU_ann[j].getOffset_end();
        if ((temp_begin < pattern_begin) || (temp_end > pattern_end))
        {
          System.out.println("Outside offsets! " + this.QU_ann[j].getSentence() + " " + temp_begin + ":" + temp_end);
          return 3;
        }
      }
    }
    if (this.VG_ann != null) {
      for (int j = 0; j < this.VG_ann.length; j++)
      {
        int temp_begin = this.VG_ann[j].getOffset_begin();
        int temp_end = this.VG_ann[j].getOffset_end();
        if ((temp_begin < pattern_begin) || (temp_end > pattern_end))
        {
          System.out.println("Outside offsets " + this.VG_ann[j].getSentence() + " " + temp_begin + ":" + temp_end);
          return 4;
        }
      }
    }
    return 0;
  }
  
  private boolean isBasicRelation(String relation)
  {
    Vector extensions = new Vector();
    extensions.addElement("has");extensions.addElement("have");extensions.addElement("had");
    extensions.addElement("is");extensions.addElement("are");extensions.addElement("was");
    extensions.addElement("were");
    if (extensions.contains(relation)) {
      return true;
    }
    return false;
  }
  
  private IEAnnotation[] GetAnnotationsforPattern(IEAnnotation[] tmp_ann, int pattern_begin, int pattern_end)
  {
    ArrayList v = new ArrayList();
    if (tmp_ann == null) {
      return null;
    }
    for (int i = 0; i < tmp_ann.length; i++)
    {
      int begin = tmp_ann[i].getOffset_begin();
      int end = tmp_ann[i].getOffset_end();
      if ((begin >= pattern_begin) && (end <= pattern_end)) {
        v.add(tmp_ann[i]);
      }
    }
    IEAnnotation[] res = new IEAnnotation[v.size()];
    v.toArray(res);
    return res;
  }
  
  public static String getType(int category)
  {
    switch (category)
    {
    case 1: 
      return "DESCRIPTION";
    case 2: 
      return "WH_GENERICTERM";
    case 3: 
      return "WH_UNKNTERM";
    case 4: 
      return "WH_UNKNREL";
    case 5: 
      return "AFFIRM_NEG";
    case 6: 
      return "AFFIRM_NEG_PSEUDOREL";
    case 7: 
      return "HOW_LONG";
    case 8: 
      return "QU_WHE";
    case 9: 
      return "AFFIRM_NEG_3TERM";
    case 10: 
      return "AFFIRM_NEG_1TERMCLAUSE";
    case 11: 
      return "WH_3UNKNREL";
    case 12: 
      return "WH_GENERIC_1TERMCLAUSE";
    case 13: 
      return "WH_UNKNTERM_2CLAUSE";
    case 14: 
      return "WH_3TERM";
    case 15: 
      return "WH_3TERM_CLAUSE";
    case 16: 
      return "WH_3TERM_1CLAUSE";
    case 17: 
      return "WH_4TERM";
    case 18: 
      return "WH_COMB_AND";
    case 19: 
      return "WH_COMB_OR";
    case 20: 
      return "WH_COMB_COND";
    case 21: 
      return "WH_COMB_COND_RELNN";
    case 22: 
      return "AFFIRM_NEG_WHCLAUSE";
    case 23: 
      return "WH_GENERIC_WHCLAUSE";
    case 24: 
      return "WH_UNKNOWN";
    case 25: 
      return "PATTERNS_2";
    case 26: 
      return "COMPOUND";
    case 27: 
      return "UNCLASSIFIED";
    case 28: 
      return "UNCLASSIFIED_3TERM";
    case 29: 
      return "IS_A_ONLY";
    }
    return "Not valid";
  }
  
  public static void main(String[] args)
    throws Exception
  {
    try
    {
      LinguisticComponent chunk = new LinguisticComponent();
      
      String question = "How many movies are in England?";
      
      String outFile = "/Users/vl474/Trabajo/NetBeans projects/BTCPowerAquaNetBeans/queriesEva/CQsResult.txt";
      BufferedWriter outF = new BufferedWriter(new FileWriter(outFile));
      String inFile = "/Users/vl474/Trabajo/NetBeans projects/BTCPowerAquaNetBeans/queriesEva/Cqs.txt";
      ArrayList<String> CQs = new ArrayList();
      try
      {
        BufferedReader in = new BufferedReader(new FileReader(inFile));
        if (!in.ready()) {
          throw new IOException();
        }
        String CQ;
        while ((CQ = in.readLine()) != null) {
          CQs.add(CQ);
        }
        in.close();
      }
      catch (IOException e)
      {
        System.out.println(e);
      }
      Iterator<String> CQIter = CQs.iterator();
      while (CQIter.hasNext())
      {
        question = (String)CQIter.next();
        
        outF.write(question);
        outF.newLine();
        
        System.out.println(question);
        chunk.parseQuestion(question);
        System.out.println("THE TYPE IS " + chunk.typeQuestion);
        
        outF.write(" *** This is the information stored on the Query-triple: ***");
        outF.newLine();
        outF.write("The number of query-triples obtained is " + chunk.queryTriples.size());
        outF.newLine();
        if (chunk.getQueryTriples() != null)
        {
          System.out.println(" *** This is the information stored on the Query-triple: ***");
          
          outF.write(" *** This is the information stored on the Query-triple: ***");
          outF.newLine();
          outF.write("The number of query-triples obtained is " + chunk.queryTriples.size());
          outF.newLine();
          
          System.out.println("The number of query-triples obtained is " + chunk.getQueryTriples().size());
          for (int x = 0; x < chunk.getQueryTriples().size(); x++)
          {
            System.out.println("------ QUESTION TYPE IS " + ((QueryTriple)chunk.getQueryTriples().get(x)).getTypeQuestion());
            
            outF.write("------ QUESTION TYPE IS " + ((QueryTriple)chunk.getQueryTriples().get(x)).getTypeQuestion());
            outF.newLine();
            if (((QueryTriple)chunk.getQueryTriples().get(x)).getQueryTerm() != null) {
              for (int n = 0; n < ((QueryTriple)chunk.getQueryTriples().get(x)).getQueryTerm().size(); n++)
              {
                System.out.println("The generic query/first term is " + (String)((QueryTriple)chunk.getQueryTriples().get(x)).getQueryTerm().get(n));
                
                outF.write("The generic query/first term is " + (String)((QueryTriple)chunk.getQueryTriples().get(x)).getQueryTerm().get(n));
                outF.newLine();
              }
            }
            System.out.println("The second term is " + ((QueryTriple)chunk.getQueryTriples().get(x)).getSecondTerm());
            System.out.println("The third term is " + ((QueryTriple)chunk.getQueryTriples().get(x)).getThirdTerm());
            System.out.println("The relation is " + ((QueryTriple)chunk.getQueryTriples().get(x)).getRelation());
            
            System.out.println("The relation feature is " + ((QueryTriple)chunk.getQueryTriples().get(x)).getRelationFeature());
            
            System.out.println("The relation category is " + ((QueryTriple)chunk.getQueryTriples().get(x)).relationCategory);
            
            System.out.println("it is passive " + ((QueryTriple)chunk.getQueryTriples().get(x)).isRelationPassive());
            
            outF.write("The second term is " + ((QueryTriple)chunk.getQueryTriples().get(x)).getSecondTerm());
            outF.newLine();
            outF.write("The third term is " + ((QueryTriple)chunk.getQueryTriples().get(x)).getThirdTerm());
            outF.newLine();
            outF.write("The relation is " + ((QueryTriple)chunk.getQueryTriples().get(x)).getRelation());
            outF.newLine();
            outF.write("The relation feature is " + ((QueryTriple)chunk.getQueryTriples().get(x)).getRelationFeature());
            outF.newLine();
            outF.write("The relation category is " + ((QueryTriple)chunk.getQueryTriples().get(x)).relationCategory);
            outF.newLine();
            
            outF.write("it is passive " + ((QueryTriple)chunk.getQueryTriples().get(x)).isRelationPassive());
            outF.newLine();
          }
          System.out.println(" *** These are the annotations obtained: *** ");
          
          outF.write(" *** These are the annotations obtained: *** ");
          outF.newLine();
          if (chunk.QU_ann != null) {
            for (int i = 0; i < chunk.QU_ann.length; i++)
            {
              System.out.println("QU ES " + chunk.QU_ann[i].getSentence() + " -- " + chunk.QU_ann[i].getOffset_begin() + ":" + chunk.QU_ann[i].getOffset_end());
              
              outF.write("QU ES " + chunk.QU_ann[i].getSentence() + " -- " + chunk.QU_ann[i].getOffset_begin() + ":" + chunk.QU_ann[i].getOffset_end());
              
              outF.newLine();
            }
          }
          if (chunk.REL_ann != null) {
            for (int i = 0; i < chunk.REL_ann.length; i++)
            {
              System.out.println("REL ES " + chunk.REL_ann[i].getSentence() + " -- " + chunk.REL_ann[i].getOffset_begin() + ":" + chunk.REL_ann[i].getOffset_end());
              
              boolean mainv = chunk.IsMainVerb(chunk.REL_ann[i].getOffset_begin(), chunk.REL_ann[i].getOffset_end());
              System.out.println("This REL contains the main verb of the NL query? " + mainv);
              
              outF.write("REL ES " + chunk.REL_ann[i].getSentence() + " -- " + chunk.REL_ann[i].getOffset_begin() + ":" + chunk.REL_ann[i].getOffset_end());
              
              outF.newLine();
              
              outF.write("This REL contains the main verb of the NL query? " + mainv);
              outF.newLine();
            }
          }
          if (chunk.NP_ann != null) {
            for (int i = 0; i < chunk.NP_ann.length; i++)
            {
              System.out.println("NP ES " + chunk.NP_ann[i].getSentence() + " -- " + chunk.NP_ann[i].getOffset_begin() + ":" + chunk.NP_ann[i].getOffset_end());
              
              outF.write("NP ES " + chunk.NP_ann[i].getSentence() + " -- " + chunk.NP_ann[i].getOffset_begin() + ":" + chunk.NP_ann[i].getOffset_end());
              
              outF.newLine();
            }
          }
          if (chunk.PATTERN_ann != null) {
            for (int i = 0; i < chunk.PATTERN_ann.length; i++)
            {
              System.out.println("PATTERN ES " + chunk.PATTERN_ann[i].getSentence() + " -- " + chunk.PATTERN_ann[i].getOffset_begin() + ":" + chunk.PATTERN_ann[i].getOffset_end());
              
              outF.write("PATTERN ES " + chunk.PATTERN_ann[i].getSentence() + " -- " + chunk.PATTERN_ann[i].getOffset_begin() + ":" + chunk.PATTERN_ann[i].getOffset_end());
              
              outF.newLine();
            }
          }
          if (chunk.VG_ann != null) {
            for (int i = 0; i < chunk.VG_ann.length; i++)
            {
              System.out.println("VG ES " + chunk.VG_ann[i].getSentence() + " -- " + chunk.VG_ann[i].getOffset_begin() + ":" + chunk.VG_ann[i].getOffset_end());
              
              outF.write("VG ES " + chunk.VG_ann[i].getSentence() + " -- " + chunk.VG_ann[i].getOffset_begin() + ":" + chunk.VG_ann[i].getOffset_end());
              
              outF.newLine();
            }
          }
        }
      }
      outF.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public String getQuestion()
  {
    return this.question;
  }
  
  public ArrayList<QueryTriple> getQueryTriples()
  {
    return this.queryTriples;
  }
}

