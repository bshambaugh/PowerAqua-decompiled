package poweraqua.WordNetJWNL;

import net.sf.extjwnl.data.Synset;

public class WNSynsetBean
{
  private double similarity;
  private boolean gloss_similarity;
  private Synset synset;
  
  public WNSynsetBean(double similarity, boolean gloss_similarity, Synset synset)
  {
    this.similarity = similarity;
    this.gloss_similarity = gloss_similarity;
    this.synset = synset;
  }
  
  public WNSynsetBean(Synset synset)
  {
    this.similarity = 1.0D;
    this.gloss_similarity = true;
    this.synset = synset;
  }
  
  public double getSimilarity()
  {
    return this.similarity;
  }
  
  public boolean isGloss_similarity()
  {
    return this.gloss_similarity;
  }
  
  public Synset getSynset()
  {
    return this.synset;
  }
}
