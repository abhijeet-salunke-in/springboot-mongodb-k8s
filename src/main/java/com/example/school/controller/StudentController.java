package com.example.school.controller;

import com.example.school.model.Student;
import com.example.school.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // GET /students
    @GetMapping
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    // GET /students/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable String id) {

        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /students
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {

        Student savedStudent = studentRepository.save(student);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedStudent);
    }

    // PUT /students/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable String id,
            @RequestBody Student student) {

        return studentRepository.findById(id)
                .map(existingStudent -> {

                    existingStudent.setName(student.getName());
                    existingStudent.setEmail(student.getEmail());
                    existingStudent.setAge(student.getAge());
                    existingStudent.setCourse(student.getCourse());

                    Student updatedStudent =
                            studentRepository.save(existingStudent);

                    return ResponseEntity.ok(updatedStudent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /students/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {

        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        studentRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
