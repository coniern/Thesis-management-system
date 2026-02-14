package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.thesis.entity.Progress;
import com.thesis.mapper.ProgressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgressService {

    @Autowired
    private ProgressMapper progressMapper;

    public void updateProgress(Long studentId, String stage, String status) {
        LambdaQueryWrapper<Progress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Progress::getStudentId, studentId)
                .eq(Progress::getStage, stage);

        Progress existingProgress = progressMapper.selectOne(wrapper);

        if (existingProgress != null) {
            LambdaUpdateWrapper<Progress> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Progress::getId, existingProgress.getId())
                    .set(Progress::getStatus, status)
                    .set(Progress::getUpdatedAt, LocalDateTime.now());
            progressMapper.update(null, updateWrapper);
        } else {
            Progress progress = new Progress();
            progress.setStudentId(studentId);
            progress.setStage(stage);
            progress.setStatus(status);
            progress.setCreatedAt(LocalDateTime.now());
            progress.setUpdatedAt(LocalDateTime.now());
            progressMapper.insert(progress);
        }
    }

    public List<Progress> getStudentProgress(Long studentId) {
        LambdaQueryWrapper<Progress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Progress::getStudentId, studentId);
        return progressMapper.selectList(wrapper);
    }

    public Progress getStudentStageProgress(Long studentId, String stage) {
        LambdaQueryWrapper<Progress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Progress::getStudentId, studentId)
                .eq(Progress::getStage, stage);
        return progressMapper.selectOne(wrapper);
    }
}