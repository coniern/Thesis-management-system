<template>
  <div class="admin-layout">
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <h1>毕业论文管理系统 - 管理员</h1>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              {{ userStore.user?.name || '管理员' }}
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
        <el-aside class="admin-sidebar">
          <el-menu
            :default-active="activeMenu"
            class="admin-menu"
            router
            :unique-opened="true"
          >
            <el-menu-item index="/admin/dashboard">
              <el-icon><house /></el-icon>
              <span>仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/admin/users">
              <el-icon><user /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/settings">
              <el-icon><setting /></el-icon>
              <span>系统设置</span>
            </el-menu-item>
            <el-menu-item index="/admin/notifications">
              <el-icon><bell /></el-icon>
              <span>通知管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/selections">
              <el-icon><select /></el-icon>
              <span>师生互选管理</span>
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores'
import { ElMessage } from 'element-plus'
import { ArrowDown, House, User, Setting, Bell, Select } from '@element-plus/icons-vue'

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
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.admin-header {
  height: 60px;
  background-color: #409EFF;
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

.admin-sidebar {
  width: 200px;
  background-color: #f0f2f5;
  border-right: 1px solid #e6e6e6;
}

.admin-menu {
  height: 100%;
}

.admin-main {
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
