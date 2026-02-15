<template>
  <div class="student-dashboard">
    <div class="welcome-section">
      <h2 class="welcome-title">欢迎回来，{{ userName }}！</h2>
      <p class="welcome-subtitle">继续你的毕业论文旅程</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card stat-card-primary">
        <div class="stat-icon">
          <el-icon :size="28"><CircleCheck /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ completedTasks }}</div>
          <div class="stat-label">已完成任务</div>
        </div>
      </div>
      
      <div class="stat-card stat-card-secondary">
        <div class="stat-icon">
          <el-icon :size="28"><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ pendingTasks }}</div>
          <div class="stat-label">待提交任务</div>
        </div>
      </div>
      
      <div class="stat-card stat-card-tertiary">
        <div class="stat-icon">
          <el-icon :size="28"><Trophy /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ paperProgress }}%</div>
          <div class="stat-label">论文进度</div>
        </div>
      </div>
    </div>

    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">论文进度</span>
        </div>
      </template>
      <div class="progress-section">
        <el-progress 
          :percentage="paperProgress" 
          :color="progressColor" 
          :stroke-width="24"
          :show-text="false"
        />
        <div class="progress-text">{{ paperProgress }}%</div>
      </div>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">各阶段状态</span>
        </div>
      </template>
      <div class="stages-container">
        <div v-for="(stage, index) in stagesList" :key="index" class="stage-item">
          <div class="stage-left">
            <div class="stage-icon-wrapper" :class="getStageIconClass(stage.status)">
              <el-icon :size="20" v-if="stage.status === 'APPROVED' || stage.status === 'RECEIVED' || stage.status === 'SUBMITTED'"><CircleCheck /></el-icon>
              <el-icon :size="20" v-else-if="stage.status === 'PENDING'"><Clock /></el-icon>
              <el-icon :size="20" v-else><CircleClose /></el-icon>
            </div>
            <div class="stage-info">
              <div class="stage-name">{{ stage.name }}</div>
              <div class="stage-status" :class="getStatusClass(stage.status)">{{ getStageStatusText(stage.status) }}</div>
            </div>
          </div>
          <div class="stage-indicator" :class="getStageIndicatorClass(stage.status)"></div>
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
import { CircleCheck, Clock, CircleClose, Trophy } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'StudentDashboard',
  components: {
    CircleCheck,
    Clock,
    CircleClose,
    Trophy
  },
  data() {
    return {
      completedTasks: 0,
      pendingTasks: 0,
      paperProgress: 0,
      stages: {},
      stagesList: [],
      refreshTimer: null,
      notificationStore: useNotificationStore(),
      userStore: useUserStore()
    }
  },
  computed: {
    userName() {
      return this.userStore.user?.name || '同学'
    },
    progressColor() {
      if (this.paperProgress < 30) return '#f56c6c'
      if (this.paperProgress < 70) return '#e6a23c'
      return '#67c23a'
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
      const studentId = parseInt(localStorage.getItem('userId'))
      
      axios.get('http://localhost:8082/api/progress/student/stats', {
        params: { studentId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        const stats = response.data
        this.completedTasks = stats.completedTasks || 0
        this.pendingTasks = stats.pendingTasks || 0
        this.paperProgress = stats.paperProgress || 0
        this.stages = stats.stages || {}
        this.updateStagesList()
      })
      .catch(error => {
        console.error('Failed to load stats:', error)
      })
    },
    updateStagesList() {
      const stageNames = {
        selection: '选题',
        task: '任务书',
        proposal: '开题报告',
        midterm: '中期检查',
        paper: '论文提交'
      }
      
      this.stagesList = Object.keys(stageNames).map(key => ({
        name: stageNames[key],
        status: this.stages[key] || 'NOT_STARTED'
      }))
    },
    getStatusClass(status) {
      if (status === 'APPROVED' || status === 'RECEIVED' || status === 'SUBMITTED') return 'status-approved'
      if (status === 'PENDING') return 'status-pending'
      return 'status-not-started'
    },
    getStageStatusText(status) {
      const statusMap = {
        'NOT_STARTED': '未开始',
        'PENDING': '进行中',
        'APPROVED': '已完成',
        'RECEIVED': '已收到',
        'SUBMITTED': '已提交'
      }
      return statusMap[status] || status
    },
    getStageIconClass(status) {
      if (status === 'APPROVED' || status === 'RECEIVED' || status === 'SUBMITTED') return 'icon-approved'
      if (status === 'PENDING') return 'icon-pending'
      return 'icon-not-started'
    },
    getStageIndicatorClass(status) {
      if (status === 'APPROVED' || status === 'RECEIVED' || status === 'SUBMITTED') return 'indicator-approved'
      if (status === 'PENDING') return 'indicator-pending'
      return 'indicator-not-started'
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
.student-dashboard {
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.stat-card-primary::before {
  background: white;
}

.stat-card-secondary {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  box-shadow: 0 8px 24px rgba(17, 153, 142, 0.3);
}

.stat-card-secondary::before {
  background: white;
}

.stat-card-tertiary {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 8px 24px rgba(240, 147, 251, 0.3);
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

.progress-section {
  text-align: center;
  padding: 20px 0;
}

.progress-section .el-progress {
  margin-bottom: 16px;
}

.progress-text {
  font-size: 36px;
  font-weight: 700;
  color: #667eea;
}

.stages-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stage-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 20px;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stage-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.stage-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.stage-icon-wrapper {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-approved {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: white;
}

.icon-pending {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
  color: white;
}

.icon-not-started {
  background: #e5e7eb;
  color: #9ca3af;
}

.stage-info {
  flex: 1;
}

.stage-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 2px;
}

.stage-status {
  font-size: 13px;
  font-weight: 500;
}

.status-approved {
  color: #67c23a;
}

.status-pending {
  color: #e6a23c;
}

.status-not-started {
  color: #9ca3af;
}

.stage-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.indicator-approved {
  background: #67c23a;
  box-shadow: 0 0 0 4px rgba(103, 194, 58, 0.1);
}

.indicator-pending {
  background: #e6a23c;
  box-shadow: 0 0 0 4px rgba(230, 162, 60, 0.1);
}

.indicator-not-started {
  background: #d1d5db;
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
  .student-dashboard {
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
