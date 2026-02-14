<template>
  <div class="admin-dashboard">
    <el-card shadow="hover" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>
      <div class="dashboard-stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-statistic
              title="总用户数"
              :value="userCount"
              :precision="0"
              value-style="color: #3f8600"
            />
          </el-col>
          <el-col :span="6">
            <el-statistic
              title="教师数"
              :value="teacherCount"
              :precision="0"
              value-style="color: #1890ff"
            />
          </el-col>
          <el-col :span="6">
            <el-statistic
              title="学生数"
              :value="studentCount"
              :precision="0"
              value-style="color: #722ed1"
            />
          </el-col>
          <el-col :span="6">
            <el-statistic
              title="论文数"
              :value="paperCount"
              :precision="0"
              value-style="color: #fa541c"
            />
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
      <el-empty description="暂无通知" />
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'AdminDashboard',
  data() {
    return {
      userCount: 0,
      teacherCount: 0,
      studentCount: 0,
      paperCount: 0
    }
  },
  mounted() {
    this.loadStats()
  },
  methods: {
    loadStats() {
      // 模拟数据
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
</style>