package poweraqua.core.utils;

import java.io.PrintStream;

public class JarClassLoader
  extends MultiClassLoader
{
  private JarResources jarResources;
  
  public JarClassLoader(String jarName)
  {
    this.jarResources = new JarResources(jarName);
  }
  
  protected byte[] loadClassBytes(String className)
  {
    className = formatClassName(className);
    
    return this.jarResources.getResource(className);
  }
  
  public static class Test
  {
    public static void main(String[] args)
      throws Exception
    {
      if (args.length != 2)
      {
        System.err.println("Usage: java JarClassLoader  ");
        System.exit(1);
      }
      JarClassLoader jarLoader = new JarClassLoader(args[0]);
      
      Class c = jarLoader.loadClass(args[1], true);
      
      Object o = c.newInstance();
    }
  }
}

