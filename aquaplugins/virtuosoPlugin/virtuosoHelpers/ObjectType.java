package virtuosoPlugin.virtuosoHelpers;

import java.io.PrintStream;
import poweraqua.indexingService.manager.virtuoso.VirtuosoServiceConfiguration;

public class ObjectType
{
  public static ObjectType ALL;
  public static ObjectType OWL_PROPERTY;
  public static ObjectType RDF_PROPERTY;
  public static ObjectType PROPERTY;
  public static ObjectType RDF_CLASS;
  public static ObjectType OWL_CLASS;
  public static ObjectType CLASS;
  public static ObjectType INSTANCE_OF_RDF_CLASS;
  public static ObjectType INSTANCE_OF_OWL_CLASS;
  public static ObjectType INSTANCE;
  public static ObjectType LITERAL;
  private static String[] labels;
  private Relation relation;
  private String[] types;
  private Literal literal;
  private boolean notProperty;
  private String[] properties;
  private String objectType;
  
  public static enum Literal
  {
    IS_LITERAL,  IS_NO_LITERAL,  NO_RESTRICTION;
    
    private Literal() {}
  }
  
  public static enum Relation
  {
    CLASS,  PROPERTY,  INSTANCE,  TYPE,  SUBCLASS,  TYPE_OF_SOMETHING_THATS_TYPE,  NO_RELATION,  NO_SPECIFIC_TYPE;
    
    private Relation() {}
  }
  
  public static enum Connector
  {
    AND,  OR,  ONE_STRING;
    
    private Connector() {}
  }
  
  static
  {
    try
    {
      labels = VirtuosoServiceConfiguration.getLabels();
      
      createStaticObjects();
    }
    catch (TypesEmptyException e)
    {
      e.printStackTrace();
    }
    catch (NoNotsException e)
    {
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public ObjectType(Relation relation, String[] types, Literal literal, boolean notProperty, String[] properties, String objectType)
    throws TypesEmptyException, NoNotsException
  {
    if ((relation != Relation.NO_SPECIFIC_TYPE) && (relation != Relation.CLASS) && (relation != Relation.INSTANCE) && (relation != Relation.PROPERTY) && (relation != Relation.NO_RELATION) && (types.length == 0)) {
      throw new TypesEmptyException();
    }
    if (properties.length == 0) {
      throw new NoNotsException();
    }
    this.objectType = objectType;
    this.properties = properties;
    this.notProperty = notProperty;
    this.relation = relation;
    this.types = types;
    this.literal = literal;
  }
  
  public String[] getProperties()
  {
    return this.properties;
  }
  
  public boolean isNotProperty()
  {
    return this.notProperty;
  }
  
  public Relation getRelation()
  {
    return this.relation;
  }
  
  public String[] getTypes()
  {
    return this.types;
  }
  
  public Literal getLiteral()
  {
    return this.literal;
  }
  
  public boolean hasTypes()
  {
    return this.types.length > 0;
  }
  
  public String getObjectType()
  {
    return this.objectType;
  }
  
  public static String[] getLabels()
  {
    return labels;
  }
  
  public static void setLabelsAndChangeStaticObjects(String[] labels)
  {
    labels = labels;
    try
    {
      createStaticObjects();
    }
    catch (TypesEmptyException e)
    {
      e.printStackTrace();
    }
    catch (NoNotsException e)
    {
      System.err.println("Change the set of labels, its not allowed to be empty");
      e.printStackTrace();
    }
  }
  
  public static void addOneLabelAndChangeStaticObjects(String label)
  {
    String[] labels = new String[labels.length + 1];
    for (int i = 0; i < labels.length; i++) {
      labels[i] = labels[i];
    }
    labels[labels.length] = label;
    labels = labels;
    try
    {
      createStaticObjects();
    }
    catch (TypesEmptyException e)
    {
      e.printStackTrace();
    }
    catch (NoNotsException e)
    {
      System.err.println("Change the set of labels, its not allowed to be empty");
      e.printStackTrace();
    }
  }
  
  private static void createStaticObjects()
    throws TypesEmptyException, NoNotsException
  {
    ALL = new ObjectType(Relation.NO_SPECIFIC_TYPE, new String[0], Literal.NO_RESTRICTION, false, labels, "class");
    
    RDF_PROPERTY = new ObjectType(Relation.TYPE, new String[] { "rdf:Property" }, Literal.NO_RESTRICTION, false, labels, "property");
    
    OWL_PROPERTY = new ObjectType(Relation.TYPE, new String[] { "owl:Property" }, Literal.NO_RESTRICTION, false, labels, "property");
    PROPERTY = new ObjectType(Relation.PROPERTY, new String[0], Literal.NO_RESTRICTION, false, labels, "property");
    
    RDF_CLASS = new ObjectType(Relation.TYPE, new String[] { "rdfs:Class" }, Literal.NO_RESTRICTION, false, labels, "class");
    OWL_CLASS = new ObjectType(Relation.TYPE, new String[] { "owl:Class" }, Literal.NO_RESTRICTION, false, labels, "class");
    CLASS = new ObjectType(Relation.CLASS, new String[0], Literal.NO_RESTRICTION, false, labels, "class");
    
    INSTANCE_OF_RDF_CLASS = new ObjectType(Relation.TYPE_OF_SOMETHING_THATS_TYPE, new String[] { "rdf:Class" }, Literal.IS_NO_LITERAL, false, labels, "instance");
    INSTANCE_OF_OWL_CLASS = new ObjectType(Relation.TYPE_OF_SOMETHING_THATS_TYPE, new String[] { "owl:Class" }, Literal.IS_NO_LITERAL, false, labels, "instance");
    INSTANCE = new ObjectType(Relation.INSTANCE, new String[0], Literal.IS_NO_LITERAL, false, labels, "instance");
    
    LITERAL = new ObjectType(Relation.NO_RELATION, new String[0], Literal.IS_LITERAL, true, labels, "literal");
  }
}

