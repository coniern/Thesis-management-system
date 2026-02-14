<template>
  <div class="login-container">
    <div class="login-form">
      <h2>毕业论文管理系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入学号/工号/管理员账号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" class="login-button">登录</el-button>
          <el-button @click="goToRegister" class="register-button">注册</el-button>
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
const loginFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const login = async () => {
  if (!loginFormRef.value) return
  
  const valid = await loginFormRef.value.validate()
  if (valid) {
    console.log('开始登录，用户名:', loginForm.username)
    const success = await userStore.login(loginForm.username, loginForm.password)
    console.log('登录结果:', success)
    console.log('用户角色:', userStore.userRole)
    console.log('localStorage中的角色:', localStorage.getItem('userRole'))
    
    if (success) {
      ElMessage.success('登录成功')
      
      // 根据角色跳转到对应页面
      console.log('准备跳转，角色:', userStore.userRole)
      switch (userStore.userRole) {
        case 'ADMIN':
          console.log('跳转到管理员页面')
          router.push('/admin/dashboard')
          break
        case 'TEACHER':
          console.log('跳转到教师页面')
          router.push('/teacher/dashboard')
          break
        case 'STUDENT':
          console.log('跳转到学生页面')
          router.push('/student/dashboard')
          break
        default:
          console.log('角色不匹配，跳转到登录页面')
          router.push('/login')
      }
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.login-button {
  width: 48%;
  margin-right: 4%;
}

.register-button {
  width: 48%;
}
</style>
