package poweraquaDB;

public abstract class ConnectionBean
{
  protected long id;
  protected int numOpenReaders;
  
  public ConnectionBean(long idConnection)
  {
    this.id = idConnection;
    this.numOpenReaders = 1;
  }
  
  public boolean equals(Object obj)
  {
    if ((obj.getClass().equals(getClass())) && 
      (((JenaConnectionBean)obj).getId() == getId())) {
      return true;
    }
    return false;
  }
  
  public long getId()
  {
    return this.id;
  }
  
  public void increaseOpenReaders()
  {
    this.numOpenReaders += 1;
  }
  
  public void decreaseOpenReaders()
  {
    this.numOpenReaders -= 1;
  }
  
  public boolean isEraseable()
  {
    if (this.numOpenReaders <= 0) {
      return true;
    }
    return false;
  }
  
  public abstract void close();
}

