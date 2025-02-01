package com.mycompany.databasehandlerlab;
public class Student {
        String studentId;
        String studentFName;
        String studentMName;
        String studentLName;
        String studentSex;
        String studentBirth;
        int studentStart;
        String studentDepartment;
        int studentUnits;
        String studentAddress;
    
        public Student(String studentId, String studentFName, String studentMName, String studentLName,
                       String studentSex, String studentBirth, int studentStart, String studentDepartment,
                       int studentUnits, String studentAddress) {
            this.studentId = studentId;
            this.studentFName = studentFName;
            this.studentMName = studentMName;
            this.studentLName = studentLName;
            this.studentSex = studentSex;
            this.studentBirth = studentBirth;
            this.studentStart = studentStart;
            this.studentDepartment = studentDepartment;
            this.studentUnits = studentUnits;
            this.studentAddress = studentAddress;
        }
    
        @Override
        public String toString() {
            return studentId + ", " + studentFName + ", " + studentMName + ", " + studentLName + ", " 
                   + studentSex + ", " + studentBirth + ", " + studentStart + ", " + studentDepartment 
                   + ", " + studentUnits + ", " + studentAddress;
        }

    }
    