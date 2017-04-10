package poweraqua.powermap.stringMetrics;

import com.wcohen.secondstring.Jaro;
import com.wcohen.secondstring.JaroWinkler;
import com.wcohen.secondstring.JaroWinklerTFIDF;
import com.wcohen.secondstring.Levenstein;
import java.io.PrintStream;

public class stringMetricsComparator
{
  private JaroWinkler jwinklerDistance;
  private Jaro jaroDistance;
  private Levenstein levenstein;
  private JaroWinklerTFIDF jaroWTFIDF;
  private double thresh_JaroDistance = 0.8D;
  private double thresh_jwinklerDistance = 0.865D;
  private double thresh_global = 0.53D;
  private double thresh_levensteinGlobal = -15.0D;
  private double thresh_jaroWTFIDFGlobal = 0.57D;
  private double thresh_levenstein = -6.0D;
  private double thresh_jaroWTFIDF = 0.69D;
  private double similar1;
  private double similar2;
  private double similar3;
  private double similar4;
  
  public stringMetricsComparator()
  {
    this.levenstein = new Levenstein();
    this.jwinklerDistance = new JaroWinkler();
    this.jaroDistance = new Jaro();
    this.jaroWTFIDF = new JaroWinklerTFIDF();
  }
  
  public stringMetricsComparator(double thresh_global, double thresh_JaroDistance, double thresh_jwinklerDistance, double thresh_levenstein, double thresh_jaroWTFIDF)
  {
    Levenstein levenstein = new Levenstein();
    JaroWinkler jwinklerDistance = new JaroWinkler();
    Jaro jaroDistance = new Jaro();
    
    this.thresh_JaroDistance = thresh_JaroDistance;
    this.thresh_jwinklerDistance = thresh_jwinklerDistance;
    
    this.thresh_levenstein = thresh_levenstein;
    this.thresh_global = thresh_global;
    this.thresh_jaroWTFIDF = thresh_jaroWTFIDF;
  }
  
  public boolean stringSimilarity(String word1, String word2)
  {
    word1 = word1.toLowerCase();
    word2 = word2.toLowerCase();
    
    this.similar1 = this.jaroDistance.score(this.jaroDistance.prepare(word1), this.jaroDistance.prepare(word2));
    if (getSimilar1() > this.thresh_JaroDistance) {
      return true;
    }
    this.similar3 = this.jwinklerDistance.score(this.jwinklerDistance.prepare(word1), this.jwinklerDistance.prepare(word2));
    if (getSimilar3() > this.thresh_jwinklerDistance) {
      return true;
    }
    this.similar2 = this.levenstein.score(this.levenstein.prepare(word1), this.levenstein.prepare(word2));
    if (getSimilar2() >= this.thresh_levenstein) {
      return true;
    }
    this.similar4 = this.jaroWTFIDF.score(this.jaroWTFIDF.prepare(word1), this.jaroWTFIDF.prepare(word2));
    if ((getSimilar1() > this.thresh_global) && (getSimilar3() > this.thresh_global) && (getSimilar4() > this.thresh_global) && (getSimilar2() >= this.thresh_levensteinGlobal) && (getSimilar4() > this.thresh_jaroWTFIDFGlobal)) {
      return true;
    }
    if ((this.similar4 >= this.thresh_jaroWTFIDF) && (this.similar2 >= this.thresh_levensteinGlobal)) {
      return true;
    }
    return false;
  }
  
  public boolean stringSimilarityStrong(String word1, String word2)
  {
    word1 = word1.toLowerCase();
    word2 = word2.toLowerCase();
    word1 = word1.replace("_", " ");
    word1 = word1.replace("-", " ");
    word2 = word2.replace("_", " ");
    word2 = word2.replace("-", " ");
    
    this.similar1 = this.jaroDistance.score(this.jaroDistance.prepare(word1), this.jaroDistance.prepare(word2));
    if (getSimilar1() > 0.91D) {
      return true;
    }
    this.similar3 = this.jwinklerDistance.score(this.jwinklerDistance.prepare(word1), this.jwinklerDistance.prepare(word2));
    if (getSimilar3() >= 0.93D) {
      return true;
    }
    return false;
  }
  
