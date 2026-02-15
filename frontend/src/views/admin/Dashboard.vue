<template>
  <div class="admin-dashboard">
    <div class="welcome-section">
      <h2 class="welcome-title">欢迎回来，{{ userName }}管理员！</h2>
      <p class="welcome-subtitle">系统全局概览</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card stat-card-primary">
        <div class="stat-icon">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ userCount }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      
      <div class="stat-card stat-card-secondary">
        <div class="stat-icon">
          <el-icon :size="28"><School /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ teacherCount }}</div>
          <div class="stat-label">教师数</div>
        </div>
      </div>
      
      <div class="stat-card stat-card-tertiary">
        <div class="stat-icon">
          <el-icon :size="28"><Reading /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ studentCount }}</div>
          <div class="stat-label">学生数</div>
        </div>
      </div>
      
      <div class="stat-card stat-card-quaternary">
        <div class="stat-icon">
          <el-icon :size="28"><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ paperCount }}</div>
          <div class="stat-label">论文数</div>
        </div>
      </div>
    </div>

    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">最近通知</span>
        </div>
      </template>
      <el-empty description="暂无通知" />
    </el-card>
  </div>
</template>

<script>
import { useUserStore } from '../../stores'
import { User, School, Reading, Document } from '@element-plus/icons-vue'

export default {
  name: 'AdminDashboard',
  components: {
    User,
    School,
    Reading,
    Document
  },
  data() {
    return {
      userCount: 0,
      teacherCount: 0,
      studentCount: 0,
      paperCount: 0,
      userStore: useUserStore()
    }
  },
  computed: {
    userName() {
      return this.userStore.user?.name || '管理员'
    }
  },
  mounted() {
    this.loadStats()
  },
  methods: {
    loadStats() {
      this.userCount = 120
      this.teacherCount = 30
      this.studentCount = 90
      this.paperCount = 85
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
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
  grid-template-columns: repeat(4, 1fr);
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

.stat-card-quaternary {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  box-shadow: 0 8px 24px rgba(250, 112, 154, 0.3);
}

.stat-card-quaternary::before {
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

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .admin-dashboard {
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
