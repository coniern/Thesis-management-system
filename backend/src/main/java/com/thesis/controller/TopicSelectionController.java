package com.thesis.controller;

import com.thesis.entity.TopicSelection;
import com.thesis.service.TopicSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic")
public class TopicSelectionController {

    @Autowired
    private TopicSelectionService topicSelectionService;

    @PostMapping("/student/submit")
    public ResponseEntity<?> submitTopic(@RequestBody TopicSelection topicSelection) {
        try {
            topicSelectionService.submitTopic(topicSelection);
            return ResponseEntity.ok("自拟题目提交成功，等待老师审核");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/teacher/pending")
    public ResponseEntity<List<TopicSelection>> getPendingTopics(@RequestParam Long teacherId) {
        List<TopicSelection> topics = topicSelectionService.getPendingTopicsByTeacherId(teacherId);
        return ResponseEntity.ok(topics);
    }

    @PostMapping("/teacher/approve")
    public ResponseEntity<?> approveTopic(@RequestParam Long topicId) {
        topicSelectionService.approveTopic(topicId);
        return ResponseEntity.ok("审核通过");
    }

    @PostMapping("/teacher/reject")
    public ResponseEntity<?> rejectTopic(@RequestParam Long topicId, @RequestParam String comment) {
        topicSelectionService.rejectTopic(topicId, comment);
        return ResponseEntity.ok("审核不通过");
    }

    @PostMapping("/teacher/upload-task")
    public ResponseEntity<?> uploadTask(@RequestParam Long topicId, @RequestParam Long taskFileId) {
        topicSelectionService.uploadTask(topicId, taskFileId);
        return ResponseEntity.ok("任务书上传成功");
    }

    @GetMapping("/student/status")
    public ResponseEntity<?> getTopicStatus(@RequestParam Long studentId) {
        try {
            // 获取学生的选题状态
            TopicSelection topicSelection = topicSelectionService.getTopicStatus(studentId);
            
            // 检查学生是否绑定了老师
            List<Long> boundTeachers = topicSelectionService.getBoundTeachersByStudentId(studentId);
            boolean isBoundToTeacher = !boundTeachers.isEmpty();
            
            // 构建返回数据
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("topicStatus", topicSelection);
            result.put("isBoundToTeacher", isBoundToTeacher);
            result.put("boundTeachers", boundTeachers);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error in getTopicStatus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/student/task")
    public ResponseEntity<Long> getTaskFile(@RequestParam Long topicId) {
        Long taskFileId = topicSelectionService.getTaskFileId(topicId);
        return ResponseEntity.ok(taskFileId);
    }

    @GetMapping("/teacher/detail")
    public ResponseEntity<TopicSelection> getTopicDetail(@RequestParam Long topicId) {
        TopicSelection topicSelection = topicSelectionService.getTopicById(topicId);
        return ResponseEntity.ok(topicSelection);
    }
}
