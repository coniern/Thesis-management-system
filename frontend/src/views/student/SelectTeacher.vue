<template>
  <div class="student-select-teacher">
    <el-card shadow="hover" class="filter-card">
      <template #header>
        <div class="card-header">
          <span>筛选条件</span>
          <el-button size="small" type="info" @click="goToApplicationStatus">查看申请状态</el-button>
        </div>
      </template>
      <div class="filter-form">
        <el-form :model="filterForm" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="所属院系">
                <el-select v-model="filterForm.department" placeholder="请选择院系" @change="handleDepartmentChange">
                  <el-option label="金融与贸易学院" value="金融与贸易学院" />
                  <el-option label="会计学院" value="会计学院" />
                  <el-option label="计算机与数学学院" value="计算机与数学学院" />
                  <el-option label="投资与保险学院" value="投资与保险学院" />
                  <el-option label="商务英语学院" value="商务英语学院" />
                  <el-option label="管理学院" value="管理学院" />
                  <el-option label="文法学院" value="文法学院" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="专业方向">
                <el-select v-model="filterForm.major" placeholder="请选择专业方向">
                  <el-option 
                    v-for="major in availableMajors" 
                    :key="major" 
                    :label="major" 
                    :value="major" 
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24" class="filter-buttons">
              <el-button type="primary" @click="loadTeachers">查询</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-card>

    <el-card shadow="hover" class="teachers-card">
      <template #header>
        <div class="card-header">
          <span>选择导师</span>
        </div>
      </template>
      <el-table :data="teachers" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="教师姓名" />
        <el-table-column prop="department" label="所属院系" />
        <el-table-column prop="major" label="专业" />
        <el-table-column prop="maxStudents" label="最大学生数" width="120" />
        <el-table-column prop="currentStudents" label="当前学生数" width="120" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="primary" @click="selectTeacher(scope.row)">申请</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="teachers.length === 0" class="empty-teachers">
        <el-empty description="暂无符合条件的教师" />
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'StudentSelectTeacher',
  data() {
    return {
      teachers: [],
      filterForm: {
        department: '',
        major: ''
      },
      isBound: false, // 是否已绑定老师
      boundTeacher: null, // 已绑定的老师信息
      hasPendingApplication: false, // 是否有等待审批的申请
      pendingTeacher: null, // 等待审批的老师信息
      // 院系专业对应关系
      departmentMajors: {
        '金融与贸易学院': [
          '金融学',
          '国际经济与贸易',
          '信用管理',
          '金融工程',
          '经济学',
          '数字经济'
        ],
        '会计学院': [
          '会计学',
          '财务管理',
          '审计学',
          '资产评估'
        ],
        '计算机与数学学院': [
          '金融科技',
          '计算机科学与技术',
          '软件工程',
          '电子商务',
          '数据计算及应用'
        ],
        '投资与保险学院': [
          '投资学',
          '保险学'
        ],
        '商务英语学院': [
          '商务英语'
        ],
        '管理学院': [
          '市场营销',
          '人力资源管理',
          '税收学',
          '公共事业管理',
          '体育经济与管理'
        ],
        '文法学院': [
          '知识产权',
          '信用风险管理与法律防控',
          '劳动与社会保障'
        ]
      }
    }
  },
  computed: {
    // 根据选定的院系获取可用专业
    availableMajors() {
      if (!this.filterForm.department) {
        return []
      }
      return this.departmentMajors[this.filterForm.department] || []
    }
  },
  mounted() {
    this.checkBindingStatus()
    this.loadTeachers()
  },
  methods: {
    // 检查绑定状态
    checkBindingStatus() {
      const token = localStorage.getItem('token')
      const studentId = localStorage.getItem('userId')
      
      axios.get('http://localhost:8082/api/student/selections', {
        params: { studentId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        // 检查是否有已接受的申请
        const boundSelection = response.data.find(item => item.status === 'ACCEPTED' || item.status === 'APPROVED')
        if (boundSelection) {
          this.isBound = true
          this.boundTeacher = {
            id: boundSelection.teacherId,
            name: boundSelection.teacherName || '未知教师',
            major: boundSelection.teacherMajor || '未知专业'
          }
        } else {
          this.isBound = false
          this.boundTeacher = null
        }
        
        // 检查是否有等待审批的申请
        const pendingSelection = response.data.find(item => item.status === 'PENDING')
        if (pendingSelection) {
          this.hasPendingApplication = true
          this.pendingTeacher = {
            id: pendingSelection.teacherId,
            name: pendingSelection.teacherName || '未知教师',
            major: pendingSelection.teacherMajor || '未知专业'
          }
        } else {
          this.hasPendingApplication = false
          this.pendingTeacher = null
        }
      })
      .catch(error => {
        console.error('Failed to check binding status:', error)
      })
    },
    loadTeachers() {
      const token = localStorage.getItem('token')
      
      // 构建请求参数
      const params = {}
      if (this.filterForm.department) {
        params.department = this.filterForm.department
      }
      if (this.filterForm.major) {
        params.major = this.filterForm.major
      }
      
      axios.get('http://localhost:8082/api/system/teachers', {
        params,
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.teachers = response.data
      })
      .catch(error => {
        console.error('Failed to load teachers:', error)
        this.$message.error('加载教师列表失败')
        
        // 使用模拟数据作为备用
        this.teachers = [
          { id: 1, name: '张老师', department: '金融与贸易学院', major: '金融学', maxStudents: 5, currentStudents: 2 },
          { id: 2, name: '李老师', department: '会计学院', major: '会计学', maxStudents: 4, currentStudents: 1 },
          { id: 3, name: '王老师', department: '计算机与数学学院', major: '金融科技', maxStudents: 6, currentStudents: 3 },
          { id: 4, name: '赵老师', department: '投资与保险学院', major: '投资学', maxStudents: 5, currentStudents: 2 },
          { id: 5, name: '刘老师', department: '管理学院', major: '市场营销', maxStudents: 4, currentStudents: 1 }
        ]
      })
    },
    resetFilter() {
      this.filterForm = {
        department: '',
        major: ''
      }
      this.loadTeachers()
    },
    handleDepartmentChange() {
      // 当院系变化时，重置专业选择
      this.filterForm.major = ''
    },
    selectTeacher(teacher) {
      // 检查是否已经绑定了老师
      if (this.isBound) {
        this.$message.warning(`您已经绑定了 ${this.boundTeacher.name} 老师，如果需要选择其他老师，请先申请解除绑定`)
        return
      }
      
      // 检查是否有等待审批的申请
      if (this.hasPendingApplication) {
        this.$message.warning(`您已经申请了 ${this.pendingTeacher.name} 老师，正在等待审批。请等待老师审批完成后再申请其他老师`)
        return
      }
      
      this.$confirm('确定申请 ' + teacher.name + ' 作为导师吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        const token = localStorage.getItem('token')
        const userId = localStorage.getItem('userId')
        
        axios.post('http://localhost:8082/api/student/select-teacher', {
          studentId: userId,
          teacherId: teacher.id
        }, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        .then(response => {
          this.$message.success('申请成功，等待审批')
          // 申请成功后跳转到申请状态页面
          this.goToApplicationStatus()
        })
        .catch(error => {
          console.error('Failed to submit selection:', error)
          this.$message.error('申请失败，请重试')
        })
      }).catch(() => {
        this.$message.info('已取消申请')
      })
    },
    goToApplicationStatus() {
      this.$router.push('/student/application-status')
    }
  }
}
</script>

<style scoped>
.student-select-teacher {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  padding: 0 20px;
}

.filter-buttons {
  text-align: right;
  margin-top: 10px;
}

.teachers-card {
  min-height: 400px;
}

.empty-teachers {
  padding: 40px 0;
}
</style>