import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    token: localStorage.getItem('token') || '',
    isLoggedIn: !!localStorage.getItem('token'),
    userRole: localStorage.getItem('userRole') || ''
  }),
  actions: {
    async login(username, password) {
      try {
        const response = await axios.post('http://localhost:8082/api/auth/login', {
          username,
          password
        })
        
        const { token, user } = response.data
        this.token = token
        this.user = user
        this.isLoggedIn = true
        this.userRole = user.role ? user.role.toUpperCase() : ''
        
        localStorage.setItem('token', token)
        localStorage.setItem('user', JSON.stringify(user))
        localStorage.setItem('userRole', user.role ? user.role.toUpperCase() : '')
        localStorage.setItem('userId', user.id)
        
        return true
      } catch (error) {
        console.error('Login failed:', error)
        return false
      }
    },
    
    async register(userData) {
      try {
        const response = await axios.post('http://localhost:8082/api/auth/register', userData)
        return response.data
      } catch (error) {
        console.error('Registration failed:', error)
        // 捕获错误信息并返回，以便前端可以根据错误类型给出不同的提示
        return { error: error.response?.data?.message || error.message || '注册失败，请重试' }
      }
    },
    
    logout() {
      this.user = null
      this.token = ''
      this.isLoggedIn = false
      this.userRole = ''
      
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('userRole')
      localStorage.removeItem('userId')
    }
  }
})

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: []
  }),
  actions: {
    async fetchNotifications(userId) {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get('http://localhost:8082/api/system/user/notifications', {
          params: { userId },
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        this.notifications = response.data
      } catch (error) {
        console.error('Failed to fetch notifications:', error)
      }
    },
    
    async markAsRead(notificationId) {
      try {
        const token = localStorage.getItem('token')
        await axios.post('http://localhost:8082/api/system/user/notification/read', null, {
          params: { notificationId },
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        
        const index = this.notifications.findIndex(n => n.id === notificationId)
        if (index !== -1) {
          this.notifications[index].status = 'READ'
        }
      } catch (error) {
        console.error('Failed to mark notification as read:', error)
      }
    }
  }
})
