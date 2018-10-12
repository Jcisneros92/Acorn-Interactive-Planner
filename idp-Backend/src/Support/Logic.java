/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Support;

import Support.Degree.Category;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Logic subsystems.
 * Note: Rules evaluation, Data filtering/processing
 */
public abstract class Logic {                    
    
    // Global variables to be accessible within the package =======================    
    protected static Map<String,Course> courses = new HashMap<>();                  // Collection of courses (Main data).            
    @SuppressWarnings("unused")
	private static final Degree userDegree = Degree.getInstance();                  // Dummy variable to enforce Degree class initialization
    protected static Set<Course> userTakenCourses = new HashSet<>();                // Set of user taken courses    
    // end global variables =======================================================            
    
    // Public methods ============================================================                              
        
    /**
     * Return Collection of all courses in database 
     * (for debugging purpose  - to be delete at final, use one in Logic Controller instead)
     * Note: this method return a reference of in memory database. changes made will directly affect the main database (use with caution!!!)
     * @return Map
     */
    public static Map<String,Course> getCourses(){
        return courses;
    }
        
        
    // End public methods ========================================================
                
    /**
     * Make copy and merge courses user has taken     
     * (take in list of courses)
     * @param takenCourses      
     */
    protected static void mergeCoursesUserHasTaken(List<Course> takenCourses){
        userTakenCourses =  takenCourses.stream().collect(Collectors.toSet());
        takenCourses.forEach(course -> courses.get(course.name()).setTaken(true));
    }                       
    
