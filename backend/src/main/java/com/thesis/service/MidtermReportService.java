package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.thesis.entity.MidtermReport;
import com.thesis.mapper.MidtermReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MidtermReportService {

    @Autowired
    private MidtermReportMapper reportMapper;
    
    @Autowired
    private ProgressCheckService progressCheckService;

    public void submitReport(MidtermReport report) {
        progressCheckService.checkCanSubmitMidterm(report.getStudentId());
        report.setReviewStatus("PENDING");
        report.setSubmitTime(LocalDateTime.now());
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportMapper.insert(report);
    }

    public MidtermReport getReportById(Long reportId) {
        LambdaQueryWrapper<MidtermReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MidtermReport::getId, reportId);
        return reportMapper.selectOne(wrapper);
    }

    public void approveReport(Long reportId, String comment) {
        LambdaUpdateWrapper<MidtermReport> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MidtermReport::getId, reportId)
                .set(MidtermReport::getReviewStatus, "APPROVED")
                .set(MidtermReport::getReviewOpinion, comment)
                .set(MidtermReport::getReviewTime, LocalDateTime.now())
                .set(MidtermReport::getUpdatedAt, LocalDateTime.now());
        reportMapper.update(null, wrapper);
    }

    public void rejectReport(Long reportId, String comment) {
        LambdaUpdateWrapper<MidtermReport> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MidtermReport::getId, reportId)
                .set(MidtermReport::getReviewStatus, "REJECTED")
                .set(MidtermReport::getReviewOpinion, comment)
                .set(MidtermReport::getReviewTime, LocalDateTime.now())
                .set(MidtermReport::getUpdatedAt, LocalDateTime.now());
        reportMapper.update(null, wrapper);
    }

    public List<MidtermReport> getReportsByStudentId(Long studentId) {
        LambdaQueryWrapper<MidtermReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MidtermReport::getStudentId, studentId)
                .orderByDesc(MidtermReport::getCreatedAt);
        return reportMapper.selectList(wrapper);
    }

    public List<MidtermReport> getReportsByTeacherId(Long teacherId) {
        LambdaQueryWrapper<MidtermReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MidtermReport::getTeacherId, teacherId)
                .orderByDesc(MidtermReport::getCreatedAt);
        return reportMapper.selectList(wrapper);
    }
}