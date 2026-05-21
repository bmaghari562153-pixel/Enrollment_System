package application;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc      = new Scanner(System.in);
    private static       Service service = new Service();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   ENROLLMENT MANAGEMENT SYSTEM");
        System.out.println("========================================");
        service.loadAll();
        System.out.println();
        runBenchmark();
        
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Choose: ");
            System.out.println();
            switch (choice) {
                case 1  -> manageStudents();
                case 2  -> manageCourses();
                case 3  -> manageEnrollments();
                case 4  -> runBenchmark();
                case 0  -> running = false;
                default -> System.out.println("Invalid option.\n");
            }
        }

        System.out.println("Goodbye!");
    }

   

    private static void printMainMenu() {
        System.out.println("----------------------------------------");
        System.out.println(" MAIN MENU");
        System.out.println("----------------------------------------");
        System.out.println(" 1. Students");
        System.out.println(" 2. Courses");
        System.out.println(" 3. Enrollments");
        System.out.println(" 4. Run Benchmark");
        System.out.println(" 0. Exit");
        System.out.println("----------------------------------------");
    }

   

    private static void manageStudents() {
        boolean back = false;
        while (!back) {
            System.out.println("=== STUDENTS ===");
            System.out.println(" 1. List all students");
            System.out.println(" 2. Find student by ID");
            System.out.println(" 3. Add student");
            System.out.println(" 4. Update student");
            System.out.println(" 5. Delete student");
            System.out.println(" 0. Back");
            int c = readInt("Choose: ");
            System.out.println();
            switch (c) {
                case 1  -> listStudents();
                case 2  -> findStudent();
                case 3  -> addStudent();
                case 4  -> updateStudent();
                case 5  -> deleteStudent();
                case 0  -> back = true;
                default -> System.out.println("Invalid option.\n");
            }
        }
    }

    private static void listStudents() {
        service.loadStudents();
        List<Student> students = service.getStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.\n");
            return;
        }
        System.out.printf("%-12s %-20s %-20s %-15s %s%n",
                "ID", "First Name", "Last Name", "Program", "Year");
        System.out.println("-".repeat(75));
        for (Student s : students) {
            System.out.printf("%-12s %-20s %-20s %-15s %d%n",
                    s.getStudentId(), s.getFirstName(), s.getLastName(),
                    s.getProgram(), s.getYearLevel());
        }
        System.out.println("Total: " + students.size() + "\n");
    }

    private static void findStudent() {
        String id = readLine("Enter student ID: ");
        Student s = service.findStudentLinear(id);
        if (s == null) {
            System.out.println("Student not found.\n");
        } else {
            System.out.println("Found: " + s + "\n");
        }
    }

    private static void addStudent() {
        System.out.println("--- Add Student ---");
        String id    = readLine("Student ID  : ");
        String first = readLine("First name  : ");
        String last  = readLine("Last name   : ");
        String prog  = readLine("Program     : ");
        int    year  = readInt ("Year level  : ");

        Student s = new Student(id, first, last, prog, year);
        boolean ok = new StudentDao().insertStudent(s);
        System.out.println(ok ? "Student added.\n" : "Failed — ID may already exist.\n");
        if (ok) service.loadStudents();
    }

    private static void updateStudent() {
        String id = readLine("Enter student ID to update: ");
        Student existing = service.findStudentLinear(id);
        if (existing == null) { System.out.println("Student not found.\n"); return; }

        System.out.println("Leave blank to keep current value.");
        String first = readLine("First name [" + existing.getFirstName() + "]: ");
        String last  = readLine("Last name  [" + existing.getLastName()  + "]: ");
        String prog  = readLine("Program    [" + existing.getProgram()   + "]: ");
        String yearS = readLine("Year level [" + existing.getYearLevel() + "]: ");

        if (!first.isEmpty()) existing.setFirstName(first);
        if (!last.isEmpty())  existing.setLastName(last);
        if (!prog.isEmpty())  existing.setProgram(prog);
        if (!yearS.isEmpty()) {
            try { existing.setYearLevel(Integer.parseInt(yearS)); }
            catch (NumberFormatException e) { System.out.println("Invalid year — keeping original."); }
        }

        boolean ok = new StudentDao().updateStudent(existing);
        System.out.println(ok ? "Student updated.\n" : "Update failed.\n");
        if (ok) service.loadStudents();
    }

    private static void deleteStudent() {
        String id = readLine("Enter student ID to delete: ");
        System.out.print("Confirm delete '" + id + "'? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Cancelled.\n"); return; }
        boolean ok = new StudentDao().deleteStudent(id);
        System.out.println(ok ? "Student deleted.\n" : "Delete failed — ID not found.\n");
        if (ok) service.loadStudents();
    }

   

    private static void manageCourses() {
        boolean back = false;
        while (!back) {
            System.out.println("=== COURSES ===");
            System.out.println(" 1. List all courses");
            System.out.println(" 2. View course details");
            System.out.println(" 3. Add course");
            System.out.println(" 4. Update course");
            System.out.println(" 5. Delete course");
            System.out.println(" 0. Back");
            int c = readInt("Choose: ");
            System.out.println();
            switch (c) {
                case 1  -> listCourses();
                case 2  -> viewCourse();
                case 3  -> addCourse();
                case 4  -> updateCourse();
                case 5  -> deleteCourse();
                case 0  -> back = true;
                default -> System.out.println("Invalid option.\n");
            }
        }
    }

    private static void listCourses() {
        service.loadCourses();
        List<Course> courses = service.getCourses();
        if (courses.isEmpty()) { System.out.println("No courses found.\n"); return; }
        System.out.printf("%-10s %-30s %5s  %-10s %-12s %-12s%n",
                "Code", "Name", "Units", "Day", "Start", "End");
        System.out.println("-".repeat(82));
        for (Course c : courses) {
            System.out.printf("%-10s %-30s %5d  %-10s %-12s %-12s%n",
                    c.getCourseCode(), c.getCourseName(), c.getUnits(),
                    nvl(c.getDay()), nvl(c.getStartTime()), nvl(c.getEndTime()));
        }
        System.out.println("Total: " + courses.size() + "\n");
    }

    private static void viewCourse() {
        String code = readLine("Enter course code: ");
        Course c = new CourseDao().getCourse(code);
        if (c == null) { System.out.println("Course not found.\n"); return; }
        System.out.println("Code        : " + c.getCourseCode());
        System.out.println("Name        : " + c.getCourseName());
        System.out.println("Units       : " + c.getUnits());
        System.out.println("Schedule    : " + c.getSchedule());
        System.out.println("Prerequisites: " + (c.getPrerequisites().isEmpty() ? "None" : c.getPrerequisites()));
        System.out.println();
    }

    private static void addCourse() {
        System.out.println("--- Add Course ---");
        String code  = readLine("Course code : ");
        String name  = readLine("Course name : ");
        int    units = readInt ("Units       : ");
        String day   = readLine("Day (e.g. MWF, TTh, blank if TBA): ");
        String start = readLine("Start time  (HH:mm, blank if TBA): ");
        String end   = readLine("End time    (HH:mm, blank if TBA): ");
        String prereqInput = readLine("Prerequisites (comma-separated codes, blank if none): ");

        List<String> prereqs = new java.util.ArrayList<>();
        if (!prereqInput.isBlank()) {
            for (String p : prereqInput.split(",")) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty()) prereqs.add(trimmed);
            }
        }

        Course c = new Course(
                code, name, units,
                day.isBlank()   ? null : day,
                start.isBlank() ? null : start,
                end.isBlank()   ? null : end,
                prereqs);

        boolean ok = new CourseDao().insertCourse(c);
        System.out.println(ok ? "Course added.\n" : "Failed — code may already exist.\n");
        if (ok) service.loadCourses();
    }

    private static void updateCourse() {
        String code = readLine("Enter course code to update: ");
        Course existing = new CourseDao().getCourse(code);
        if (existing == null) { System.out.println("Course not found.\n"); return; }

        System.out.println("Leave blank to keep current value.");
        String name  = readLine("Course name [" + existing.getCourseName()  + "]: ");
        String units = readLine("Units       [" + existing.getUnits()       + "]: ");
        String day   = readLine("Day         [" + nvl(existing.getDay())    + "]: ");
        String start = readLine("Start time  [" + nvl(existing.getStartTime()) + "]: ");
        String end   = readLine("End time    [" + nvl(existing.getEndTime())   + "]: ");

        if (!name.isEmpty())  existing.setCourseName(name);
        if (!units.isEmpty()) {
            try { existing.setUnits(Integer.parseInt(units)); }
            catch (NumberFormatException e) { System.out.println("Invalid units — keeping original."); }
        }
        if (!day.isEmpty())   existing.setDay(day);
        if (!start.isEmpty()) existing.setStartTime(start);
        if (!end.isEmpty())   existing.setEndTime(end);

        boolean ok = new CourseDao().updateCourse(existing);
        System.out.println(ok ? "Course updated.\n" : "Update failed.\n");
        if (ok) service.loadCourses();
    }

    private static void deleteCourse() {
        String code = readLine("Enter course code to delete: ");
        System.out.print("Confirm delete '" + code + "'? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Cancelled.\n"); return; }
        boolean ok = new CourseDao().deleteCourse(code);
        System.out.println(ok ? "Course deleted.\n" : "Delete failed — code not found.\n");
        if (ok) service.loadCourses();
    }

  

    private static void manageEnrollments() {
        boolean back = false;
        while (!back) {
            System.out.println("=== ENROLLMENTS ===");
            System.out.println(" 1. List all enrollments");
            System.out.println(" 2. List enrollments by student");
            System.out.println(" 3. List enrollments by course");
            System.out.println(" 4. Enroll a student");
            System.out.println(" 5. Update enrollment status");
            System.out.println(" 6. Drop enrollment");
            System.out.println(" 7. Check prerequisites");
            System.out.println(" 8. Generate simple schedule");
            System.out.println(" 0. Back");
            int c = readInt("Choose: ");
            System.out.println();
            switch (c) {
                case 1  -> listAllEnrollments();
                case 2  -> listEnrollmentsByStudent();
                case 3  -> listEnrollmentsByCourse();
                case 4  -> enrollStudent();
                case 5  -> updateEnrollmentStatus();
                case 6  -> dropEnrollment();
                case 7  -> checkPrerequisites();
                case 8  -> generateSchedule();
                case 0  -> back = true;
                default -> System.out.println("Invalid option.\n");
            }
        }
    }

    private static void listAllEnrollments() {
        service.loadEnrollments();
        List<Enrollment> list = service.getEnrollments();
        if (list.isEmpty()) { System.out.println("No enrollments found.\n"); return; }
        printEnrollmentTable(list);
    }

    private static void listEnrollmentsByStudent() {
        String id = readLine("Enter student ID: ");
        List<Enrollment> list = new EnrollmentDao().getEnrollmentsByStudent(id);
        if (list.isEmpty()) { System.out.println("No enrollments found for that student.\n"); return; }
        printEnrollmentTable(list);
    }

    private static void listEnrollmentsByCourse() {
        String code = readLine("Enter course code: ");
        List<Enrollment> list = new EnrollmentDao().getEnrollmentsByCourse(code);
        if (list.isEmpty()) { System.out.println("No enrollments found for that course.\n"); return; }
        printEnrollmentTable(list);
    }

    private static void printEnrollmentTable(List<Enrollment> list) {
        System.out.printf("%-15s %-12s %-12s%n", "Student ID", "Course Code", "Status");
        System.out.println("-".repeat(42));
        for (Enrollment e : list) {
            System.out.printf("%-15s %-12s %-12s%n",
                    e.getStudentId(), e.getCourseCode(), e.getStatus());
        }
        System.out.println("Total: " + list.size() + "\n");
    }

    private static void enrollStudent() {
        System.out.println("--- Enroll Student ---");
        String sid  = readLine("Student ID  : ");
        String code = readLine("Course code : ");

        
        service.loadAll();
        boolean prereqOk = service.checkPrerequisitesNested(sid, code);
        if (!prereqOk) {
            System.out.println("WARNING: Prerequisites not met for this course.");
            System.out.print("Proceed anyway? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("Enrollment cancelled.\n");
                return;
            }
        }

        System.out.println("Status options: ENROLLED, PENDING, COMPLETED, DROPPED, FAILED");
        String status = readLine("Status [ENROLLED]: ");
        if (status.isBlank()) status = Enrollment.STATUS_ENROLLED;

        Enrollment e = new Enrollment(sid, code, status.toUpperCase());
        boolean ok = new EnrollmentDao().enrollStudent(e);
        System.out.println(ok ? "Enrollment recorded.\n" : "Failed — may already exist.\n");
        if (ok) service.loadEnrollments();
    }

    private static void updateEnrollmentStatus() {
        String sid  = readLine("Student ID  : ");
        String code = readLine("Course code : ");
        System.out.println("Status options: ENROLLED, PENDING, COMPLETED, DROPPED, FAILED");
        String status = readLine("New status  : ");
        boolean ok = new EnrollmentDao().updateStatus(sid, code, status.toUpperCase());
        System.out.println(ok ? "Status updated.\n" : "Update failed — enrollment not found.\n");
        if (ok) service.loadEnrollments();
    }

    private static void dropEnrollment() {
        String sid  = readLine("Student ID  : ");
        String code = readLine("Course code : ");
        System.out.print("Confirm drop enrollment? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Cancelled.\n"); return; }
        boolean ok = new EnrollmentDao().deleteEnrollment(sid, code);
        System.out.println(ok ? "Enrollment removed.\n" : "Failed — enrollment not found.\n");
        if (ok) service.loadEnrollments();
    }

    private static void checkPrerequisites() {
        String sid  = readLine("Student ID  : ");
        String code = readLine("Course code : ");
        service.loadAll();
        boolean ok = service.checkPrerequisitesNested(sid, code);
        System.out.println(ok
                ? "✓ Prerequisites met — student may enroll.\n"
                : "✗ Prerequisites NOT met — student cannot enroll.\n");
    }

    private static void generateSchedule() {
        String sid = readLine("Student ID: ");
        service.loadAll();
        List<Course> schedule = service.simpleSchedule(sid);
        if (schedule.isEmpty()) {
            System.out.println("No eligible courses found for this student.\n");
            return;
        }
        System.out.println("Suggested schedule for " + sid + ":");
        System.out.printf("%-10s %-30s %5s  %s%n", "Code", "Name", "Units", "Schedule");
        System.out.println("-".repeat(65));
        int totalUnits = 0;
        for (Course c : schedule) {
            System.out.printf("%-10s %-30s %5d  %s%n",
                    c.getCourseCode(), c.getCourseName(), c.getUnits(), c.getSchedule());
            totalUnits += c.getUnits();
        }
        System.out.println("Total units: " + totalUnits + "\n");
    }

    

    private static void runBenchmark() {
        System.out.println("\n==========================================");
        System.out.println("   PERFORMANCE BENCHMARK (BASELINE)");
        System.out.println("==========================================");

        
        Service benchService = new Service();
        benchService.loadAll();

        List<Student> students = benchService.getStudents();

        if (students.isEmpty()) {
            System.out.println("No students loaded. Check DB connection.");
            return;
        }

       
        String target = students.get(students.size() - 1).getStudentId();
        int size = students.size();

      
        int runs = 5;
        long total = 0;
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            benchService.findStudentLinear(target);
            total += System.nanoTime() - start;
        }
        long avgSearch = total / runs;

       
        String courseCode = benchService.getCourses().isEmpty()
                ? "" : benchService.getCourses().get(0).getCourseCode();
        total = 0;
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            benchService.checkPrerequisitesNested(target, courseCode);
            total += System.nanoTime() - start;
        }
        long avgPrereq = total / runs;

       
        total = 0;
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            benchService.simpleSchedule(target);
            total += System.nanoTime() - start;
        }
        long avgSchedule = total / runs;

        System.out.println("\nDataset: " + size + " students, "
                + benchService.getCourses().size() + " courses, "
                + benchService.getEnrollments().size() + " enrollments");
        System.out.println("Repetitions per test: " + runs);
        System.out.println();
        System.out.printf("  findStudentLinear()        O(n)   : %,d ns  (%.4f ms)%n",
                avgSearch, avgSearch / 1_000_000.0);
        System.out.printf("  checkPrerequisitesNested() O(p×n) : %,d ns  (%.4f ms)%n",
                avgPrereq, avgPrereq / 1_000_000.0);
        System.out.printf("  simpleSchedule()           O(n²)  : %,d ns  (%.4f ms)%n",
                avgSchedule, avgSchedule / 1_000_000.0);

        System.out.println("\n==========================================\n");
    }
   

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try { return Integer.parseInt(line); }
            catch (NumberFormatException e) { System.out.println("Please enter a number."); }
        }
    }

    private static long avg(long[] arr) {
        long sum = 0;
        for (long v : arr) sum += v;
        return sum / arr.length;
    }

    private static double toMs(long nanos) { return nanos / 1_000_000.0; }

    private static String nvl(String s) { return s != null ? s : ""; }
}