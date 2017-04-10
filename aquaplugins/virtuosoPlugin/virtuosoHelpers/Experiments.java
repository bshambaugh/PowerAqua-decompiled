package virtuosoPlugin.virtuosoHelpers;

public class Experiments
{
  private String keyword;
  private int LIMIT;
  private String[] ids;
  private long[][] times;
  private int[][] numbers;
  
  public Experiments(int graphs)
  {
    this.ids = new String[graphs];
    this.times = new long[graphs][4];
    this.numbers = new int[graphs][4];
  }
  
  public void addValues(String id, int type, long time, int number)
    throws Exception
  {
    for (int graph = 0; graph < this.ids.length; graph++) {
      if (this.ids[graph].contains(id))
      {
        this.times[graph][type] = time;
        this.numbers[graph][type] = number;
        return;
      }
    }
    throw new Exception("no such graph");
  }
  
  public void addID(String id)
    throws Exception
  {
    for (int graph = 0; graph < this.ids.length; graph++) {
      if (this.ids[graph] == null)
      {
        this.ids[graph] = id;
        return;
      }
    }
    throw new Exception("already full");
  }
  
  public void setKeyword(String keyword)
  {
    this.keyword = keyword;
  }
  
  public void setLIMIT(int lIMIT)
  {
    this.LIMIT = lIMIT;
  }
  
  public String toString()
  {
    String string = "";
    for (int id = 0; id < this.ids.length; id++) {
      for (int type = 0; type < 4; type++)
      {
        string = string + this.keyword + ",";
        string = string + this.LIMIT + ",";
        string = string + this.ids[id] + ",";
        string = string + this.numbers[id][type] + ",";
        string = string + this.times[id][type] + "\n";
      }
    }
    return string;
  }
}

