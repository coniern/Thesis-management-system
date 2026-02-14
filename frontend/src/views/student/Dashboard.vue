<template>
  <div class="student-dashboard">
    <el-card shadow="hover" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>学生工作台</span>
        </div>
      </template>
      <div class="dashboard-stats">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-statistic
              title="已完成任务"
              :value="completedTasks"
              :precision="0"
              value-style="color: #3f8600"
            />
          </el-col>
          <el-col :span="8">
            <el-statistic
              title="待提交任务"
              :value="pendingTasks"
              :precision="0"
              value-style="color: #1890ff"
            />
          </el-col>
          <el-col :span="8">
            <el-statistic
              title="论文进度"
              :value="paperProgress"
              suffix="%"
              value-style="color: #722ed1"
            />
          </el-col>
        </el-row>
      </div>
      <div class="progress-bar-section">
        <h3 style="margin-bottom: 15px">论文进度</h3>
        <el-progress :percentage="paperProgress" :color="progressColor" :stroke-width="20" />
      </div>
    </el-card>

    <el-card shadow="hover" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>各阶段状态</span>
        </div>
      </template>
      <div class="stages-container">
        <el-row :gutter="10">
          <el-col :span="24" v-for="(stage, index) in stagesList" :key="index">
            <div class="stage-item">
              <div class="stage-icon">
                <el-icon v-if="stage.status === 'APPROVED' || stage.status === 'RECEIVED' || stage.status === 'SUBMITTED'"><CircleCheck /></el-icon>
                <el-icon v-else-if="stage.status === 'PENDING'"><Clock /></el-icon>
                <el-icon v-else><CircleClose /></el-icon>
              </div>
              <div class="stage-info">
                <div class="stage-name">{{ stage.name }}</div>
                <div class="stage-status" :class="getStatusClass(stage.status)">{{ getStageStatusText(stage.status) }}</div>
              </div>
            </div>
          </el-col>
        </el-row>
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
import { CircleCheck, Clock, CircleClose } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'StudentDashboard',
  components: {
    CircleCheck,
    Clock,
    CircleClose
  },
  data() {
    return {
      completedTasks: 0,
      pendingTasks: 0,
      paperProgress: 0,
      stages: {},
      stagesList: [],
      refreshTimer: null,
      notificationStore: useNotificationStore()
    }
  },
  computed: {
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

.progress-bar-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eaeaea;
}

.stages-container {
  margin-top: 20px;
}

.stage-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 10px;
}

.stage-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
}

.stage-icon .el-icon {
  color: #67c23a;
}

.stage-icon .el-icon:nth-child(2) {
  color: #e6a23c;
}

.stage-icon .el-icon:nth-child(3) {
  color: #909399;
}

.stage-info {
  flex: 1;
}

.stage-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stage-status {
  font-size: 14px;
}

.status-approved {
  color: #67c23a;
}

.status-pending {
  color: #e6a23c;
}

.status-not-started {
  color: #909399;
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