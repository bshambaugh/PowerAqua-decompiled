package poweraqua.core.utils;

import java.io.PrintStream;
import java.util.Hashtable;

public abstract class MultiClassLoader
  extends ClassLoader
{
  private Hashtable classes = new Hashtable();
  private char classNameReplacementChar;
  protected boolean monitorOn = false;
  protected boolean sourceMonitorOn = true;
  
  public Class loadClass(String className)
    throws ClassNotFoundException
  {
    return loadClass(className, true);
  }
  
  public synchronized Class loadClass(String className, boolean resolveIt)
    throws ClassNotFoundException
  {
    monitor(">> MultiClassLoader.loadClass(" + className + ", " + resolveIt + ")");
    
    Class result = (Class)this.classes.get(className);
    if (result != null)
    {
      monitor(">> returning cached result.");
      return result;
    }
    try
    {
      ClassLoader system = getSystemClassLoader();
      ClassLoader sys = Thread.currentThread().getContextClassLoader();
      result = sys.loadClass(className);
      
      monitor(">> returning system class (in CLASSPATH).");
      return result;
    }
    catch (ClassNotFoundException e)
    {
      monitor(">> Not a system class.");
      System.out.println(">> Not a system class." + e);
      
      byte[] classBytes = loadClassBytes(className);
      if (classBytes == null) {
        throw new ClassNotFoundException();
      }
      result = defineClass(className, classBytes, 0, classBytes.length);
      if (result == null) {
        throw new ClassFormatError();
      }
      if (resolveIt) {
        resolveClass(result);
      }
      this.classes.put(className, result);
      monitor(">> Returning newly loaded class.");
      System.out.println(">> Returning newly loaded class.");
    }
    return result;
  }
  
  public void setClassNameReplacementChar(char replacement)
  {
    this.classNameReplacementChar = replacement;
  }
  
  protected abstract byte[] loadClassBytes(String paramString);
  
  protected String formatClassName(String className)
  {
    if (this.classNameReplacementChar == 0) {
      return className.replace('.', '/') + ".class";
    }
    return className.replace('.', this.classNameReplacementChar) + ".class";
  }
  
  protected void monitor(String text)
  {
    if (this.monitorOn) {
      print(text);
    }
  }
  
  protected static void print(String text)
  {
    System.out.println(text);
  }
}

