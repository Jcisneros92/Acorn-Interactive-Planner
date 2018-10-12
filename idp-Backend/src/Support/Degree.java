/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;


/**
 * Degree Class.
 * Note: contains all rules and restrictions, status flags
 * Singleton
 */
public class Degree {
    private volatile static Degree instance;

    public static final int totalHoursRequired = 121;
    
    static boolean preEnglishMet = false;
    static boolean preMathematicsMet = false;
    static boolean preNaturalScienceMet = false;
    static boolean preCSEngrMet = false;
    
    static boolean genLanguageCultureMet = false;
    static boolean genCommunicationMet = false;
    static boolean genCreativeArtMet = false;
    static boolean genAmericanHistoryMet = false;
    static boolean genPoliticalScienceMet = false;
    static boolean genSocialBehavioralScienceMet = false;
    
    static boolean proCSEngrMet = false;
    static boolean proIndustrialEngrMet = false;
    static boolean proMathematicsMet = false;
    static boolean proScienceMet = false;
    static boolean proTechnicalElectiveMet = false;                
    
    static Rule preEnglishRule;             
    static Rule preMathematicsRule;         
    static Rule preNaturalScienceRule;      
    static Rule preCSEngrRule;              
    
    static Rule genLanguageCultureRule;        
    static Rule genCommunicationRule;          
    static Rule genCreativeArtRule;            
    static Rule genAmericanHistoryRule;        
    static Rule genPoliticalSciencRule;        
    static Rule genSocialBehaviorScienceRule;  
    
    static Rule proCSEngrRule;                 
    static Rule proIndustrialEngrRule;         
    static Rule proMathematicsRule;            
    static Rule proScienceRule;                
    static Rule proTechnicalElectiveRule;          
    
    /**
     * Category enum
     * Index starting at 1 (0 reserved for not applicable)
     */
    public static enum Category {
        NOT_REQUIRED(1),PRE_ENGLISH(2),PRE_MATHEMATICS(3),PRE_NATURAL_SCIENCE(4),PRE_CSENGR(5),
        GEN_LANGUAGE_CULTURE(6),GEN_COMMUNICATION(7),GEN_CREATIVE_ART(8),GEN_AMERICAN_HISTORY(9),GEN_POLITICAL_SCIENCE(10),GEN_SOCIAL_SCIENCE(11),
        PRO_CSENGR(12),PRO_INDUSTRIAL_ENGR(13),PRO_MATHEMATICS(14),PRO_SCIENCE(15),PRO_TECHNICAL_ELECTIVE(16);
        
        private final int id; 
        Category(int id){this.id = id;}
        public int value(){return this.id;}
        
        @Override
        public String toString(){
            String output;
            switch(id){
                case 1:
                    output = "-Not required";
                    break;
                case 2:
                    output = "PreProf-English";
                    break;
                case 3:
                    output = "PreProf-Mathematics";
                    break;
                case 4:
                    output = "PreProf-Natural Science";
                    break;
                case 5:
                    output = "PreProf-CS & Engr";
                    break;
                case 6:
                    output = "GenEd-Lang & Culture";
                    break;
                case 7:
                    output = "GenEd-Communication";
                    break;
                case 8:
                    output = "GenEd-Creative Arts";
                    break;
                case 9:
                    output = "GenEd-American History";
                    break;
                case 10:
                    output = "GenEd-Political Science";
                    break;
                case 11:
                    output = "GenEd-Social Science";
                    break;
                case 12:
                    output = "Prof-CS & Engr";
                    break;
                case 13:
                    output = "Prof-Industrial Engr";
                    break;
                case 14:
                    output = "Prof-Mathematics";
                    break;
                case 15:
                    output = "Prof-Science";
                    break;
                default:
                    output = "Prof-Tech Elective";                    
            }
            return output;
        }
    }
        
