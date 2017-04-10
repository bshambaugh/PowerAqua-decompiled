package poweraqua.WordNetJWNL;

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.PointerTarget;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTree;
import net.sf.extjwnl.data.relationship.AsymmetricRelationship;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;
import poweraqua.core.utils.StringUtils;

public class WordNet
{
  private static Dictionary WNdictionary;
  public String term;
  public IndexWord termIndex;
  public IndexWord termIndexAdj;
  public int pos;
  public int num_senses;
  private PointerTargetNodeList meronyms;
  private PointerTargetNodeList hypernyms;
  private PointerTargetNodeList hyponyms;
  private PointerTargetNodeList holonyms;
  private PointerTargetNodeList synonyms;
  private PointerTargetNodeList derived;
  private ArrayList<String> synonymWords;
  private ArrayList<String> hypernymWords;
  private ArrayList<String> hyponymWords;
  private ArrayList<String> derivedWords;
  private ArrayList<String> meronymWords;
  private String[] holonymWords;
  private static String realPath = "./";
  private static String jaws_wordnet = "wordnet-mac/3.0/dict/";
  private static WordNetDatabase database;
  private final Lock lock = new ReentrantLock();
  int NOUN = 1;
  int VERB = 2;
  int ADJECTIVE = 3;
  int ADVERB = 4;
  
  public WordNet()
    throws Exception
  {}
  
  public WordNet(String realpath)
    throws Exception
  {
    realPath = realpath;
  }
  
  public static void OpenDictionary(String realpath)
    throws Exception
  {
    String propsFile = realpath + "file_properties.xml";
    System.out.println("Initializing WORDNET " + propsFile);
    
    WNdictionary = Dictionary.getInstance(new FileInputStream(propsFile));
    realPath = realpath;
    database = WordNetDatabase.getFileInstance();
    System.setProperty("wordnet.database.dir", realPath + jaws_wordnet);
  }
  
  public static void OpenDictionary()
    throws Exception
  {
    if (WNdictionary == null)
    {
      String propsFile = "file_properties.xml";
      
      WNdictionary = Dictionary.getInstance(new FileInputStream(propsFile));
      database = WordNetDatabase.getFileInstance();
      System.setProperty("wordnet.database.dir", realPath + jaws_wordnet);
    }
  }
  
  public boolean Initialize(String term)
    throws Exception
  {
    if (!is_parsedEntryWN(term)) {
      return false;
    }
    try
    {
      this.term = term.toLowerCase().trim();
      this.term = URLDecoder.decode(this.term, "UTF-8");
      this.term = StringUtils.RemoveDiacritics(this.term);
      
      this.lock.lock();
      try
      {
        this.termIndexAdj = WNdictionary.lookupIndexWord(POS.ADJECTIVE, this.term);
      }
      finally
      {
        this.lock.unlock();
      }
      this.lock.lock();
      try
      {
        this.termIndex = WNdictionary.lookupIndexWord(POS.NOUN, this.term);
      }
      finally
      {
        this.lock.unlock();
      }
      if ((this.termIndex != null) && (this.termIndex.getLemma().length() > 1))
      {
        this.num_senses = this.termIndex.getSenses().size();
        
        this.pos = this.NOUN;
        return true;
      }
      this.lock.lock();
      try
      {
        System.out.println("WN: look up verb for " + this.term);
        this.termIndex = WNdictionary.lookupIndexWord(POS.VERB, this.term);
      }
      finally
      {
        this.lock.unlock();
      }
      if ((this.termIndex != null) && (this.termIndex.getLemma().length() > 1))
      {
        this.num_senses = this.termIndex.getSenses().size();
        
        this.pos = this.VERB;
        return true;
      }
      if ((this.termIndexAdj != null) && (this.termIndexAdj.getLemma().length() > 1))
      {
        this.num_senses = this.termIndexAdj.getSenses().size();
        
        this.pos = this.ADJECTIVE;
        return true;
      }
      this.lock.lock();
      try
      {
        System.out.println("WN: look up adverb for " + this.term);
        this.termIndex = WNdictionary.lookupIndexWord(POS.ADVERB, this.term);
      }
      finally
      {
        this.lock.unlock();
      }
      if ((this.termIndex != null) && (this.termIndex.getLemma().length() > 1))
      {
        this.num_senses = this.termIndex.getSenses().size();
        
        this.pos = this.ADVERB;
        return true;
      }
      return false;
    }
    catch (Exception ex)
    {
      System.out.println("Exception initializing WN for " + this.term + " : " + ex);
    }
    return false;
  }
  
