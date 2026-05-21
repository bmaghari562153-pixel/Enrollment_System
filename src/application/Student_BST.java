package application;

import java.util.ArrayList;
import java.util.List;


public class Student_BST {

	
	
	private Node root;
	
	public Student_BST() {
		this.root = null;
	}
	
	
	
	public void insert(Student s) {
		root = insertRec(root, s);
	}
	
		
	public Student search(String studentId) {
	    Node node = searchRec(root, studentId);
	    return (node != null) ? node.data : null;
	}
		
	public void delete(String studentId) {
		root = deleteRec(root, studentId);
	}
		
	public List<Student> inOrder() {
		List<Student> result = new ArrayList<>();
		inOrderRec(root, result);
		return result;
	}
	
	
	public boolean isEmpty() {
		return root == null;
	}
	
	
	public void clear() {
		root = null;
	}
	
	
	public int size() {
		return sizeRec(root);
	}
	
	
	private Node insertRec(Node node, Student s) {
		if (node == null)
			return new Node(s);
		int cmp = s.getStudentId().compareTo(node.data.getStudentId());
		if (cmp < 0) {
		    node.left = insertRec(node.left, s);
		} else if (cmp > 0) {

					node.right = insertRec(node.right, s);
					
			} else {
		       
		       node.data = s;
		        return node;
		    }
		
		  
		    updateHeight(node);
		
		 
		    return rebalance(node);
		}

	
			private Node searchRec(Node node, String studentId) {
				 
			  
			    if (node == null) return null;
			
			    int cmp = studentId.compareTo(node.data.getStudentId());
			
			    if (cmp < 0) {
			       			        return searchRec(node.left, studentId);
			
			    } else if (cmp > 0) {
			      
			        return searchRec(node.right, studentId);
			
			    } else {
			       
			        return node;
			    }
			}

	
			 private Node deleteRec(Node node, String studentId) {
				 
			     
			        if (node == null) return null;
			 
			        int cmp = studentId.compareTo(node.data.getStudentId());
			 
			        if (cmp < 0) {
			         
			            node.left = deleteRec(node.left, studentId);
			 
			        } else if (cmp > 0) {
			           
			            node.right = deleteRec(node.right, studentId);
			 
			        } else {
			          
			 			        
			            if (node.left == null) return node.right;						           
			            if (node.right == null) return node.left;
			 
			          			     
			            Node successor = findMin(node.right);
			            node.data  = successor.data;
			            node.right = deleteRec(node.right, successor.data.getStudentId());
			        }
			 
			      
			        updateHeight(node);
			        return rebalance(node);
			    }
	
	
				 private void inOrderRec(Node node, List<Student> result) {
			        if (node == null) return;
			        inOrderRec(node.left, result);   
			        result.add(node.data);          
			        inOrderRec(node.right, result);  
			    }
	
				 private int sizeRec(Node node) {
			        if (node == null) return 0;
			        return 1 + sizeRec(node.left) + sizeRec(node.right);
			    }
	
	
				  private int height(Node node) {
			        return (node == null) ? 0 : node.height;
			    }
	
	
			  private void updateHeight(Node node) {
			        node.height = 1 + Math.max(height(node.left), height(node.right));
			    }
	
				  private int balanceFactor(Node node) {
			        return (node == null) ? 0 : height(node.left) - height(node.right);
			    }
	
				  private Node rebalance(Node node) {
			        int bf = balanceFactor(node);
			        
			        
			        	if (bf > 1) {
			            if (balanceFactor(node.left) < 0) {
			               
			                node.left = rotateLeft(node.left);
			            }
			          
			            return rotateRight(node);
			        }
			        
			        			        
			        if (bf < -1) {
			            if (balanceFactor(node.right) > 0) {
			               
			                node.right = rotateRight(node.right);
			            }
			            
			            return rotateLeft(node);
			        }
			 
			        
			        return node;
			    }
			        
			        
			  private Node rotateRight(Node y) {
			        Node x  = y.left;
			        Node T2 = x.right;
			 
			      
			        x.right = y;
			        y.left  = T2;
			 
			        
			        updateHeight(y);
			        updateHeight(x);
			 
			        return x;  
			    }
			        
			        
			        private Node rotateLeft(Node x) {
			        Node y  = x.right;
			        Node T2 = y.left;
			 
			       
			        y.left  = x;
			        x.right = T2;
			 
			      
			        updateHeight(x);
			        updateHeight(y);
			 
			        return y;  
			    }
			        
			        
			        
			  	  private Node findMin(Node node) {
			        while (node.left != null) node = node.left;
			        return node;
			    }
			        
			        
			        
			        
}
