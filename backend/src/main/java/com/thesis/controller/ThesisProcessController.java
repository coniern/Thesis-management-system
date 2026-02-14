package com.thesis.controller;

import com.thesis.entity.AssignmentTask;
import com.thesis.entity.MidtermReport;
import com.thesis.entity.Paper;
import com.thesis.entity.ProposalReport;
import com.thesis.entity.TopicSelection;
import com.thesis.service.AssignmentTaskService;
import com.thesis.service.MidtermReportService;
import com.thesis.service.PaperService;
import com.thesis.service.ProposalReportService;
import com.thesis.service.TopicSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/thesis")
public class ThesisProcessController {

    @Autowired
    private TopicSelectionService topicService;

    @Autowired
    private AssignmentTaskService taskService;

    @Autowired
    private ProposalReportService proposalService;

    @Autowired
    private MidtermReportService midtermService;

    @Autowired
    private PaperService paperService;

    // 开题报告相关接口
    @PostMapping("/student/submit-proposal")
    public ResponseEntity<?> submitProposal(@RequestBody ProposalReport report) {
        proposalService.submitReport(report);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/student/proposals")
    public ResponseEntity<List<ProposalReport>> getStudentProposals(@RequestParam Long studentId) {
        List<ProposalReport> reports = proposalService.getReportsByStudentId(studentId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/teacher/proposals")
    public ResponseEntity<List<ProposalReport>> getTeacherProposals(@RequestParam Long teacherId) {
        List<ProposalReport> reports = proposalService.getReportsByTeacherId(teacherId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/proposal/{reportId}")
    public ResponseEntity<ProposalReport> getProposalById(@PathVariable Long reportId) {
        ProposalReport report = proposalService.getReportById(reportId);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @PostMapping("/teacher/approve-proposal")
    public ResponseEntity<?> approveProposal(@RequestParam Long reportId, @RequestParam(required = false) String comment) {
        proposalService.approveReport(reportId, comment);
        return ResponseEntity.ok("Proposal report approved successfully");
    }

    @PostMapping("/teacher/reject-proposal")
    public ResponseEntity<?> rejectProposal(@RequestParam Long reportId, @RequestParam(required = false) String comment) {
        proposalService.rejectReport(reportId, comment);
        return ResponseEntity.ok("Proposal report rejected successfully");
    }

    // 中期检查相关接口
    @PostMapping("/student/submit-midterm")
    public ResponseEntity<?> submitMidterm(@RequestBody MidtermReport report) {
        midtermService.submitReport(report);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/student/midterms")
    public ResponseEntity<List<MidtermReport>> getStudentMidterms(@RequestParam Long studentId) {
        List<MidtermReport> reports = midtermService.getReportsByStudentId(studentId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/teacher/midterms")
    public ResponseEntity<List<MidtermReport>> getTeacherMidterms(@RequestParam Long teacherId) {
        List<MidtermReport> reports = midtermService.getReportsByTeacherId(teacherId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/midterm/{reportId}")
    public ResponseEntity<MidtermReport> getMidtermById(@PathVariable Long reportId) {
        MidtermReport report = midtermService.getReportById(reportId);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @PostMapping("/teacher/approve-midterm")
    public ResponseEntity<?> approveMidterm(@RequestParam Long reportId, @RequestParam(required = false) String comment) {
        midtermService.approveReport(reportId, comment);
        return ResponseEntity.ok("Midterm report approved successfully");
    }

    @PostMapping("/teacher/reject-midterm")
    public ResponseEntity<?> rejectMidterm(@RequestParam Long reportId, @RequestParam(required = false) String comment) {
        midtermService.rejectReport(reportId, comment);
        return ResponseEntity.ok("Midterm report rejected successfully");
    }

    // 论文相关接口
    @PostMapping("/student/submit-paper")
    public ResponseEntity<?> submitPaper(@RequestBody Paper paper) {
        paperService.submitPaper(paper);
        return ResponseEntity.ok(paper);
    }

    @GetMapping("/student/papers")
    public ResponseEntity<List<Paper>> getStudentPapers(@RequestParam Long studentId) {
        List<Paper> papers = paperService.getPapersByStudentId(studentId);
        return ResponseEntity.ok(papers);
    }

    @GetMapping("/teacher/papers")
    public ResponseEntity<List<Paper>> getTeacherPapers(@RequestParam Long teacherId) {
        List<Paper> papers = paperService.getPapersByTeacherId(teacherId);
        return ResponseEntity.ok(papers);
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<Paper> getPaperById(@PathVariable Long paperId) {
        Paper paper = paperService.getPaperById(paperId);
        if (paper == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paper);
    }

    @PostMapping("/teacher/grade-paper")
    public ResponseEntity<?> gradePaper(@RequestParam Long paperId, @RequestParam Integer score, @RequestParam(required = false) String comment) {
        paperService.gradePaper(paperId, score, comment);
        return ResponseEntity.ok("Paper graded successfully");
    }

    // 任务书相关接口
    @GetMapping("/student/tasks")
    public ResponseEntity<List<AssignmentTask>> getStudentTasks(@RequestParam Long studentId) {
        List<AssignmentTask> tasks = taskService.getTasksByStudentId(studentId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/teacher/tasks")
    public ResponseEntity<List<AssignmentTask>> getTeacherTasks(@RequestParam Long teacherId) {
        List<AssignmentTask> tasks = taskService.getTasksByTeacherId(teacherId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<AssignmentTask> getTaskById(@PathVariable Long taskId) {
        AssignmentTask task = taskService.getTaskById(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }
}