  public boolean isIs_wordnetCompound()
  {
    if (!StringUtils.isCompound(this.term)) {
      return true;
    }
    if (this.termIndex.getLemma().equalsIgnoreCase(this.term)) {
      return true;
    }
    String singular = this.term;
    singular.replaceAll("\"", "");
    if (this.termIndex.getLemma().length() + 3 < singular.length())
    {
      System.out.println("lemma " + this.termIndex.getLemma() + " too different to keyword " + singular);
      
      return false;
    }
    return true;
  }
  
  public net.sf.extjwnl.data.Synset getSynsetfromID(String id)
    throws Exception
  {
    int i = id.indexOf("_");
    String pos = id.substring(0, i);
    long offset = Long.parseLong(id.substring(i + 1, id.length()));
    return getSynsetfromOffset(pos, offset);
  }
  
  public net.sf.extjwnl.data.Synset getSynsetfromOffset(String pos, long offset)
    throws Exception
  {
    if (pos.equals("NOUN")) {
      return WNdictionary.getSynsetAt(POS.NOUN, offset);
    }
    if (pos.equals("VERB")) {
      return WNdictionary.getSynsetAt(POS.VERB, offset);
    }
    if (pos.equals("ADJECTIVE")) {
      return WNdictionary.getSynsetAt(POS.ADJECTIVE, offset);
    }
    if (pos.equals("ADVERB")) {
      return WNdictionary.getSynsetAt(POS.ADVERB, offset);
    }
    return null;
  }
  
  public boolean is_parsedEntryWN(String termino)
  {
    if ((termino.indexOf(".") > -1) || (termino.indexOf(":") > -1) || (termino.length() > 40)) {
      return false;
    }
    if (termino.indexOf("$") > -1) {
      return false;
    }
    if (StringUtils.countCompounds(termino) > 4) {
      return false;
    }
    return true;
  }
  
  public ArrayList<String> isWordNetInput(String termino)
    throws Exception
  {
    if (WNdictionary == null) {
      throw new Exception("WordNet dictionary is null!");
    }
    ArrayList<String> lemmas = new ArrayList();
    if (!is_parsedEntryWN(termino)) {
      return lemmas;
    }
    try
    {
      IndexWord word = null;
      List list1 = null;
      
      int compounds_num = StringUtils.countCompounds(termino);
      this.lock.lock();
      try
      {
        word = WNdictionary.lookupIndexWord(POS.NOUN, termino);
        if ((word != null) && (compounds_num == 2)) {
          list1 = WNdictionary.getMorphologicalProcessor().lookupAllBaseForms(POS.NOUN, termino);
        }
      }
      finally
      {
        this.lock.unlock();
      }
      if (word != null) {
        lemmas.add(word.getLemma());
      }
      if (word == null) {
        return lemmas;
      }
      if (compounds_num > 2) {
        return lemmas;
      }
      if (list1 != null) {
        for (int i = 0; i < list1.size(); i++)
        {
          String element = (String)list1.get(i);
          if ((!lemmas.contains(element)) && (element.length() > 1)) {
            lemmas.add(element);
          }
        }
      }
    }
    catch (Exception e)
    {
      System.out.println("jwnl on lookupAllBaseForms for " + termino + " : " + e.getMessage());
    }
    return lemmas;
  }
  
  public String getPluralNoun(String noun_term)
    throws Exception
  {
    return getPluralNoun(noun_term, true);
  }
  
  public String getPluralNoun(String noun_term, boolean isWN)
    throws Exception
  {
    boolean plural = false;
    if (noun_term.endsWith("s")) {
      plural = true;
    }
    if (noun_term.endsWith("s\"")) {
      plural = true;
    }
    if (plural) {
      return noun_term;
    }
    String lastCompound = noun_term;
    if (StringUtils.isCompound(noun_term))
    {
      int index = -1;
      int index_aux = noun_term.lastIndexOf(" ");
      if (index_aux > index) {
        index = index_aux;
      }
      index_aux = noun_term.lastIndexOf("_");
      if (index_aux > index) {
        index = index_aux;
      }
      index_aux = noun_term.lastIndexOf("-");
      if (index_aux > index) {
        index = index_aux;
      }
      lastCompound = noun_term.substring(index, noun_term.length()).trim();
    }
    else if (getUniqueLemma() != null)
    {
      lastCompound = getUniqueLemma();
    }
    String queryCompound = StringUtils.SingularToPlural(lastCompound);
    
    boolean wordnet = false;
    IndexWord pluralWord = null;
    this.lock.lock();
    try
    {
      if (isWN) {
        pluralWord = WNdictionary.lookupIndexWord(POS.NOUN, queryCompound);
      }
    }
    finally
    {
      this.lock.unlock();
    }
    if (pluralWord != null) {
      wordnet = true;
    } else {
      return noun_term;
    }
    plural = false;
    if ((wordnet) && (pluralWord.getLemma().equalsIgnoreCase(lastCompound))) {
      plural = true;
    } else {
      return noun_term;
    }
    if (!StringUtils.isCompound(noun_term)) {
      return queryCompound;
    }
    return noun_term.replace(lastCompound, queryCompound).trim();
  }
  