  public boolean stringSimilarityLight(String word1, String word2)
  {
    word1 = word1.toLowerCase();
    word2 = word2.toLowerCase();
    
    this.similar1 = this.jaroDistance.score(this.jaroDistance.prepare(word1), this.jaroDistance.prepare(word2));
    if (getSimilar1() > this.thresh_JaroDistance) {
      return true;
    }
    this.similar3 = this.jwinklerDistance.score(this.jwinklerDistance.prepare(word1), this.jwinklerDistance.prepare(word2));
    if (getSimilar3() > this.thresh_jwinklerDistance)
    {
      System.out.println("!!!!!!! SIM3");
      return true;
    }
    return false;
  }
  
  public double getSimilar1()
  {
    return this.similar1;
  }
  
  public double getSimilar2()
  {
    return this.similar2;
  }
  
  public double getSimilar3()
  {
    return this.similar3;
  }
  
  public double getSimilar4()
  {
    return this.similar4;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    stringMetricsComparator comparator = new stringMetricsComparator();
    
    comparator.stringSimilarityStrong("star", "starring");
    comparator.stringSimilarityStrong("organs", "aquatic_organisms");
    comparator.stringSimilarityStrong("organs", "microorganisms");
    comparator.stringSimilarityStrong("organs", "organisms");
    comparator.stringSimilarityStrong("organs", "organ");
    comparator.stringSimilarityStrong("sweetener", "sweeteners");
    
    comparator.stringSimilarityStrong("bread", "brandy");
    
    comparator.stringSimilarityStrong("bread", "breadcrumb");
    
    comparator.stringSimilarityStrong("edible_salt", "edible_bean");
    comparator.stringSimilarityStrong("fish", "fishes");
    comparator.stringSimilarityStrong("pork", "pork_chop");
    comparator.stringSimilarityStrong("pancake", "prawn_cracker");
    comparator.stringSimilarityStrong("brand", "bread");
    comparator.stringSimilarityStrong("pasta", "pastry");
    comparator.stringSimilarityStrong("food", "seafood");
    comparator.stringSimilarityStrong("plant_part", "plantain");
    comparator.stringSimilarityStrong("savoury", "savory");
    comparator.stringSimilarityStrong("species", "spices");
    comparator.stringSimilarityStrong("pasta", "paste");
    comparator.stringSimilarityStrong("dairy_product", "dairyProduct");
    comparator.stringSimilarityStrong("organ", "oregano");
    comparator.stringSimilarityStrong("cut_of_pork", "cut of pork");
    comparator.stringSimilarityStrong("vegetable", "vegetable_oil");
    comparator.stringSimilarityStrong("paste", "pate");
    
    comparator.stringSimilarity("pimiento rojo", "pimiento");
    
    System.out.println(" Similar1 atlas_blue and blue_air" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("Software Reuse: Nemesis or Nirvana? (Panel)", "Nirvana");
    
    System.out.println(" Similar1 Software Reuse: Nemesis or Nirvana? (Panel) and Nirvana" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("The largest state in the UnitedStates", "United States");
    
    System.out.println(" Similar1 The largest state in the UnitedStates and United States" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("isHighestPointOf ", "highest point");
    
    System.out.println(" Similar1 isHighestPointOf and highest pointf " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("belgium_country", "countri");
    
    System.out.println(" Similar1 belgium_country and countri " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("countri", "belgium_country");
    System.out.println(" Similar1  countri and belgium_country " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "norht western europe");
    
    System.out.println(" Similar1 europe and north western europe" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "west europe");
    
    System.out.println(" Similar1 europe and west europe" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "south east europe");
    
    System.out.println(" Similar1 europe and south east europe" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "north europe");
    
    System.out.println(" Similar1 europe and noth europe " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "central europe");
    
    System.out.println(" Similar1 europe and central europe " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "western europe");
    
    System.out.println(" Similar1 europe and western europe " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "southern europe");
    
    System.out.println(" Similar1 europe and southern europe " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "west europe");
    
    System.out.println(" Similar1 europe and west europe " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "eastern europe");
    
    System.out.println(" Similar1 europe and eastern europe " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("europe", "northwest europe");
    
    System.out.println(" Similar1 europe and northwest europe " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("incandescent_ligth_bulb", "light_bulb");
    
    System.out.println(" Similar1 incandescent_ligth_bulb light_bulb" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("VernerEMHC01", "researchers");
    
    System.out.println(" Similar1 VernerEMHC01 researchers" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("knowledge media institute", "knowledge-media-institute-at-the-open-university");
    
    System.out.println(" Similar1 knowledge media institute and knowledge-media-institute-at-the-open-university " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("academics", "academic-staff-member");
    
    System.out.println(" Similar1 academics academic-staff-member" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("researcher", "research-fellow");
    
    System.out.println(" Similar1 researcher research-fellow" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("vanesa lopez garci", "vanessa-lopez");
    
    System.out.println(" Similar1 vanesa lopez garci and vanessa-lopez" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("knowledge", "knowledge-based-programs");
    
    System.out.println(" Similar1 knowledge and knowledge-based-programs" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("knowledge", "knowledge discovery in databases");
    
    System.out.println(" Similar1 knowledge and knowledge discovery in databases" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("knowledge", "knowledge processing and commonsense");
    
    System.out.println(" Similar1 knowledge and knowledge processing and commonsense" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("knowledge", "narasimhan97");
    
    System.out.println(" Similar1 knowledge narasimhan97" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("work", "SWEET_36052");
    
    System.out.println(" Similar1 work SWEET_36052 " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("academic staffs", "academic-staff-member");
    
    System.out.println(" Similar1 academic staffs and academic-staff-member " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("ORGANIZATION$ORGANISATION$ARRANGEMENT", "organization");
    
    System.out.println(" Similar1 ORGANIZATION$ORGANISATION$ARRANGEMENT organization" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("publication", "Scientific-Publication");
    
    System.out.println(" Similar1 publication Scientific-Publication" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("research areas", "mentions-research-area");
    
    System.out.println(" Similar1 research areas mentions-research-area" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("person", "working-person");
    
    System.out.println(" Similar1 person working-person" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("person", "wnat#form.dollar.word_form");
    
    System.out.println(" Similar1 person wnat#form.dollar.word_form" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("stories", "cold_stores");
    
    System.out.println(" Similar1 stories cold_stores " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("organizations", "international_organizations");
    
    System.out.println(" Similar1 organizations international_organizations" + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("incandescent_ligth_bulb", "light_bulb");
    
    System.out.println(" Similar1 incandescent_ligth_bulb light_bulb " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("group", "group");
    
    System.out.println(" Similar1 group group " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("group", "groups");
    
    System.out.println(" Similar1 group groups " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("group", "wave_groups");
    
    System.out.println(" Similar1 group wave_groups " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("group", "group_velocity");
    
    System.out.println(" Similar1 group group_velocity " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("group", "grt_group_range");
    
    System.out.println(" Similar1 group grt_group_range " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("woman", "women");
    
    System.out.println(" Similar1 woman women " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("branch", "branches");
    
    System.out.println(" Similar1 branch branches " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("campin", "camping");
    
    System.out.println(" Similar1 campin camping " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
    
    comparator.stringSimilarity("knowledge media institute", "knowledge-medika-institute-at-the-open-university");
    
    System.out.println(" knowledge media institute knowledge-medika-institute-at-the-open-university " + comparator.similar1 + " similar2 " + comparator.similar2 + " similar 3 " + comparator.similar3);
    
    System.out.println("--------------------------------------------------------");
  }
}