    /**
     * Default constructor for CS major.
     * All requirement rules are based on 2018-2019 guide by the CS Dept
     */
    private Degree(){
        preEnglishRule = new Rule("ENGL1301", "");
        preMathematicsRule = new Rule("MATH1426&MATH2425", "");
        preNaturalScienceRule = new Rule("PHYS1443&PHYS1444", "");
        preCSEngrRule = new Rule("ENGR1250&CSE1105&CSE1320&CSE1325&CSE2100&CSE2312&CSE2315&CSE2320&(UNIV1131|ENGR1101)", "");
        genLanguageCultureRule = new Rule("ANTH2322|ARAB2314|ARCH2300|ART1317|CHIN2314|CLAS1300|ENGL2303|ENGL2309|ENGL2319|ENGL2329|FREN2314|GERM2314|GLOBAL2301|GREK2314|INTS1310|KORE2314|LATN2314|LING2371|PHIL1304|PHIL2300|PORT2314|RUSS2314|SPAN2314","");
        genCommunicationRule = new Rule("COMS2302", "");
        genCreativeArtRule = new Rule("ARCH1301|ART1301|ART1309|ART1310|MUSI1300|MUSI1302|MUSI2300|MUSI2301|THEA1342|THEA1343","");
        genAmericanHistoryRule = new Rule("HIST1311&HIST1312", "");
        genPoliticalSciencRule = new Rule("POLS2311&POLS2312", "");
        genSocialBehaviorScienceRule = new Rule("IE2308|ECON2305", "");
        proCSEngrRule = new Rule("CSE3302&CSE3310&CSE3315&CSE3320&CSE3330&CSE4308&CSE4314&CSE4316&CSE4317&CSE4344&(CSE4303|CSE4305|CSE4360)", "");
        proIndustrialEngrRule = new Rule("IE3301|MATH3313", "");
        proMathematicsRule = new Rule("(CSE3380|MATH3330)&(MATH2326|MATH3303|MATH3304|CSE4345)", "");
        proScienceRule = new Rule("BIOL1441|CHEM1441|CHEM1465", "");
        proTechnicalElectiveRule = new Rule("", "04");
    }
    
    public static Degree getInstance(){
        if(instance==null)
            instance = new Degree();
        return instance;
    }            
    
    /**
     * Set met flags
     * @param category
     * @param bValue 
     */
    protected static void setMetFlags(Category category, boolean bValue){
        switch(category){
            case PRE_ENGLISH:
                preEnglishMet = bValue;
                break;
            case PRE_MATHEMATICS:
                preMathematicsMet = bValue;
                break;
            case PRE_NATURAL_SCIENCE:
                preNaturalScienceMet = bValue;
                break;
            case PRE_CSENGR:
                preCSEngrMet = bValue;
                break;
            case GEN_LANGUAGE_CULTURE:
                genLanguageCultureMet = bValue;
                break;
            case GEN_COMMUNICATION:
                genCommunicationMet = bValue;
                break;
            case GEN_CREATIVE_ART:
                genCreativeArtMet = bValue;
                break;
            case GEN_AMERICAN_HISTORY:
                genAmericanHistoryMet = bValue;
                break;
            case GEN_POLITICAL_SCIENCE:
                genPoliticalScienceMet = bValue;
                break;
            case GEN_SOCIAL_SCIENCE:
                genSocialBehavioralScienceMet = bValue;
                break;
            case PRO_CSENGR:
                proCSEngrMet = bValue;
                break;
            case PRO_INDUSTRIAL_ENGR:
                proIndustrialEngrMet = bValue;
                break;
            case PRO_MATHEMATICS:
                proMathematicsMet = bValue;
                break;
            case PRO_SCIENCE:
                proScienceMet = bValue;
                break;
            case PRO_TECHNICAL_ELECTIVE:
                proTechnicalElectiveMet = bValue;
                break;
            default:                        
        }
    }        

    /**
     * Return rule by category
     * @param category
     * @return rule
     */
    protected static Rule getRules(Category category){        
        switch (category){            
            case PRE_ENGLISH:
                return preEnglishRule;                
            case PRE_MATHEMATICS:
                return preMathematicsRule;                
            case PRE_NATURAL_SCIENCE:
                return preNaturalScienceRule;                
            case PRE_CSENGR:
                return preCSEngrRule;                
            case GEN_LANGUAGE_CULTURE:
                return genLanguageCultureRule;                
            case GEN_COMMUNICATION:
                return genCommunicationRule;                
            case GEN_CREATIVE_ART:
                return genCreativeArtRule;                
            case GEN_AMERICAN_HISTORY:
                return genAmericanHistoryRule;                
            case GEN_POLITICAL_SCIENCE:
                return genPoliticalSciencRule;                
            case GEN_SOCIAL_SCIENCE:
                return genSocialBehaviorScienceRule;                
            case PRO_CSENGR:
                return proCSEngrRule;                
            case PRO_INDUSTRIAL_ENGR:
                return proIndustrialEngrRule;                
            case PRO_MATHEMATICS:
                return proMathematicsRule;                
            case PRO_SCIENCE:
                return proScienceRule;                
            case PRO_TECHNICAL_ELECTIVE:
                return proTechnicalElectiveRule;                
            default:
                return null;
        }        
    }    
    
    /**
     * Rule class.
     * Note: helper
     */
    public class Rule {
        String primaryRule;         // Primary rule: logical expression
        String secondaryRule;       // Secondary rule: for tecnical elective, minimum number in TechElective set

        public Rule(String prime, String second) {
            this.primaryRule = prime;
            this.secondaryRule = second;        
        }
    
        public String primeRule(){return primaryRule;}
        public String secRule(){return secondaryRule;}    
    }
}