  public String getSingularNoun(String noun_term)
    throws Exception
  {
    return getSingularNoun(noun_term, true);
  }
  
  public String getSingularNoun(String noun_term, boolean isWN)
    throws Exception
  {
    boolean plural = false;
    if (noun_term.endsWith("s")) {
      plural = true;
    }
    if (noun_term.endsWith("s\"")) {
      plural = true;
    }
    if (!plural) {
      return noun_term;
    }
    if ((noun_term.endsWith("s")) && (!noun_term.endsWith("es"))) {
      return noun_term.substring(0, noun_term.length() - 1);
    }
    if ((noun_term.endsWith("s\"")) && (!noun_term.endsWith("es\"")))
    {
      String queryTerm = noun_term.substring(0, noun_term.length() - 2);
      queryTerm = queryTerm.concat("\"");
      return queryTerm;
    }
    if (StringUtils.isCompound(noun_term))
    {
      int index = -1;
      int index_aux = noun_term.lastIndexOf(" ");
      if (index_aux > index) {
        index = index_aux;
      }
      index_aux = noun_term.lastIndexOf("_");
      if (index_aux > index) {
        index = index_aux;
      }
      index_aux = noun_term.lastIndexOf("-");
      if (index_aux > index) {
        index = index_aux;
      }
      String queryTerm;
      String lastCompound;
      String queryTerm;
      if (noun_term.endsWith("es\""))
      {
        String lastCompound = noun_term.substring(index, noun_term.length() - 1);
        queryTerm = noun_term.substring(index, noun_term.length() - 2);
      }
      else
      {
        lastCompound = noun_term.substring(index, noun_term.length()).trim();
        queryTerm = noun_term.substring(index, noun_term.length() - 1).trim();
      }
      boolean wordnet = false;
      this.lock.lock();
      try
      {
        if ((isWN) && (WNdictionary.lookupIndexWord(POS.NOUN, queryTerm) != null)) {
          wordnet = true;
        }
      }
      finally
      {
        this.lock.unlock();
      }
      if (!wordnet) {
        queryTerm = queryTerm.substring(0, queryTerm.length() - 1).trim();
      }
      return noun_term.replaceAll(lastCompound, queryTerm);
    }
    String queryTerm = noun_term.substring(0, noun_term.length() - 1);
    boolean wordnet = false;
    this.lock.lock();
    try
    {
      if ((isWN) && (WNdictionary.lookupIndexWord(POS.NOUN, queryTerm) != null)) {
        wordnet = true;
      }
    }
    finally
    {
      this.lock.unlock();
    }
    if (wordnet) {
      return queryTerm;
    }
    return noun_term.substring(0, noun_term.length() - 2);
  }
  
  public void getLexicalRelatedWords()
    throws Exception
  {
    this.synonymWords = getSynonyms();
    this.hypernymWords = getHypernyms();
    this.hyponymWords = getHyponyms();
  }
  
  private IndexWord getIndexWord(String termino)
    throws Exception
  {
    int pos = MorphologicalAnalysis(termino);
    if (pos == this.NOUN)
    {
      this.lock.lock();
      IndexWord res;
      try
      {
        res = WNdictionary.lookupIndexWord(POS.NOUN, termino);
      }
      finally
      {
        this.lock.unlock();
      }
      return res;
    }
    if (pos == this.VERB)
    {
      this.lock.lock();
      IndexWord res;
      try
      {
        res = WNdictionary.lookupIndexWord(POS.VERB, termino);
      }
      finally
      {
        this.lock.unlock();
      }
      return res;
    }
    if (pos == this.ADJECTIVE)
    {
      this.lock.lock();
      IndexWord res;
      try
      {
        res = WNdictionary.lookupIndexWord(POS.ADJECTIVE, termino);
      }
      finally
      {
        this.lock.unlock();
      }
      return res;
    }
    if (pos == this.ADVERB)
    {
      this.lock.lock();
      IndexWord res;
      try
      {
        res = WNdictionary.lookupIndexWord(POS.ADVERB, termino);
      }
      finally
      {
        this.lock.unlock();
      }
      return res;
    }
    return null;
  }
  
