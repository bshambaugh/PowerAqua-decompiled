package poweraqua.indexingService.manager;

public class IndexBean
{
  private static final String INSTANCES_DIR = "_instances";
  private String index_dir;
  private String instances_index_dir;
  private String spell_index_dir;
  private String instances_spell_index_dir;
  private String metadata_index_db;
  private String metadata_index_db_login;
  private String metadata_index_db_password;
  private String metadata_index_db_table;
  private String instances_metadata_index_db_table;
  
  public IndexBean(String index_dir, String spell_index_dir, String metadata_index_db, String metadata_index_db_login, String metadata_index_db_password, String metadata_index_db_table)
  {
    this.index_dir = index_dir;
    this.instances_index_dir = (index_dir + "_instances");
    
    this.spell_index_dir = spell_index_dir;
    this.instances_spell_index_dir = (spell_index_dir + "_instances");
    
    this.metadata_index_db = metadata_index_db;
    this.metadata_index_db_login = metadata_index_db_login;
    this.metadata_index_db_password = metadata_index_db_password;
    this.metadata_index_db_table = metadata_index_db_table;
  }
  
  public String getIndex_dir()
  {
    return this.index_dir;
  }
  
  public String getSpell_index_dir()
  {
    return this.spell_index_dir;
  }
  
  public boolean equals(Object obj)
  {
    if (obj.getClass() != getClass()) {
      return false;
    }
    IndexBean indexBean = (IndexBean)obj;
    if ((indexBean.getIndex_dir().equals(getIndex_dir())) && (indexBean.getSpell_index_dir().equals(getSpell_index_dir()))) {
      return true;
    }
    return false;
  }
  
  public String getInstances_index_dir()
  {
    return this.instances_index_dir;
  }
  
  public void setInstances_index_dir(String instances_index_dir)
  {
    this.instances_index_dir = instances_index_dir;
  }
  
  public String getInstances_spell_index_dir()
  {
    return this.instances_spell_index_dir;
  }
  
  public void setInstances_spell_index_dir(String instances_spell_index_dir)
  {
    this.instances_spell_index_dir = instances_spell_index_dir;
  }
  
  public String getMetadata_index_db()
  {
    return this.metadata_index_db;
  }
  
  public String getMetadata_index_db_login()
  {
    return this.metadata_index_db_login;
  }
  
  public String getMetadata_index_db_password()
  {
    return this.metadata_index_db_password;
  }
  
  public String getMetadata_index_db_table()
  {
    return this.metadata_index_db_table;
  }
}

