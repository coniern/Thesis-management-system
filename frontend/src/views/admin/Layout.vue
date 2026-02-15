<template>
  <div class="admin-layout">
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-button class="menu-toggle" @click="toggleSidebar" circle>
            <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </el-button>
          <h1 class="header-title">毕业论文管理系统 - 管理员</h1>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span class="user-name">{{ userStore.user?.name || '管理员' }}</span>
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
        <el-aside class="admin-sidebar" :class="{ 'is-collapse': isCollapse }" :width="isCollapse ? '64px' : '220px'">
          <div class="sidebar-overlay" @click="closeSidebar" v-if="isMobile && isCollapse === false"></div>
          <el-menu
            :default-active="activeMenu"
            class="admin-menu"
            :collapse="isCollapse"
            :collapse-transition="false"
            router
            :unique-opened="true"
          >
            <el-menu-item index="/admin/dashboard">
              <el-icon><house /></el-icon>
              <template #title>仪表盘</template>
            </el-menu-item>
            <el-menu-item index="/admin/users">
              <el-icon><user /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            <el-menu-item index="/admin/settings">
              <el-icon><setting /></el-icon>
              <template #title>系统设置</template>
            </el-menu-item>
            <el-menu-item index="/admin/notifications">
              <el-icon><bell /></el-icon>
              <template #title>通知管理</template>
            </el-menu-item>
            <el-menu-item index="/admin/selections">
              <el-icon><select /></el-icon>
              <template #title>师生互选管理</template>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="admin-main">
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
import { ArrowDown, House, User, Setting, Bell, Select, Fold, Expand, SwitchButton } from '@element-plus/icons-vue'

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
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.admin-layout .el-container {
  height: calc(100vh - 60px);
}

.admin-header {
  height: 60px;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.25);
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

.admin-sidebar {
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

.admin-menu {
  height: 100%;
  border-right: none;
  background: transparent;
}

.admin-menu:not(.el-menu--collapse) {
  padding: 12px 0;
}

.admin-menu .el-menu-item {
  margin: 4px 12px;
  border-radius: 8px;
  height: 48px;
  line-height: 48px;
  font-weight: 500;
}

.admin-menu .el-menu-item:hover {
  background: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.admin-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  color: white;
}

.admin-menu .el-menu-item .el-icon {
  font-size: 18px;
}

.admin-main {
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
  .admin-header {
    padding: 0 16px;
  }

  .header-title {
    font-size: 14px;
  }

  .user-name {
    display: none;
  }

  .admin-sidebar {
    position: fixed;
    left: 0;
    top: 60px;
    bottom: 0;
    z-index: 10;
    box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);
  }

  .admin-sidebar.is-collapse {
    transform: translateX(-100%);
    width: 220px;
  }

  .admin-sidebar:not(.is-collapse) {
    transform: translateX(0);
    width: 220px;
  }

  .admin-main {
    padding: 16px;
  }
}
</style>
