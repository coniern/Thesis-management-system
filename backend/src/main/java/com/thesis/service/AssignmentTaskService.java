package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thesis.entity.AssignmentTask;
import com.thesis.entity.TopicSelection;
import com.thesis.entity.User;
import com.thesis.mapper.AssignmentTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentTaskService {

    @Autowired
    private AssignmentTaskMapper taskMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TopicSelectionService topicSelectionService;

    public void createTask(AssignmentTask task) {
        taskMapper.insert(task);
    }

    public List<AssignmentTask> getTasksByStudentId(Long studentId) {
        LambdaQueryWrapper<AssignmentTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssignmentTask::getStudentId, studentId);
        List<AssignmentTask> tasks = taskMapper.selectList(wrapper);
        enrichTasksWithDetails(tasks);
        return tasks;
    }

    public List<AssignmentTask> getTasksByTeacherId(Long teacherId) {
        LambdaQueryWrapper<AssignmentTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssignmentTask::getTeacherId, teacherId);
        List<AssignmentTask> tasks = taskMapper.selectList(wrapper);
        enrichTasksWithDetails(tasks);
        return tasks;
    }

    public AssignmentTask getTaskById(Long taskId) {
        AssignmentTask task = taskMapper.selectById(taskId);
        if (task != null) {
            enrichTaskWithDetails(task);
        }
        return task;
    }
    
    private void enrichTasksWithDetails(List<AssignmentTask> tasks) {
        for (AssignmentTask task : tasks) {
            enrichTaskWithDetails(task);
        }
    }
    
    private void enrichTaskWithDetails(AssignmentTask task) {
        if (task.getStudentId() != null) {
            User student = userService.findById(task.getStudentId());
            if (student != null) {
                task.setStudentName(student.getName());
            }
        }
        
        if (task.getTopicId() != null) {
            TopicSelection topic = topicSelectionService.getTopicById(task.getTopicId());
            if (topic != null) {
                task.setTopicName(topic.getTopicName());
            }
        }
    }
}