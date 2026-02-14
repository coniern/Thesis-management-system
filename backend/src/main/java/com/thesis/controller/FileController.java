package com.thesis.controller;

import com.thesis.entity.File;
import com.thesis.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam Long uploaderId,
                                        @RequestParam String uploaderRole,
                                        @RequestParam Long relatedId,
                                        @RequestParam String fileType) {
        System.out.println("=== File upload request received ===");
        try {
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Uploader ID: " + uploaderId);
            System.out.println("Uploader role: " + uploaderRole);
            System.out.println("Related ID: " + relatedId);
            System.out.println("File type: " + fileType);
            
            // 检查文件是否为空
            if (file.isEmpty()) {
                System.out.println("Error: File is empty");
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            // 检查参数是否有效
            if (uploaderId == null || uploaderRole == null || relatedId == null || fileType == null) {
                System.out.println("Error: Missing required parameters");
                return ResponseEntity.badRequest().body("Missing required parameters");
            }
            
            System.out.println("All parameters are valid, calling fileService.uploadFile");
            File uploadedFile = fileService.uploadFile(file, uploaderId, uploaderRole, relatedId, fileType);
            System.out.println("File upload successful: " + uploadedFile.getFileName());
            System.out.println("File ID: " + uploadedFile.getFileId());
            return ResponseEntity.ok(uploadedFile);
        } catch (Exception e) {
            System.err.println("=== Error in uploadFile ===");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId) {
        System.out.println("=== Download file request received ===");
        System.out.println("File ID: " + fileId);
        
        try {
            // 获取文件信息
            File file = fileService.getFileById(fileId);
            System.out.println("File found: " + (file != null));
            
            if (file == null) {
                System.out.println("File not found");
                return ResponseEntity.notFound().build();
            }

            // 检查文件路径
            String filePath = file.getFilePath();
            System.out.println("File path: " + filePath);
            
            if (filePath == null || filePath.isEmpty()) {
                System.out.println("File path is empty");
                return ResponseEntity.badRequest().body("File path is empty");
            }
            
            // 检查文件是否存在
            java.io.File fileObj = new java.io.File(filePath);
            if (!fileObj.exists()) {
                System.out.println("File does not exist on disk: " + filePath);
                return ResponseEntity.notFound().build();
            }

            // 读取文件内容
            byte[] fileContent = Files.readAllBytes(fileObj.toPath());
            System.out.println("File content read successfully, size: " + fileContent.length);

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            String fileName = file.getFileName();
            
            // 设置Content-Disposition响应头
            try {
                String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);
            } catch (Exception e) {
                System.err.println("Error encoding file name: " + e.getMessage());
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.bin");
            }
            
            // 设置Content-Type响应头
            String contentType = getContentType(fileName);
            headers.set(HttpHeaders.CONTENT_TYPE, contentType);
            
            // 设置Content-Length响应头
            headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileContent.length));

            System.out.println("File download successful");
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("=== Error in downloadFile ===");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File download failed: " + e.getMessage());
        }
    }
    
    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentType(String fileName) {
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            case "7z":
                return "application/x-7z-compressed";
            default:
                return "application/octet-stream";
        }
    }

    @GetMapping("/files/user")
    public ResponseEntity<List<File>> getUserFiles(@RequestParam Long userId,
                                                  @RequestParam String userRole) {
        List<File> files = fileService.getFilesByUserId(userId, userRole);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/related")
    public ResponseEntity<List<File>> getRelatedFiles(@RequestParam Long relatedId,
                                                     @RequestParam String fileType) {
        List<File> files = fileService.getFilesByRelatedId(relatedId, fileType);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.ok("File deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File deletion failed: " + e.getMessage());
        }
    }

    @GetMapping("/student/files")
    public ResponseEntity<List<File>> getStudentFiles(@RequestParam Long studentId) {
        List<File> files = fileService.getFilesForStudent(studentId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/teacher/files")
    public ResponseEntity<List<File>> getTeacherFiles(@RequestParam Long teacherId) {
        List<File> files = fileService.getFilesForTeacher(teacherId);
        return ResponseEntity.ok(files);
    }

    @PutMapping("/files/{fileId}/sync")
    public ResponseEntity<?> syncFile(@PathVariable Long fileId) {
        System.out.println("=== Sync file request received ===");
        System.out.println("File ID: " + fileId);
        
        try {
            File updatedFile = fileService.updateSyncStatus(fileId, "synced");
            if (updatedFile == null) {
                System.out.println("File not found");
                return ResponseEntity.notFound().build();
            }
            System.out.println("File sync status updated to 'synced'");
            return ResponseEntity.ok(updatedFile);
        } catch (Exception e) {
            System.err.println("=== Error in syncFile ===");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File sync failed: " + e.getMessage());
        }
    }
}
