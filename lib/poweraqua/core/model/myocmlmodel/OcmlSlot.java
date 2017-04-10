package poweraqua.core.model.myocmlmodel;

import java.io.Serializable;
import java.util.ArrayList;

public class OcmlSlot
  implements Serializable
{
  private String slotName;
  private String dataType;
  private String cardinality = null;
  private String minCardinality = null;
  private String maxCardinality = null;
  private ArrayList hasValues;
  private boolean isDataTypePrimitive = false;
  
  public OcmlSlot(String slotName)
  {
    this(slotName, "");
  }
  
  public OcmlSlot(String slotName, String type)
  {
    this.slotName = slotName.trim();
    
    setDataType(type);
    
    this.hasValues = new ArrayList();
  }
  
  public void setSlotName(String s)
  {
    this.slotName = s;
  }
  
  public void setDataType(String dataType)
  {
    this.dataType = dataType;
    checkingDataType();
  }
  
  public void setCardinality(String cardinality)
  {
    this.cardinality = cardinality;
  }
  
  public void setMinCardinality(String minCardinality)
  {
    this.minCardinality = minCardinality;
  }
  
  public void setMaxCardinality(String maxCardinality)
  {
    this.maxCardinality = maxCardinality;
  }
  
  public void setMaxCardinality(int m)
  {
    this.maxCardinality = new Integer(m).toString();
  }
  
  public void setMinCardinality(int m)
  {
    this.minCardinality = new Integer(m).toString();
  }
  
  public void setCardinality(int m)
  {
    this.cardinality = new Integer(m).toString();
  }
  
  public String getSlotName()
  {
    return this.slotName;
  }
  
  public String getDataType()
  {
    return this.dataType;
  }
  
  public String getCardinality()
  {
    return this.cardinality;
  }
  
  public String getMinCardinality()
  {
    return this.minCardinality;
  }
  
  public String getMaxCardinality()
  {
    return this.maxCardinality;
  }
  
  public boolean hasExplicitDataType()
  {
    if ((this.dataType != null) && (this.dataType.trim().length() > 0)) {
      return true;
    }
    return false;
  }
  
  public boolean isDataTypePrimitive()
  {
    return this.isDataTypePrimitive;
  }
  
  public String getXMLDataType()
  {
    if (!this.isDataTypePrimitive) {
      return null;
    }
    String type = this.dataType;
    if (type.toLowerCase().equals("negative-integer")) {
      type = "negativeInteger";
    } else if (type.toLowerCase().equals("non-negative-integer")) {
      type = "nonNegativeInteger";
    } else if (type.toLowerCase().equals("positive-integer")) {
      type = "PositiveInteger";
    } else if (type.toLowerCase().equals("non-positive-integer")) {
      type = "nonPositiveInteger";
    } else if (type.toLowerCase().equals("number")) {
      type = "decimal";
    } else if (type.toLowerCase().equals("real-number")) {
      type = "decimal";
    } else if (type.toLowerCase().equals("url")) {
      type = "string";
    } else if (type.toLowerCase().endsWith("#url")) {
      type = "http://www.w3.org/2001/XMLSchema#string";
    }
    return type;
  }
  
  private void checkingDataType()
  {
    if (this.dataType == null)
    {
      this.isDataTypePrimitive = false;
      return;
    }
    String[] primitiveTypes = { "string", "boolean", "number", "integer", "int", "decimal", "double", "float", "long", "negativeinteger", "negative-integer", "nonnegativeinteger", "non-negative-integer", "positiveinteger", "positive-integer", "nonpositiveinteger", "non-positive-integer", "short", "date", "time", "anyuri", "url" };
    
    String type = this.dataType.toLowerCase();
    for (int i = 0; i < primitiveTypes.length; i++) {
      if ((type.equals(primitiveTypes[i])) || (type.endsWith('#' + primitiveTypes[i])))
      {
        this.isDataTypePrimitive = true;
        return;
      }
    }
    this.isDataTypePrimitive = false;
  }
  
  public String toString()
  {
    return this.slotName;
  }
  
  public boolean isMinCardinalityEmpty()
  {
    if ((this.minCardinality == null) || (this.minCardinality.trim().length() <= 0) || (this.minCardinality.toLowerCase().trim().equals("null"))) {
      return true;
    }
    return false;
  }
  
  public boolean isMaxCardinalityEmpty()
  {
    if ((this.maxCardinality == null) || (this.maxCardinality.trim().length() <= 0) || (this.maxCardinality.toLowerCase().trim().equals("null"))) {
      return true;
    }
    return false;
  }
  
  public boolean isCardinalityEmpty()
  {
    if ((this.cardinality == null) || (this.cardinality.trim().length() <= 0) || (this.cardinality.toLowerCase().trim().equals("null"))) {
      return true;
    }
    return false;
  }
  
  public void addDefaultValue(String value)
  {
    value = StringUtility.lrTrim(value);
    if (value != null) {
      this.hasValues.add(value);
    }
  }
  
  public int getDefaultValuesCount()
  {
    return this.hasValues.size();
  }
  
  public boolean hasDefaultValue()
  {
    return !this.hasValues.isEmpty();
  }
  
  public String getDefaultValue(int index)
  {
    if ((index < 0) || (index > getDefaultValuesCount() - 1)) {
      return null;
    }
    return (String)this.hasValues.get(index);
  }
  
  public boolean sameAs(OcmlSlot slot)
  {
    if (!slot.getSlotName().equals(this.slotName)) {
      return false;
    }
    if (hasExplicitDataType())
    {
      if (!slot.hasExplicitDataType()) {
        return false;
      }
      if (!getDataType().equals(slot.getDataType())) {
        return false;
      }
    }
    else if (slot.hasExplicitDataType())
    {
      return false;
    }
    return true;
  }
}