    /**
     * List courses user permitted to select (uncategorized).
     * note: Set must be used to eliminate duplicates and utilize bulk data operation on collections
     * @return collection of courses     
     */
    protected static Set<Course> coursesUserCanSelect(){                
        // Update met flags before continuing
        for(Degree.Category category:Degree.Category.values()){
            isRequirementMet(category);
        }
        
        // Start with initial set of Courses that have not been taken, which have prerequisites met,
        // including those with prerequisites met but not Co-requisites, excluding courses in NOT_REQUIRED category        
        Set<Course> initialSet = courses.values().parallelStream()
                .filter(course -> (!course.taken() && 
                        (isPrereqAndCoreqMet(course) || 
                                isPrereqMetButNotCoreqMet(course))))
                .collect(Collectors.toSet());                                
        
        // Handle special cases ===============================
        // Remove UNIV1131 if ENGR1101 is taken and vice versa
        if(isCourseTaken("ENGR1101")){initialSet.remove(courses.get("UNIV1131"));}                                    
        if(isCourseTaken("UNIV1131")){initialSet.remove(courses.get("ENGR1101"));}
        
        // Remove IE2308 if ECON2305 is taken and vice versa
        if(isCourseTaken("IE2308")){initialSet.remove(courses.get("ECON2305"));}                                    
        if(isCourseTaken("ECON2305")){initialSet.remove(courses.get("IE2308"));}        
        
        // Remove IE3301 if MATH3313 is taken and vice versa
        if(isCourseTaken("IE3301")){initialSet.remove(courses.get("MATH3313"));}                                    
        if(isCourseTaken("MATH3313")){initialSet.remove(courses.get("IE3301"));}
        
        // Remove CSE3380 if MATH3330 is taken and vice versa
        if(isCourseTaken("CSE3380")){initialSet.remove(courses.get("MATH3330"));}                                    
        if(isCourseTaken("MATH3330")){initialSet.remove(courses.get("CSE3380"));}                
        
        // If neither MATH2326, MATH3303,MATH3304 is taken, and CSE4345 is taken, and the number of courses taken to full fill tech elective is 4
        // then CSE4345 has set metFlags on both math elective and tech elective to true incorrectly. We need to reverse these 2 flags
        if((!isCourseTaken("MATH2326")) && (!isCourseTaken("MATH3303")) && (!isCourseTaken("MATH3304")) && isCourseTaken("CSE4345")){
            long techElectiveCount = courses.values().parallelStream()
                    .filter(course -> isInCategory(course, Degree.Category.PRO_TECHNICAL_ELECTIVE)&& isCourseTaken(course.name()))
                    .count();
            if(techElectiveCount == 4){
                //Special case detected, reverse met flags on PRO_MATHEMATICS and PRO_TECHNICAL_ELECTIVE
                Degree.setMetFlags(Degree.Category.PRO_MATHEMATICS, false);
                Degree.setMetFlags(Degree.Category.PRO_TECHNICAL_ELECTIVE, false);
            }
        }                
        // End handle special cases ============================                

        // Eliminate courses no longer needed because genLanguageCulture requirement is met
        if(Degree.genLanguageCultureMet){
            Set<Course> genLanguageCulture = initialSet.parallelStream().filter(course -> isInCategory(course, Degree.Category.GEN_LANGUAGE_CULTURE)).collect(Collectors.toSet());
            initialSet.removeAll(genLanguageCulture);
        }                

        // Eliminate courses no longer needed because genCreativeArt requirement is met
        if(Degree.genCreativeArtMet){
            Set<Course> genCreativeArt = initialSet.parallelStream().filter(course -> isInCategory(course, Degree.Category.GEN_CREATIVE_ART)).collect(Collectors.toSet());
            initialSet.removeAll(genCreativeArt);
        }                

        // Eliminate courses no longer needed because genSocialBehavioralScience requirement is met
        if(Degree.genSocialBehavioralScienceMet){
            Set<Course> genSocialBehavioralScience = initialSet.parallelStream().filter(course -> isInCategory(course, Degree.Category.GEN_SOCIAL_SCIENCE)).collect(Collectors.toSet());
            initialSet.removeAll(genSocialBehavioralScience);
        }                

        // Eliminate courses no longer needed because proCSEngr requirement is met
        Set<Course> proCSEngrAndProTechElect = initialSet.parallelStream()
                .filter(course -> (isInCategory(course, Degree.Category.PRO_CSENGR) && isInCategory(course, Degree.Category.PRO_TECHNICAL_ELECTIVE)) )
                .collect(Collectors.toSet());        
        
        if(Degree.proCSEngrMet){
            Set<Course> proCSEngr = initialSet.parallelStream().filter(course -> isInCategory(course, Degree.Category.PRO_CSENGR)).collect(Collectors.toSet());
            
            initialSet.removeAll(proCSEngr);                // Remove proCSEngr including proCSEngrAndproTechElec
            initialSet.addAll(proCSEngrAndProTechElect);    // Add proCSEngrAndproTechElect back to the set. This line must appear before eliminating proTechnicalElective.
        }                

        // Eliminate courses no longer needed because proIndustrialEngr requirement is met
        if(Degree.proIndustrialEngrMet){
            Set<Course> proIndustrialEngr = initialSet.parallelStream().filter(cr -> isInCategory(cr, Degree.Category.PRO_INDUSTRIAL_ENGR)).collect(Collectors.toSet());
            initialSet.removeAll(proIndustrialEngr);
        }                

        // Eliminate courses no longer needed because proMathematics requirement is met
        if(Degree.proMathematicsMet){
            Set<Course> proMathematics = initialSet.parallelStream().filter(cr -> isInCategory(cr, Degree.Category.PRO_MATHEMATICS)).collect(Collectors.toSet());
            initialSet.removeAll(proMathematics);
            //Because special case on this was taken care of previously, 
            //CSE4345 was not used to fullfill proMathematics
            // Add CSE4345 back to the list because it can be used for both Math elective and Tech elective
            if(!courses.get("CSE4345").taken())
                initialSet.add(courses.get("CSE4345")); 
        }                

        // Eliminate courses no longer needed because proScience requirement is met
        if(Degree.proScienceMet){
            Set<Course> proScience = initialSet.parallelStream().filter(cr -> isInCategory(cr, Degree.Category.PRO_SCIENCE)).collect(Collectors.toSet());
            initialSet.removeAll(proScience);
        }                

        // Eliminate courses no longer needed because proTechnicalElective requirement is met
        if(Degree.proTechnicalElectiveMet){
            Set<Course> proTechnicalElective = initialSet.parallelStream().filter(cr -> isInCategory(cr, Degree.Category.PRO_TECHNICAL_ELECTIVE)).collect(Collectors.toSet());
            initialSet.removeAll(proTechnicalElective);
            if(!courses.get("CSE4345").taken())
                initialSet.add(courses.get("CSE4345"));         //Add CSE4345 back because it can be used for Math and tech elective
            initialSet.addAll(proCSEngrAndProTechElect);        //Add proCSEngrAndProTechElect back to the list
        }                
        
        //Handle special cases ===============================================
        //If both proMathematics and proTechnicalElective met, then remove CSE4345
        if(Degree.proMathematicsMet && Degree.proTechnicalElectiveMet && (!courses.get("CSE4345").taken()))
            initialSet.remove(courses.get("CSE4345"));
        
        //If both proCSEngr and proTechnicalElective met, then remove the proCSEngrAndProTechElect set (intersection of both sets)
        if(Degree.proCSEngrMet && Degree.proTechnicalElectiveMet)
            initialSet.removeAll(proCSEngrAndProTechElect);
        // End handle special cases ==========================================
        
        //Handling Corequisite
        //Find all courses that have PreReq met but not CoReq
        Set<Course> xx = initialSet.stream()
                .filter(course -> isPrereqMetButNotCoreqMet(course))
                .collect(Collectors.toSet());
        
        for(Course course: xx){
            Set<Course> coreqSet = findCorequisites(course);    //Find corequisites
            for(Course cr:coreqSet){                            //for each coreq
                if(isPrereqAndCoreqMet(cr)){                    //check if this coreq also has prereq and coreq met
                    //Test if taking this coreq will satisfy prereq and coreq requirement for the course
                    cr.setTaken(!cr.taken());                   //flip the flag to test
                    if(isPrereqAndCoreqMet(course)){                                    
                        initialSet.add(cr);                     //Add this coreq to the list
                    }
                    cr.setTaken(!cr.taken());                   //reverse the flag set
                }else{
                    initialSet.remove(course);                  //remove this course because its coreq cannot be taken at this moment
                }
            }
        }
        
        return initialSet;
    }
    
