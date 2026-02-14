package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thesis.entity.File;
import com.thesis.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private FileMapper fileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public File uploadFile(MultipartFile file, Long uploaderId, String uploaderRole, Long relatedId, String fileType) throws IOException {
        // 确保上传目录存在
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 检查是否已有同类型、同relatedId的文件
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getRelatedId, relatedId)
                .eq(File::getFileType, fileType)
                .orderByDesc(File::getVersion)
                .last("LIMIT 1");
        File existingFile = fileMapper.selectOne(wrapper);
        
        int newVersion = 1;
        if (existingFile != null) {
            newVersion = existingFile.getVersion() + 1;
        }

        // 生成文件名
        String fileName = System.currentTimeMillis() + "_v" + newVersion + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // 保存文件到本地
        Files.copy(file.getInputStream(), filePath);

        // 保存文件信息到数据库
        File fileEntity = new File();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFilePath(filePath.toString());
        fileEntity.setUploaderId(uploaderId);
        fileEntity.setReceiverId(uploaderId);
        fileEntity.setRelatedId(relatedId);
        fileEntity.setFileType(fileType);
        fileEntity.setVersion(newVersion);
        fileEntity.setSyncStatus("pending");
        fileEntity.setUploadTime(new Date());
        fileEntity.setCreatedAt(new Date());
        fileEntity.setUpdatedAt(new Date());

        try {
            fileMapper.insert(fileEntity);
            return fileEntity;
        } catch (Exception e) {
            // 如果数据库插入失败，删除已上传的文件
            Files.deleteIfExists(filePath);
            throw e;
        }
    }

    public File getFileById(Long fileId) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getFileId, fileId);
        return fileMapper.selectOne(wrapper);
    }

    public List<File> getFilesByUserId(Long userId, String userRole) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getUploaderId, userId);
        return fileMapper.selectList(wrapper);
    }

    public List<File> getFilesByRelatedId(Long relatedId, String fileType) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getRelatedId, relatedId)
                .eq(File::getFileType, fileType);
        return fileMapper.selectList(wrapper);
    }

    public void deleteFile(Long fileId) throws IOException {
        File file = getFileById(fileId);
        if (file != null) {
            // 删除本地文件
            Path filePath = Paths.get(file.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            // 删除数据库记录
            LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(File::getFileId, fileId);
            fileMapper.delete(wrapper);
        }
    }

    public List<File> getFilesForStudent(Long studentId) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getRelatedId, studentId);
        return fileMapper.selectList(wrapper);
    }

    public List<File> getFilesForTeacher(Long teacherId) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getUploaderId, teacherId);
        return fileMapper.selectList(wrapper);
    }

    public File updateSyncStatus(Long fileId, String syncStatus) {
        File file = getFileById(fileId);
        if (file != null) {
            file.setSyncStatus(syncStatus);
            file.setUpdatedAt(new Date());
            fileMapper.updateById(file);
        }
        return file;
    }
}