  private IndexWord getIndexWord(String termino, int pos)
    throws Exception
  {
    if (pos == this.NOUN)
    {
      this.lock.lock();
      IndexWord res;
      try
      {
        res = WNdictionary.lookupIndexWord(POS.NOUN, termino);
      }
      finally
      {
        this.lock.unlock();
      }
      return res;
    }
    if (pos == this.VERB)
    {
      this.lock.lock();
      IndexWord res;
      try
      {
        res = WNdictionary.lookupIndexWord(POS.VERB, termino);
      }
      finally
      {
        this.lock.unlock();
      }
      return res;
    }
    if (pos == this.ADJECTIVE)
    {
      this.lock.lock();
      IndexWord res;
      try
      {
        res = WNdictionary.lookupIndexWord(POS.ADJECTIVE, termino);
      }
      finally
      {
        this.lock.unlock();
      }
      return res;
    }
    if (pos == this.ADVERB)
    {
      this.lock.lock();
      IndexWord res;
      try
      {
        res = WNdictionary.lookupIndexWord(POS.ADVERB, termino);
      }
      finally
      {
        this.lock.unlock();
      }
      return res;
    }
    return null;
  }
  
  public Object[] getSynsets()
    throws Exception
  {
    List<net.sf.extjwnl.data.Synset> syn = this.termIndex.getSenses();
    ArrayList v = new ArrayList();
    for (int i = 0; i < syn.size(); i++)
    {
      v.add(((net.sf.extjwnl.data.Synset)syn.get(i)).getKey());
      System.out.println(((net.sf.extjwnl.data.Synset)syn.get(i)).getKey());
      System.out.println(((net.sf.extjwnl.data.Synset)syn.get(i)).getGloss());
    }
    Object[] synsets = new Object[v.size()];
    v.toArray(synsets);
    return synsets;
  }
  
  public List<net.sf.extjwnl.data.Synset> getSynset()
    throws Exception
  {
    return this.termIndex.getSenses();
  }
  
  private int MorphologicalAnalysis(String term)
    throws JWNLException
  {
    this.lock.lock();
    IndexWordSet morph;
    try
    {
      morph = WNdictionary.lookupAllIndexWords(term);
    }
    finally
    {
      this.lock.unlock();
    }
    int pos = 0;
    if (morph.isValidPOS(POS.NOUN)) {
      pos = this.NOUN;
    } else if (morph.isValidPOS(POS.VERB)) {
      pos = this.VERB;
    } else if (morph.isValidPOS(POS.ADJECTIVE)) {
      pos = this.ADJECTIVE;
    } else if (morph.isValidPOS(POS.ADVERB)) {
      pos = this.ADVERB;
    }
    return pos;
  }
  
  public void DirectHypernymsOperation(int sense)
    throws JWNLException
  {
    this.hypernyms = PointerUtils.getDirectHypernyms((net.sf.extjwnl.data.Synset)this.termIndex.getSenses().get(sense));
  }
  
  public void DirectHyponymsOperation(int sense)
    throws JWNLException
  {
    this.hyponyms = PointerUtils.getDirectHyponyms((net.sf.extjwnl.data.Synset)this.termIndex.getSenses().get(sense));
  }
  
  private String[] getFirstMeronymWords(net.sf.extjwnl.data.Synset sense)
    throws Exception
  {
    List<Pointer> pointers = sense.getPointers();
    ArrayList v = new ArrayList();
    for (Pointer pointer : pointers) {
      if (pointer.getType().getLabel().equals("member meronym"))
      {
        List<Word> sw = pointer.getTargetSynset().getWords();
        if ((sw != null) && (sw.size() > 0)) {
          if ((!v.contains(((Word)sw.get(0)).getLemma())) && (!((Word)sw.get(0)).getLemma().equalsIgnoreCase(this.termIndex.getLemma()))) {
            if (!StringUtils.isCompound(this.term)) {
              v.add(((Word)sw.get(0)).getLemma().replace(" ", "-"));
            } else {
              v.add(this.term.replace(this.termIndex.getLemma(), ((Word)sw.get(0)).getLemma()).replace(" ", "-"));
            }
          }
        }
      }
    }
    if (v == null) {
      return null;
    }
    String[] words = new String[v.size()];
    v.toArray(words);
    return words;
  }
  
  private String[] getDerivedWords(net.sf.extjwnl.data.Synset sense)
    throws Exception
  {
    List<Pointer> pointers = sense.getPointers();
    ArrayList v = new ArrayList();
    for (Pointer pointer : pointers) {
      if ((pointer.getType().getLabel().equals("derived")) || (pointer.getType().getLabel().equals("pertainym")) || (pointer.getType().getLabel().equals("nominalization")))
      {
        List<Word> sw = pointer.getTargetSynset().getWords();
        if ((sw != null) && 
          (this.termIndex != null))
        {
          int limit = 1;
          for (int x = 0; x < sw.size(); x++)
          {
            if ((x >= limit) && (pointer.getType().getLabel().equals("nominalization"))) {
              break;
            }
            if ((!v.contains(((Word)sw.get(x)).getLemma())) && (!((Word)sw.get(x)).getLemma().equalsIgnoreCase(this.termIndex.getLemma()))) {
              if (this.pos == 3)
              {
                if (!StringUtils.isCompound(this.term)) {
                  v.add(((Word)sw.get(x)).getLemma().replace(" ", "-"));
                } else {
                  v.add(this.term.replace(this.termIndexAdj.getLemma(), ((Word)sw.get(x)).getLemma()).replace(" ", "-"));
                }
              }
              else if (!StringUtils.isCompound(this.term)) {
                v.add(((Word)sw.get(x)).getLemma().replace(" ", "-"));
              } else {
                v.add(this.term.replace(this.termIndex.getLemma(), ((Word)sw.get(x)).getLemma()).replace(" ", "-"));
              }
            }
          }
        }
      }
    }
    if (v == null) {
      return null;
    }
    String[] words = new String[v.size()];
    v.toArray(words);
    return words;
  }
  
