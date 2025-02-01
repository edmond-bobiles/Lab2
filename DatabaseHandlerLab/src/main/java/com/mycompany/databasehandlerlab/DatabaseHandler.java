package com.mycompany.databasehandlerlab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DatabaseHandler {
    private Connection conn;
    
    public DatabaseHandler(String database) {
        String connStr = "jdbc:sqlite:" + database;
        System.out.println(connStr);
        try {
            conn = DriverManager.getConnection(connStr);
        } catch (SQLException e) {
            System.err.println("Failed to create connection");
            System.err.println(e.toString());
        }
    }
    
    public void initializeStudents() {
        String sql = "DROP TABLE IF EXISTS students; " + 
                     "CREATE TABLE students(\n" +
                     "    student_id TEXT PRIMARY KEY CHECK (\n" +
                     "        student_id GLOB '[0-9][0-9][0-9][0-9]010[0-9][0-9][0-9][0-9]'\n" +
                     "    ),\n" +
                     "    student_fname TEXT NOT NULL,\n" +
                     "    student_mname TEXT NOT NULL,\n" +
                     "    student_lname TEXT NOT NULL,\n" +
                     "    student_sex TEXT CHECK(student_sex IN ('M','F')),\n" +
                     "    student_birth TEXT CHECK (\n" +
                     "        student_birth GLOB '[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]'\n" +
                     "    ),\n" +
                     "    student_start INTEGER CHECK (\n" +
                     "        student_start GLOB '[0-9][0-9][0-9][0-9]'\n" +
                     "    ),\n" +
                     "    student_department TEXT,\n" +
                     "    student_units INTEGER,\n" +
                     "    student_address TEXT\n" +
                     ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("students table created successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to create students table: " + e.getMessage());
        }
    }
    
    public Student getStudentFromNumber(String studentNumber) throws SQLException {
        String sql = "SELECT * FROM students s WHERE s.student_id = ?"; 
        Student student = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                student = new Student(
                        rs.getString("student_id"),
                        rs.getString("student_fname"),
                        rs.getString("student_mname"),
                        rs.getString("student_lname"),
                        rs.getString("student_sex"),
                        rs.getString("student_birth"),
                        rs.getInt("student_start"),
                        rs.getString("student_department"),
                        rs.getInt("student_units"),
                        rs.getString("student_address")
                );
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve student: " + e.getMessage());
        }

        return student;
    }
    
    public Student getStudentFromName(String studentFname, String studentMname, String studentLname) {
        String sql = "SELECT * FROM students s WHERE s.student_fname = ? " +
                     "AND s.student_mname = ? " +
                     "AND s.student_lname = ?";
        
        Student student = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentFname);
            pstmt.setString(2, studentMname);
            pstmt.setString(3, studentLname);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                student = new Student(
                        rs.getString("student_id"),
                        rs.getString("student_fname"),
                        rs.getString("student_mname"),
                        rs.getString("student_lname"),
                        rs.getString("student_sex"),
                        rs.getString("student_birth"),
                        rs.getInt("student_start"),
                        rs.getString("student_department"),
                        rs.getInt("student_units"),
                        rs.getString("student_address")
                );
            }

        } catch (SQLException e) {
            System.err.println("Failed to retrieve student: " + e.getMessage());
        }

        return student;
    }
    
    public ArrayList<Student> getStudents() throws SQLException {
        String sql = "SELECT * FROM students";
        ArrayList<Student> students = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {  // Use while instead of if
                Student student = new Student(
                    rs.getString("student_id"),
                    rs.getString("student_fname"),
                    rs.getString("student_mname"),
                    rs.getString("student_lname"),
                    rs.getString("student_sex"),
                    rs.getString("student_birth"),
                    rs.getInt("student_start"),
                    rs.getString("student_department"),
                    rs.getInt("student_units"),
                    rs.getString("student_address")
                );
                students.add(student);
            }
        }
        return students;
    }   

    
    public boolean removeStudent(String studentNumber) {
        String sql = "DELETE FROM students WHERE student_id LIKE ?";
        
        Student student = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentNumber);

            int rowsDeleted = pstmt.executeUpdate();
            
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("Failed to delete student: " + e.getMessage());
            return false;
        }
    }
    
    public boolean getStudentsByYear(int year) {
    String sql = "SELECT * FROM students WHERE student_id LIKE ?";
    boolean found = false;
    ArrayList<Student> students = new ArrayList<>();

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        // Ensure the query matches student IDs starting with the year
        pstmt.setString(1, year + "%");

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            students.add(new Student(
                    rs.getString("student_id"),
                    rs.getString("student_fname"),
                    rs.getString("student_mname"),
                    rs.getString("student_lname"),
                    rs.getString("student_sex"),
                    rs.getString("student_birth"),
                    rs.getInt("student_start"),
                    rs.getString("student_department"),
                    rs.getInt("student_units"),
                    rs.getString("student_address")
            ));
        }

        if (!students.isEmpty()) {
            found = true;
        }

    } catch (SQLException e) {
        System.err.println("Failed to retrieve student: " + e.getMessage());
    }

    if (found) {
        for (Student student : students) {
            System.out.println(student.toString());
        }
    } else {
        System.out.println("No student found for year " + year);
    }

    return found;
}


   
    public boolean updateStudentInfo(String studentID, Student studentInfo){
        String sql = "UPDATE students " + 
                    "SET student_fname = ?" + 
                        ", student_mname = ?" + 
                        ", student_lname = ?" + 
                        ", student_department = ?" + 
                        ", student_address = ?" +
                    "WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentInfo.studentFName);
            pstmt.setString(2, studentInfo.studentMName);
            pstmt.setString(3, studentInfo.studentLName);
            pstmt.setString(4, studentInfo.studentDepartment);
            pstmt.setString(5, studentInfo.studentAddress);
            pstmt.setString(6, studentID);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.err.println("Failed to update student information: " + e.getMessage());
            return false;
        }
   }
   
   public boolean updateStudentUnits(String studentNumber, int subtractedUnits) {
        String sql = "UPDATE students SET student_units = student_units - ? WHERE student_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, subtractedUnits); 
            pstmt.setString(2, studentNumber); 

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update student units: " + e.getMessage());
            return false;
        }
    }
   
   public boolean insertStudent(Student newStudent) {
        String sql = "INSERT INTO students (" +
                     "student_id, student_fname, student_mname, student_lname, " +
                     "student_sex, student_birth, student_start, student_department, " +
                     "student_units, student_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStudent.studentId);
            pstmt.setString(2, newStudent.studentFName);
            pstmt.setString(3, newStudent.studentMName);
            pstmt.setString(4, newStudent.studentLName);
            pstmt.setString(5, newStudent.studentSex);
            pstmt.setString(6, newStudent.studentBirth);
            pstmt.setInt(7, newStudent.studentStart);
            pstmt.setString(8, newStudent.studentDepartment);
            pstmt.setInt(9, newStudent.studentUnits);
            pstmt.setString(10, newStudent.studentAddress);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0; 
        } catch (SQLException e) {
            System.err.println("Failed to Insert: " + e.getMessage());
            return false;
        }
    }


    
    public static void main(String[] args) {
        String directPathToDb = "C:\\Users\\edjbo\\ics2609\\Lab2\\DatabaseHandlerLab\\students";
        DatabaseHandler dh = new DatabaseHandler(directPathToDb);
        dh.initializeStudents();

        // Create singular student entry
        Student newStudent = new Student(
           "20220102022","Rin","Genius","Itoshi","M","2010-09-09", 2023, "CICS",21,"Quirino Bulacan City"   
        );

        // Insert singular student entry
        boolean isInsertedSingle = dh.insertStudent(newStudent);
        System.out.println("Insert Successful: " + isInsertedSingle);
        
        // Create multiple student entries
        Student[] newStudents = {
            new Student("20230102023", "Michael", "Impact", "Kaiser", "M", "2006-12-25", 2023, "CICS", 19, "Espana Manila City"),
            new Student("20240102024", "Yoichi", "Metavision", "Isagi", "M", "2008-04-01", 2024, "Faculty of Engineering", 17, "Palmera Caloocan City"),
            new Student("20250102025", "Shouei", "Donkey", "Barou", "M", "2007-06-27", 2025, "CS", 18, "Batasan Quezon City")
        };

        // Insert multiple student entries
        for (Student student : newStudents) {
            boolean isInsertedMultiple = dh.insertStudent(student);
            System.out.println("Insert Successful: " + isInsertedMultiple);
        }

        // Display all students
        try {
            ArrayList<Student> students = dh.getStudents();
            for (Student student : students) {
                System.out.println(student.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Create updated student entry
        Student updatedStudent = new Student(
            "20220102022","Rin","Flow","Itoshi","M","2010-09-09", 2023, "CICS",21,"Quirino Bulacan City"   
        );

        // Update student info
        boolean isUpdatedStudentInfo = dh.updateStudentInfo("20220102022", updatedStudent);
        System.out.println("Update Student Info Successful: " + isUpdatedStudentInfo);

        // Update student units
        boolean isUpdatedStudentUnits = dh.updateStudentUnits("20240102024",5);
        System.out.println("Update Student Units Successful: " + isUpdatedStudentUnits);
        
        // Remove student entry
        boolean isRemovedStudent = dh.removeStudent("20250102025");
        System.out.println("Remove Student Successful: " + isRemovedStudent);
    
        // Get student entry by year
        boolean isGetStudentsByYear = dh.getStudentsByYear(2023);
    }

}
