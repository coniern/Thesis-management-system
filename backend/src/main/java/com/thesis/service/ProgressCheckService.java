package com.thesis.service;

import com.thesis.entity.AssignmentTask;
import com.thesis.entity.MidtermReport;
import com.thesis.entity.Paper;
import com.thesis.entity.ProposalReport;
import com.thesis.entity.TopicSelection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProgressCheckService {

    @Autowired
    private TeacherStudentSelectionService teacherStudentSelectionService;

    @Autowired
    private TopicSelectionService topicSelectionService;

    @Autowired
    private AssignmentTaskService assignmentTaskService;

    @Autowired
    private ProposalReportService proposalReportService;

    @Autowired
    private MidtermReportService midtermReportService;

    @Autowired
    private PaperService paperService;

    public boolean isStudentBoundToTeacher(Long studentId) {
        List<Long> teacherIds = teacherStudentSelectionService.getBoundTeachersByStudentId(studentId);
        return !teacherIds.isEmpty();
    }

    public boolean isTopicApproved(Long studentId) {
        TopicSelection topic = topicSelectionService.getTopicStatus(studentId);
        return topic != null && "APPROVED".equals(topic.getReviewStatus());
    }

    public boolean hasTaskFile(Long studentId) {
        List<AssignmentTask> tasks = assignmentTaskService.getTasksByStudentId(studentId);
        if (tasks.isEmpty()) {
            return false;
        }
        AssignmentTask task = tasks.get(0);
        return task.getFileId() != null && task.getFileId() != 0;
    }

    public boolean isProposalApproved(Long studentId) {
        List<ProposalReport> reports = proposalReportService.getReportsByStudentId(studentId);
        if (reports.isEmpty()) {
            return false;
        }
        ProposalReport report = reports.get(0);
        return "APPROVED".equals(report.getReviewStatus());
    }

    public boolean isMidtermApproved(Long studentId) {
        List<MidtermReport> reports = midtermReportService.getReportsByStudentId(studentId);
        if (reports.isEmpty()) {
            return false;
        }
        MidtermReport report = reports.get(0);
        return "APPROVED".equals(report.getReviewStatus());
    }

    public void checkCanSubmitTopic(Long studentId) {
        if (!isStudentBoundToTeacher(studentId)) {
            throw new RuntimeException("请先绑定导师后再提交选题");
        }
    }

    public void checkCanSubmitProposal(Long studentId) {
        if (!isStudentBoundToTeacher(studentId)) {
            throw new RuntimeException("请先绑定导师");
        }
        if (!isTopicApproved(studentId)) {
            throw new RuntimeException("请先等待选题审核通过");
        }
        if (!hasTaskFile(studentId)) {
            throw new RuntimeException("请先等待老师下达任务书");
        }
    }

    public void checkCanSubmitMidterm(Long studentId) {
        if (!isStudentBoundToTeacher(studentId)) {
            throw new RuntimeException("请先绑定导师");
        }
        if (!isProposalApproved(studentId)) {
            throw new RuntimeException("请先等待开题报告审核通过");
        }
    }

    public void checkCanSubmitPaper(Long studentId) {
        if (!isStudentBoundToTeacher(studentId)) {
            throw new RuntimeException("请先绑定导师");
        }
        if (!isMidtermApproved(studentId)) {
            throw new RuntimeException("请先等待中期检查审核通过");
        }
    }

    public Map<String, Object> getStudentStats(Long studentId) {
        Map<String, Object> stats = new HashMap<>();
        
        int completedTasks = 0;
        int pendingTasks = 0;
        int paperProgress = 0;
        Map<String, String> stages = new HashMap<>();

        boolean isBound = isStudentBoundToTeacher(studentId);
        boolean topicApproved = isTopicApproved(studentId);
        boolean hasTask = hasTaskFile(studentId);
        boolean proposalApproved = isProposalApproved(studentId);
        boolean midtermApproved = isMidtermApproved(studentId);
        boolean paperSubmitted = isPaperSubmitted(studentId);

        stages.put("selection", isBound ? (topicApproved ? "APPROVED" : "PENDING") : "NOT_STARTED");
        stages.put("task", hasTask ? "RECEIVED" : (topicApproved ? "PENDING" : "NOT_STARTED"));
        stages.put("proposal", proposalApproved ? "APPROVED" : (hasTask ? "PENDING" : "NOT_STARTED"));
        stages.put("midterm", midtermApproved ? "APPROVED" : (proposalApproved ? "PENDING" : "NOT_STARTED"));
        stages.put("paper", paperSubmitted ? "SUBMITTED" : (midtermApproved ? "PENDING" : "NOT_STARTED"));

        if (topicApproved) {
            paperProgress += 10;
            completedTasks++;
        }
        if (hasTask) {
            paperProgress += 15;
            completedTasks++;
        }
        if (proposalApproved) {
            paperProgress += 30;
            completedTasks++;
        }
        if (midtermApproved) {
            paperProgress += 25;
            completedTasks++;
        }
        if (paperSubmitted) {
            paperProgress += 20;
            completedTasks++;
        }

        pendingTasks = 5 - completedTasks;
        if (pendingTasks < 0) pendingTasks = 0;

        stats.put("completedTasks", completedTasks);
        stats.put("pendingTasks", pendingTasks);
        stats.put("paperProgress", paperProgress);
        stats.put("stages", stages);

        return stats;
    }

    public Map<String, Object> getTeacherStats(Long teacherId) {
        Map<String, Object> stats = new HashMap<>();

        int studentCount = 0;
        int pendingCount = 0;
        int completedPapers = 0;
        int pendingTopics = 0;
        int pendingProposals = 0;
        int pendingMidterms = 0;
        int pendingPapers = 0;

        List<TopicSelection> topics = topicSelectionService.getPendingTopicsByTeacherId(teacherId);
        pendingTopics = topics.size();
        pendingCount += pendingTopics;

        List<ProposalReport> proposals = proposalReportService.getReportsByTeacherId(teacherId);
        for (ProposalReport p : proposals) {
            if ("PENDING".equals(p.getReviewStatus())) {
                pendingProposals++;
                pendingCount++;
            }
        }

        List<MidtermReport> midterms = midtermReportService.getReportsByTeacherId(teacherId);
        for (MidtermReport m : midterms) {
            if ("PENDING".equals(m.getReviewStatus())) {
                pendingMidterms++;
                pendingCount++;
            }
        }

        List<Paper> papers = paperService.getPapersByTeacherId(teacherId);
        for (Paper p : papers) {
            if ("PENDING".equals(p.getReviewStatus())) {
                pendingPapers++;
                pendingCount++;
            } else if ("GRADED".equals(p.getReviewStatus())) {
                completedPapers++;
            }
        }

        studentCount = getStudentCountForTeacher(teacherId);

        stats.put("studentCount", studentCount);
        stats.put("pendingCount", pendingCount);
        stats.put("completedPapers", completedPapers);
        stats.put("pendingTopics", pendingTopics);
        stats.put("pendingProposals", pendingProposals);
        stats.put("pendingMidterms", pendingMidterms);
        stats.put("pendingPapers", pendingPapers);

        return stats;
    }

    public Map<String, Object> getStudentProgressDetail(Long studentId) {
        Map<String, Object> detail = new HashMap<>();

        detail.put("isBoundToTeacher", isStudentBoundToTeacher(studentId));
        detail.put("isTopicApproved", isTopicApproved(studentId));
        detail.put("hasTaskFile", hasTaskFile(studentId));
        detail.put("isProposalApproved", isProposalApproved(studentId));
        detail.put("isMidtermApproved", isMidtermApproved(studentId));
        detail.put("isPaperSubmitted", isPaperSubmitted(studentId));

        return detail;
    }

    private boolean isPaperSubmitted(Long studentId) {
        List<Paper> papers = paperService.getPapersByStudentId(studentId);
        return !papers.isEmpty();
    }

    private int getStudentCountForTeacher(Long teacherId) {
        return topicSelectionService.getPendingTopicsByTeacherId(teacherId).size() + 
               proposalReportService.getReportsByTeacherId(teacherId).size();
    }
}
