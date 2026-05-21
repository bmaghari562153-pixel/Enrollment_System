package application;

import java.util.List;


 
public class Datasyncservice {

    private final StudentDao    studentDao;
    private final CourseDao     courseDao;
    private final Student_BST   studentBST;
    private final Course_Graph  courseGraph;

    public Datasyncservice(Student_BST studentBST, Course_Graph courseGraph) {
        this.studentDao  = new StudentDao();
        this.courseDao   = new CourseDao();
        this.studentBST  = studentBST;
        this.courseGraph = courseGraph;
    }

    public void loadStudentsToBST() {
        studentBST.clear();

        List<Student> all = studentDao.getAllStudents();
        for (Student s : all) {
            studentBST.insert(s);
        }

        System.out.println("[DataSyncService] Loaded " + all.size() + " students into BST.");
    }

    
    public void loadCoursesToGraph() {
        courseGraph.clear();

        List<Course> all = courseDao.getAllCourses();
        for (Course c : all) {
            courseGraph.addCourse(c.getCourseCode());
            for (String prereq : c.getPrerequisites()) {
                courseGraph.addPrerequisite(c.getCourseCode(), prereq);
            }
        }

        System.out.println("[DataSyncService] Loaded " + all.size() + " courses into graph.");
    }

   
    public void loadAll() {
        loadStudentsToBST();
        loadCoursesToGraph();
        System.out.println("[DataSyncService] All data loaded.");
    }

    public boolean addStudent(Student s) {
        boolean saved = studentDao.insertStudent(s);
        if (saved) {
            studentBST.insert(s);
            System.out.println("[DataSyncService] Added student: " + s.getStudentId());
        } else {
            System.err.println("[DataSyncService] Failed to add student: " + s.getStudentId());
        }
        return saved;
    }

   
    public boolean updateStudent(Student s) {
        boolean updated = studentDao.updateStudent(s);
        if (updated) {
            studentBST.insert(s); 
            System.out.println("[DataSyncService] Updated student: " + s.getStudentId());
        } else {
            System.err.println("[DataSyncService] Failed to update student: " + s.getStudentId());
        }
        return updated;
    }

   
    public boolean removeStudent(String studentId) {
        boolean deleted = studentDao.deleteStudent(studentId);
        if (deleted) {
            studentBST.delete(studentId);
            System.out.println("[DataSyncService] Removed student: " + studentId);
        } else {
            System.err.println("[DataSyncService] Failed to remove student: " + studentId);
        }
        return deleted;
    }

    public boolean addCourse(Course c) {
        boolean saved = courseDao.insertCourse(c);
        if (saved) {
            courseGraph.addCourse(c.getCourseCode());
            for (String prereq : c.getPrerequisites()) {
                courseGraph.addPrerequisite(c.getCourseCode(), prereq);
            }
            System.out.println("[DataSyncService] Added course: " + c.getCourseCode());
        } else {
            System.err.println("[DataSyncService] Failed to add course: " + c.getCourseCode());
        }
        return saved;
    }

   
    public boolean updateCourse(Course c) {
        boolean updated = courseDao.updateCourse(c);
        if (updated) {
           
            loadCoursesToGraph();
            System.out.println("[DataSyncService] Updated course: " + c.getCourseCode());
        } else {
            System.err.println("[DataSyncService] Failed to update course: " + c.getCourseCode());
        }
        return updated;
    }

    
    public boolean removeCourse(String courseCode) {
        boolean deleted = courseDao.deleteCourse(courseCode);
        if (deleted) {
            courseGraph.removeCourse(courseCode);
            System.out.println("[DataSyncService] Removed course: " + courseCode);
        } else {
            System.err.println("[DataSyncService] Failed to remove course: " + courseCode);
        }
        return deleted;
    }

   
    public boolean addPrerequisite(String courseCode, String prereqCode) {
        boolean saved = courseDao.insertPrerequisite(courseCode, prereqCode);
        if (saved) {
            courseGraph.addPrerequisite(courseCode, prereqCode);
            System.out.println("[DataSyncService] Added prerequisite: "
                    + courseCode + " → " + prereqCode);
        }
        return saved;
    }

   
    public boolean removePrerequisite(String courseCode, String prereqCode) {
        boolean deleted = courseDao.deletePrerequisite(courseCode, prereqCode);
        if (deleted) {
            courseGraph.removePrerequisite(courseCode, prereqCode);
            System.out.println("[DataSyncService] Removed prerequisite: "
                    + courseCode + " → " + prereqCode);
        }
        return deleted;
    }

   
    public Student_BST  getStudentBST()  { return studentBST; }
    public Course_Graph getCourseGraph() { return courseGraph; }
}