    // Private methods ===========================================================    
        
    /**
     * Check if requirement is met by category
     * @param category
     * @return True: met; False: not met     
     */
    private static boolean isRequirementMet(Category category){
        boolean ret;
        Degree.Rule rule = Degree.getRules(category);
        if(rule == null) return true;       // if this is NOT_REQUIRED category return true
        
        String primaryRule = rule.primeRule();
        String secondaryRule = rule.secRule();
        
        boolean result1 = evaluateRule1(logicInfix2Postfix(primaryRule),Logic::isCourseTaken);   // check primary rule
        boolean result2 = evaluateRule2(secondaryRule);                                         // check secondary rule (Tech Elective ONLY)
        ret = result1 && result2;
        
        // update degree flags
        Degree.setMetFlags(category, ret);
        
        return ret;
    }    
    
    /**
     * Verify if a course is in the specified category
     * @param course
     * @param category
     * @return True: it is; False: it is not     
     */
    public static boolean isInCategory(Course course, Category category){                
        boolean ret =  (Integer.valueOf(course.category().substring(0, 2)) == category.value() || 
                Integer.valueOf(course.category().substring(2, 4)) == category.value());
        return ret;
    }                    
        
    /**
     * Test if both Prerequisites and Co-requisites met
     * @param course
     * @return True/False
     */
    private static boolean isPrereqAndCoreqMet(Course course){        
        return evaluateRule1(logicInfix2Postfix(course.prerequisites()),Logic::isCourseTaken);
    }
    
    /**
     * Test if Prerequisites met but not Co-requisites
     * Note: purpose is to find course that can be selected
     *      even though Co-requisites have not been taken. 
     * @param course
     * @return True/False
     */
    protected static boolean isPrereqMetButNotCoreqMet(Course course){
        boolean ret = false;
        boolean beforeTest = isPrereqAndCoreqMet(course);       //Get the logic value before test
        if(beforeTest == false){                                //Only test if this is false
            Set<Course> coreqSet = findCorequisites(course);    //Find Co-requisites set
            coreqSet.forEach(cr -> cr.setTaken(!cr.taken()));   //Flip the taken flags booleans
            boolean afterTest = isPrereqAndCoreqMet(course);    //Get the logic value
            coreqSet.forEach(cr -> cr.setTaken(!cr.taken()));   //Reverse the taken flags
            if(beforeTest^afterTest)                            //If Logic value changed as the result then
                ret = true;                                     //this course has prerequisites met but not Co-requisites
        }
        return ret;
    }
    
    /**
     * Used in ScheduleList class for schedule consistency check     
     * @param course
     * @return True: met; False: not met
     */
    protected static boolean consistencyCheck_isPrereqMet(Course course){        
        return evaluateRule1(logicInfix2Postfix(course.prerequisites()),ScheduleList::consistencyCheck_isCourseMarked);
    }
        
    /**
     * Check if course is taken.     
     * @param courseName
     * @return True: taken; False: not taken or not in database
     */
    private static boolean isCourseTaken(String courseName){
        return (courses.get(courseName.toUpperCase())==null) ? false : courses.get(courseName.toUpperCase()).taken();
    }
    
    /**
     * Find Co-requisites.
     * Note: Co-requisites are identified by lower cases, specified in the prerequisites field,
     *      the return set will contain names that have been converted to upper cases
     *      This method works with assumption that there is no spelling mistake in course names in the prerequisites field
     *      since misspelled course names could give the same result (not found) and are mistakenly ID'ed as Co-requisites
     *      Currently, there is no plan to do error checking on this task.
     * @param course
     * @return Collection of courses
     */
    protected static Set<Course> findCorequisites(Course course){
        Set<Course> ret = new HashSet<>();
        if(!course.prerequisites().isEmpty()){
            String[] listOfPrereqCoreq = logicInfix2Postfix(course.prerequisites()).split(",");
            for(String item:listOfPrereqCoreq){
                if(!(item.contains("&")||item.contains("|"))){                
                    if(courses.get(item)==null)                 // course cannot be found (because of lowercase) then it is corequisite
                        ret.add(courses.get(item.toUpperCase()));
                }
            }
        }        
        return ret;
    }
        
