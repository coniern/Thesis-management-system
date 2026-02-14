<template>
  <div class="teacher-layout">
    <el-container>
      <el-header class="teacher-header">
        <div class="header-left">
          <h1>毕业论文管理系统 - 教师</h1>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              {{ userStore.user?.name || '教师' }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside class="teacher-sidebar">
          <el-menu
            :default-active="activeMenu"
            class="teacher-menu"
            router
            :unique-opened="true"
          >
            <el-menu-item index="/teacher/dashboard">
              <el-icon><house /></el-icon>
              <span>仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/teacher/students">
              <el-icon><user /></el-icon>
              <span>学生管理</span>
            </el-menu-item>
            <el-menu-item index="/teacher/topics">
              <el-icon><document /></el-icon>
              <span>选题管理</span>
            </el-menu-item>
            <el-menu-item index="/teacher/tasks">
              <el-icon><check /></el-icon>
              <span>任务书管理</span>
            </el-menu-item>
            <el-menu-item index="/teacher/reports">
              <el-icon><document-checked /></el-icon>
              <span>报告管理</span>
            </el-menu-item>
            <el-menu-item index="/teacher/papers">
              <el-icon><reading /></el-icon>
              <span>论文管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="teacher-main">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores'
import { ElMessage } from 'element-plus'
import { ArrowDown, House, User, Document, Check, DocumentChecked, Reading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => {
  return route.path
})

const logout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}
</script>

<style scoped>
.teacher-layout {
  height: 100vh;
  overflow: hidden;
}

.teacher-header {
  height: 60px;
  background-color: #67C23A;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h1 {
  font-size: 18px;
  margin: 0;
}

.user-info {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.teacher-sidebar {
  width: 200px;
  background-color: #f0f2f5;
  border-right: 1px solid #e6e6e6;
}

.teacher-menu {
  height: 100%;
}

.teacher-main {
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
