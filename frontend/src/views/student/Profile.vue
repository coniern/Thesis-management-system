<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </div>
      </template>
      
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        
        <el-form-item label="院系" prop="department">
          <el-input v-model="form.department" placeholder="请输入院系" />
        </el-form-item>
        
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入专业" />
        </el-form-item>
        
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系方式" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存修改</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../../stores'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import axios from 'axios'

const userStore = useUserStore()
const formRef = ref(null)

const form = reactive({
  userId: userStore.user?.id,
  username: userStore.user?.username || '',
  name: userStore.user?.name || '',
  department: userStore.user?.department || '',
  major: userStore.user?.major || '',
  contact: userStore.user?.contact || ''
})

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入院系', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    const token = localStorage.getItem('token')
    await axios.put('http://localhost:8082/api/system/user/profile', form, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    
    ElMessage.success('个人信息更新成功')
    
    userStore.user.name = form.name
    userStore.user.department = form.department
    userStore.user.major = form.major
    userStore.user.contact = form.contact
    localStorage.setItem('user', JSON.stringify(userStore.user))
    
  } catch (error) {
    if (error !== false) {
      console.error('Update profile failed:', error)
      ElMessage.error('更新失败，请重试')
    }
  }
}

const handleReset = () => {
  form.name = userStore.user?.name || ''
  form.department = userStore.user?.department || ''
  form.major = userStore.user?.major || ''
  form.contact = userStore.user?.contact || ''
}

onMounted(() => {
  if (userStore.user) {
    form.userId = userStore.user.id
    form.username = userStore.user.username || ''
    form.name = userStore.user.name || ''
    form.department = userStore.user.department || ''
    form.major = userStore.user.major || ''
    form.contact = userStore.user.contact || ''
  }
})
</script>

<style scoped>
.profile-container {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #E6A23C;
}

.card-header .el-icon {
  font-size: 20px;
}
</style>