    /**
     * Evaluate primary rule (Logical expression).
     * Note: This method evaluates logical expression  
     *      which has already been converted to postfix notation
     *      The evaluating function must implement Predicate<String> interface     
     * @param inExpression
     * @return True: met; false: not met     
     */
    private static boolean evaluateRule1(String inExpression, Predicate<String> inFunc){        
        if(inExpression.isEmpty()) return true;
        
        Stack<Boolean> bStack = new Stack<>();
        String[] strArray = inExpression.split(",");
        int len = strArray.length;
        
        for(int i =0; i<len; i++){
            if(strArray[i].contains("&")){
                boolean tmp1 = bStack.pop();
                boolean tmp2 = bStack.pop();
                bStack.push(tmp1 && tmp2);                
                
            }else if(strArray[i].contains("|")){
                boolean tmp3 = bStack.pop();
                boolean tmp4 = bStack.pop();
                bStack.push(tmp3 || tmp4);                
                
            }else{                
                bStack.add(inFunc.test(strArray[i]));
            }
        }
        return bStack.pop();
    }
    
    /**
     * Evaluate secondary rule (Technical Elective ONLY)
     * @param inExpression
     * @return True: requirement met; False: requirement not met     
     */
    private static boolean evaluateRule2(String inExpression){        
        if(inExpression.isEmpty()) return true;
                        
        boolean ret = false;                
        int targetCount = Integer.valueOf(inExpression.substring(0, 2));        

        // Find set1 of all courses can be used to fulfill Pro Technical Elective requirement
        Set<Course> ProTechElectiveCourses = courses.values().parallelStream()
                .filter(course -> (isInCategory(course, Degree.Category.PRO_TECHNICAL_ELECTIVE)))
                .collect(Collectors.toSet());
        
        // Find set2 of all courses can be used to fulfill either Pro CSEngr or Pro Technical Elective requirement
        Set<Course> ProCSEngrAndProTechElectiveCourses = courses.values().parallelStream()
                .filter(course -> (isInCategory(course, Degree.Category.PRO_CSENGR)) && isInCategory(course, Degree.Category.PRO_TECHNICAL_ELECTIVE))
                .collect(Collectors.toSet());
        
        // Randomly pick one from the course taken in set2 (ProCSEngrAndProTechElectiveCourses). 
        // This course is used to fulfill ProCSEngr requirement and no longer can be used for ProTechElective
        // therefore remove it from set1 (ProTechElectiveCourses)
        for(Course course:ProCSEngrAndProTechElectiveCourses){
            if(course.taken()){
                ProTechElectiveCourses.remove(course);
                break;
            }
        }                                
        
        // verify the number of taken courses in this set against technical elective requirement
        // if equals or exceeds number of courses required then tech elective is met 
        int count = 0;
        for(Course course:ProTechElectiveCourses){
            if(course.taken())
                count++;
            if(count>=targetCount){
                ret = true;
                break;
            }                
        }                              
        return ret;
    }
    
    /**
     * Convert logic infix notation to postfix notation
     * @param inInfix: Infix notation 
     * @return Postfix notation     
     */
    private static String logicInfix2Postfix(String inInfix){        
        Queue<String> operandQueue = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();
        int idx = 0; int len = inInfix.length();
        while (idx < len) {            
            if(inInfix.charAt(idx)=='(') {
                operatorStack.push(String.valueOf(inInfix.charAt(idx)));
                idx++;
                continue;
            }
                
            if(inInfix.charAt(idx)==')') {                                                        
                while(!operatorStack.peek().contains("(")){
                    operandQueue.add(",");
                    operandQueue.add(operatorStack.pop());
                }
                operatorStack.pop();    // flush out the '('
                
                idx++;
                continue;
            }
            if((inInfix.charAt(idx)=='&')||(inInfix.charAt(idx)=='|')){ 
                operatorStack.push(String.valueOf(inInfix.charAt(idx)));
                operandQueue.add(",");
                idx++;
                continue;
            }
            operandQueue.add(String.valueOf(inInfix.charAt(idx)));
            idx++;
        }
        while(!operatorStack.isEmpty()){
            operandQueue.add(",");
            operandQueue.add(operatorStack.pop());
        }
        StringBuilder ret = new StringBuilder(len);
        while (!operandQueue.isEmpty()) {            
            ret.append(operandQueue.poll());
        }        
        return ret.toString().trim();
    }        
    
    // End Private methods ==================================================    
}