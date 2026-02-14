package com.thesis.controller;

import com.thesis.dto.SelectionWithStudentInfo;
import com.thesis.entity.TeacherStudentSelection;
import com.thesis.service.TeacherStudentSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class TeacherStudentSelectionController {

    @Autowired
    private TeacherStudentSelectionService selectionService;

    @PostMapping("/student/select-teacher")
    public ResponseEntity<?> studentSelectTeacher(@RequestBody SelectionRequest request) {
        try {
            selectionService.studentSelectTeacher(request.getStudentId(), request.getTeacherId());
            return ResponseEntity.ok("Selection submitted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/teacher/selections")
    public ResponseEntity<List<SelectionWithStudentInfo>> getTeacherSelections(@RequestParam Long teacherId) {
        List<SelectionWithStudentInfo> selections = selectionService.getSelectionsWithStudentInfoByTeacherId(teacherId);
        return ResponseEntity.ok(selections);
    }

    @PostMapping("/teacher/accept-selection")
    public ResponseEntity<?> teacherAcceptSelection(@RequestParam Long selectionId) {
        selectionService.teacherAcceptSelection(selectionId);
        return ResponseEntity.ok("Selection accepted successfully");
    }

    @PostMapping("/teacher/reject-selection")
    public ResponseEntity<?> teacherRejectSelection(@RequestParam Long selectionId) {
        selectionService.teacherRejectSelection(selectionId);
        return ResponseEntity.ok("Selection rejected successfully");
    }

    @GetMapping("/admin/pending-selections")
    public ResponseEntity<List<TeacherStudentSelection>> getPendingSelections() {
        List<TeacherStudentSelection> selections = selectionService.getPendingSelections();
        return ResponseEntity.ok(selections);
    }

    @PostMapping("/admin/approve-selection")
    public ResponseEntity<?> adminApproveSelection(@RequestParam Long selectionId) {
        selectionService.adminApproveSelection(selectionId);
        return ResponseEntity.ok("Selection approved successfully");
    }

    @GetMapping("/student/selections")
    public ResponseEntity<List<SelectionWithStudentInfo>> getStudentSelections(@RequestParam Long studentId) {
        List<SelectionWithStudentInfo> selections = selectionService.getSelectionsWithTeacherInfoByStudentId(studentId);
        return ResponseEntity.ok(selections);
    }

    @PostMapping("/student/unbind-request")
    public ResponseEntity<?> studentRequestUnbind(@RequestBody UnbindRequest request) {
        selectionService.studentRequestUnbind(request.getSelectionId());
        return ResponseEntity.ok("Unbind request submitted successfully");
    }

    @PostMapping("/teacher/approve-unbind")
    public ResponseEntity<?> teacherApproveUnbind(@RequestParam Long selectionId) {
        selectionService.teacherApproveUnbind(selectionId);
        return ResponseEntity.ok("Unbind request approved successfully");
    }

    public static class SelectionRequest {
        private Long studentId;
        private Long teacherId;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }
    }

    public static class UnbindRequest {
        private Long selectionId;

        public Long getSelectionId() {
            return selectionId;
        }

        public void setSelectionId(Long selectionId) {
            this.selectionId = selectionId;
        }
    }
}