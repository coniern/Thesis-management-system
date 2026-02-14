<template>
  <div class="teacher-topics">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>题目管理</span>
        </div>
      </template>
      <el-table :data="pendingTopics" style="width: 100%">
        <el-table-column prop="topicId" label="ID" width="80" />
        <el-table-column label="学生信息">
          <template #default="scope">
            <div>{{ scope.row.studentName || '未知学生' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="topicName" label="论文题目" />
        <el-table-column prop="createdAt" label="提交时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewTopic(scope.row)">查看</el-button>
            <el-button size="small" type="success" @click="approveTopic(scope.row)">通过</el-button>
            <el-button size="small" type="danger" @click="rejectTopic(scope.row)">不通过</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="pendingTopics.length === 0" class="empty-topics">
        <el-empty description="暂无自拟题目申请" />
      </div>
    </el-card>
    
    <!-- 查看自拟题目对话框 -->
    <el-dialog
      v-model="viewTopicDialogVisible"
      title="自拟题目详情"
      width="80%"
    >
      <el-descriptions title="自拟题目信息" :column="1">
        <el-descriptions-item label="论文题目">{{ currentTopic?.topicName }}</el-descriptions-item>
        <el-descriptions-item label="选题描述">{{ currentTopic?.topicDescription }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentTopic?.createdAt }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 学生上传文件列表 -->
      <div v-if="topicFiles.length > 0" class="topic-files">
        <h3 style="margin-top: 20px; margin-bottom: 10px">学生上传文件</h3>
        <el-table :data="topicFiles" style="width: 100%">
          <el-table-column prop="fileName" label="文件名" />
          <el-table-column prop="uploadTime" label="上传时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button size="small" @click="downloadFile(scope.row.fileId)">下载</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else class="empty-files">
        <el-empty description="暂无上传文件" />
      </div>
      
      <!-- 任务书上传 (审核通过后显示) -->
      <div v-if="currentTopic?.reviewStatus === 'APPROVED'" class="task-upload">
        <h3 style="margin-top: 20px; margin-bottom: 10px">任务书上传</h3>
        <el-upload
          class="upload-demo"
          action="http://localhost:8082/api/upload"
          :headers="uploadHeaders"
          :data="getTaskUploadData"
          :on-success="handleTaskUploadSuccess"
          :on-error="handleTaskUploadError"
          :file-list="taskFileList"
          list-type="picture"
        >
          <el-button type="primary">
            <el-icon><Upload /></el-icon>
            上传任务书
          </el-button>
        </el-upload>
      </div>
    </el-dialog>
    
    <!-- 审核不通过对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="审核不通过"
      width="50%"
    >
      <el-form :model="rejectForm">
        <el-form-item label="审核意见" required>
          <el-input
            v-model="rejectForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReject">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Upload } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'TeacherTopics',
  components: {
    Upload
  },
  data() {
    return {
      pendingTopics: [],
      viewTopicDialogVisible: false,
      rejectDialogVisible: false,
      currentTopic: null,
      rejectForm: {
        comment: ''
      },
      taskFileList: [],
      topicFiles: []
    }
  },
  computed: {
    uploadHeaders() {
      const token = localStorage.getItem('token')
      return {
        Authorization: `Bearer ${token}`
      }
    },
    getTaskUploadData() {
      const teacherId = parseInt(localStorage.getItem('userId'))
      return {
        uploaderId: teacherId,
        uploaderRole: 'TEACHER',
        relatedId: this.currentTopic?.topicId || 0,
        fileType: 'TASK'
      }
    }
  },
  mounted() {
    this.loadPendingTopics()
  },
  methods: {
    loadPendingTopics() {
      const token = localStorage.getItem('token')
      const teacherId = localStorage.getItem('userId')
      
      axios.get('http://localhost:8082/api/topic/teacher/pending', {
        params: { teacherId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        // 构建学生ID到学生信息的映射
        const studentPromises = response.data.map(topic => {
          return axios.get('http://localhost:8082/api/system/user', {
            params: { userId: topic.studentId },
            headers: {
              Authorization: `Bearer ${token}`
            }
          })
          .then(userResponse => {
            return {
              ...topic,
              studentName: userResponse.data.name || '未知学生'
            }
          })
          .catch(() => {
            return {
              ...topic,
              studentName: '未知学生'
            }
          })
        })
        
        Promise.all(studentPromises).then(topics => {
          this.pendingTopics = topics
        })
      })
      .catch(error => {
        console.error('Failed to load pending topics:', error)
        this.$message.error('加载待审核题目失败，请重试')
        this.pendingTopics = []
      })
    },
    
    downloadFile(fileId) {
      const token = localStorage.getItem('token')
      const downloadUrl = `http://localhost:8082/api/file/${fileId}?token=${token}`
      
      // 创建一个隐藏的<a>标签来触发下载
      const link = document.createElement('a')
      link.href = downloadUrl
      link.target = '_self' // 在当前窗口下载，避免跳转
      link.download = '' // 让浏览器决定文件名
      link.style.display = 'none' // 隐藏标签
      
      // 将标签添加到页面中
      document.body.appendChild(link)
      
      // 触发点击事件
      link.click()
      
      // 从页面中移除标签
      setTimeout(() => {
        document.body.removeChild(link)
      }, 100)
    },
    
    viewTopic(topic) {
      const token = localStorage.getItem('token')
      
      axios.get('http://localhost:8082/api/topic/teacher/detail', {
        params: { topicId: topic.topicId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.currentTopic = response.data
        this.loadTopicFiles(response.data.topicId)
        this.viewTopicDialogVisible = true
      })
      .catch(error => {
        console.error('Failed to get topic detail:', error)
        this.currentTopic = topic
        this.loadTopicFiles(topic.topicId)
        this.viewTopicDialogVisible = true
      })
    },
    
    loadTopicFiles(topicId) {
      const token = localStorage.getItem('token')
      
      axios.get('http://localhost:8082/api/files/related', {
        params: { relatedId: topicId, fileType: 'TOPIC' },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.topicFiles = response.data
      })
      .catch(error => {
        console.error('Failed to load topic files:', error)
        this.topicFiles = []
      })
    },
    
    approveTopic(topic) {
      const token = localStorage.getItem('token')
      
      axios.post('http://localhost:8082/api/topic/teacher/approve', null, {
        params: { topicId: topic.topicId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.$message.success('审核通过')
        this.loadPendingTopics()
      })
      .catch(error => {
        console.error('Failed to approve topic:', error)
        this.$message.error('审核通过失败，请重试')
      })
    },
    
    rejectTopic(topic) {
      this.currentTopic = topic
      this.rejectForm.comment = ''
      this.rejectDialogVisible = true
    },
    
    confirmReject() {
      const token = localStorage.getItem('token')
      
      axios.post('http://localhost:8082/api/topic/teacher/reject', null, {
        params: {
          topicId: this.currentTopic.topicId,
          comment: this.rejectForm.comment
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.$message.success('审核不通过')
        this.rejectDialogVisible = false
        this.loadPendingTopics()
      })
      .catch(error => {
        console.error('Failed to reject topic:', error)
        this.$message.error('审核不通过失败，请重试')
      })
    },
    
    handleTaskUploadSuccess(response, file, fileList) {
      const token = localStorage.getItem('token')
      // 从响应中正确获取文件ID
      const fileId = response.fileId || response.file_id || 1
      
      axios.post('http://localhost:8082/api/topic/teacher/upload-task', null, {
        params: {
          topicId: this.currentTopic.topicId,
          taskFileId: fileId
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.$message.success('任务书上传成功')
      })
      .catch(error => {
        console.error('Failed to upload task:', error)
        this.$message.error('任务书上传失败，请重试')
      })
    },
    
    handleTaskUploadError(error, file, fileList) {
      this.$message.error('任务书上传失败，请重试')
    }
  }
}
</script>

<style scoped>
.teacher-topics {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-topics {
  padding: 40px 0;
}

.task-upload {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eaeaea;
}

.dialog-footer {
  width: 100%;
  text-align: right;
}
</style>