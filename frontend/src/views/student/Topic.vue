<template>
  <div class="student-topic">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>课题管理</span>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <!-- 已选课题标签页 -->
        <el-tab-pane label="已选课题" name="selected">
          <div v-if="selectedTopic">
            <el-descriptions title="已选课题" :column="1">
              <el-descriptions-item label="课题名称">{{ selectedTopic.topicName }}</el-descriptions-item>
              <el-descriptions-item label="课题描述">{{ selectedTopic.description }}</el-descriptions-item>
              <el-descriptions-item label="指导老师">{{ selectedTopic.teacherName }}</el-descriptions-item>
              <el-descriptions-item label="选择时间">{{ selectedTopic.selectedTime }}</el-descriptions-item>
            </el-descriptions>
          </div>
          <div v-else>
            <el-empty description="暂未选择课题" />
          </div>
        </el-tab-pane>
        
        <!-- 自拟题目标签页 -->
        <el-tab-pane label="自拟题目" name="self">
          <!-- 审核状态提示 -->
          <div v-if="topicStatus" class="status-alert">
            <el-alert
              :title="getStatusText(topicStatus.reviewStatus)"
              :type="getStatusType(topicStatus.reviewStatus)"
              :closable="false"
              show-icon
            >
              <template #default>
                <div v-if="topicStatus.reviewStatus === 'REJECTED'">
                  <p>老师审核意见：{{ topicStatus.reviewOpinion }}</p>
                  <el-button type="primary" size="small" @click="resetForm" style="margin-top: 10px">重新提交</el-button>
                </div>
                <div v-else-if="topicStatus.reviewStatus === 'APPROVED'">
                  <p>老师审核通过，请下载任务书</p>
                  <el-button type="success" size="small" @click="downloadTask" :loading="downloading" style="margin-top: 10px">下载任务书</el-button>
                </div>
                <div v-else-if="topicStatus.reviewStatus === 'PENDING'">
                  <p>自拟题目已提交，等待老师审核</p>
                </div>
              </template>
            </el-alert>
          </div>
          
          <!-- 自拟题目表单 -->
          <el-form :model="topicForm" ref="topicForm" label-width="100px" style="margin-top: 20px">
            <el-form-item label="论文题目" required>
              <el-input v-model="topicForm.topicName" placeholder="请输入论文题目" />
            </el-form-item>
            
            <el-form-item label="选题描述" required>
              <el-input
                v-model="topicForm.topicDescription"
                type="textarea"
                :rows="8"
                placeholder="请输入选题背景、研究内容和预期目标"
              />
            </el-form-item>
            
            <el-form-item label="上传文件">
              <el-upload
                class="upload-demo"
                action="http://localhost:8082/api/upload"
                :headers="uploadHeaders"
                :data="getUploadData"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :multiple="true"
                :file-list="fileList"
                list-type="picture"
              >
                <el-button type="primary">
                  <el-icon><Upload /></el-icon>
                  点击上传
                </el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    支持上传相关文件，如开题报告、文献综述等
                  </div>
                </template>
              </el-upload>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitTopic" :loading="submitting" :disabled="!topicForm.topicName || !topicForm.topicDescription || (topicStatus && topicStatus.reviewStatus === 'PENDING')">
                {{ topicStatus && topicStatus.reviewStatus === 'REJECTED' ? '重新提交' : '提交' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { Upload } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'StudentTopic',
  components: {
    Upload
  },
  data() {
    return {
      activeTab: 'self',
      selectedTopic: null,
      topicStatus: null,
      topicForm: {
        topicName: '',
        topicDescription: ''
      },
      fileList: [],
      submitting: false,
      downloading: false
    }
  },
  mounted() {
    this.loadSelectedTopic()
    this.loadTopicStatus()
  },
  computed: {
    uploadHeaders() {
      const token = localStorage.getItem('token')
      return {
        Authorization: `Bearer ${token}`
      }
    },
    getUploadData() {
      const studentId = localStorage.getItem('userId')
      return {
        uploaderId: parseInt(studentId),
        uploaderRole: 'STUDENT',
        relatedId: this.topicStatus?.topicId || 0,
        fileType: 'TOPIC'
      }
    }
  },
  
  methods: {
    loadSelectedTopic() {
      // 保持selectedTopic为null，直到学生上传自拟论文题目并被老师审核通过后，再显示状态
      this.selectedTopic = null
    },
    
    loadTopicStatus() {
      const token = localStorage.getItem('token')
      const studentId = localStorage.getItem('userId')
      
      axios.get('http://localhost:8082/api/topic/student/status', {
        params: { studentId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        const data = response.data
        // 处理后端返回的新格式数据
        if (data.topicStatus) {
          this.topicStatus = data.topicStatus
          // 填充表单数据，方便重新提交
          this.topicForm.topicName = this.topicStatus.topicName || ''
          this.topicForm.topicDescription = this.topicStatus.topicDescription || ''
        } else if (data) {
          // 兼容旧格式数据
          this.topicStatus = data
          // 填充表单数据，方便重新提交
          this.topicForm.topicName = this.topicStatus.topicName || ''
          this.topicForm.topicDescription = this.topicStatus.topicDescription || ''
        }
      })
      .catch(error => {
        console.error('Failed to load topic status:', error)
      })
    },
    
    handleUploadSuccess(response, file, fileList) {
      this.$message.success('文件上传成功')
    },
    
    handleUploadError(error, file, fileList) {
      this.$message.error('文件上传失败，请重试')
    },
    
    submitTopic() {
      this.submitting = true
      const token = localStorage.getItem('token')
      const studentId = localStorage.getItem('userId')
      
      // 构建提交数据
      const topicData = {
        studentId: parseInt(studentId),
        topicName: this.topicForm.topicName,
        topicDescription: this.topicForm.topicDescription
      }
      
      axios.post('http://localhost:8082/api/topic/student/submit', topicData, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.$message.success('自拟题目提交成功，等待老师审核')
        this.loadTopicStatus()
      })
      .catch(error => {
        console.error('Failed to submit topic:', error)
        this.$message.error(error.response?.data || '提交失败，请重试')
      })
      .finally(() => {
        this.submitting = false
      })
    },
    
    resetForm() {
      this.topicForm = {
        topicName: this.topicStatus.topicName || '',
        topicDescription: this.topicStatus.topicDescription || ''
      }
      this.activeTab = 'self'
    },
    
    downloadTask() {
      this.downloading = true
      const token = localStorage.getItem('token')
      const studentId = parseInt(localStorage.getItem('userId'))
      console.log('Token:', token)
      console.log('Student ID:', studentId)
      
      // 获取学生的任务书信息
      axios.get('http://localhost:8082/api/thesis/student/tasks', {
        params: { studentId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        const tasks = response.data
        if (tasks && tasks.length > 0) {
          const task = tasks[0]
          
          // 检查任务书是否有文件ID
          if (task.fileId) {
            // 直接使用任务书的fileId下载文件
            window.open(`http://localhost:8082/api/file/${task.fileId}?token=${token}`, '_blank')
            this.$message.success('任务书下载开始')
          } else {
            throw new Error('任务书文件不存在')
          }
        } else {
          throw new Error('暂无任务书')
        }
      })
      .catch(error => {
        console.error('Failed to download task:', error)
        this.$message.error(error.message || '任务书下载失败，请重试')
      })
      .finally(() => {
        this.downloading = false
      })
    },
    
    getStatusText(status) {
      switch (status) {
        case 'PENDING':
          return '等待审核'
        case 'APPROVED':
          return '审核通过'
        case 'REJECTED':
          return '审核不通过'
        default:
          return '未知状态'
      }
    },
    
    getStatusType(status) {
      switch (status) {
        case 'PENDING':
          return 'warning'
        case 'APPROVED':
          return 'success'
        case 'REJECTED':
          return 'danger'
        default:
          return 'info'
      }
    }
  }
}
</script>

<style scoped>
.student-topic {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-alert {
  margin-bottom: 20px;
}

.upload-demo {
  margin-top: 10px;
}
</style>