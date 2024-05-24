package com.example.Register;

import com.example.Register.student.Student;
import com.example.Register.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class StudentController {

    public static String uploadDirectory = System.getProperty("user.dir")+ "/src/main/webapp/images";

    @Autowired
    private StudentService studentService;

    @GetMapping("/register/student")
    public String register(){
        return "register";
    }

    @GetMapping("/dashboard")
    public String homePage(@RequestParam("id") Long studentId, Model model) {
        Student student = studentService.findStudentById(studentId);
        model.addAttribute("student", student);
        return "student";
    }

    @PostMapping("/save/student")
    public String saveStudent(@ModelAttribute Student student,
                              @RequestParam("image") MultipartFile file, Model model) throws IOException {
        // Save student data first to get the generated ID
        Student savedStudent = studentService.saveStudentData(student);
        Long studentId = savedStudent.getId();

        // Create a subfolder named with the student's ID
        String studentFolder = uploadDirectory + "/" + studentId;
        File directory = new File(studentFolder);
        //i first deleted the previous existing student's imsge folder
        if (directory.exists()) {
            deleteDirectory(directory);
        }
        // i then created a new folder
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique numeric name for the file
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileExtension;
        Path fileNameAndPath = Paths.get(studentFolder, uniqueFileName);
        Files.write(fileNameAndPath, file.getBytes());

        // Update student record with the relative path of the profile image
        savedStudent.setProfileImage(studentId + "/" + uniqueFileName);
        studentService.saveStudentData(savedStudent);

        model.addAttribute("student", savedStudent);
        return "redirect:/dashboard?id=" + savedStudent.getId();
    }

    // Utility method to get file extension
    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return ""; // empty extension
        }
        return fileName.substring(lastIndexOfDot);
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        List<Student> students = studentService.findAllStudents();
        model.addAttribute("students", students);
        return "list";
    }

    @PostMapping("/delete/student")
    public String deleteStudent(@RequestParam("id") Long studentId) {
        // Get the student information
        Student student = studentService.findStudentById(studentId);

        // Delete the image folder associated with the student
        String studentFolder = uploadDirectory + "/" + studentId;
        File directory = new File(studentFolder);
        if (directory.exists()) {
            deleteDirectory(directory);
        }

        // Delete the student record
        studentService.deleteStudentById(studentId);

        return "redirect:/students";
    }

    // Utility method to recursively delete a directory and its contents
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

}
