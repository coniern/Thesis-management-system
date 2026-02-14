<template>
  <div class="student-task">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>任务书管理</span>
        </div>
      </template>
      <div v-if="task" class="task-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="任务书ID">{{ task.taskId }}</el-descriptions-item>
          <el-descriptions-item label="选题ID">{{ task.topicId }}</el-descriptions-item>
          <el-descriptions-item label="教师ID">{{ task.teacherId }}</el-descriptions-item>
          <el-descriptions-item label="下达时间">{{ task.issueTime }}</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ task.deadline }}</el-descriptions-item>
          <el-descriptions-item label="查看状态" :span="1">
            <el-tag :type="task.viewed ? 'success' : 'warning'">
              {{ task.viewed ? '已查看' : '未查看' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="file-section">
          <h3>任务书文件</h3>
          <el-button type="primary" @click="downloadTaskFile" :loading="downloading">
            下载任务书
          </el-button>
        </div>
        
        <div class="action-section">
          <el-button 
            type="success" 
            @click="confirmView" 
            :disabled="task.viewed || confirming"
            :loading="confirming"
          >
            确认已查看
          </el-button>
        </div>
      </div>
      <div v-else class="empty-state">
        <el-empty description="暂无任务书" />
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'StudentTask',
  data() {
    return {
      task: null,
      downloading: false,
      confirming: false
    }
  },
  mounted() {
    this.loadTask()
  },
  methods: {
    loadTask() {
      const token = localStorage.getItem('token')
      const studentId = localStorage.getItem('userId')
      
      console.log('Loading task for student:', studentId)
      
      axios.get('http://localhost:8082/api/thesis/student/tasks', {
        params: {
          studentId
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        console.log('Task API response:', response.data)
        
        // 后端返回的是任务书列表，我们只取第一个
        this.task = response.data && response.data.length > 0 ? response.data[0] : null
        
        if (this.task) {
          console.log('Task loaded:', this.task)
          // 标准化任务书字段名，确保使用驼峰命名
          this.normalizeTaskFields()
          // 检查任务书文件是否已同步
          this.checkFileSync()
        } else {
          console.log('No task found for student:', studentId)
          this.$message.info('暂无任务书')
        }
      })
      .catch(error => {
        console.error('Failed to load task:', error)
        if (error.response) {
          console.error('Error response:', error.response.data)
        }
        this.$message.error('获取任务书失败，请重试')
      })
    },
    
    normalizeTaskFields() {
      // 标准化任务书字段名，确保使用驼峰命名
      if (this.task) {
        // 标准化基本字段
        this.task.taskId = this.task.taskId || this.task.task_id
        this.task.topicId = this.task.topicId || this.task.topic_id
        this.task.studentId = this.task.studentId || this.task.student_id
        this.task.teacherId = this.task.teacherId || this.task.teacher_id
        this.task.issueTime = this.task.issueTime || this.task.issue_time
        this.task.deadline = this.task.deadline
        
        // 标准化文件相关字段
        this.task.fileId = this.task.fileId || this.task.file_id
        this.task.fileName = this.task.fileName || this.task.file_name
        this.task.viewed = this.task.viewed || false
      }
    },
    
    checkFileSync() {
      const token = localStorage.getItem('token')
      
      axios.get('http://localhost:8082/api/files/related', {
        params: {
          relatedId: this.task.topicId,
          fileType: 'TASK'
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        console.log('File sync check response:', response.data)
        if (response.data && response.data.length > 0) {
          const taskFile = response.data[0]
          this.task.fileId = taskFile.fileId || taskFile.file_id
          this.task.fileName = taskFile.fileName || taskFile.file_name
          this.task.viewed = taskFile.syncStatus === 'synced' || taskFile.sync_status === 'synced'
        }
      })
      .catch(error => {
        console.error('Failed to check file sync:', error)
      })
    },
    
    downloadTaskFile() {
      if (!this.task.fileId) {
        this.$message.warning('任务书文件不存在')
        return
      }
      
      this.downloading = true
      const token = localStorage.getItem('token')
      const downloadUrl = `http://localhost:8082/api/file/${this.task.fileId}?token=${token}`
      
      // 创建一个隐藏的<a>标签来触发下载
      const link = document.createElement('a')
      link.href = downloadUrl
      link.target = '_self' // 在当前窗口下载，避免跳转
      link.download = this.task.fileName || '任务书'
      link.style.display = 'none' // 隐藏标签
      
      // 将标签添加到页面中
      document.body.appendChild(link)
      
      // 触发点击事件
      link.click()
      
      // 从页面中移除标签
      setTimeout(() => {
        document.body.removeChild(link)
        this.downloading = false
      }, 100)
    },
    
    confirmView() {
      if (!this.task.fileId) {
        this.$message.warning('请先下载任务书')
        return
      }
      
      this.confirming = true
      const token = localStorage.getItem('token')
      
      axios.put(`http://localhost:8082/api/files/${this.task.fileId}/sync`, {}, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.task.viewed = true
        this.$message.success('已确认查看任务书')
      })
      .catch(error => {
        console.error('Failed to confirm view:', error)
        this.$message.error('确认查看失败')
      })
      .finally(() => {
        this.confirming = false
      })
    }
  }
}
</script>

<style scoped>
.student-task {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-content {
  margin-top: 20px;
}

.file-section {
  margin: 30px 0;
}

.file-section h3 {
  margin-bottom: 15px;
  font-size: 16px;
}

.action-section {
  margin-top: 30px;
  text-align: center;
}

.empty-state {
  padding: 50px 0;
  text-align: center;
}
</style>