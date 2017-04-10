package poweraqua.fusion;

import java.util.Map;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;
import poweraqua.powermap.triplePhase.TripleSimilarityService;

public abstract interface IFusionService
{
  public static final int SORT_ALPHABET = 0;
  public static final int SORT_COMBINED = 4;
  public static final int SORT_CONFIDENCE = 1;
  public static final int SORT_POPULARITY = 2;
  public static final int SORT_SYNSET = 3;
  public static final int MAX_NUMBER_OF_ANSWERS_SYNONYMS = 200;
  public static final int MAX_NUMBER_OF_ANSWERS_COMPARE_STRICT = 500;
  public static final int MAX_NUMBER_OF_ANSWERS_COMPARE_MILD = 100;
  
  public abstract void formRDFEntityEntries(QueryTriple paramQueryTriple);
  
  public abstract Map<QueryTriple, FusedAnswerBean> getAnswerBeanMap();
  
  public abstract FusedAnswerBean getFinalAnswerBean();
  
  public abstract FusedAnswerBean getFinalAnswerBeanSortedBy(int paramInt);
  
  public abstract int getNumberOfComparisons();
  
  public abstract int getNumberOfLuceneSearchCalls();
  
  public abstract TripleSimilarityService getTripleSimilarityService();
  
  public abstract void mergeByQueryTriples();
  
  public abstract void setTripleSimilarityService(TripleSimilarityService paramTripleSimilarityService);
}

