import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/Layout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue')
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('../views/admin/Settings.vue')
      },
      {
        path: 'notifications',
        name: 'AdminNotifications',
        component: () => import('../views/admin/Notifications.vue')
      },
      {
        path: 'selections',
        name: 'AdminSelections',
        component: () => import('../views/admin/Selections.vue')
      }
    ]
  },
  {
    path: '/teacher',
    name: 'Teacher',
    component: () => import('../views/teacher/Layout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: () => import('../views/teacher/Dashboard.vue')
      },
      {
        path: 'students',
        name: 'TeacherStudents',
        component: () => import('../views/teacher/Students.vue')
      },
      {
        path: 'topics',
        name: 'TeacherTopics',
        component: () => import('../views/teacher/Topics.vue')
      },
      {
        path: 'tasks',
        name: 'TeacherTasks',
        component: () => import('../views/teacher/Tasks.vue')
      },
      {
        path: 'reports',
        name: 'TeacherReports',
        component: () => import('../views/teacher/Reports.vue')
      },
      {
        path: 'papers',
        name: 'TeacherPapers',
        component: () => import('../views/teacher/Papers.vue')
      }
    ]
  },
  {
    path: '/student',
    name: 'Student',
    component: () => import('../views/student/Layout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'StudentDashboard',
        component: () => import('../views/student/Dashboard.vue')
      },
      {
        path: 'select-teacher',
        name: 'StudentSelectTeacher',
        component: () => import('../views/student/SelectTeacher.vue')
      },
      {
        path: 'application-status',
        name: 'StudentApplicationStatus',
        component: () => import('../views/student/ApplicationStatus.vue')
      },
      {
        path: 'topic',
        name: 'StudentTopic',
        component: () => import('../views/student/Topic.vue')
      },
      {
        path: 'task',
        name: 'StudentTask',
        component: () => import('../views/student/Task.vue')
      },
      {
        path: 'proposal',
        name: 'StudentProposal',
        component: () => import('../views/student/Proposal.vue')
      },
      {
        path: 'midterm',
        name: 'StudentMidterm',
        component: () => import('../views/student/Midterm.vue')
      },
      {
        path: 'paper',
        name: 'StudentPaper',
        component: () => import('../views/student/Paper.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  
  if (to.path.startsWith('/admin') && (!token || userRole !== 'ADMIN')) {
    next('/login')
  } else if (to.path.startsWith('/teacher') && (!token || userRole !== 'TEACHER')) {
    next('/login')
  } else if (to.path.startsWith('/student') && (!token || userRole !== 'STUDENT')) {
    next('/login')
  } else {
    next()
  }
})

export default router
