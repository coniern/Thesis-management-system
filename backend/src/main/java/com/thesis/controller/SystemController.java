package com.thesis.controller;

import com.thesis.entity.Notification;
import com.thesis.entity.Progress;
import com.thesis.entity.SystemSetting;
import com.thesis.entity.User;
import com.thesis.service.NotificationService;
import com.thesis.service.ProgressService;
import com.thesis.service.SystemSettingService;
import com.thesis.service.TeacherStudentSelectionService;
import com.thesis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemSettingService settingService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherStudentSelectionService selectionService;

    // 进度相关接口
    @PostMapping("/student/progress")
    public ResponseEntity<?> updateProgress(@RequestParam Long studentId, @RequestParam String stage, @RequestParam String status) {
        progressService.updateProgress(studentId, stage, status);
        return ResponseEntity.ok("Progress updated successfully");
    }

    @GetMapping("/student/progress")
    public ResponseEntity<List<Progress>> getStudentProgress(@RequestParam Long studentId) {
        List<Progress> progressList = progressService.getStudentProgress(studentId);
        return ResponseEntity.ok(progressList);
    }

    // 通知相关接口
    @PostMapping("/admin/notification/global")
    public ResponseEntity<?> createGlobalNotification(@RequestBody NotificationRequest request) {
        notificationService.createGlobalNotification(request.getTitle(), request.getContent(), request.getSenderId());
        return ResponseEntity.ok("Global notification created successfully");
    }

    @PostMapping("/admin/notification/user")
    public ResponseEntity<?> createUserNotification(@RequestBody UserNotificationRequest request) {
        notificationService.createNotification(request.getTitle(), request.getContent(), request.getSenderId(), "user");
        return ResponseEntity.ok("User notification created successfully");
    }

    @DeleteMapping("/admin/notification/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted successfully");
    }

    @PutMapping("/admin/notification/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody NotificationRequest request) {
        notificationService.updateNotification(id, request.getTitle(), request.getContent());
        return ResponseEntity.ok("Notification updated successfully");
    }

    @GetMapping("/user/notifications")
    public ResponseEntity<List<Notification>> getUserNotifications(@RequestParam Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/user/notification/read")
    public ResponseEntity<?> markNotificationAsRead(@RequestParam Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }

    @PostMapping("/user/notification/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    // 系统设置相关接口
    @GetMapping("/admin/settings")
    public ResponseEntity<List<SystemSetting>> getAllSettings() {
        List<SystemSetting> settings = settingService.getAllSettings();
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/admin/settings")
    public ResponseEntity<?> updateSetting(@RequestBody SettingRequest request) {
        settingService.updateSetting(request.getKey(), request.getValue(), request.getDescription());
        return ResponseEntity.ok("Setting updated successfully");
    }

    @GetMapping("/settings/{key}")
    public ResponseEntity<?> getSettingValue(@PathVariable String key) {
        String value = settingService.getSettingValue(key);
        return ResponseEntity.ok(value);
    }

    // 用户管理相关接口
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/admin/user")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        userService.update(user);
        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/admin/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/admin/user")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok("User added successfully");
    }

    // 老师管理相关接口
    @GetMapping("/teachers")
    public ResponseEntity<List<User>> getTeachers(@RequestParam(required = false) String department, @RequestParam(required = false) String major) {
        List<User> teachers;
        if (department != null || major != null) {
            teachers = userService.getTeachersByDepartmentAndMajor(department, major);
        } else {
            teachers = userService.getTeachers();
        }
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/teacher/students")
    public ResponseEntity<List<User>> getTeacherStudents(@RequestParam Long teacherId) {
        List<User> students = selectionService.getStudentsByTeacherId(teacherId);
        return ResponseEntity.ok(students);
    }
    
    // 获取单个用户信息
    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestParam Long userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    // 请求体类
    public static class NotificationRequest {
        private String title;
        private String content;
        private String type;
        private Long senderId;

        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
    }

    public static class UserNotificationRequest extends NotificationRequest {
        private Long receiverId;

        public Long getReceiverId() { return receiverId; }
        public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    }

    public static class SettingRequest {
        private String key;
        private String value;
        private String description;

        // getters and setters
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
