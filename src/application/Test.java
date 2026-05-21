package application;

import java.util.List;


public class Test {

    private static final int REPETITIONS = 5;

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println("  BASELINE PERFORMANCE BENCHMARK");
        System.out.println("  Service.java — ArrayList + Nested Loops");
        System.out.println("=================================================\n");

       
        Service baseline = new Service();
        baseline.loadAll();

        int studentCount    = baseline.getStudents().size();
        int courseCount     = baseline.getCourses().size();
        int enrollmentCount = baseline.getEnrollments().size();

        System.out.println("Dataset loaded:");
        System.out.println("  Students    : " + studentCount);
        System.out.println("  Courses     : " + courseCount);
        System.out.println("  Enrollments : " + enrollmentCount);
        System.out.println();

        
        String testStudentId  = baseline.getStudents().isEmpty()
                ? "2024-0001"
                : baseline.getStudents().get(0).getStudentId();

        String testCourseCode = baseline.getCourses().isEmpty()
                ? "CC103"
                : baseline.getCourses().get(baseline.getCourses().size() / 2).getCourseCode();

        System.out.println("Test targets:");
        System.out.println("  Student ID  : " + testStudentId);
        System.out.println("  Course Code : " + testCourseCode);
        System.out.println();

      
        System.out.println("-------------------------------------------------");
        System.out.println("TEST 1: findStudentLinear(studentId)  [O(n)]");
        System.out.println("-------------------------------------------------");

        long[] t1 = new long[REPETITIONS];
        for (int i = 0; i < REPETITIONS; i++) {
            long start = System.nanoTime();
            baseline.findStudentLinear(testStudentId);
            t1[i] = System.nanoTime() - start;
            System.out.printf("  Run %d: %,d ns  (%.4f ms)%n", i + 1, t1[i], toMs(t1[i]));
        }
        System.out.printf("  Average: %,d ns  (%.4f ms)%n%n", average(t1), toMs(average(t1)));


    
        System.out.println("-------------------------------------------------");
        System.out.println("TEST 2: checkPrerequisitesNested(studentId, courseCode)  [O(p x n)]");
        System.out.println("-------------------------------------------------");

        long[] t2 = new long[REPETITIONS];
        for (int i = 0; i < REPETITIONS; i++) {
            long start = System.nanoTime();
            baseline.checkPrerequisitesNested(testStudentId, testCourseCode);
            t2[i] = System.nanoTime() - start;
            System.out.printf("  Run %d: %,d ns  (%.4f ms)%n", i + 1, t2[i], toMs(t2[i]));
        }
        System.out.printf("  Average: %,d ns  (%.4f ms)%n%n", average(t2), toMs(average(t2)));


       
        System.out.println("-------------------------------------------------");
        System.out.println("TEST 3: simpleSchedule(studentId)  [O(n²)]");
        System.out.println("-------------------------------------------------");

        long[] t3 = new long[REPETITIONS];
        for (int i = 0; i < REPETITIONS; i++) {
            long start = System.nanoTime();
            List<Course> result = baseline.simpleSchedule(testStudentId);
            t3[i] = System.nanoTime() - start;
            System.out.printf("  Run %d: %,d ns  (%.4f ms)  [%d courses scheduled]%n",
                    i + 1, t3[i], toMs(t3[i]), result.size());
        }
        System.out.printf("  Average: %,d ns  (%.4f ms)%n%n", average(t3), toMs(average(t3)));


      
        System.out.println("=================================================");
        System.out.println("  SUMMARY — BASELINE RESULTS");
        System.out.println("=================================================");
        System.out.printf("  %-35s %15s%n", "Operation", "Avg Time (ms)");
        System.out.println("  " + "-".repeat(52));
        System.out.printf("  %-35s %15.4f ms%n", "findStudentLinear()  O(n)",          toMs(average(t1)));
        System.out.printf("  %-35s %15.4f ms%n", "checkPrerequisitesNested()  O(p×n)", toMs(average(t2)));
        System.out.printf("  %-35s %15.4f ms%n", "simpleSchedule()  O(n²)",            toMs(average(t3)));
        System.out.println();
        System.out.println("  Dataset: " + studentCount + " students, "
                + courseCount + " courses, " + enrollmentCount + " enrollments");
        System.out.println("  Repetitions per test: " + REPETITIONS);
        System.out.println("=================================================");
    }

   

    private static long average(long[] times) {
        long sum = 0;
        for (long t : times) sum += t;
        return sum / times.length;
    }

    private static double toMs(long nanos) {
        return nanos / 1_000_000.0;
    }
}
