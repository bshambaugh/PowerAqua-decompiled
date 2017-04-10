package poweraqua.WordNetJWNL;

import java.util.ArrayList;
import java.util.Hashtable;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;

public class WNSynsetSetBean
{
  private Hashtable<String, WNSynsetBean> synsetSetBean;
  private ArrayList<WNSynsetSimilarity> synsetSimilarityList;
  
  public WNSynsetSetBean()
  {
    this.synsetSetBean = new Hashtable();
    this.synsetSimilarityList = new ArrayList();
  }
  
  public void addSynsetBean(WNSynsetBean synsetBean)
  {
    String indice = getIDfromSynset(synsetBean.getSynset());
    if (indice == null) {
      return;
    }
    if (getSynsetSetBean().containsKey(indice))
    {
      WNSynsetBean repetition = (WNSynsetBean)getSynsetSetBean().get(indice);
      if (synsetBean.getSimilarity() > repetition.getSimilarity()) {
        getSynsetSetBean().put(indice, synsetBean);
      }
      if ((synsetBean.getSimilarity() == repetition.getSimilarity()) && (synsetBean.isGloss_similarity())) {
        getSynsetSetBean().put(indice, synsetBean);
      }
    }
    else
    {
      getSynsetSetBean().put(indice, synsetBean);
    }
  }
  
  public ArrayList<Synset> getSynsetList()
  {
    ArrayList<Synset> synsetList = new ArrayList();
    for (WNSynsetBean synsetBean : getSynsetSetBean().values()) {
      synsetList.add(synsetBean.getSynset());
    }
    return synsetList;
  }
  
  public void addSynsetSimilarity(WNSynsetSimilarity synsetSimilarity)
  {
    getSynsetSimilarityList().add(synsetSimilarity);
  }
  
  public static String getIDfromSynset(Synset synset)
  {
    if (synset == null) {
      return null;
    }
    return new String(synset.getPOS().toString() + "_" + synset.getOffset());
  }
  
  public static long getOffsetfromSynset(Synset synset)
  {
    return synset.getOffset();
  }
  
  public ArrayList<WNSynsetBean> getAllSynsetBean()
  {
    ArrayList<WNSynsetBean> res = new ArrayList();
    for (WNSynsetBean synsetBean : getSynsetSetBean().values()) {
      res.add(synsetBean);
    }
    return res;
  }
  
  public ArrayList<WNSynsetSimilarity> getSynsetSimilarityList()
  {
    return this.synsetSimilarityList;
  }
  
  public Hashtable<String, WNSynsetBean> getSynsetSetBean()
  {
    return this.synsetSetBean;
  }
}

