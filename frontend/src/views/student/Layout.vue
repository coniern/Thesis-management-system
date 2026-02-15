<template>
  <div class="student-layout">
    <el-container>
      <el-header class="student-header">
        <div class="header-left">
          <el-button class="menu-toggle" @click="toggleSidebar" circle>
            <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </el-button>
          <h1 class="header-title">毕业论文管理系统 - 学生</h1>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span class="user-name">{{ userStore.user?.name || '学生' }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside class="student-sidebar" :class="{ 'is-collapse': isCollapse }" :width="isCollapse ? '64px' : '220px'">
          <div class="sidebar-overlay" @click="closeSidebar" v-if="isMobile && isCollapse === false"></div>
          <el-menu
            :default-active="activeMenu"
            class="student-menu"
            :collapse="isCollapse"
            :collapse-transition="false"
            router
            :unique-opened="true"
          >
            <el-menu-item index="/student/dashboard">
              <el-icon><house /></el-icon>
              <template #title>仪表盘</template>
            </el-menu-item>
            <el-menu-item index="/student/select-teacher">
              <el-icon><select /></el-icon>
              <template #title>选择导师</template>
            </el-menu-item>
            <el-menu-item index="/student/application-status">
              <el-icon><timer /></el-icon>
              <template #title>申请状态</template>
            </el-menu-item>
            <el-menu-item index="/student/topic">
              <el-icon><document /></el-icon>
              <template #title>论文选题</template>
            </el-menu-item>
            <el-menu-item index="/student/task">
              <el-icon><check /></el-icon>
              <template #title>任务书</template>
            </el-menu-item>
            <el-menu-item index="/student/proposal">
              <el-icon><document-checked /></el-icon>
              <template #title>开题报告</template>
            </el-menu-item>
            <el-menu-item index="/student/midterm">
              <el-icon><timer /></el-icon>
              <template #title>中期检查</template>
            </el-menu-item>
            <el-menu-item index="/student/paper">
              <el-icon><reading /></el-icon>
              <template #title>最终论文</template>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="student-main">
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
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores'
import { ElMessage } from 'element-plus'
import { ArrowDown, House, Select, Document, Check, DocumentChecked, Timer, Reading, Fold, Expand, User, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const isMobile = ref(false)

const activeMenu = computed(() => {
  return route.path
})

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
  if (isMobile.value) {
    isCollapse.value = true
  }
}

const toggleSidebar = () => {
  if (isMobile.value) {
    isCollapse.value = !isCollapse.value
  } else {
    isCollapse.value = !isCollapse.value
  }
}

const closeSidebar = () => {
  if (isMobile.value) {
    isCollapse.value = true
  }
}

const logout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.student-layout {
  height: 100vh;
  overflow: hidden;
}

.student-layout .el-container {
  height: calc(100vh - 60px);
}

.student-header {
  height: 60px;
  background: linear-gradient(135deg, #E6A23C 0%, #f0ad4e 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 12px 0 rgba(230, 162, 60, 0.25);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-toggle {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  margin-right: 8px;
}

.menu-toggle:hover {
  background: rgba(255, 255, 255, 0.3);
  color: white;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  letter-spacing: 0.5px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.25);
}

.user-name {
  font-weight: 500;
}

.student-sidebar {
  background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
  border-right: 1px solid #e9ecef;
  height: 100%;
  transition: width 0.3s ease;
  position: relative;
}

.sidebar-overlay {
  position: fixed;
  top: 60px;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 9;
}

.student-menu {
  height: 100%;
  border-right: none;
  background: transparent;
}

.student-menu:not(.el-menu--collapse) {
  padding: 12px 0;
}

.student-menu .el-menu-item {
  margin: 4px 12px;
  border-radius: 8px;
  height: 48px;
  line-height: 48px;
  font-weight: 500;
}

.student-menu .el-menu-item:hover {
  background: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.student-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #E6A23C 0%, #f0ad4e 100%);
  color: white;
}

.student-menu .el-menu-item .el-icon {
  font-size: 18px;
}

.student-main {
  padding: 24px;
  overflow-y: auto;
  height: 100%;
  background-color: #f5f7fa;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .student-header {
    padding: 0 16px;
  }

  .header-title {
    font-size: 14px;
  }

  .user-name {
    display: none;
  }

  .student-sidebar {
    position: fixed;
    left: 0;
    top: 60px;
    bottom: 0;
    z-index: 10;
    box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);
  }

  .student-sidebar.is-collapse {
    transform: translateX(-100%);
    width: 220px;
  }

  .student-sidebar:not(.is-collapse) {
    transform: translateX(0);
    width: 220px;
  }

  .student-main {
    padding: 16px;
  }
}
</style>