  private String[] getHypernymsWordsLemma(int sense)
    throws Exception
  {
    DirectHypernymsOperation(sense);
    
    ArrayList v = new ArrayList();
    Iterator it = this.hypernyms.iterator();
    while (it.hasNext())
    {
      PointerTargetNode key = (PointerTargetNode)it.next();
      net.sf.extjwnl.data.Synset s = key.getSynset();
      List<Word> sw = s.getWords();
      if (sw != null) {
        for (int x = 0; x < sw.size(); x++) {
          if ((!v.contains(((Word)sw.get(x)).getLemma())) && (!((Word)sw.get(x)).getLemma().equalsIgnoreCase(this.termIndex.getLemma()))) {
            v.add(((Word)sw.get(x)).getLemma());
          }
        }
      }
    }
    if (v == null) {
      return null;
    }
    String[] words = new String[v.size()];
    v.toArray(words);
    return words;
  }
  
  private String[] getHypernymsWords(int sense)
    throws Exception
  {
    DirectHypernymsOperation(sense);
    
    ArrayList v = new ArrayList();
    Iterator it = this.hypernyms.iterator();
    while (it.hasNext())
    {
      PointerTargetNode key = (PointerTargetNode)it.next();
      net.sf.extjwnl.data.Synset s = key.getSynset();
      List<Word> sw = s.getWords();
      if (sw != null) {
        for (int x = 0; x < sw.size(); x++) {
          if ((!v.contains(((Word)sw.get(x)).getLemma())) && (!((Word)sw.get(x)).getLemma().equalsIgnoreCase(this.termIndex.getLemma()))) {
            if (StringUtils.isCompound(this.term)) {
              v.add(this.term.replace(this.termIndex.getLemma(), ((Word)sw.get(x)).getLemma()).replace(" ", "-"));
            } else {
              v.add(((Word)sw.get(x)).getLemma());
            }
          }
        }
      }
    }
    if (v == null) {
      return null;
    }
    String[] words = new String[v.size()];
    v.toArray(words);
    return words;
  }
  
  private String[] getHyponymsWords(int sense)
    throws Exception
  {
    DirectHyponymsOperation(sense);
    
    ArrayList v = new ArrayList();
    Iterator it = this.hyponyms.iterator();
    while (it.hasNext())
    {
      PointerTargetNode key = (PointerTargetNode)it.next();
      net.sf.extjwnl.data.Synset s = key.getSynset();
      List<Word> sw = s.getWords();
      if (sw != null) {
        for (int x = 0; x < sw.size(); x++) {
          if ((!v.contains(((Word)sw.get(x)).getLemma())) && (!((Word)sw.get(x)).getLemma().equalsIgnoreCase(this.termIndex.getLemma()))) {
            if (StringUtils.isCompound(this.term)) {
              v.add(this.term.replace(this.termIndex.getLemma(), ((Word)sw.get(x)).getLemma()).replace(" ", "-"));
            } else {
              v.add(((Word)sw.get(x)).getLemma());
            }
          }
        }
      }
    }
    String[] words = new String[v.size()];
    v.toArray(words);
    return words;
  }
  
  public String[] getHolonymsWords(int sense)
    throws Exception
  {
    HolonymsOperation(sense);
    
    ArrayList v = new ArrayList();
    Iterator it = this.holonyms.iterator();
    while (it.hasNext())
    {
      PointerTargetNode key = (PointerTargetNode)it.next();
      net.sf.extjwnl.data.Synset s = key.getSynset();
      List<Word> sw = s.getWords();
      if (sw != null) {
        for (int x = 0; x < sw.size(); x++) {
          if (!v.contains(((Word)sw.get(x)).getLemma())) {
            v.add(((Word)sw.get(x)).getLemma());
          }
        }
      }
    }
    String[] words = new String[v.size()];
    v.toArray(words);
    this.holonymWords = words;
    return words;
  }
  
