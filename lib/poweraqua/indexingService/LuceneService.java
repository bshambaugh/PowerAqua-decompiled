package poweraqua.indexingService;

import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneService
{
  public static IndexWriter createStandardIndex(String directory)
    throws IOException
  {
    return new IndexWriter(directory, new StandardAnalyzer(), true);
  }
  
  public static IndexWriter openStandardIndex(String directory)
    throws IOException
  {
    return new IndexWriter(directory, new StandardAnalyzer(), false);
  }
  
  public static SpellChecker createSpellCheckerFromIndex(String indexPath, String indexField, String spellIndexPath)
    throws IOException
  {
    Directory spellIndexDirectory = FSDirectory.getDirectory(spellIndexPath, true);
    SpellChecker spellChecker = new SpellChecker(spellIndexDirectory);
    
    IndexReader indexReader = IndexReader.open(indexPath);
    
    LuceneDictionary dictionary = new LuceneDictionary(indexReader, indexField);
    if (dictionary.getWordsIterator().hasNext() == true)
    {
      spellChecker.indexDictionary(dictionary);
      indexReader.close();
    }
    return spellChecker;
  }
  
  public static SpellChecker openSpellChecker(String spellIndexPath)
    throws IOException
  {
    Directory spellIndexDirectory = FSDirectory.getDirectory(spellIndexPath, false);
    return new SpellChecker(spellIndexDirectory);
  }
  
  public static void closeIndex(IndexWriter indexWritter)
    throws IOException
  {
    indexWritter.optimize();
    indexWritter.close();
  }
}

