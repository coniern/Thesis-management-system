<template>
  <div class="teacher-dashboard">
    <el-card shadow="hover" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>教师工作台</span>
        </div>
      </template>
      <div class="dashboard-stats">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-statistic
              title="指导学生数"
              :value="studentCount"
              :precision="0"
              value-style="color: #3f8600"
            />
          </el-col>
          <el-col :span="8">
            <el-statistic
              title="待审批任务"
              :value="pendingTasks"
              :precision="0"
              value-style="color: #1890ff"
            />
          </el-col>
          <el-col :span="8">
            <el-statistic
              title="已完成论文"
              :value="completedPapers"
              :precision="0"
              value-style="color: #722ed1"
            />
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-card shadow="hover" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>学生进度概览</span>
        </div>
      </template>
      <div v-if="studentProgressList.length === 0" class="empty-progress">
        <el-empty description="暂无学生数据" />
      </div>
      <div v-else class="student-progress-list">
        <div v-for="(item, index) in studentProgressList" :key="index" class="student-progress-item">
          <div class="student-info">
            <div class="student-name">{{ item.studentName }}</div>
            <div class="student-topic">{{ item.topicName }}</div>
          </div>
          <div class="progress-wrapper">
            <el-progress 
              :percentage="item.progress" 
              :color="getProgressColor(item.progress)" 
              :stroke-width="10" 
            />
            <div class="progress-text">{{ item.progress }}%</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>最近通知</span>
        </div>
      </template>
      <div v-if="notificationStore.notifications.length === 0" class="empty-notifications">
        <el-empty description="暂无通知" />
      </div>
      <div v-else class="notifications-list">
        <el-timeline>
          <el-timeline-item
            v-for="notification in notificationStore.notifications"
            :key="notification.id"
            :timestamp="formatTime(notification.publishTime)"
          >
            <el-card shadow="hover" class="notification-card">
              <h4 class="notification-title">{{ notification.title }}</h4>
              <p class="notification-content">{{ notification.content }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script>
import { useNotificationStore } from '../../stores'
import axios from 'axios'

export default {
  name: 'TeacherDashboard',
  data() {
    return {
      studentCount: 0,
      pendingTasks: 0,
      completedPapers: 0,
      studentProgressList: [],
      refreshTimer: null,
      notificationStore: useNotificationStore()
    }
  },
  mounted() {
    this.loadStats()
    this.fetchNotifications()
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
        this.loadStats()
      }, 30000)
    },
    loadStats() {
      const token = localStorage.getItem('token')
      const teacherId = parseInt(localStorage.getItem('userId'))
      
      axios.get('http://localhost:8082/api/progress/teacher/stats', {
        params: { teacherId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        const stats = response.data
        this.studentCount = stats.studentCount || 0
        this.pendingTasks = stats.pendingTasks || 0
        this.completedPapers = stats.completedPapers || 0
        this.studentProgressList = stats.studentProgressList || []
      })
      .catch(error => {
        console.error('Failed to load stats:', error)
      })
    },
    getProgressColor(progress) {
      if (progress < 30) return '#f56c6c'
      if (progress < 70) return '#e6a23c'
      return '#67c23a'
    },
    fetchNotifications() {
      const userId = localStorage.getItem('userId')
      if (userId) {
        this.notificationStore.fetchNotifications(userId)
      }
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString()
    }
  }
}
</script>

<style scoped>
.teacher-dashboard {
  padding: 20px;
}

.dashboard-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-stats {
  margin-top: 20px;
}

.student-progress-list {
  margin-top: 20px;
}

.student-progress-item {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 12px;
}

.student-info {
  margin-bottom: 10px;
}

.student-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 4px;
}

.student-topic {
  font-size: 14px;
  color: #666;
}

.progress-wrapper {
  display: flex;
  align-items: center;
  gap: 15px;
}

.progress-wrapper .el-progress {
  flex: 1;
}

.progress-text {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
  min-width: 50px;
  text-align: right;
}

.empty-progress {
  padding: 40px 0;
}

.notifications-list {
  margin-top: 20px;
}

.notification-card {
  width: 100%;
}

.notification-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.notification-content {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.empty-notifications {
  padding: 40px 0;
}
</style>