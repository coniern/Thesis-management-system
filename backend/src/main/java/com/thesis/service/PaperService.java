package com.thesis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.thesis.entity.Paper;
import com.thesis.mapper.PaperMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaperService {

    @Autowired
    private PaperMapper paperMapper;
    
    @Autowired
    private ProgressCheckService progressCheckService;

    public void submitPaper(Paper paper) {
        progressCheckService.checkCanSubmitPaper(paper.getStudentId());
        paper.setReviewStatus("PENDING");
        paper.setSubmitTime(LocalDateTime.now());
        paper.setCreatedAt(LocalDateTime.now());
        paper.setUpdatedAt(LocalDateTime.now());
        paperMapper.insert(paper);
    }

    public Paper getPaperById(Long paperId) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getId, paperId);
        return paperMapper.selectOne(wrapper);
    }

    public void gradePaper(Long paperId, Integer score, String comment) {
        LambdaUpdateWrapper<Paper> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Paper::getId, paperId)
                .set(Paper::getScore, score)
                .set(Paper::getReviewOpinion, comment)
                .set(Paper::getReviewStatus, "GRADED")
                .set(Paper::getReviewTime, LocalDateTime.now())
                .set(Paper::getUpdatedAt, LocalDateTime.now());
        paperMapper.update(null, wrapper);
    }

    public List<Paper> getPapersByStudentId(Long studentId) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getStudentId, studentId)
                .orderByDesc(Paper::getCreatedAt);
        return paperMapper.selectList(wrapper);
    }

    public List<Paper> getPapersByTeacherId(Long teacherId) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getTeacherId, teacherId)
                .orderByDesc(Paper::getCreatedAt);
        return paperMapper.selectList(wrapper);
    }
}