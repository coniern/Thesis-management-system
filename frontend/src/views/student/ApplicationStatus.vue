<template>
  <div class="application-status">
    <el-card shadow="hover" class="status-card">
      <template #header>
        <div class="card-header">
          <span>申请状态</span>
          <el-button size="small" type="primary" @click="loadStatus">刷新状态</el-button>
        </div>
      </template>
      <el-table :data="applications" style="width: 100%">
        <el-table-column prop="id" label="申请ID" width="80" />
        <el-table-column label="教师信息">
          <template #default="scope">
            <div>
              <div>{{ scope.row.teacherName || '未知教师' }}</div>
              <div class="teacher-major">{{ scope.row.teacherMajor || '未知专业' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <template v-if="(scope.row.status === 'ACCEPTED' || scope.row.status === 'APPROVED')">
              <el-button size="small" type="danger" @click="applyUnbind(scope.row.id)">申请解除绑定</el-button>
            </template>
            <template v-else-if="scope.row.status === 'UNBIND_REQUESTED'">
              <el-button size="small" disabled>解除绑定申请中</el-button>
            </template>
            <template v-else-if="scope.row.status === 'UNBOUND'">
              <el-button size="small" disabled>已解除绑定</el-button>
            </template>
            <template v-else>
              <el-button size="small" disabled>{{ scope.row.status === 'PENDING' ? '等待审批' : '已拒绝' }}</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="applications.length === 0" class="empty-applications">
        <el-empty description="暂无申请记录" />
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ApplicationStatus',
  data() {
    return {
      applications: []
    }
  },
  mounted() {
    this.loadStatus()
  },
  methods: {
    loadStatus() {
      const token = localStorage.getItem('token')
      const studentId = localStorage.getItem('userId')
      
      axios.get('http://localhost:8082/api/student/selections', {
        params: { studentId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        // 格式化申请数据
        this.applications = response.data.map(item => ({
          id: item.id,
          teacherId: item.teacherId,
          teacherName: item.teacherName || '未知教师',
          teacherMajor: item.teacherMajor || '未知专业',
          applyTime: item.createdAt || item.applyTime,
          status: item.status || 'PENDING'
        }))
      })
      .catch(error => {
        console.error('Failed to load application status:', error)
        this.$message.error('加载申请状态失败')
        
        // 使用模拟数据作为备用
        this.applications = [
          {
            id: 1,
            teacherId: 3,
            teacherName: '王老师',
            teacherMajor: '大数据',
            applyTime: '2026-02-09 10:00:00',
            status: 'PENDING'
          }
        ]
      })
    },
    getStatusType(status) {
      switch (status) {
        case 'PENDING':
          return 'warning'
        case 'ACCEPTED':
          return 'success'
        case 'REJECTED':
          return 'danger'
        case 'APPROVED':
          return 'info'
        case 'UNBIND_REQUESTED':
          return 'warning'
        case 'UNBOUND':
          return 'info'
        case 'TIMED_OUT':
          return 'danger'
        default:
          return 'info'
      }
    },
    getStatusText(status) {
      switch (status) {
        case 'PENDING':
          return '待审批'
        case 'ACCEPTED':
          return '已接受'
        case 'REJECTED':
          return '已拒绝'
        case 'APPROVED':
          return '已批准'
        case 'UNBIND_REQUESTED':
          return '解除绑定申请中'
        case 'UNBOUND':
          return '已解除绑定'
        case 'TIMED_OUT':
          return '申请超时'
        default:
          return '未知状态'
      }
    },
    applyUnbind(selectionId) {
      this.$confirm('确定申请解除绑定吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const token = localStorage.getItem('token')
        
        axios.post('http://localhost:8082/api/student/unbind-request', {
          selectionId
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        .then(response => {
          this.$message.success('申请解除绑定成功，请等待老师审批')
          this.loadStatus()
        })
        .catch(error => {
          console.error('Failed to submit unbind request:', error)
          this.$message.error('申请解除绑定失败，请重试')
        })
      }).catch(() => {
        this.$message.info('已取消申请')
      })
    }
  }
}
</script>

<style scoped>
.application-status {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-card {
  min-height: 400px;
}

.empty-applications {
  padding: 40px 0;
}

.teacher-major {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
