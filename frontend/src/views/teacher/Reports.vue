<template>
  <div class="teacher-reports">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>报告管理</span>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="开题报告" name="proposal">
          <el-table :data="proposalReports" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="studentName" label="学生姓名" width="120" />
            <el-table-column prop="reportTitle" label="报告标题" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="提交时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" @click="viewReport(scope.row, 'proposal')">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="中期报告" name="midterm">
          <el-table :data="midtermReports" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="studentName" label="学生姓名" width="120" />
            <el-table-column prop="reportTitle" label="报告标题" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="提交时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" @click="viewReport(scope.row, 'midterm')">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      v-model="viewDialogVisible"
      title="报告详情"
      width="70%"
    >
      <el-descriptions :column="1" border v-if="currentReport">
        <el-descriptions-item label="报告标题">{{ currentReport.reportTitle }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentReport.studentName }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatDate(currentReport.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentReport.status)">{{ getStatusText(currentReport.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="导师意见" v-if="currentReport.teacherComment">
          {{ currentReport.teacherComment }}
        </el-descriptions-item>
        <el-descriptions-item label="报告内容">
          <div style="white-space: pre-wrap">{{ currentReport.reportContent }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="附件" v-if="currentReport.fileId">
          <el-button type="primary" @click="downloadFile(currentReport.fileId)">下载附件</el-button>
        </el-descriptions-item>
      </el-descriptions>
      <div v-if="currentReport && currentReport.status === 'PENDING'" style="margin-top: 20px">
        <el-form :model="reviewForm" label-width="100px">
          <el-form-item label="审核意见">
            <el-input
              v-model="reviewForm.comment"
              type="textarea"
              :rows="4"
              placeholder="请输入审核意见"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="confirmApprove">批准</el-button>
            <el-button type="danger" @click="confirmReject">拒绝</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'TeacherReports',
  data() {
    return {
      activeTab: 'proposal',
      proposalReports: [],
      midtermReports: [],
      viewDialogVisible: false,
      currentReport: null,
      currentReportType: null,
      reviewForm: {
        comment: ''
      }
    }
  },
  mounted() {
    this.loadReports()
  },
  methods: {
    loadReports() {
      const token = localStorage.getItem('token')
      const teacherId = parseInt(localStorage.getItem('userId'))

      Promise.all([
        axios.get('http://localhost:8082/api/thesis/teacher/proposals', {
          params: { teacherId },
          headers: {
            Authorization: `Bearer ${token}`
          }
        }),
        axios.get('http://localhost:8082/api/thesis/teacher/midterms', {
          params: { teacherId },
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
      ])
      .then(async ([proposalResponse, midtermResponse]) => {
        this.proposalReports = await this.loadStudentNames(proposalResponse.data, token)
        this.midtermReports = await this.loadStudentNames(midtermResponse.data, token)
      })
      .catch(error => {
        console.error('Failed to load reports:', error)
        this.$message.error('加载报告列表失败，请重试')
      })
    },
    async loadStudentNames(reports, token) {
      const promises = reports.map(report => {
        return axios.get('http://localhost:8082/api/system/user', {
          params: { userId: report.studentId },
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        .then(userResponse => {
          return {
            ...report,
            studentName: userResponse.data.name || '未知学生'
          }
        })
        .catch(() => {
          return {
            ...report,
            studentName: '未知学生'
          }
        })
      })

      return Promise.all(promises)
    },
    viewReport(report, type) {
      this.currentReport = report
      this.currentReportType = type
      this.reviewForm.comment = ''
      this.viewDialogVisible = true
    },
    approveReport(id, type) {
      this.currentReport = { id }
      this.currentReportType = type
      this.reviewForm.comment = ''
      this.viewDialogVisible = true
    },
    rejectReport(id, type) {
      this.currentReport = { id }
      this.currentReportType = type
      this.reviewForm.comment = ''
      this.viewDialogVisible = true
    },
    confirmApprove() {
      const token = localStorage.getItem('token')
      const id = this.currentReport.id
      const type = this.currentReportType

      const url = type === 'proposal'
        ? 'http://localhost:8082/api/thesis/teacher/approve-proposal'
        : 'http://localhost:8082/api/thesis/teacher/approve-midterm'

      axios.post(url, null, {
        params: {
          reportId: id,
          comment: this.reviewForm.comment
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.$message.success('批准成功')
        this.viewDialogVisible = false
        this.loadReports()
      })
      .catch(error => {
        console.error('Failed to approve report:', error)
        this.$message.error('批准失败，请重试')
      })
    },
    confirmReject() {
      const token = localStorage.getItem('token')
      const id = this.currentReport.id
      const type = this.currentReportType

      const url = type === 'proposal'
        ? 'http://localhost:8082/api/thesis/teacher/reject-proposal'
        : 'http://localhost:8082/api/thesis/teacher/reject-midterm'

      axios.post(url, null, {
        params: {
          reportId: id,
          comment: this.reviewForm.comment
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.$message.success('拒绝成功')
        this.viewDialogVisible = false
        this.loadReports()
      })
      .catch(error => {
        console.error('Failed to reject report:', error)
        this.$message.error('拒绝失败，请重试')
      })
    },
    downloadFile(fileId) {
      const token = localStorage.getItem('token')
      const downloadUrl = `http://localhost:8082/api/file/${fileId}?token=${token}`

      const link = document.createElement('a')
      link.href = downloadUrl
      link.target = '_self'
      link.download = ''
      link.style.display = 'none'

      document.body.appendChild(link)
      link.click()

      setTimeout(() => {
        document.body.removeChild(link)
      }, 100)
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString('zh-CN')
    },
    getStatusText(status) {
      const statusMap = {
        'PENDING': '待审批',
        'APPROVED': '已批准',
        'REJECTED': '已拒绝'
      }
      return statusMap[status] || status
    },
    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>

<style scoped>
.teacher-reports {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
