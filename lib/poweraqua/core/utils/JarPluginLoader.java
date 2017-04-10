package poweraqua.core.utils;

import java.io.File;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import poweraqua.core.plugin.OntologyPlugin;

public class JarPluginLoader
{
  Hashtable<String, OntologyPlugin> ontologyPlugins = null;
  
  public JarPluginLoader(String pluginsDir)
  {
    this.ontologyPlugins = new Hashtable();
    
    File pluginsDirectory = new File(pluginsDir);
    File[] pluginFiles = pluginsDirectory.listFiles();
    
    JarFile pluginFile = null;
    if (pluginFiles != null) {
      for (int i = 0; i < pluginFiles.length; i++)
      {
        System.out.println("loading " + pluginFiles[i].getAbsolutePath());
        try
        {
          pluginFile = new JarFile(pluginFiles[i].getAbsolutePath());
          String mainClassName = pluginFile.getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
          
          JarClassLoader jarLoader = new JarClassLoader(pluginFile.getName());
          Class pluginClass = jarLoader.loadClass(mainClassName);
          System.out.println("load main class .. " + mainClassName);
          
          OntologyPlugin plugin = (OntologyPlugin)pluginClass.newInstance();
          
          System.out.println("adding in-out plugin : " + plugin.getName());
          this.ontologyPlugins.put(plugin.getName(), plugin);
          pluginFile.close();
        }
        catch (Exception e)
        {
          System.out.println("Exception in plugin manager " + e);
          e.printStackTrace();
        }
      }
    } else {
      System.out.println("the plugings directory is wrong or does not contain any pluging");
    }
  }
  
  public static OntologyPlugin createPlugin(String plugins_dir, String onto_plugin)
    throws Exception
  {
    System.out.println("Create plugin for " + plugins_dir + " " + onto_plugin);
    
    File pluginsDirectory = new File(plugins_dir);
    File[] pluginFiles = pluginsDirectory.listFiles();
    
    JarFile pluginFile = null;
    if (pluginFiles != null) {
      for (int i = 0; i < pluginFiles.length; i++)
      {
        System.out.println("loading " + pluginFiles[i].getAbsolutePath());
        try
        {
          pluginFile = new JarFile(pluginFiles[i].getAbsolutePath());
          String mainClassName = pluginFile.getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
          System.out.println("---- main class = " + mainClassName);
          System.out.println("jar class loader " + pluginFile.getName());
          
          JarClassLoader jarLoader = new JarClassLoader(pluginFile.getName());
          System.out.println("load class " + mainClassName);
          Class pluginClass = jarLoader.loadClass(mainClassName);
          System.out.println("load class .. " + mainClassName);
          
          OntologyPlugin plugin = (OntologyPlugin)pluginClass.newInstance();
          if (plugin.getName().equals(onto_plugin))
          {
            System.out.println("adding in-out plugin : " + plugin.getName());
            pluginFile.close();
            return plugin;
          }
          pluginFile.close();
        }
        catch (Exception e)
        {
          System.out.println("Exception in plugin manager " + e);
          e.printStackTrace();
        }
      }
    }
    System.out.println("NO plugin found " + onto_plugin);
    return null;
  }
  
  public Hashtable<String, OntologyPlugin> getOntologyPlugins()
  {
    return this.ontologyPlugins;
  }
}

