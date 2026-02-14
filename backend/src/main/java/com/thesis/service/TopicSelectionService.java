package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.thesis.entity.AssignmentTask;
import com.thesis.entity.TopicSelection;
import com.thesis.mapper.AssignmentTaskMapper;
import com.thesis.mapper.TopicSelectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TopicSelectionService {

    @Autowired
    private TopicSelectionMapper topicSelectionMapper;

    @Autowired
    private AssignmentTaskMapper assignmentTaskMapper;

    @Autowired
    private TeacherStudentSelectionService teacherStudentSelectionService;

    @Autowired
    @Lazy
    private ProgressCheckService progressCheckService;

    public void submitTopic(TopicSelection topicSelection) {
        progressCheckService.checkCanSubmitTopic(topicSelection.getStudentId());
        
        // 检查学生是否绑定了老师
        List<Long> teacherIds = teacherStudentSelectionService.getBoundTeachersByStudentId(topicSelection.getStudentId());
        
        // 设置老师ID为绑定的老师
        topicSelection.setTeacherId(teacherIds.get(0));
        topicSelection.setReviewStatus("PENDING");
        topicSelection.setSubmitTime(new Date());
        topicSelection.setUpdatedAt(new Date());
        
        // 检查学生是否已经提交过选题
        TopicSelection existingTopic = getTopicStatus(topicSelection.getStudentId());
        if (existingTopic != null) {
            // 如果已经提交过，更新原有记录
            existingTopic.setTopicName(topicSelection.getTopicName());
            existingTopic.setTopicDescription(topicSelection.getTopicDescription());
            existingTopic.setTeacherId(topicSelection.getTeacherId());
            existingTopic.setReviewStatus("PENDING");
            existingTopic.setSubmitTime(new Date());
            existingTopic.setUpdatedAt(new Date());
            topicSelectionMapper.updateById(existingTopic);
        } else {
            // 如果没有提交过，插入新记录
            topicSelection.setCreatedAt(new Date());
            topicSelectionMapper.insert(topicSelection);
        }
    }

    public List<TopicSelection> getPendingTopicsByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TopicSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicSelection::getTeacherId, teacherId)
                .eq(TopicSelection::getReviewStatus, "PENDING");
        return topicSelectionMapper.selectList(wrapper);
    }

    public void approveTopic(Long topicId) {
        // 更新选题状态为APPROVED
        LambdaUpdateWrapper<TopicSelection> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TopicSelection::getTopicId, topicId)
                .set(TopicSelection::getReviewStatus, "APPROVED")
                .set(TopicSelection::getReviewTime, new Date())
                .set(TopicSelection::getUpdatedAt, new Date());
        topicSelectionMapper.update(null, wrapper);
        
        // 获取选题信息
        TopicSelection topicSelection = getTopicById(topicId);
        if (topicSelection != null) {
            // 创建任务记录
            AssignmentTask task = new AssignmentTask();
            task.setTopicId(topicId);
            task.setStudentId(topicSelection.getStudentId());
            task.setTeacherId(topicSelection.getTeacherId());
            task.setFileId(0L);
            task.setIssueTime(java.time.LocalDateTime.now());
            
            // 设置截止时间为30天后
            java.time.LocalDateTime deadline = java.time.LocalDateTime.now().plusDays(30);
            task.setDeadline(deadline);
            
            // 保存任务记录
            assignmentTaskMapper.insert(task);
        }
    }

    public void rejectTopic(Long topicId, String comment) {
        LambdaUpdateWrapper<TopicSelection> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TopicSelection::getTopicId, topicId)
                .set(TopicSelection::getReviewStatus, "REJECTED")
                .set(TopicSelection::getReviewOpinion, comment)
                .set(TopicSelection::getReviewTime, new Date())
                .set(TopicSelection::getUpdatedAt, new Date());
        topicSelectionMapper.update(null, wrapper);
    }

    public void uploadTask(Long topicId, Long taskFileId) {
        // 查找与选题关联的任务记录
        LambdaQueryWrapper<AssignmentTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssignmentTask::getTopicId, topicId);
        AssignmentTask task = assignmentTaskMapper.selectOne(wrapper);
        
        if (task != null) {
            // 更新任务记录，关联任务书文件ID
            task.setFileId(taskFileId);
            task.setUpdatedAt(java.time.LocalDateTime.now());
            assignmentTaskMapper.updateById(task);
        }
    }

    public TopicSelection getTopicStatus(Long studentId) {
        LambdaQueryWrapper<TopicSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicSelection::getStudentId, studentId)
                .orderByDesc(TopicSelection::getCreatedAt)
                .last("LIMIT 1");
        return topicSelectionMapper.selectOne(wrapper);
    }

    public TopicSelection getTopicById(Long topicId) {
        LambdaQueryWrapper<TopicSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TopicSelection::getTopicId, topicId);
        return topicSelectionMapper.selectOne(wrapper);
    }

    public Long getTaskFileId(Long topicId) {
        // 任务书文件ID需要从assignment_tasks表中获取
        // 这里暂时返回null
        return null;
    }

    // 辅助方法：获取学生绑定的老师ID列表
    public List<Long> getBoundTeachersByStudentId(Long studentId) {
        // 这里需要调用TeacherStudentSelectionService的方法来获取绑定的老师
        // 由于TeacherStudentSelectionService中没有直接提供此方法，这里简单实现
        // 实际项目中应该在TeacherStudentSelectionService中添加此方法
        return teacherStudentSelectionService.getBoundTeachersByStudentId(studentId);
    }
}
