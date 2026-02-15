<template>
  <div class="student-midterm">
    <el-card shadow="hover" class="progress-card">
      <template #header>
        <div class="card-header">
          <span>当前进度</span>
        </div>
      </template>
      <div class="progress-section">
        <div class="progress-info">
          <div class="progress-title">论文进度</div>
          <div class="progress-value">{{ paperProgress }}%</div>
        </div>
        <el-progress :percentage="paperProgress" :color="progressColor" :stroke-width="15" />
        <div class="current-stage">当前阶段：中期检查</div>
      </div>
    </el-card>

    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>中期报告</span>
        </div>
      </template>
      <div v-if="midterm">
        <el-descriptions title="已提交中期报告" :column="1" border>
          <el-descriptions-item label="报告标题">{{ midterm.reportTitle }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDate(midterm.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(midterm.status)">{{ getStatusText(midterm.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="导师意见" v-if="midterm.teacherComment">
            {{ midterm.teacherComment }}
          </el-descriptions-item>
          <el-descriptions-item label="附件" v-if="midterm.fileId">
            <el-button type="primary" size="small" @click="downloadFile(midterm.fileId)">
              下载附件
            </el-button>
          </el-descriptions-item>
        </el-descriptions>
        <div style="margin-top: 20px">
          <el-button type="primary" @click="resetForm">重新提交</el-button>
        </div>
      </div>
      <div v-else>
        <el-form :model="midtermForm" ref="midtermFormRef" label-width="120px">
          <el-form-item label="报告标题" required>
            <el-input v-model="midtermForm.reportTitle" placeholder="请输入报告标题" />
          </el-form-item>
          <el-form-item label="报告内容" required>
            <el-input
              v-model="midtermForm.reportContent"
              type="textarea"
              :rows="8"
              placeholder="请输入报告内容"
            />
          </el-form-item>
          <el-form-item label="上传文件">
            <el-upload
              class="upload-demo"
              action="http://localhost:8082/api/upload"
              :headers="uploadHeaders"
              :data="getUploadData"
              :on-success="handleFileUploadSuccess"
              :on-error="handleFileUploadError"
              :limit="1"
              :file-list="fileList"
              accept=".pdf,.doc,.docx"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>
                选择文件
              </el-button>
              <template #tip>
                <div class="el-upload__tip">
                  只能上传 PDF、Word 文件，且不超过 10MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitMidterm" :loading="submitting">
              提交报告
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
import { Upload } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'StudentMidterm',
  components: {
    Upload
  },
  data() {
    return {
      midterm: null,
      midtermForm: {
        reportTitle: '',
        reportContent: '',
        fileId: null
      },
      fileList: [],
      submitting: false,
      paperProgress: 0,
      refreshTimer: null
    }
  },
  computed: {
    progressColor() {
      if (this.paperProgress < 30) return '#f56c6c'
      if (this.paperProgress < 70) return '#e6a23c'
      return '#67c23a'
    },
    uploadHeaders() {
      const token = localStorage.getItem('token')
      return {
        Authorization: `Bearer ${token}`
      }
    },
    getUploadData() {
      const studentId = parseInt(localStorage.getItem('userId'))
      return {
        uploaderId: studentId,
        uploaderRole: 'STUDENT',
        relatedId: this.midterm?.id || 0,
        fileType: 'MIDTERM'
      }
    }
  },
  mounted() {
    this.loadMidterm()
    this.loadProgress()
    this.startAutoRefresh()
  },
  beforeUnmount() {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer)
    }
  },
  methods: {
    startAutoRefresh() {
      this.refreshTimer = setInterval(() => {
        this.loadProgress()
      }, 30000)
    },
    loadProgress() {
      const token = localStorage.getItem('token')
      const studentId = parseInt(localStorage.getItem('userId'))
      
      axios.get('http://localhost:8082/api/progress/student/stats', {
        params: { studentId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        const stats = response.data
        this.paperProgress = stats.paperProgress || 0
      })
      .catch(error => {
        console.error('Failed to load progress:', error)
      })
    },
    loadMidterm() {
      const token = localStorage.getItem('token')
      const studentId = parseInt(localStorage.getItem('userId'))

      axios.get('http://localhost:8082/api/thesis/student/midterms', {
        params: { studentId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        if (response.data && response.data.length > 0) {
          this.midterm = response.data[0]
        } else {
          this.midterm = null
        }
      })
      .catch(error => {
        console.error('Failed to load midterm:', error)
        this.midterm = null
      })
    },
    resetForm() {
      this.midterm = null
      this.midtermForm = {
        reportTitle: '',
        reportContent: '',
        fileId: null
      }
      this.fileList = []
    },
    handleFileUploadSuccess(response, file, fileList) {
      this.midtermForm.fileId = response.fileId
      this.$message.success('文件上传成功')
    },
    handleFileUploadError(error, file, fileList) {
      this.$message.error('文件上传失败，请重试')
    },
    submitMidterm() {
      if (!this.midtermForm.reportTitle || !this.midtermForm.reportContent) {
        this.$message.warning('请填写完整信息')
        return
      }

      this.submitting = true
      const token = localStorage.getItem('token')
      const studentId = parseInt(localStorage.getItem('userId'))

      axios.get('http://localhost:8082/api/topic/student/status', {
        params: { studentId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        const topicStatus = response.data.topicStatus || response.data
        if (!topicStatus) {
          this.$message.error('请先选题')
          this.submitting = false
          return
        }

        const midtermData = {
          topicId: topicStatus.topicId,
          studentId: studentId,
          teacherId: topicStatus.teacherId,
          reportTitle: this.midtermForm.reportTitle,
          reportContent: this.midtermForm.reportContent,
          fileId: this.midtermForm.fileId
        }

        return axios.post('http://localhost:8082/api/thesis/student/submit-midterm', midtermData, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
      })
      .then(response => {
        this.$message.success('中期报告提交成功')
        this.loadMidterm()
      })
      .catch(error => {
        console.error('Failed to submit midterm:', error)
        const errorMsg = error.response?.data?.message || error.response?.data || error.message || '提交失败，请重试'
        this.$message.error(errorMsg)
      })
      .finally(() => {
        this.submitting = false
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
.student-midterm {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-card {
  margin-bottom: 20px;
}

.progress-section {
  padding: 10px 0;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.progress-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.progress-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

.current-stage {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
  color: #909399;
}
</style>
