<template>
  <div class="admin-users">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" size="small" @click="addUser">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="role" label="角色" />
        <el-table-column prop="department" label="部门" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑用户弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑用户"
      width="500px"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-select v-model="editForm.department" placeholder="请选择部门">
            <el-option v-for="dept in departments" :key="dept" :label="dept" :value="dept"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="editForm.role" placeholder="请选择角色">
            <el-option v-for="role in roles" :key="role.value" :label="role.label" :value="role.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加用户弹窗 -->
    <el-dialog
      v-model="addDialogVisible"
      title="添加用户"
      width="500px"
    >
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-select v-model="addForm.department" placeholder="请选择部门">
            <el-option v-for="dept in departments" :key="dept" :label="dept" :value="dept"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="addForm.role" placeholder="请选择角色">
            <el-option v-for="role in roles" :key="role.value" :label="role.label" :value="role.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'AdminUsers',
  components: {
    Plus
  },
  data() {
    return {
      users: [],
      editDialogVisible: false,
      addDialogVisible: false,
      editForm: {
        id: '',
        name: '',
        department: '',
        role: ''
      },
      addForm: {
        username: '',
        password: '',
        name: '',
        department: '',
        role: ''
      },
      editRules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        department: [
          { required: true, message: '请选择部门', trigger: 'change' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      },
      addRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        department: [
          { required: true, message: '请选择部门', trigger: 'change' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ]
      },
      departments: [
        '金融与贸易学院',
        '投资与保险学院',
        '文法学院',
        '计算机与数学学院',
        '管理学院',
        '商务英语学院',
        '会计学院'
      ],
      roles: [
        { label: '管理员', value: 'ADMIN' },
        { label: '教师', value: 'TEACHER' },
        { label: '学生', value: 'STUDENT' }
      ]
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    loadUsers() {
      const token = localStorage.getItem('token')
      axios.get('http://localhost:8082/api/system/admin/users', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.users = response.data
      })
      .catch(error => {
        console.error('Failed to load users:', error)
        this.$message.error('加载用户数据失败')
      })
    },
    editUser(user) {
      // 回显用户信息
      this.editForm = {
        id: user.id,
        name: user.name,
        department: user.department,
        role: user.role ? user.role.toUpperCase() : ''
      }
      this.editDialogVisible = true
    },
    submitEditForm() {
      if (!this.$refs.editFormRef) return
      
      this.$refs.editFormRef.validate((valid) => {
        if (valid) {
          const token = localStorage.getItem('token')
          axios.put('http://localhost:8082/api/system/admin/user', this.editForm, {
            headers: {
              Authorization: `Bearer ${token}`
            }
          })
          .then(response => {
            this.editDialogVisible = false
            this.loadUsers()
            this.$message.success('编辑成功')
          })
          .catch(error => {
            console.error('Failed to update user:', error)
            this.$message.error('编辑失败，请重试')
          })
        }
      })
    },
    deleteUser(id) {
      this.$confirm('确定要删除该用户吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const token = localStorage.getItem('token')
        axios.delete(`http://localhost:8082/api/system/admin/user/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        .then(response => {
          this.loadUsers()
          this.$message.success('删除成功')
        })
        .catch(error => {
          console.error('Failed to delete user:', error)
          this.$message.error('删除失败，请重试')
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    addUser() {
      // 重置表单
      this.addForm = {
        username: '',
        password: '',
        name: '',
        department: '',
        role: ''
      }
      this.addDialogVisible = true
    },
    submitAddForm() {
      if (!this.$refs.addFormRef) return
      
      this.$refs.addFormRef.validate((valid) => {
        if (valid) {
          const token = localStorage.getItem('token')
          axios.post('http://localhost:8082/api/system/admin/user', this.addForm, {
            headers: {
              Authorization: `Bearer ${token}`
            }
          })
          .then(response => {
            this.addDialogVisible = false
            this.loadUsers()
            this.$message.success('添加成功')
          })
          .catch(error => {
            console.error('Failed to add user:', error)
            this.$message.error('添加失败，请重试')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.admin-users {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>