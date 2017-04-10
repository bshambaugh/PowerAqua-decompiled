package poweraqua.core.model.myrdfmodel;

public class OwlRestriction
{
  private String has_restriction_type = null;
  private String has_restriction_value = null;
  private String on_property = null;
  
  public OwlRestriction(String onproperty, String restrictionType, String restrictionValue)
  {
    this.on_property = onproperty;
    this.has_restriction_type = restrictionType;
    this.has_restriction_value = restrictionValue;
  }
  
  public String getOnProperty()
  {
    return this.on_property;
  }
  
  public String getRestrictionType()
  {
    return this.has_restriction_type;
  }
  
  public String getRestrictionValue()
  {
    return this.has_restriction_value;
  }
  
  public boolean isAllValuesFromRestriction()
  {
    if (this.has_restriction_type.toLowerCase().endsWith("allvaluesfrom")) {
      return true;
    }
    return false;
  }
  
  public boolean isSomeValuesFromRestriction()
  {
    if (this.has_restriction_type.toLowerCase().endsWith("somevaluesfrom")) {
      return true;
    }
    return false;
  }
  
  public boolean isCardinalityRestriction()
  {
    if (this.has_restriction_type.toLowerCase().endsWith("#cardinality")) {
      return true;
    }
    return false;
  }
  
  public boolean isMinCardinalityRestriction()
  {
    if (this.has_restriction_type.toLowerCase().endsWith("#mincardinality")) {
      return true;
    }
    return false;
  }
  
  public boolean isMaxCardinalityRestriction()
  {
    if (this.has_restriction_type.toLowerCase().endsWith("#maxcardinality")) {
      return true;
    }
    return false;
  }
  
  public boolean isHasValueRestriction()
  {
    if (this.has_restriction_type.toLowerCase().endsWith("hasvalue")) {
      return true;
    }
    return false;
  }
}