  public ArrayList<String> getHyponyms()
    throws Exception
  {
    ArrayList<String> hyponyms = new ArrayList();
    if (this.termIndex != null)
    {
      for (int i = 0; i < this.num_senses; i++)
      {
        String[] aux = getHyponymsWords(i);
        if (aux != null) {
          for (int h = 0; h < aux.length; h++) {
            if (!hyponyms.contains(aux[h].toLowerCase())) {
              hyponyms.add(aux[h].toLowerCase());
            }
          }
        }
      }
      this.hyponymWords = hyponyms;
    }
    return hyponyms;
  }
  
  public ArrayList<String> getDerived()
    throws Exception
  {
    ArrayList<String> derived = new ArrayList();
    if ((this.pos == 3) || (this.pos == 2) || (this.termIndexAdj != null))
    {
      List<net.sf.extjwnl.data.Synset> senses = null;
      if (this.pos == 2) {
        senses = this.termIndex.getSenses();
      } else if ((this.pos == 3) || (this.termIndexAdj != null)) {
        senses = this.termIndexAdj.getSenses();
      }
      if (senses == null)
      {
        this.derivedWords = derived;
        return derived;
      }
      for (net.sf.extjwnl.data.Synset sense : senses)
      {
        String[] aux = getDerivedWords(sense);
        if (aux != null) {
          for (int h = 0; h < aux.length; h++) {
            if (!derived.contains(aux[h].toLowerCase())) {
              derived.add(aux[h].toLowerCase());
            }
          }
        }
      }
    }
    this.derivedWords = derived;
    return derived;
  }
  
  public ArrayList<String> getFirstMeronyms()
    throws Exception
  {
    ArrayList<String> meronyms = new ArrayList();
    if (this.termIndex != null)
    {
      List<net.sf.extjwnl.data.Synset> senses = this.termIndex.getSenses();
      for (net.sf.extjwnl.data.Synset sense : senses)
      {
        String[] aux = getFirstMeronymWords(sense);
        if (aux != null) {
          for (int h = 0; h < aux.length; h++) {
            if (!meronyms.contains(aux[h].toLowerCase())) {
              meronyms.add(aux[h].toLowerCase());
            }
          }
        }
      }
    }
    this.meronymWords = meronyms;
    return meronyms;
  }
  
  public ArrayList<String> getHypernymsLemma()
    throws Exception
  {
    boolean isInstance = isInstanceHypernyms();
    
    ArrayList<String> hypernyms = new ArrayList();
    if (isInstance) {
      return hypernyms;
    }
    if (this.termIndex != null) {
      for (int i = 0; i < this.num_senses; i++)
      {
        String[] aux = getHypernymsWordsLemma(i);
        if (aux != null) {
          for (int h = 0; h < aux.length; h++) {
            if (!hypernyms.contains(aux[h].toLowerCase())) {
              hypernyms.add(aux[h].toLowerCase());
            }
          }
        }
      }
    }
    this.hypernymWords = hypernyms;
    return hypernyms;
  }
  
  public ArrayList<String> getHypernyms()
    throws Exception
  {
    boolean isInstance = isInstanceHypernyms();
    
    ArrayList<String> hypernyms = new ArrayList();
    if (isInstance) {
      return hypernyms;
    }
    if (this.termIndex != null) {
      for (int i = 0; i < this.num_senses; i++)
      {
        String[] aux = getHypernymsWords(i);
        if (aux != null) {
          for (int h = 0; h < aux.length; h++) {
            if (!hypernyms.contains(aux[h].toLowerCase())) {
              hypernyms.add(aux[h].toLowerCase());
            }
          }
        }
      }
    }
    this.hypernymWords = hypernyms;
    return hypernyms;
  }
  
  public String getUniqueLemma()
  {
    if ((this.termIndex == null) && (this.termIndexAdj == null)) {
      return null;
    }
    if (this.termIndex != null) {
      return this.termIndex.getLemma();
    }
    return this.termIndexAdj.getLemma();
  }
  
