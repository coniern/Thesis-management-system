<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <div class="logo-icon">
          <el-icon :size="48"><User /></el-icon>
        </div>
        <h1>用户注册</h1>
        <p>加入我们，开启毕业论文管理之旅</p>
      </div>
      
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form">
        <el-form-item prop="role">
          <div class="role-selector">
            <div 
              v-for="role in roleOptions" 
              :key="role.value"
              class="role-option"
              :class="{ active: registerForm.role === role.value }"
              @click="registerForm.role = role.value"
            >
              <el-icon :size="24"><component :is="role.icon" /></el-icon>
              <span>{{ role.label }}</span>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            placeholder="学生请输入学号，教师请输入工号"
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="name">
          <el-input 
            v-model="registerForm.name" 
            placeholder="请输入姓名"
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Edit /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码（至少6位）"
            size="large"
            class="custom-input"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            size="large"
            class="custom-input"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item v-if="registerForm.role === 'STUDENT'" prop="department">
          <el-input 
            v-model="registerForm.department" 
            placeholder="请输入院系"
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><OfficeBuilding /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item v-if="registerForm.role === 'STUDENT'" prop="major">
          <el-input 
            v-model="registerForm.major" 
            placeholder="请输入专业"
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Document /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item v-if="registerForm.role === 'TEACHER'" prop="department">
          <el-input 
            v-model="registerForm.department" 
            placeholder="请输入院系"
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><OfficeBuilding /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item v-if="registerForm.role === 'TEACHER'" prop="major">
          <el-input 
            v-model="registerForm.major" 
            placeholder="请输入专业"
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Document /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item v-if="registerForm.role === 'TEACHER'" prop="maxStudents">
          <el-input-number 
            v-model="registerForm.maxStudents" 
            :min="1"
            :max="20"
            size="large"
            class="custom-input"
            placeholder="请输入最大指导学生数"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="register" 
            class="register-button"
            size="large"
            :loading="loading"
          >
            立即注册
          </el-button>
        </el-form-item>
        
        <div class="login-link">
          <span>已有账号？</span>
          <router-link to="/login" class="link-text">返回登录</router-link>
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
import { User, Edit, Lock, OfficeBuilding, Document, School, Teacher } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref(null)
const loading = ref(false)

const roleOptions = [
  { value: 'STUDENT', label: '学生', icon: School },
  { value: 'TEACHER', label: '教师', icon: Teacher }
]

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
      loading.value = true
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
      loading.value = false
      if (result && !result.error) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } else {
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
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  pointer-events: none;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.register-card {
  width: 100%;
  max-width: 500px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  padding: 48px 40px;
  animation: slideUp 0.6s ease-out;
  position: relative;
  z-index: 1;
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

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.register-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}

.register-header p {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
}

.register-form {
  margin-top: 32px;
}

.role-selector {
  display: flex;
  gap: 12px;
}

.role-option {
  flex: 1;
  padding: 16px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.role-option:hover {
  border-color: #667eea;
  background: #f8f7ff;
}

.role-option.active {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.role-option .el-icon {
  color: #9ca3af;
  transition: color 0.3s ease;
}

.role-option.active .el-icon {
  color: #667eea;
}

.role-option span {
  font-size: 14px;
  font-weight: 600;
  color: #4b5563;
  transition: color 0.3s ease;
}

.role-option.active span {
  color: #667eea;
}

.custom-input {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 8px 16px;
  box-shadow: 0 0 0 1px #e5e7eb inset;
  transition: all 0.3s ease;
  background: white;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea inset;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.3) inset;
}

.custom-input :deep(.el-input__prefix) {
  color: #9ca3af;
}

.custom-input :deep(.el-input__inner) {
  font-size: 15px;
  color: #1f2937;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: #9ca3af;
}

.register-button {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  margin-top: 8px;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.5);
}

.register-button:active {
  transform: translateY(0);
}

.login-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #6b7280;
}

.link-text {
  color: #667eea;
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
  transition: color 0.3s ease;
}

.link-text:hover {
  color: #764ba2;
  text-decoration: underline;
}

@media (max-width: 640px) {
  .register-card {
    padding: 32px 24px;
    border-radius: 20px;
  }
  
  .register-header h1 {
    font-size: 24px;
  }
  
  .logo-icon {
    width: 70px;
    height: 70px;
  }
}
</style>
