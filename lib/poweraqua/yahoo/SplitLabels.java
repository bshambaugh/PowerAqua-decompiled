package poweraqua.yahoo;

import java.util.ArrayList;
import poweraqua.core.utils.LabelSplitter;

public class SplitLabels
{
  public static ArrayList<String> splitLabels(ArrayList<String> semanticData)
  {
    ArrayList<String> wordList = new ArrayList();
    for (String word : semanticData)
    {
      word = new LabelSplitter().splitOnSeparators(word);
      word = word.replace("/", " ");
      word = word.replace("\"", "");
      word = word.trim();
      if (word.endsWith("@en")) {
        word = word.substring(0, word.length() - 3);
      }
      if (word.contains(" "))
      {
        String[] words = word.split(" ");
        for (String w : words) {
          if (!wordList.contains(w)) {
            wordList.add(w);
          }
        }
      }
      else if (!wordList.contains(word))
      {
        wordList.add(word);
      }
    }
    return wordList;
  }
}

