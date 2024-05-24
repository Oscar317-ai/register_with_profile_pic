package com.example.Register.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepo studentRepo;

    @Autowired
    public StudentService(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    public Student saveStudentData(Student student) {
        return studentRepo.save(student);
    }

    public Student findStudentById(Long studentId) {
        Optional<Student> studentOptional = studentRepo.findById(studentId);
        if (studentOptional.isPresent()) {
            return studentOptional.get();
        } else {
            throw new StudentNotFoundException("Student with ID " + studentId + " not found");
        }
    }

    public List<Student> findAllStudents() {
        return studentRepo.findAll();
    }

    public void deleteStudentById(Long studentId) {
        studentRepo.deleteById(studentId);
    }
}
