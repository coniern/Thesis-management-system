<template>
  <div class="admin-notifications">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>通知管理</span>
          <el-button type="primary" size="small" @click="openSendNotificationDialog">
            <el-icon><Plus /></el-icon>
            发送通知
          </el-button>
        </div>
      </template>
      <el-table :data="notifications" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" width="300" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewNotification(scope.row)">查看</el-button>
            <el-button size="small" @click="editNotification(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteNotification(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发送通知弹窗 -->
    <el-dialog
      v-model="sendDialogVisible"
      title="发送通知"
      width="500px"
    >
      <el-form :model="sendForm" :rules="sendRules" ref="sendFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="sendForm.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="sendForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入通知内容"
          />
        </el-form-item>
        <el-form-item label="发送范围" prop="sendScope">
          <el-radio-group v-model="sendForm.sendScope">
            <el-radio label="global">全局通知</el-radio>
            <el-radio label="specific">指定用户</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="sendDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitSendForm">发送</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑通知弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑通知"
      width="500px"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入通知内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'AdminNotifications',
  components: {
    Plus
  },
  data() {
    return {
      notifications: [],
      // 发送通知相关
      sendDialogVisible: false,
      sendForm: {
        title: '',
        content: '',
        sendScope: 'global'
      },
      sendRules: {
        title: [
          { required: true, message: '请输入通知标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入通知内容', trigger: 'blur' }
        ]
      },
      // 编辑通知相关
      editDialogVisible: false,
      editForm: {
        id: '',
        title: '',
        content: ''
      },
      editRules: {
        title: [
          { required: true, message: '请输入通知标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入通知内容', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadNotifications()
  },
  methods: {
    loadNotifications() {
      const token = localStorage.getItem('token')
      const userId = localStorage.getItem('userId')
      axios.get('http://localhost:8082/api/system/user/notifications', {
        params: { userId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.notifications = response.data
      })
      .catch(error => {
        console.error('Failed to load notifications:', error)
        this.$message.error('加载通知数据失败')
      })
    },
    viewNotification(notification) {
      this.$message.info('查看通知: ' + notification.title)
    },
    deleteNotification(id) {
      this.$confirm('确定要删除该通知吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const token = localStorage.getItem('token')
        axios.delete(`http://localhost:8082/api/system/admin/notification/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        .then(response => {
          this.$message.success('删除成功')
          this.loadNotifications()
        })
        .catch(error => {
          console.error('Failed to delete notification:', error)
          this.$message.error('删除失败，请重试')
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    // 发送通知相关方法
    openSendNotificationDialog() {
      // 重置表单
      this.sendForm = {
        title: '',
        content: '',
        sendScope: 'global'
      }
      this.sendDialogVisible = true
    },
    submitSendForm() {
      if (!this.$refs.sendFormRef) return
      
      this.$refs.sendFormRef.validate((valid) => {
        if (valid) {
          const token = localStorage.getItem('token')
          const userId = localStorage.getItem('userId')
          
          if (this.sendForm.sendScope === 'global') {
            // 发送全局通知
            axios.post('http://localhost:8082/api/system/admin/notification/global', {
              title: this.sendForm.title,
              content: this.sendForm.content,
              senderId: userId
            }, {
              headers: {
                Authorization: `Bearer ${token}`
              }
            })
            .then(response => {
              this.sendDialogVisible = false
              this.loadNotifications()
              this.$message.success('发送成功')
            })
            .catch(error => {
              console.error('Failed to send global notification:', error)
              this.$message.error('发送失败，请重试')
            })
          } else {
            // 这里可以添加指定用户的发送逻辑
            this.$message.info('指定用户发送功能待实现')
          }
        }
      })
    },
    // 编辑通知相关方法
    editNotification(notification) {
      // 回显通知信息
      this.editForm = {
        id: notification.id,
        title: notification.title,
        content: notification.content
      }
      this.editDialogVisible = true
    },
    submitEditForm() {
      if (!this.$refs.editFormRef) return
      
      this.$refs.editFormRef.validate((valid) => {
        if (valid) {
          const token = localStorage.getItem('token')
          
          axios.put(`http://localhost:8082/api/system/admin/notification/${this.editForm.id}`, this.editForm, {
            headers: {
              Authorization: `Bearer ${token}`
            }
          })
          .then(response => {
            this.editDialogVisible = false
            this.loadNotifications()
            this.$message.success('编辑成功')
          })
          .catch(error => {
            console.error('Failed to update notification:', error)
            this.$message.error('编辑失败，请重试')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.admin-notifications {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>