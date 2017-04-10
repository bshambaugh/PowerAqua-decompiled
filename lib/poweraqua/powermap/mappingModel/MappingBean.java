package poweraqua.powermap.mappingModel;

import java.io.BufferedWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.query.QueryElementsBean;

public class MappingBean
{
  private WordNetBean WordNetBean;
  private boolean find_hypernyms = true;
  private boolean is_queryTerm = false;
  private EntityMappingTable entityMappingTable;
  public static final String separator_AND = "AND";
  public static final String separator_OR = "OR";
  private String separator = "AND";
  private String realpath;
  private RecyclingBean recyclingBean;
  
  public MappingBean(String keyword, ArrayList<String> restrictedKeywords, String realpath, boolean isqueryTerm)
  {
    try
    {
      this.realpath = realpath;
      this.WordNetBean = new WordNetBean(keyword, realpath);
      setEntityMappingTable(new EntityMappingTable(keyword));
      this.entityMappingTable.setRestrictedKeywords(restrictedKeywords);
      this.recyclingBean = new RecyclingBean(keyword);
      setIs_queryTerm(isqueryTerm);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public MappingBean(String keyword, String realpath, boolean isqueryTerm)
  {
    try
    {
      this.realpath = realpath;
      this.WordNetBean = new WordNetBean(keyword, realpath);
      setEntityMappingTable(new EntityMappingTable(keyword));
      this.recyclingBean = new RecyclingBean(keyword);
      setIs_queryTerm(isqueryTerm);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public void setKeyword(String keyword)
  {
    try
    {
      setWordNetBean(new WordNetBean(keyword, this.realpath));
      setEntityMappingTable(new EntityMappingTable(keyword));
      this.recyclingBean = new RecyclingBean(keyword);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private void mergeKeyword(String keyword)
  {
    getEntityMappingTable().mergeKeyword(keyword);
  }
  
  public void mergeBean(MappingBean bean2)
  {
    mergeKeyword(bean2.getKeyword());
    for (String onto : bean2.getEntityMappingTable().getOntologyIDMappings()) {
      getEntityMappingTable().addMappingList(bean2.getEntityMappingTable().getOntologyMappings(onto));
    }
    getEntityMappingTable().setExactMappingsToFalse();
  }
  
  public String getKeyword()
  {
    return this.entityMappingTable.getKeyword();
  }
  
  public EntityMappingTable getEntityMappingTable()
  {
    return this.entityMappingTable;
  }
  
  public void setEntityMappingTable(EntityMappingTable entityMappingTable)
  {
    this.entityMappingTable = entityMappingTable;
  }
  
  public static ArrayList<MappingBean> getMappingBeans(QueryElementsBean queryElements, String realPath, boolean isqueryTerm)
  {
    ArrayList<MappingBean> mappingBeans = new ArrayList();
    for (String query : queryElements.getQueryElements())
    {
      MappingBean mappingBean = new MappingBean(query, realPath, isqueryTerm);
      mappingBeans.add(mappingBean);
    }
    return mappingBeans;
  }
  
  public String getSeparator()
  {
    return this.separator;
  }
  
  public void print(String key)
  {
    String word = getKeyword();
    
    ArrayList<SearchSemanticResult> SSRs = getEntityMappingTable().getOntologyMappings(key);
    if ((SSRs != null) && (!SSRs.isEmpty()))
    {
      System.out.println("WORD.......... " + word);
      for (SearchSemanticResult SSR : SSRs) {
        SSR.print();
      }
    }
  }
  
  public String printString()
  {
    String word = getKeyword();
    String res = new String();
    for (String key : getEntityMappingTable().getOntologyIDMappings())
    {
      ArrayList<SearchSemanticResult> SSRs = getEntityMappingTable().getOntologyMappings(key);
      if ((SSRs != null) && (!SSRs.isEmpty()))
      {
        res = res.concat("\n" + new String(new StringBuilder().append("WORD.......... ").append(word).toString()));
        for (SearchSemanticResult SSR : SSRs) {
          res = res.concat(SSR.printString());
        }
      }
    }
    return res;
  }
  
  public String printString(String key)
  {
    String word = getKeyword();
    String res = new String();
    ArrayList<SearchSemanticResult> SSRs = getEntityMappingTable().getOntologyMappings(key);
    if ((SSRs != null) && (!SSRs.isEmpty()))
    {
      res = new String("WORD.......... " + word);
      for (SearchSemanticResult SSR : SSRs) {
        res = res.concat(SSR.printString());
      }
    }
    return res;
  }
  
  public void printShort(String key)
  {
    String word = getKeyword();
    
    ArrayList<SearchSemanticResult> SSRs = getEntityMappingTable().getOntologyMappings(key);
    if ((SSRs != null) && (!SSRs.isEmpty()))
    {
      System.out.println("WORD.......... " + word + " mappings: " + SSRs.size());
      for (SearchSemanticResult SSR : SSRs) {
        System.out.println(SSR.getEntity().getURI() + " : " + SSR.getEntity().getLabel() + " : " + SSR.getScore() + " : " + SSR.getEntity().getType() + " : " + SSR.getSemanticRelation() + " : " + SSR.getDirectParents().getUris().toString() + " : " + SSR.getSuperclasses().getUris().toString());
      }
    }
  }
  
  public void printShortToFile(BufferedWriter out, String key)
  {
    try
    {
      String word = getKeyword();
      ArrayList<SearchSemanticResult> SSRs = getEntityMappingTable().getOntologyMappings(key);
      if ((SSRs != null) && (!SSRs.isEmpty()))
      {
        out.write("WORD.......... " + word + " mappings: " + SSRs.size() + "\n");
        out.write("*******************************************\n");
        for (SearchSemanticResult SSR : SSRs)
        {
          out.write("Mappings for key " + key + "\n");
          
          out.write(SSR.getEntity().getURI() + " : " + SSR.getEntity().getLabel() + " : " + SSR.getScore() + " : " + SSR.getEntity().getType() + " : " + SSR.getSemanticRelation() + " : " + SSR.getDirectParents().getUris().toString() + " : " + SSR.getSuperclasses().getUris().toString() + "\n");
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public RecyclingBean getRecyclingBean()
  {
    return this.recyclingBean;
  }
  
  public boolean isFind_hypernyms()
  {
    return this.find_hypernyms;
  }
  
  public void setFind_hypernyms(boolean find_WNhypernyms)
  {
    this.find_hypernyms = find_WNhypernyms;
  }
  
  public boolean isIs_wordnet()
  {
    return this.WordNetBean.isIs_wordnet();
  }
  
  public boolean isIs_wordnet(boolean compound)
  {
    boolean is_wordnet = this.WordNetBean.isIs_wordnet();
    if (!is_wordnet) {
      return false;
    }
    if ((compound) && (this.WordNetBean.getWN_lemma().equalsIgnoreCase(getKeyword()))) {
      return true;
    }
    String singular = getKeyword();
    singular.replaceAll("\"", "");
    if (this.WordNetBean.getWN_lemma().length() + 3 < singular.length())
    {
      System.out.println("lemma " + this.WordNetBean.getWN_lemma() + " too different to keyword " + singular);
      
      return false;
    }
    return true;
  }
  
  public boolean isIs_queryTerm()
  {
    return this.is_queryTerm;
  }
  
  private void setIs_queryTerm(boolean is_queryTerm)
  {
    this.is_queryTerm = is_queryTerm;
  }
  
  public String getRealpath()
  {
    return this.realpath;
  }
  
  public WordNetBean getWordNetBean()
  {
    return this.WordNetBean;
  }
  
  public void setWordNetBean(WordNetBean WordNetBean)
  {
    this.WordNetBean = WordNetBean;
  }
}

