package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.thesis.entity.ProposalReport;
import com.thesis.mapper.ProposalReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProposalReportService {

    @Autowired
    private ProposalReportMapper reportMapper;
    
    @Autowired
    private ProgressCheckService progressCheckService;

    public void submitReport(ProposalReport report) {
        progressCheckService.checkCanSubmitProposal(report.getStudentId());
        report.setReviewStatus("PENDING");
        report.setSubmitTime(LocalDateTime.now());
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportMapper.insert(report);
    }

    public ProposalReport getReportById(Long reportId) {
        LambdaQueryWrapper<ProposalReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProposalReport::getId, reportId);
        return reportMapper.selectOne(wrapper);
    }

    public void approveReport(Long reportId, String comment) {
        LambdaUpdateWrapper<ProposalReport> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ProposalReport::getId, reportId)
                .set(ProposalReport::getReviewStatus, "APPROVED")
                .set(ProposalReport::getReviewOpinion, comment)
                .set(ProposalReport::getReviewTime, LocalDateTime.now())
                .set(ProposalReport::getUpdatedAt, LocalDateTime.now());
        reportMapper.update(null, wrapper);
    }

    public void rejectReport(Long reportId, String comment) {
        LambdaUpdateWrapper<ProposalReport> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ProposalReport::getId, reportId)
                .set(ProposalReport::getReviewStatus, "REJECTED")
                .set(ProposalReport::getReviewOpinion, comment)
                .set(ProposalReport::getReviewTime, LocalDateTime.now())
                .set(ProposalReport::getUpdatedAt, LocalDateTime.now());
        reportMapper.update(null, wrapper);
    }

    public List<ProposalReport> getReportsByStudentId(Long studentId) {
        LambdaQueryWrapper<ProposalReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProposalReport::getStudentId, studentId)
                .orderByDesc(ProposalReport::getCreatedAt);
        return reportMapper.selectList(wrapper);
    }

    public List<ProposalReport> getReportsByTeacherId(Long teacherId) {
        LambdaQueryWrapper<ProposalReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProposalReport::getTeacherId, teacherId)
                .orderByDesc(ProposalReport::getCreatedAt);
        return reportMapper.selectList(wrapper);
    }
}