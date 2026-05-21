package application;

import java.util.ArrayList;
import java.util.List;

public class Service {

    private final List<Student>    students;
    private final List<Course>     courses;
    private final List<Enrollment> enrollments;

    private final StudentDao    studentDao;
    private final CourseDao     courseDao;
    private final EnrollmentDao enrollmentDao;

    public Service() {
        this.students    = new ArrayList<>();
        this.courses     = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.studentDao    = new StudentDao();
        this.courseDao     = new CourseDao();
        this.enrollmentDao = new EnrollmentDao();
    }



    public void loadStudents() {
        students.clear();
        students.addAll(studentDao.getAllStudents());
        System.out.println("[Service] Loaded " + students.size() + " students into ArrayList.");
    }

    public void loadCourses() {
        courses.clear();
        courses.addAll(courseDao.getAllCourses());
        System.out.println("[Service] Loaded " + courses.size() + " courses into ArrayList.");
    }

    public void loadEnrollments() {
        enrollments.clear();
        enrollments.addAll(enrollmentDao.getAllEnrollments());
        System.out.println("[Service] Loaded " + enrollments.size() + " enrollments into ArrayList.");
    }

    public void loadAll() {
        loadStudents();
        loadCourses();
        loadEnrollments();
    }

  

    public Student findStudentLinear(String studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(studentId)) {
                return students.get(i);
            }
        }
        return null;
    }



    public boolean checkPrerequisitesNested(String studentId, String courseCode) {

        Course target = null;
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseCode().equals(courseCode)) {
                target = courses.get(i);
                break;
            }
        }

        if (target == null) return false;
        if (target.getPrerequisites().isEmpty()) return true;

        for (int p = 0; p < target.getPrerequisites().size(); p++) {
            String  prereqCode = target.getPrerequisites().get(p);
            boolean completed  = false;

            for (int e = 0; e < enrollments.size(); e++) {
                Enrollment enrollment = enrollments.get(e);
                if (enrollment.getStudentId().equals(studentId)
                        && enrollment.getCourseCode().equals(prereqCode)
                        && enrollment.isCompleted()) {
                    completed = true;
                    break;
                }
            }

            if (!completed) return false;
        }

        return true;
    }

  

    public List<Course> simpleSchedule(String studentId) {
        List<Course> scheduled = new ArrayList<>();

        for (int i = 0; i < courses.size(); i++) {
            Course candidate = courses.get(i);

            if (isAlreadyTaken(studentId, candidate.getCourseCode())) continue;
            if (!checkPrerequisitesNested(studentId, candidate.getCourseCode())) continue;
            if (candidate.getStartTime() == null || candidate.getEndTime() == null) continue;

            boolean conflicts = false;
            for (int j = 0; j < scheduled.size(); j++) {
                if (hasConflict(candidate, scheduled.get(j))) {
                    conflicts = true;
                    break;
                }
            }

            if (!conflicts) scheduled.add(candidate);
        }

        System.out.println("[Service] Simple schedule for " + studentId
                + ": " + scheduled.size() + " courses.");
        return scheduled;
    }



    private boolean isAlreadyTaken(String studentId, String courseCode) {
        for (int i = 0; i < enrollments.size(); i++) {
            Enrollment e = enrollments.get(i);
            if (e.getStudentId().equals(studentId) && e.getCourseCode().equals(courseCode))
                return true;
        }
        return false;
    }

    private boolean hasConflict(Course a, Course b) {
        if (a.getDay() == null || b.getDay() == null) return false;
        if (!a.getDay().equals(b.getDay())) return false;

        String startA = a.getStartTime(), endA = a.getEndTime();
        String startB = b.getStartTime(), endB = b.getEndTime();

        if (startA == null || endA == null || startB == null || endB == null) return false;
        return startA.compareTo(endB) < 0 && startB.compareTo(endA) < 0;
    }



    public List<Student>    getStudents()    { return students; }
    public List<Course>     getCourses()     { return courses; }
    public List<Enrollment> getEnrollments() { return enrollments; }
}
