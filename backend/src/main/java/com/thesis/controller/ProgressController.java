package com.thesis.controller;

import com.thesis.service.ProgressCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private ProgressCheckService progressCheckService;

    @GetMapping("/student/status")
    public ResponseEntity<Map<String, Object>> getStudentProgress(@RequestParam Long studentId) {
        Map<String, Object> progress = new HashMap<>();
        progress.put("isBoundToTeacher", progressCheckService.isStudentBoundToTeacher(studentId));
        progress.put("isTopicApproved", progressCheckService.isTopicApproved(studentId));
        progress.put("hasTaskFile", progressCheckService.hasTaskFile(studentId));
        progress.put("isProposalApproved", progressCheckService.isProposalApproved(studentId));
        progress.put("isMidtermApproved", progressCheckService.isMidtermApproved(studentId));
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/student/stats")
    public ResponseEntity<Map<String, Object>> getStudentStats(@RequestParam Long studentId) {
        Map<String, Object> stats = progressCheckService.getStudentStats(studentId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/teacher/stats")
    public ResponseEntity<Map<String, Object>> getTeacherStats(@RequestParam Long teacherId) {
        Map<String, Object> stats = progressCheckService.getTeacherStats(teacherId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/student/detail")
    public ResponseEntity<Map<String, Object>> getStudentProgressDetail(@RequestParam Long studentId) {
        Map<String, Object> detail = progressCheckService.getStudentProgressDetail(studentId);
        return ResponseEntity.ok(detail);
    }
}
