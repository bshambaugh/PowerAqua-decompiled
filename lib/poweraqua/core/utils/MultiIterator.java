package poweraqua.core.utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MultiIterator
  implements Iterator
{
  ArrayList<Iterator> iterCollection;
  Iterator usedIterator;
  int indexIterators;
  
  public MultiIterator(Iterator iter)
  {
    this.iterCollection = new ArrayList();
    this.iterCollection.add(iter);
    this.usedIterator = iter;
    this.indexIterators = 0;
  }
  
  public void addIterator(Iterator iter)
  {
    this.iterCollection.add(iter);
  }
  
  public void backToStartPoint()
  {
    this.indexIterators = 0;
    this.usedIterator = ((Iterator)this.iterCollection.get(this.indexIterators));
  }
  
  public boolean hasNext()
  {
    if (this.usedIterator.hasNext()) {
      return true;
    }
    this.indexIterators += 1;
    while (this.iterCollection.size() > this.indexIterators)
    {
      this.usedIterator = ((Iterator)this.iterCollection.get(this.indexIterators));
      if (this.usedIterator.hasNext()) {
        return true;
      }
      this.indexIterators += 1;
    }
    return false;
  }
  
  public Object next()
  {
    return this.usedIterator.next();
  }
  
  public void remove()
  {
    this.usedIterator.remove();
  }
  
  public static void main(String[] args)
  {
    ArrayList coleccion1 = new ArrayList();
    ArrayList coleccion2 = new ArrayList();
    
    coleccion2.add(Integer.valueOf(4));coleccion2.add(Integer.valueOf(5));coleccion2.add(Integer.valueOf(6));
    
    MultiIterator iter = new MultiIterator(coleccion1.iterator());
    iter.addIterator(coleccion2.iterator());
    while (iter.hasNext()) {
      System.out.println(iter.next());
    }
  }
}
