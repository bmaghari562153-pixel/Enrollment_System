package application;
import java.util.ArrayList;
import java.util.List;



public class Min_Heap {

	
	
	
	   private final List<Course> heap;
	   
	    public Min_Heap() {
	        this.heap = new ArrayList<>();
	    }
	
	
	
	
	
	    public void insert(Course c) {
	        heap.add(c);                   
	        heapifyUp(heap.size() - 1);   
	    }
	
	
	
	    public Course extractMin() {
	        if (heap.isEmpty()) return null;
	 
	        Course min = heap.get(0);             
	        int last = heap.size() - 1;
	 
	        heap.set(0, heap.get(last));         
	        heap.remove(last);                     
	 
	        if (!heap.isEmpty()) heapifyDown(0); 
	 
	        return min;
	    }
	
	
	
	    public Course peek() {
	        return heap.isEmpty() ? null : heap.get(0);
	    }
	
	
	    public boolean isEmpty() {
	        return heap.isEmpty();
	    }
	
	
	
	    public int size() {
	        return heap.size();
	    }
	
	
	
	    public void clear() {
	        heap.clear();
	    }
	
	
	
	
	    private void heapifyUp(int i) {
	        while (i > 0) {
	            int parent = (i - 1) / 2;
	 
	            if (compareEndTime(heap.get(i), heap.get(parent)) < 0) {
	               
	                swap(i, parent);
	                i = parent;  
	            } else {
	                break;  
	            }
	        }
	    }
	
	
	
	    private void heapifyDown(int i) {
	        int size = heap.size();
	 
	        while (true) {
	            int left    = 2 * i + 1;
	            int right   = 2 * i + 2;
	            int smallest = i;
	 
	          
	            if (left < size && compareEndTime(heap.get(left), heap.get(smallest)) < 0) {
	                smallest = left;
	            }
	 
	            
	            if (right < size && compareEndTime(heap.get(right), heap.get(smallest)) < 0) {
	                smallest = right;
	            }
	 
	            if (smallest != i) {
	              
	                swap(i, smallest);
	                i = smallest;
	            } else {
	                break;  
	            }
	        }
	    }
	
	
	
	    public List<Course> greedySchedule(List<Course> candidates) {
	        clear();
	        for (Course c : candidates) insert(c);
	 
	        List<Course> scheduled = new ArrayList<>();
	        String       lastEnd   = null;   
	 
	        while (!isEmpty()) {
	            Course current = extractMin();
	 
	            if (current.getStartTime() == null || current.getEndTime() == null) {
	                continue; 
	            }
	 
	            if (lastEnd == null || current.getStartTime().compareTo(lastEnd) >= 0) {
	                
	                scheduled.add(current);
	                lastEnd = current.getEndTime();
	            }
	            
	        }
	 
	        return scheduled;
	    }
	
	
	
	
	    private int compareEndTime(Course a, Course b) {
	        String endA = (a.getEndTime() != null) ? a.getEndTime() : "";
	        String endB = (b.getEndTime() != null) ? b.getEndTime() : "";
	        return endA.compareTo(endB);
	    }
	
	
	
	
	
	    private void swap(int i, int j) {
	        Course temp = heap.get(i);
	        heap.set(i, heap.get(j));
	        heap.set(j, temp);
	    }
	
	
	
	
}
