package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.thesis.dto.SelectionWithStudentInfo;
import com.thesis.entity.TeacherStudentSelection;
import com.thesis.entity.User;
import com.thesis.mapper.TeacherStudentSelectionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherStudentSelectionService {

    @Autowired
    private TeacherStudentSelectionMapper selectionMapper;

    @Autowired
    private UserService userService;

    public void studentSelectTeacher(Long studentId, Long teacherId) {
        // 检查学生是否有pending状态的申请
        if (hasPendingApplication(studentId)) {
            throw new RuntimeException("学生已经有等待审批的申请，请等待老师审批完成后再申请其他老师");
        }
        
        TeacherStudentSelection selection = new TeacherStudentSelection();
        selection.setStudentId(studentId);
        selection.setTeacherId(teacherId);
        selection.setStatus("PENDING");
        selectionMapper.insert(selection);
    }

    public void teacherAcceptSelection(Long selectionId) {
        LambdaUpdateWrapper<TeacherStudentSelection> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TeacherStudentSelection::getId, selectionId)
                .set(TeacherStudentSelection::getStatus, "ACCEPTED");
        selectionMapper.update(null, wrapper);
    }

    public void teacherRejectSelection(Long selectionId) {
        LambdaUpdateWrapper<TeacherStudentSelection> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TeacherStudentSelection::getId, selectionId)
                .set(TeacherStudentSelection::getStatus, "REJECTED");
        selectionMapper.update(null, wrapper);
    }

    public void adminApproveSelection(Long selectionId) {
        LambdaUpdateWrapper<TeacherStudentSelection> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TeacherStudentSelection::getId, selectionId)
                .set(TeacherStudentSelection::getStatus, "APPROVED");
        selectionMapper.update(null, wrapper);
    }

    public List<TeacherStudentSelection> getSelectionsByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherStudentSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStudentSelection::getTeacherId, teacherId);
        return selectionMapper.selectList(wrapper);
    }

    public List<SelectionWithStudentInfo> getSelectionsWithStudentInfoByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherStudentSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStudentSelection::getTeacherId, teacherId);
        List<TeacherStudentSelection> selections = selectionMapper.selectList(wrapper);

        // 转换为包含学生信息的DTO
        return selections.stream().map(selection -> {
            SelectionWithStudentInfo dto = new SelectionWithStudentInfo();
            dto.setId(selection.getId());
            dto.setStudentId(selection.getStudentId());
            dto.setTeacherId(selection.getTeacherId());
            dto.setStatus(selection.getStatus());
            dto.setCreatedAt(selection.getCreatedAt());
            dto.setUpdatedAt(selection.getUpdatedAt());
            dto.setApplyTime(selection.getApplyTime());

            // 获取学生信息
            try {
                User student = userService.findById(selection.getStudentId());
                if (student != null) {
                    dto.setStudentName(student.getName());
                    dto.setStudentMajor(student.getMajor());
                    dto.setStudentDepartment(student.getDepartment());
                }
            } catch (Exception e) {
                // 忽略异常，保持默认值
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public List<TeacherStudentSelection> getSelectionsByStudentId(Long studentId) {
        LambdaQueryWrapper<TeacherStudentSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStudentSelection::getStudentId, studentId);
        return selectionMapper.selectList(wrapper);
    }

    public List<SelectionWithStudentInfo> getSelectionsWithTeacherInfoByStudentId(Long studentId) {
        LambdaQueryWrapper<TeacherStudentSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStudentSelection::getStudentId, studentId);
        List<TeacherStudentSelection> selections = selectionMapper.selectList(wrapper);

        // 转换为包含教师信息的DTO
        return selections.stream().map(selection -> {
            SelectionWithStudentInfo dto = new SelectionWithStudentInfo();
            dto.setId(selection.getId());
            dto.setStudentId(selection.getStudentId());
            dto.setTeacherId(selection.getTeacherId());
            dto.setStatus(selection.getStatus());
            dto.setCreatedAt(selection.getCreatedAt());
            dto.setUpdatedAt(selection.getUpdatedAt());
            dto.setApplyTime(selection.getApplyTime());

            // 获取教师信息
            try {
                User teacher = userService.findById(selection.getTeacherId());
                if (teacher != null) {
                    dto.setTeacherName(teacher.getName());
                    dto.setTeacherMajor(teacher.getMajor());
                }
            } catch (Exception e) {
                // 忽略异常，保持默认值
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public List<TeacherStudentSelection> getPendingSelections() {
        LambdaQueryWrapper<TeacherStudentSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStudentSelection::getStatus, "PENDING");
        return selectionMapper.selectList(wrapper);
    }

    public boolean isTeacherFull(Long teacherId) {
        User teacher = userService.findByUsername("" + teacherId);
        if (teacher == null) return true;
        
        Integer maxStudents = teacher.getMaxStudents();
        if (maxStudents == null) return false;
        
        LambdaQueryWrapper<TeacherStudentSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStudentSelection::getTeacherId, teacherId)
                .in(TeacherStudentSelection::getStatus, "ACCEPTED", "APPROVED");
        long count = selectionMapper.selectCount(wrapper);
        
        return count >= maxStudents;
    }
    
    public List<User> getStudentsByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherStudentSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStudentSelection::getTeacherId, teacherId)
                .in(TeacherStudentSelection::getStatus, "ACCEPTED", "APPROVED");
        List<TeacherStudentSelection> selections = selectionMapper.selectList(wrapper);
        
        return selections.stream().map(selection -> {
            try {
                return userService.findById(selection.getStudentId());
            } catch (Exception e) {
                return null;
            }
        }).filter(user -> user != null).collect(Collectors.toList());
    }

    public boolean hasPendingApplication(Long studentId) {
        LambdaQueryWrapper<TeacherStudentSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStudentSelection::getStudentId, studentId)
                .eq(TeacherStudentSelection::getStatus, "PENDING");
        return selectionMapper.exists(wrapper);
    }

    public void studentRequestUnbind(Long selectionId) {
        LambdaUpdateWrapper<TeacherStudentSelection> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TeacherStudentSelection::getId, selectionId)
                .in(TeacherStudentSelection::getStatus, "ACCEPTED", "APPROVED")
                .set(TeacherStudentSelection::getStatus, "UNBIND_REQUESTED");
        selectionMapper.update(null, wrapper);
    }

    public void teacherApproveUnbind(Long selectionId) {
        LambdaUpdateWrapper<TeacherStudentSelection> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TeacherStudentSelection::getId, selectionId)
                .eq(TeacherStudentSelection::getStatus, "UNBIND_REQUESTED")
                .set(TeacherStudentSelection::getStatus, "UNBOUND");
        selectionMapper.update(null, wrapper);
    }

    public void checkAndProcessTimeoutApplications() {
        // 计算48小时前的时间
        long fortyEightHoursAgo = System.currentTimeMillis() - 48 * 60 * 60 * 1000;
        
        LambdaUpdateWrapper<TeacherStudentSelection> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TeacherStudentSelection::getStatus, "PENDING")
                .lt(TeacherStudentSelection::getCreatedAt, new java.util.Date(fortyEightHoursAgo))
                .set(TeacherStudentSelection::getStatus, "TIMED_OUT");
        
        selectionMapper.update(null, wrapper);
    }

    public List<Long> getBoundTeachersByStudentId(Long studentId) {
        LambdaQueryWrapper<TeacherStudentSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherStudentSelection::getStudentId, studentId)
                .in(TeacherStudentSelection::getStatus, "ACCEPTED", "APPROVED");
        List<TeacherStudentSelection> selections = selectionMapper.selectList(wrapper);
        
        return selections.stream()
                .map(TeacherStudentSelection::getTeacherId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }
}