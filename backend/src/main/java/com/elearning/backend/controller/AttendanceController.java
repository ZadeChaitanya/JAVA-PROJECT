package com.elearning.backend.controller;
import com.elearning.backend.model.Attendance;
import  com.elearning.backend.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:3000") // allow frontend later
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @PostMapping
    public Attendance markAttendance(@RequestBody Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @GetMapping("/student/{id}")
    public List<Attendance> getByStudent(@PathVariable Long id) {
        return attendanceRepository.findByStudentId(id);
    }

    @GetMapping("/teacher/{id}")
    public List<Attendance> getByTeacher(@PathVariable Long id) {
        return attendanceRepository.findByTeacherId(id);
    }
    @GetMapping("/date/{date}")
    public List<Attendance> getAttendanceByDate(@PathVariable LocalDate date) {

        // Calls the new method in the repository
        return attendanceRepository.findByDate(date);
    }
}
