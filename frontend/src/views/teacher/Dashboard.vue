<template>
  <div class="teacher-dashboard">
    <div class="welcome-section">
      <h2 class="welcome-title">欢迎回来，{{ userName }}老师！</h2>
      <p class="welcome-subtitle">管理你的学生论文进度</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card stat-card-primary">
        <div class="stat-icon">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ studentCount }}</div>
          <div class="stat-label">指导学生数</div>
        </div>
      </div>
      
      <div class="stat-card stat-card-secondary">
        <div class="stat-icon">
          <el-icon :size="28"><DocumentChecked /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ pendingTasks }}</div>
          <div class="stat-label">待审批任务</div>
        </div>
      </div>
      
      <div class="stat-card stat-card-tertiary">
        <div class="stat-icon">
          <el-icon :size="28"><Trophy /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ completedPapers }}</div>
          <div class="stat-label">已完成论文</div>
        </div>
      </div>
    </div>

    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">学生进度概览</span>
        </div>
      </template>
      <div v-if="studentProgressList.length === 0" class="empty-progress">
        <el-empty description="暂无学生数据" />
      </div>
      <div v-else class="student-progress-list">
        <div v-for="(item, index) in studentProgressList" :key="index" class="student-progress-item">
          <div class="student-info">
            <div class="student-name">{{ item.studentName }}</div>
            <div class="student-topic">{{ item.topicName || '暂无选题' }}</div>
          </div>
          <div class="progress-wrapper">
            <el-progress 
              :percentage="item.progress" 
              :color="getProgressColor(item.progress)" 
              :stroke-width="10"
              :show-text="false"
            />
            <div class="progress-text">{{ item.progress }}%</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">最近通知</span>
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
            placement="top"
          >
            <div class="notification-item">
              <h4 class="notification-title">{{ notification.title }}</h4>
              <p class="notification-content">{{ notification.content }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script>
import { useNotificationStore, useUserStore } from '../../stores'
import { User, DocumentChecked, Trophy } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'TeacherDashboard',
  components: {
    User,
    DocumentChecked,
    Trophy
  },
  data() {
    return {
      studentCount: 0,
      pendingTasks: 0,
      completedPapers: 0,
      studentProgressList: [],
      refreshTimer: null,
      notificationStore: useNotificationStore(),
      userStore: useUserStore()
    }
  },
  computed: {
    userName() {
      return this.userStore.user?.name || '老师'
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
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  margin-bottom: 32px;
  animation: fadeInDown 0.5s ease-out;
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}

.welcome-subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  animation: fadeInUp 0.5s ease-out 0.1s both;
}

.stat-card {
  padding: 24px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  opacity: 0.1;
  transform: translate(30%, -30%);
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-card-primary {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  box-shadow: 0 8px 24px rgba(17, 153, 142, 0.3);
}

.stat-card-primary::before {
  background: white;
}

.stat-card-secondary {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 8px 24px rgba(240, 147, 251, 0.3);
}

.stat-card-secondary::before {
  background: white;
}

.stat-card-tertiary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.stat-card-tertiary::before {
  background: white;
}

.stat-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  backdrop-filter: blur(10px);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: white;
  line-height: 1.2;
  letter-spacing: -1px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 4px;
  font-weight: 500;
}

.section-card {
  margin-bottom: 24px;
  border-radius: 16px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  animation: fadeInUp 0.5s ease-out 0.2s both;
}

.section-card:last-child {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
}

.student-progress-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.student-progress-item {
  padding: 18px 20px;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.student-progress-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.student-info {
  margin-bottom: 12px;
}

.student-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.student-topic {
  font-size: 13px;
  color: #6b7280;
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
  font-size: 18px;
  font-weight: 700;
  color: #667eea;
  min-width: 50px;
  text-align: right;
}

.empty-progress {
  padding: 40px 0;
}

.empty-notifications {
  padding: 40px 0;
}

.notifications-list {
  margin-top: 8px;
}

.notification-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.notification-item:hover {
  background: #f1f5f9;
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px;
}

.notification-content {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .teacher-dashboard {
    padding: 16px;
  }
  
  .welcome-title {
    font-size: 24px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .stat-value {
    font-size: 28px;
  }
}
</style>
