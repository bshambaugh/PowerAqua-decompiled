package poweraqua.core.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class JarResources
{
  public boolean debugOn = false;
  private Hashtable htSizes = new Hashtable();
  private Hashtable htJarContents = new Hashtable();
  private String jarFileName;
  
  public JarResources(String jarFileName)
  {
    this.jarFileName = jarFileName;
    init();
  }
  
  public byte[] getResource(String name)
  {
    return (byte[])this.htJarContents.get(name);
  }
  
  private void init()
  {
    try
    {
      ZipFile zf = new ZipFile(this.jarFileName);
      
      Enumeration e = zf.entries();
      while (e.hasMoreElements())
      {
        ZipEntry ze = (ZipEntry)e.nextElement();
        if (this.debugOn) {
          System.out.println(dumpZipEntry(ze));
        }
        this.htSizes.put(ze.getName(), new Integer((int)ze.getSize()));
      }
      zf.close();
      
      FileInputStream fis = new FileInputStream(this.jarFileName);
      
      BufferedInputStream bis = new BufferedInputStream(fis);
      
      ZipInputStream zis = new ZipInputStream(bis);
      
      ZipEntry ze = null;
      while ((ze = zis.getNextEntry()) != null) {
        if (!ze.isDirectory())
        {
          if (this.debugOn) {
            System.out.println("ze.getName()=" + ze.getName() + "," + "getSize()=" + ze.getSize());
          }
          int size = (int)ze.getSize();
          if (size == -1) {
            size = ((Integer)this.htSizes.get(ze.getName())).intValue();
          }
          byte[] b = new byte[size];
          
          int rb = 0;
          int chunk = 0;
          while (size - rb > 0)
          {
            chunk = zis.read(b, rb, size - rb);
            if (chunk == -1) {
              break;
            }
            rb += chunk;
          }
          this.htJarContents.put(ze.getName(), b);
          if (this.debugOn) {
            System.out.println("Done: " + ze.getName() + "  rb=" + rb + ",size=" + size + ",csize=" + ze.getCompressedSize());
          }
        }
      }
      fis.close();
      bis.close();
      zis.close();
    }
    catch (NullPointerException e)
    {
      System.out.println("done.");
    }
    catch (FileNotFoundException e)
    {
      System.out.println("Exception " + e);
      e.printStackTrace();
    }
    catch (IOException e)
    {
      System.out.println("Exception " + e);
      e.printStackTrace();
    }
    catch (Exception e)
    {
      System.out.println("Exception " + e);
      e.printStackTrace();
    }
  }
  
  private String dumpZipEntry(ZipEntry ze)
  {
    StringBuffer sb = new StringBuffer();
    if (ze.isDirectory()) {
      sb.append("d ");
    } else {
      sb.append("f ");
    }
    if (ze.getMethod() == 0) {
      sb.append("stored   ");
    } else {
      sb.append("defalted ");
    }
    sb.append(ze.getName());
    sb.append("\t");
    sb.append("" + ze.getSize());
    if (ze.getMethod() == 8) {
      sb.append("/" + ze.getCompressedSize());
    }
    return sb.toString();
  }
  
  public static void main(String[] args)
    throws IOException
  {
    if (args.length != 2)
    {
      System.err.println("usage: java JarResources <jar file name> <resource name>");
      
      System.exit(1);
    }
    JarResources jr = new JarResources(args[0]);
    byte[] buff = jr.getResource(args[1]);
    if (buff == null) {
      System.out.println("Could not find " + args[1] + ".");
    } else {
      System.out.println("Found " + args[1] + " (length=" + buff.length + ").");
    }
  }
}

