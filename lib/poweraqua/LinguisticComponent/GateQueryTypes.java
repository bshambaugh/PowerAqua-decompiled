package poweraqua.LinguisticComponent;

public abstract interface GateQueryTypes
{
  public static final int NON_IMPLEMENTED = 0;
  public static final int DESCRIPTION = 1;
  public static final int WH_GENERICTERM = 2;
  public static final int WH_UNKNTERM = 3;
  public static final int WH_UNKNREL = 4;
  public static final int AFFIRM_NEG = 5;
  public static final int AFFIRM_NEG_PSEUDOREL = 6;
  public static final int HOW_LONG = 7;
  public static final int QU_WHE = 8;
  public static final int AFFIRM_NEG_3TERM = 9;
  public static final int AFFIRM_NEG_1TERMCLAUSE = 10;
  public static final int WH_3UNKNREL = 11;
  public static final int WH_GENERIC_1TERMCLAUSE = 12;
  public static final int WH_UNKNTERM_2CLAUSE = 13;
  public static final int WH_3TERM = 14;
  public static final int WH_3TERM_CLAUSE = 15;
  public static final int WH_3TERM_1CLAUSE = 16;
  public static final int WH_4TERM = 17;
  public static final int WH_COMB_AND = 18;
  public static final int WH_COMB_OR = 19;
  public static final int WH_COMB_COND = 20;
  public static final int WH_COMB_COND_RELNN = 21;
  public static final int AFFIRM_NEG_WHCLAUSE = 22;
  public static final int WH_GENERIC_WHCLAUSE = 23;
  public static final int WH_UNKNOWN = 24;
  public static final int PATTERNS_2 = 25;
  public static final int COMPOUND = 26;
  public static final int UNCLASSIFIED = 27;
  public static final int UNCLASSIFIED_3TERM = 28;
  public static final int IS_A_ONLY = 29;
  public static final String WHAT_IS = "what_is";
  public static final String IS_A_RELATION = "IS_A_Relation";
}