  public ArrayList<String> getSynonyms()
    throws Exception
  {
    ArrayList<String> synonymWords = new ArrayList();
    if (this.termIndex != null)
    {
      try
      {
        if (this.pos == this.NOUN)
        {
          edu.smu.tspell.wordnet.Synset[] synsets = database.getSynsets(this.term, SynsetType.NOUN);
          for (int i = 0; i < synsets.length; i++)
          {
            NounSynset nounSynset = (NounSynset)synsets[i];
            for (int j = 0; j < nounSynset.getWordForms().length; j++) {
              synonymWords.add(nounSynset.getWordForms()[j]);
            }
          }
        }
        else if (this.pos == this.ADJECTIVE)
        {
          edu.smu.tspell.wordnet.Synset[] synsets = database.getSynsets(this.term, SynsetType.ADJECTIVE);
          for (int i = 0; i < synsets.length; i++)
          {
            AdjectiveSynset nounSynset = (AdjectiveSynset)synsets[i];
            for (int j = 0; j < nounSynset.getWordForms().length; j++) {
              synonymWords.add(nounSynset.getWordForms()[j]);
            }
          }
        }
        else if (this.pos == this.VERB)
        {
          edu.smu.tspell.wordnet.Synset[] synsets = database.getSynsets(this.term, SynsetType.VERB);
          for (int i = 0; i < synsets.length; i++)
          {
            VerbSynset nounSynset = (VerbSynset)synsets[i];
            for (int j = 0; j < nounSynset.getWordForms().length; j++) {
              synonymWords.add(nounSynset.getWordForms()[j]);
            }
          }
        }
        else
        {
          edu.smu.tspell.wordnet.Synset[] synsets = database.getSynsets(this.term, SynsetType.ADVERB);
          for (int i = 0; i < synsets.length; i++)
          {
            AdverbSynset nounSynset = (AdverbSynset)synsets[i];
            for (int j = 0; j < nounSynset.getWordForms().length; j++) {
              synonymWords.add(nounSynset.getWordForms()[j]);
            }
          }
        }
      }
      catch (Exception e)
      {
        System.out.println("Exception in jaws wordnet");
        e.printStackTrace();
      }
      this.synonymWords = synonymWords;
    }
    return synonymWords;
  }
  
  public ArrayList<String> getSynonyms(int max_number)
    throws Exception
  {
    ArrayList<String> synonyms_max = new ArrayList();
    ArrayList<String> synonymWords = getSynonyms();
    int i = 0;
    while ((i < max_number) && (i < this.synonymWords.size()))
    {
      synonyms_max.add(this.synonymWords.get(i));
      i++;
    }
    return synonyms_max;
  }
  
  public ArrayList<String> getDerived(int max_number)
    throws Exception
  {
    ArrayList<String> derived_max = new ArrayList();
    ArrayList<String> derivedWords = getDerived();
    int i = 0;
    while ((i < max_number) && (i < this.derivedWords.size()))
    {
      derived_max.add(this.derivedWords.get(i));
      i++;
    }
    return derived_max;
  }
  
  public void HolonymsOperation(int sense)
    throws JWNLException
  {
    this.holonyms = PointerUtils.getHolonyms((net.sf.extjwnl.data.Synset)this.termIndex.getSenses().get(sense));
    System.out.println("Direct Holonyms of \"" + this.termIndex.getLemma() + "\":");
    this.holonyms.print();
  }
  
  public void MeronymsOperation(int sense)
    throws JWNLException
  {
    this.meronyms = PointerUtils.getMeronyms((net.sf.extjwnl.data.Synset)this.termIndex.getSenses().get(sense));
    System.out.println("Direct meronyms of \"" + this.termIndex.getLemma() + "\":");
    this.meronyms.print();
  }
  
  private void demonstrateTreeOperation(int sense)
    throws JWNLException
  {
    PointerTargetTree hyponyms = PointerUtils.getHyponymTree((net.sf.extjwnl.data.Synset)this.termIndex.getSenses().get(sense));
    System.out.println("Hyponyms of \"" + this.termIndex.getLemma() + "\":");
    hyponyms.print();
  }
  
  public RelationshipList demonstrateAsymmetricRelationshipOperation(String startTerm, String endTerm, int pos1, int pos2, int sensestart, int senseend)
    throws Exception
  {
    IndexWord start = getIndexWord(startTerm, pos1);
    IndexWord end = getIndexWord(endTerm, pos2);
    
    RelationshipList list = RelationshipFinder.findRelationships((net.sf.extjwnl.data.Synset)start.getSenses().get(sensestart), (net.sf.extjwnl.data.Synset)end.getSenses().get(senseend), PointerType.HYPERNYM);
    return list;
  }
  
  public RelationshipList demonstrateAsymmetricRelationshipOperation(IndexWord start, IndexWord end, int sensestart, int senseend)
    throws Exception
  {
    RelationshipList list = RelationshipFinder.findRelationships((net.sf.extjwnl.data.Synset)start.getSenses().get(sensestart), (net.sf.extjwnl.data.Synset)end.getSenses().get(senseend), PointerType.HYPERNYM);
    return list;
  }
  
  public static RelationshipList demonstrateAsymmetricRelationshipOperation(net.sf.extjwnl.data.Synset start, net.sf.extjwnl.data.Synset end)
    throws Exception
  {
    RelationshipList list = RelationshipFinder.findRelationships(start, end, PointerType.HYPERNYM);
    return list;
  }
  
