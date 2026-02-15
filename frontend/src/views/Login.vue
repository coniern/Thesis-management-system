<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <el-icon><Document /></el-icon>
        </div>
        <h1>毕业论文管理系统</h1>
        <p>Thesis Management System</p>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入学号/工号/管理员账号"
            size="large"
            prefix-icon="User"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="login"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" class="login-button" size="large">
            登录
          </el-button>
        </el-form-item>
        <div class="register-link">
          还没有账号？
          <el-button type="primary" link @click="goToRegister">立即注册</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores'
import { ElMessage } from 'element-plus'
import { Document, User, Lock } from '@element-plus/icons-vue'

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
    const success = await userStore.login(loginForm.username, loginForm.password)
    
    if (success) {
      ElMessage.success('登录成功')
      
      switch (userStore.userRole) {
        case 'ADMIN':
          router.push('/admin/dashboard')
          break
        case 'TEACHER':
          router.push('/teacher/dashboard')
          break
        case 'STUDENT':
          router.push('/student/dashboard')
          break
        default:
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
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.login-card {
  width: 420px;
  padding: 48px 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  position: relative;
  z-index: 1;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.logo .el-icon {
  font-size: 36px;
  color: white;
}

.login-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px;
  letter-spacing: 0.5px;
}

.login-header p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  font-weight: 400;
}

.login-form {
  margin-bottom: 0;
}

.login-form .el-form-item {
  margin-bottom: 24px;
}

.login-form .el-input__wrapper {
  border-radius: 12px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.login-form .el-input__wrapper:hover {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.login-form .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.3);
}

.login-button {
  width: 100%;
  margin-top: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  height: 48px;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.5);
}

.login-button:active {
  transform: translateY(0);
}

.register-link {
  text-align: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
  color: #6b7280;
  font-size: 14px;
}

.register-link .el-button {
  font-weight: 600;
  padding: 0 4px;
}

@media (max-width: 768px) {
  .login-card {
    width: 90%;
    padding: 32px 24px;
    margin: 20px;
  }

  .login-header h1 {
    font-size: 22px;
  }

  .logo {
    width: 60px;
    height: 60px;
  }

  .logo .el-icon {
    font-size: 28px;
  }
}
</style>
