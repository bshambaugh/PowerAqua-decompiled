package poweraqua.serviceConfig;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.core.utils.JarPluginLoader;

public class MultiOntologyManager
{
  private ServiceConfiguration context;
  private Hashtable<String, OntologyPlugin> osPlugins;
  private Hashtable<String, OntologyPlugin> typeOntologyPlugins;
  
  public MultiOntologyManager(ArrayList<OntologyPlugin> plugings)
    throws Exception
  {
    this.context = new ServiceConfiguration();
    this.context.readConfigurationFile();
    this.osPlugins = new Hashtable();
    JarPluginLoader jarPlugingLoader = new JarPluginLoader(getContext().getPluginsDirectory());
    
    this.typeOntologyPlugins = jarPlugingLoader.getOntologyPlugins();
    for (OntologyPlugin plugin : plugings) {
      this.osPlugins.put(plugin.getPluginID(), plugin);
    }
  }
  
  public MultiOntologyManager(String path, ArrayList<OntologyPlugin> plugings)
    throws Exception
  {
    this.context = new ServiceConfiguration();
    this.context.readConfigurationFile(path);
    this.osPlugins = new Hashtable();
    JarPluginLoader jarPlugingLoader = new JarPluginLoader(getContext().getPluginsDirectory());
    
    this.typeOntologyPlugins = jarPlugingLoader.getOntologyPlugins();
    for (OntologyPlugin plugin : plugings) {
      this.osPlugins.put(plugin.getPluginID(), plugin);
    }
  }
  
  public MultiOntologyManager(String path)
    throws Exception
  {
    this.context = new ServiceConfiguration();
    this.context.readConfigurationFile(path);
    System.out.println("I get the config " + getContext().getPluginsDirectory());
    loadPlugings();
  }
  
  public MultiOntologyManager()
    throws Exception
  {
    this.context = new ServiceConfiguration();
    getContext().readConfigurationFile();
    System.out.println("I get the config " + getContext().getPluginsDirectory());
    loadPlugings();
  }
  
  public ArrayList<String> listIdPlugings()
  {
    return new ArrayList(getOsPlugins().keySet());
  }
  
  public OntologyPlugin getPlugin(String idPlugin)
  {
    OntologyPlugin plugg = (OntologyPlugin)getOsPlugins().get(idPlugin);
    if (plugg == null)
    {
      System.out.println("Creating watson plugin for " + idPlugin);
      try
      {
        plugg = (OntologyPlugin)((OntologyPlugin)this.typeOntologyPlugins.get("watson")).getClass().newInstance();
        Repository r = new Repository();
        r.setRepositoryName(idPlugin);
        plugg.loadPlugin(r);
        getOsPlugins().put(idPlugin, plugg);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
      return plugg;
    }
    return plugg;
  }
  
  public ArrayList<OntologyPlugin> getAllPlugins()
  {
    return new ArrayList(getOsPlugins().values());
  }
  
  public void filterPlugin(String idPlugin)
  {
    System.out.println("Selecting plugin " + idPlugin + " all other " + this.osPlugins.size() + " are discarded");
    Hashtable<String, OntologyPlugin> new_osPlugins = new Hashtable();
    new_osPlugins.put(idPlugin, this.osPlugins.get(idPlugin));
    setOsPlugins(new_osPlugins);
  }
  
  private void loadPlugings()
  {
    setOsPlugins(new Hashtable());
    
    JarPluginLoader jarPlugingLoader = new JarPluginLoader(getContext().getPluginsDirectory());
    
    this.typeOntologyPlugins = jarPlugingLoader.getOntologyPlugins();
    for (Repository repository : getContext().getRepositories())
    {
      OntologyPlugin plugin = null;
      try
      {
        plugin = (OntologyPlugin)((OntologyPlugin)this.typeOntologyPlugins.get(repository.getPluginType())).getClass().newInstance();
      }
      catch (IllegalAccessException ex)
      {
        ex.printStackTrace();
      }
      catch (InstantiationException ex)
      {
        ex.printStackTrace();
      }
      if (plugin != null) {
        try
        {
          plugin.loadPlugin(repository);
        }
        catch (Exception e)
        {
          System.out.println("Imposible to intialize pluging " + repository.getPluginType() + " " + repository.getServerURL());
        }
      }
      getOsPlugins().put(plugin.getPluginID(), plugin);
    }
  }
  
  public ServiceConfiguration getContext()
  {
    return this.context;
  }
  
  public int getNumberPlugins()
  {
    return getOsPlugins().keySet().size();
  }
  
  public static void main(String[] args)
    throws Exception
  {
    MultiOntologyManager mom = new MultiOntologyManager();
    
    System.out.println(mom.getContext().getRepositories());
  }
  
  public Hashtable<String, OntologyPlugin> getOsPlugins()
  {
    return this.osPlugins;
  }
  
  public void setOsPlugins(Hashtable<String, OntologyPlugin> osPlugins)
  {
    this.osPlugins = osPlugins;
  }
}

