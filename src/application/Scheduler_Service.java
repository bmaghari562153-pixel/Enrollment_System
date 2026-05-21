package application;
import java.util.ArrayList;
import java.util.List;


public class Scheduler_Service {

	
	
	 private final Student_BST   studentBST;
	    private final Course_Graph  courseGraph;
	    private final Min_Heap      minHeap;
	    private final EnrollmentDao enrollmentDao;
	    private final CourseDao     courseDao;
	 
	    public Scheduler_Service(Student_BST studentBST, Course_Graph courseGraph) {
	        this.studentBST   = studentBST;
	        this.courseGraph  = courseGraph;
	        this.minHeap      = new Min_Heap();
	        this.enrollmentDao = new EnrollmentDao();
	        this.courseDao     = new CourseDao();
	    }
	 
	
	
	    public Student findStudent(String studentId) {
	        return studentBST.search(studentId);
	    }
	
	
	
	    public ValidationResult validatePrerequisites(String studentId, String courseCode) {
	    	 
	       
	        Student student = studentBST.search(studentId);
	        if (student == null) {
	            return new ValidationResult(false, "Student not found: " + studentId);
	        }
	 
	       
	        if (!courseGraph.getAllCourses().contains(courseCode)) {
	            return new ValidationResult(false, "Course not found: " + courseCode);
	        }
	 
	     
	        List<String> completed = enrollmentDao.getCompletedCourseCodes(studentId);
	 
	      
	        List<String> prereqs = courseGraph.getPrerequisites(courseCode);
	 
	        List<String> missing = new ArrayList<>();
	        for (String prereq : prereqs) {
	            if (!completed.contains(prereq)) {
	                missing.add(prereq);
	            }
	        }
	 
	        if (missing.isEmpty()) {
	            return new ValidationResult(true, "Prerequisites satisfied.");
	        } else {
	            return new ValidationResult(false, "Missing prerequisites: " + missing.toString());
	        }
	    }
	
	    public boolean isAlreadyEnrolled(String studentId, String courseCode) {
	        Enrollment existing = enrollmentDao.getEnrollment(studentId, courseCode);
	        return existing != null && existing.isEnrolled();
	    }
	
	
	
	
	    public List<Course> generateSchedule(String studentId) {
	    	 
	  
	        Student student = studentBST.search(studentId);
	        if (student == null) {
	            System.err.println("[SchedulerService] Student not found: " + studentId);
	            return new ArrayList<>();
	        }
	 
	      
	        List<String> completed = enrollmentDao.getCompletedCourseCodes(studentId);
	 
	     
	        List<Enrollment> currentEnrollments = enrollmentDao.getEnrollmentsByStudent(studentId);
	        List<String> currentCodes = new ArrayList<>();
	        for (Enrollment e : currentEnrollments) {
	            currentCodes.add(e.getCourseCode());
	        }
	 
	      
	        List<String> topoOrder = courseGraph.topologicalSort();
	        if (topoOrder.isEmpty()) {
	            System.err.println("[SchedulerService] Cycle detected in course graph.");
	            return new ArrayList<>();
	        }
	 
	  
	        List<Course> eligible = new ArrayList<>();
	        for (String courseCode : topoOrder) {
	           
	            if (currentCodes.contains(courseCode) || completed.contains(courseCode)) continue;
	 
	          
	            if (courseGraph.prerequisitesSatisfied(courseCode, completed)) {
	                Course c = courseDao.getCourse(courseCode);
	                if (c != null && c.getStartTime() != null) {
	                    eligible.add(c);
	                }
	            }
	        }
	 
	        
	        quickSort(eligible, 0, eligible.size() - 1);
	 
	       
	        List<Course> schedule = minHeap.greedySchedule(eligible);
	 
	        System.out.println("[SchedulerService] Generated schedule for " + studentId
	                + ": " + schedule.size() + " courses.");
	        return schedule;
	    }
	
	
	
	    public boolean hasConflict(Course a, Course b) {
	        if (a.getDay() == null || b.getDay() == null) return false;
	        if (!a.getDay().equals(b.getDay())) return false;  
	 
	        String startA = a.getStartTime();
	        String endA   = a.getEndTime();
	        String startB = b.getStartTime();
	        String endB   = b.getEndTime();
	 
	        if (startA == null || endA == null || startB == null || endB == null) return false;
	 
	        
	        return startA.compareTo(endB) < 0 && startB.compareTo(endA) < 0;
	    }
	 
	
	
	
	
	    public List<Course> resolveConflicts(List<Course> proposed) {
	        List<Course> resolved = new ArrayList<>();
	 
	        for (Course candidate : proposed) {
	            boolean conflicts = false;
	            for (Course accepted : resolved) {
	                if (hasConflict(candidate, accepted)) {
	                    conflicts = true;
	                    break;
	                }
	            }
	            if (!conflicts) resolved.add(candidate);
	        }
	 
	        return resolved;
	    }
	
	
	
	
	    private void quickSort(List<Course> list, int low, int high) {
	        if (low < high) {
	            int pivotIndex = partition(list, low, high);
	            quickSort(list, low, pivotIndex - 1);
	            quickSort(list, pivotIndex + 1, high);
	        }
	    }
	 
	    private int partition(List<Course> list, int low, int high) {
	     
	        int randomIndex = low + (int)(Math.random() * (high - low + 1));
	        swap(list, randomIndex, high);
	 
	        String pivot = endTime(list.get(high));
	        int i = low - 1;
	 
	        for (int j = low; j < high; j++) {
	            if (endTime(list.get(j)).compareTo(pivot) <= 0) {
	                i++;
	                swap(list, i, j);
	            }
	        }
	 
	        swap(list, i + 1, high);
	        return i + 1;
	    }
	 
	    private void swap(List<Course> list, int i, int j) {
	        Course temp = list.get(i);
	        list.set(i, list.get(j));
	        list.set(j, temp);
	    }
	 
	    private String endTime(Course c) {
	        return (c.getEndTime() != null) ? c.getEndTime() : "";
	    }
	
	
	
	    public static class ValidationResult {
	        private final boolean passed;
	        private final String  message;
	 
	        public ValidationResult(boolean passed, String message) {
	            this.passed  = passed;
	            this.message = message;
	        }
	 
	        public boolean isPassed()  { return passed; }
	        public String  getMessage(){ return message; }
	 
	        @Override
	        public String toString() {
	            return (passed ? "PASS" : "FAIL") + ": " + message;
	        }
	    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