  public void printRelationShipList(RelationshipList list)
  {
    System.out.println("Hypernym relationship between \"" + ((Relationship)list.get(0)).getSourcePointerTarget().toString() + ((Relationship)list.get(0)).getTargetSynset().toString() + "\":");
    if (list.size() == 0)
    {
      System.out.println("Nothing");
    }
    else
    {
      for (Iterator itr = list.iterator(); itr.hasNext();) {
        ((Relationship)itr.next()).getNodeList().print();
      }
      System.out.println("Common Parent Index (from the source): " + ((AsymmetricRelationship)list.get(0)).getCommonParentIndex());
      System.out.println("Depth: " + ((Relationship)list.get(0)).getDepth());
    }
  }
  
  public void printCommonSubsummers(RelationshipList list)
    throws Exception
  {
    System.out.println("Calling common subsummers between \"" + ((Relationship)list.get(0)).getSourcePointerTarget().toString() + ((Relationship)list.get(0)).getTargetSynset().toString() + "\":");
    
    int listsize = list.size();
    System.out.println("the size of the list " + listsize);
    for (int y = 0; y < listsize; y++)
    {
      Relationship rs = (Relationship)list.get(y);
      int cpi = ((AsymmetricRelationship)list.get(y)).getCommonParentIndex();
      System.out.println("The CPI from the source is " + cpi);
      System.out.println("And the common subsummer is ");
      System.out.println(rs.getNodeList().get(cpi));
      net.sf.extjwnl.data.Synset synset = ((PointerTargetNode)rs.getNodeList().get(cpi)).getSynset();
      
      System.out.println("Therefore the common subsummers are ");
      getCommonSubsummers(synset, y);
    }
  }
  
  public RelationshipList demonstrateSymmetricRelationshipOperation(String startTerm, String endTerm, int sensestart, int senseend)
    throws Exception
  {
    IndexWord start = getIndexWord(startTerm);
    IndexWord end = getIndexWord(endTerm);
    
    RelationshipList list = RelationshipFinder.findRelationships((net.sf.extjwnl.data.Synset)start.getSenses().get(sensestart), (net.sf.extjwnl.data.Synset)end.getSenses().get(senseend), PointerType.SIMILAR_TO);
    return list;
  }
  
  public RelationshipList demonstrateHypoRelationshipOperation(String startTerm, String endTerm, int sensestart, int senseend)
    throws Exception
  {
    IndexWord start = getIndexWord(startTerm);
    IndexWord end = getIndexWord(endTerm);
    
    RelationshipList list = RelationshipFinder.findRelationships((net.sf.extjwnl.data.Synset)start.getSenses().get(sensestart), (net.sf.extjwnl.data.Synset)end.getSenses().get(senseend), PointerType.HYPONYM);
    return list;
  }
  
  private void getCommonSubsummers(net.sf.extjwnl.data.Synset synsetterm, int listnum)
    throws Exception
  {
    if (this.pos == this.NOUN)
    {
      PointerTargetTree hyperTree = PointerUtils.getHypernymTree(synsetterm);
      
      List<PointerTargetNodeList> nodelist = hyperTree.reverse();
      
      PointerTargetNodeList node = (PointerTargetNodeList)nodelist.get(listnum);
      Iterator it = node.iterator();
      while (it.hasNext())
      {
        PointerTargetNode pt = (PointerTargetNode)it.next();
        System.out.println("We are in iteration  = " + listnum + pt.toString());
      }
    }
  }
  
  public void closeDictionary() {}
  
  public boolean isInstanceHypernyms()
  {
    try
    {
      edu.smu.tspell.wordnet.Synset[] synsets = database.getSynsets(this.term, SynsetType.NOUN);
      for (int i = 0; i < synsets.length; i++)
      {
        NounSynset nounSynset = (NounSynset)synsets[i];
        NounSynset[] instancehypernyms = nounSynset.getInstanceHypernyms();
        if (instancehypernyms.length > 0) {
          return true;
        }
      }
    }
    catch (Exception e)
    {
      System.out.println("Exception in jaws wordnet");
      e.printStackTrace();
      return false;
    }
    return false;
  }
  
  public static void main(String[] args)
  {
    try
    {
      WordNet WN = new WordNet();
      OpenDictionary();
      
      boolean ss = WN.Initialize("countries");
      
      System.out.println(WN.termIndex);
      
      System.out.println(WN.termIndex.getLemma());
      System.out.println("Exits? " + ss);
      
      ArrayList<String> syns = WN.getDerived();
      for (String syn : syns) {
        System.out.println("derived: " + syn);
      }
      syns = WN.getFirstMeronyms();
      for (String syn : syns) {
        System.out.println("meronyms: " + syn);
      }
      syns = WN.getSynonyms();
      for (String syn : syns) {
        System.out.println("synonyms: " + syn);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      System.exit(-1);
    }
  }
}

