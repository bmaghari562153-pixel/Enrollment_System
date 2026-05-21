package application;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Student_BST     studentBST  = new Student_BST();
    public static Course_Graph    courseGraph  = new Course_Graph();
    public static Datasyncservice dataSync     = new Datasyncservice(studentBST, courseGraph);

    @Override
    public void start(Stage primaryStage) {
        try {
        	
        	 runBenchmark();
            dataSync.loadAll();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            primaryStage.setTitle("Enrollment System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void runBenchmark() {
        System.out.println("=== PERFORMANCE BENCHMARK ===");

        int[] sizes = {10, 100, 1000};

        for (int size : sizes) {
            
            Student_BST bst = new Student_BST();
            List<Student> list = new ArrayList<>();

            for (int i = 0; i < size; i++) {
            	Student s = new Student("S" + i, "First" + i, "Last" + i, "BS IT", 1);
                bst.insert(s);
                list.add(s);
            }

            String target = "S" + (size - 1); 

          
            long start = System.nanoTime();
            for (Student s : list) {
                if (s.getStudentId().equals(target)) break;
            }
            long linearTime = System.nanoTime() - start;

           
            start = System.nanoTime();
            bst.search(target);
            long bstTime = System.nanoTime() - start;

            System.out.println("\nSize: " + size);
            System.out.println("  Linear Search : " + linearTime + " ns");
            System.out.println("  BST Search    : " + bstTime    + " ns");
            System.out.printf ("  Improvement   : %.2fx faster%n",
                               (double) linearTime / bstTime);
        }

        System.out.println("\n=============================");
    }
    
    

    public static void main(String[] args) {
        launch(args);
    }
}