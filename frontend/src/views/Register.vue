<template>
  <div class="register-container">
    <div class="register-form">
      <h2>用户注册</h2>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef">
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio value="STUDENT">学生</el-radio>
            <el-radio value="TEACHER">教师</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="学生请输入学号，教师请输入工号"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="registerForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码"></el-input>
        </el-form-item>
        <el-form-item v-if="registerForm.role === 'STUDENT'" label="院系" prop="department">
          <el-input v-model="registerForm.department" placeholder="请输入院系"></el-input>
        </el-form-item>
        <el-form-item v-if="registerForm.role === 'STUDENT'" label="专业" prop="major">
          <el-input v-model="registerForm.major" placeholder="请输入专业"></el-input>
        </el-form-item>
        <el-form-item v-if="registerForm.role === 'TEACHER'" label="院系" prop="department">
          <el-input v-model="registerForm.department" placeholder="请输入院系"></el-input>
        </el-form-item>
        <el-form-item v-if="registerForm.role === 'TEACHER'" label="专业" prop="major">
          <el-input v-model="registerForm.major" placeholder="请输入专业"></el-input>
        </el-form-item>
        <el-form-item v-if="registerForm.role === 'TEACHER'" label="最大学生数" prop="maxStudents">
          <el-input v-model.number="registerForm.maxStudents" type="number" placeholder="请输入最大指导学生数"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="register" class="register-button">注册</el-button>
          <el-button @click="goToLogin" class="login-button">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref(null)

const registerForm = reactive({
  role: 'STUDENT',
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
  department: '',
  major: '',
  maxStudents: 5
})

const rules = {
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  department: [
    { required: true, message: '请输入院系', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  maxStudents: [
    { required: true, message: '请输入最大学生数', trigger: 'blur' },
    { type: 'number', min: 1, message: '最大学生数至少为1', trigger: 'blur' }
  ]
}

const register = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      const userData = {
        username: registerForm.username,
        password: registerForm.password,
        name: registerForm.name,
        role: registerForm.role,
        department: registerForm.department
      }
      
      if (registerForm.role === 'STUDENT') {
        userData.major = registerForm.major
      } else if (registerForm.role === 'TEACHER') {
        userData.major = registerForm.major
        userData.maxStudents = registerForm.maxStudents
      }
      
      const result = await userStore.register(userData)
      if (result && !result.error) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } else {
        // 检查是否是用户名重复错误
        const errorMessage = result?.error || '注册失败，请重试'
        if (errorMessage.includes('Duplicate entry') || errorMessage.includes('duplicate key') || errorMessage.includes('用户名已存在')) {
          ElMessage.error('学号/工号已存在，请使用其他学号/工号注册')
        } else {
          ElMessage.error(errorMessage)
        }
      }
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.register-form {
  width: 500px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.register-button {
  width: 48%;
  margin-right: 4%;
}

.login-button {
  width: 48%;
}
</style>
