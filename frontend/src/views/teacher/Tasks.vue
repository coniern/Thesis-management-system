<template>
  <div class="teacher-tasks">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>任务书管理</span>
        </div>
      </template>
      <el-table :data="taskList" style="width: 100%">
        <el-table-column prop="taskId" label="任务ID" width="80" />
        <el-table-column label="学生信息">
          <template #default="scope">
            <div>{{ scope.row.studentName || '未知学生' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="topicName" label="论文题目" />
        <el-table-column prop="issueTime" label="下达时间" width="180" />
        <el-table-column prop="deadline" label="截止日期" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="viewTask(scope.row)">查看</el-button>
            <el-button size="small" @click="uploadTaskFile(scope.row)">上传任务书</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="taskList.length === 0" class="empty-tasks">
        <el-empty description="暂无任务书" />
      </div>
    </el-card>
    
    <!-- 任务书上传对话框 -->
    <el-dialog
      v-model="uploadTaskDialogVisible"
      title="任务书上传"
      width="80%"
    >
      <el-descriptions title="任务信息" :column="1">
        <el-descriptions-item label="任务ID">{{ currentTask?.taskId }}</el-descriptions-item>
        <el-descriptions-item label="学生信息">{{ currentTask?.studentName || '未知学生' }}</el-descriptions-item>
        <el-descriptions-item label="论文题目">{{ currentTask?.topicName }}</el-descriptions-item>
        <el-descriptions-item label="下达时间">{{ currentTask?.issueTime }}</el-descriptions-item>
        <el-descriptions-item label="截止日期">{{ currentTask?.deadline }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="task-upload">
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
  </div>
</template>

<script>
import { Upload } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'TeacherTasks',
  components: {
    Upload
  },
  data() {
    return {
      taskList: [],
      uploadTaskDialogVisible: false,
      currentTask: null,
      taskFileList: []
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
        relatedId: this.currentTask?.topicId || 0,
        fileType: 'TASK'
      }
    }
  },
  mounted() {
    this.loadTaskList()
  },
  methods: {
    loadTaskList() {
      const token = localStorage.getItem('token')
      const teacherId = parseInt(localStorage.getItem('userId'))
      
      const apiPath = 'http://localhost:8082/api/thesis/teacher/tasks'
      
      axios.get(apiPath, {
        params: { teacherId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        const tasks = response.data
        
        if (!tasks || tasks.length === 0) {
          this.taskList = []
          this.$message.info('暂无任务书')
          return
        }
        
        this.taskList = tasks
      })
      .catch(error => {
        console.error('Failed to load task list from', apiPath, ':', error)
        
        if (error.response && error.response.status === 403) {
          this.$message.error('权限不足，请检查您的角色')
        } else if (error.response && error.response.status === 401) {
          this.$message.error('认证失败，请重新登录')
        } else if (error.response && error.response.status === 404) {
          this.$message.error('API路径错误，请联系管理员')
        } else {
          this.$message.error('加载任务列表失败，请重试')
        }
        
        this.taskList = []
      })
    },
    
    viewTask(task) {
      if (task.fileId && task.fileId !== 0) {
        this.downloadFile(task.fileId)
      } else {
        this.$message.info('该任务尚未上传任务书')
      }
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
    
    uploadTaskFile(task) {
      this.currentTask = task
      this.taskFileList = []
      this.uploadTaskDialogVisible = true
    },
    
    handleTaskUploadSuccess(response, file, fileList) {
      const token = localStorage.getItem('token')
      const fileId = response.fileId || response.file_id || 1 // 从响应中正确获取文件ID
      
      console.log('File uploaded successfully, fileId:', fileId)
      console.log('Current task:', this.currentTask)
      
      if (!this.currentTask || !this.currentTask.topicId) {
        this.$message.error('任务信息不完整，上传失败')
        return
      }
      
      axios.post('http://localhost:8082/api/topic/teacher/upload-task', null, {
        params: {
          topicId: this.currentTask.topicId,
          taskFileId: fileId
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        console.log('Task upload API response:', response)
        this.$message.success('任务书上传成功')
        this.uploadTaskDialogVisible = false
        // 刷新任务列表，确保显示最新状态
        this.loadTaskList()
      })
      .catch(error => {
        console.error('Failed to upload task:', error)
        if (error.response) {
          console.error('Error response:', error.response.data)
        }
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
.teacher-tasks {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-tasks {
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