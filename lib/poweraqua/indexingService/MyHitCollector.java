package poweraqua.indexingService;

import java.util.Comparator;
import org.apache.lucene.search.TopDocCollector;

public class MyHitCollector
  extends TopDocCollector
{
  private double maxScore;
  private int numHits;
  
  public class CollectorBean
  {
    private Integer docId;
    private Float score;
    
    public CollectorBean(Integer docId, Float score)
    {
      this.docId = docId;
      this.score = score;
    }
    
    public Integer getDocId()
    {
      return this.docId;
    }
    
    public Float getScore()
    {
      return this.score;
    }
  }
  
  public class CollectorBeanComparator
    implements Comparator<MyHitCollector.CollectorBean>
  {
    public CollectorBeanComparator() {}
    
    public int compare(MyHitCollector.CollectorBean c1, MyHitCollector.CollectorBean c2)
      throws ClassCastException
    {
      int r1 = (int)(c1.getScore().floatValue() * 1000.0F);
      int r2 = (int)(c2.getScore().floatValue() * 1000.0F);
      
      return r2 - r1;
    }
  }
  
  public MyHitCollector(int numHits, float score)
  {
    super(numHits);
    this.maxScore = score;
    this.numHits = numHits;
  }
  
  public void collect(int doc, float score)
  {
    score /= 10.0F;
    if (getTotalHits() >= this.numHits) {
      throw new LimitExceeded(doc);
    }
    if (score > this.maxScore) {
      super.collect(doc, score);
    }
  }
  
  public static class LimitExceeded
    extends RuntimeException
  {
    private int maxDoc;
    
    public LimitExceeded(int maxDoc)
    {
      this.maxDoc = maxDoc;
    }
  }
}

