package application;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;





public class Course_Graph {

	
	
	  private final Map<String, List<String>> adjList;
	  
	    public Course_Graph() {
	        this.adjList = new HashMap<>();
	    }
	
		    public void addCourse(String courseCode) {
	        adjList.putIfAbsent(courseCode, new ArrayList<>());
	    }
	
	
		    public void addPrerequisite(String courseCode, String prereqCode) {
	        adjList.putIfAbsent(courseCode, new ArrayList<>());
	        adjList.putIfAbsent(prereqCode, new ArrayList<>());
	 
	        List<String> prereqs = adjList.get(courseCode);
	        if (!prereqs.contains(prereqCode)) {
	            prereqs.add(prereqCode);
	        }
	    }
	
		    public void removePrerequisite(String courseCode, String prereqCode) {
	        List<String> prereqs = adjList.get(courseCode);
	        if (prereqs != null) prereqs.remove(prereqCode);
	    }
	
		    public void removeCourse(String courseCode) {
	        adjList.remove(courseCode);
	        for (List<String> prereqs : adjList.values()) {
	            prereqs.remove(courseCode);
	        }
	    }
	
		    public void clear() {
	        adjList.clear();
	    }
	
	
	    public List<String> topologicalSort() {
	    	 
	        Map<String, List<String>> reverseAdj = new HashMap<>();
	        Map<String, Integer>      inDegree   = new HashMap<>();
	 	     
	        for (String course : adjList.keySet()) {
	            reverseAdj.putIfAbsent(course, new ArrayList<>());
	            inDegree.putIfAbsent(course, 0);
	        }
	 	       
	        for (String course : adjList.keySet()) {
	            for (String prereq : adjList.get(course)) {
	            
	                reverseAdj.putIfAbsent(prereq, new ArrayList<>());
	                reverseAdj.get(prereq).add(course);
	 
	          
	                inDegree.put(course, inDegree.getOrDefault(course, 0) + 1);
	            }
	        }
	 	
	        Queue<String> queue = new LinkedList<>();
	        for (String course : inDegree.keySet()) {
	            if (inDegree.get(course) == 0) {
	                queue.offer(course);
	            }
	        }
	
	        List<String> order = new ArrayList<>();
	        
	        while (!queue.isEmpty()) {
	            String current = queue.poll();
	            order.add(current);
	 
	           
	            for (String next : reverseAdj.getOrDefault(current, new ArrayList<>())) {
	                inDegree.put(next, inDegree.get(next) - 1);
	                if (inDegree.get(next) == 0) {
	                    queue.offer(next);
	                }
	            }
	        }
	 
	        
	        return (order.size() == adjList.size()) ? order : new ArrayList<>();
	    }
	
	
		    public boolean hasCycle() {
	        return topologicalSort().isEmpty() && !adjList.isEmpty();
	    }
	
		
	    public boolean prerequisitesSatisfied(String courseCode, List<String> completedCodes) {
	        List<String> prereqs = adjList.get(courseCode);
	        if (prereqs == null || prereqs.isEmpty()) return true;
	        return completedCodes.containsAll(prereqs);
	    }
	 
		
	    public List<String> getPrerequisites(String courseCode) {
	        return adjList.getOrDefault(courseCode, new ArrayList<>());
	    }
	
	    public List<String> getAllCourses() {
	        return new ArrayList<>(adjList.keySet());
	    }
		
	    public Map<String, List<String>> getAdjList() {
	        return adjList;
	    }
	
	
	
	
	
	
	
	
	
}
