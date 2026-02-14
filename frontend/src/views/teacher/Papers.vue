<template>
  <div class="teacher-papers">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>论文管理</span>
        </div>
      </template>
      <el-table :data="papers" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="paperTitle" label="论文标题" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="80" v-if="papers.some(p => p.score)">
          <template #default="scope">
            <el-tag v-if="scope.row.score" type="success">{{ scope.row.score }}分</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewPaper(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status === 'PENDING'" size="small" type="primary" @click="gradePaper(scope.row)">评分</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="viewDialogVisible"
      title="论文详情"
      width="70%"
    >
      <el-descriptions :column="1" border v-if="currentPaper">
        <el-descriptions-item label="论文标题">{{ currentPaper.paperTitle }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentPaper.studentName }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatDate(currentPaper.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentPaper.status)">{{ getStatusText(currentPaper.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="导师意见" v-if="currentPaper.teacherComment">
          {{ currentPaper.teacherComment }}
        </el-descriptions-item>
        <el-descriptions-item label="评分" v-if="currentPaper.score">
          <el-tag type="success">{{ currentPaper.score }}分</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="论文摘要">
          <div style="white-space: pre-wrap">{{ currentPaper.paperAbstract }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="附件" v-if="currentPaper.fileId">
          <el-button type="primary" @click="downloadFile(currentPaper.fileId)">下载附件</el-button>
        </el-descriptions-item>
      </el-descriptions>
      <div v-if="currentPaper && currentPaper.status === 'PENDING'" style="margin-top: 20px">
        <el-form :model="gradeForm" label-width="100px">
          <el-form-item label="评分" required>
            <el-input-number v-model="gradeForm.score" :min="0" :max="100" :step="1" />
          </el-form-item>
          <el-form-item label="评语">
            <el-input
              v-model="gradeForm.comment"
              type="textarea"
              :rows="4"
              placeholder="请输入评语"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="confirmGrade">提交评分</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'TeacherPapers',
  data() {
    return {
      papers: [],
      viewDialogVisible: false,
      currentPaper: null,
      gradeForm: {
        score: null,
        comment: ''
      }
    }
  },
  mounted() {
    this.loadPapers()
  },
  methods: {
    loadPapers() {
      const token = localStorage.getItem('token')
      const teacherId = parseInt(localStorage.getItem('userId'))

      axios.get('http://localhost:8082/api/thesis/teacher/papers', {
        params: { teacherId },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(async response => {
        this.papers = await this.loadStudentNames(response.data, token)
      })
      .catch(error => {
        console.error('Failed to load papers:', error)
        this.$message.error('加载论文列表失败，请重试')
      })
    },
    async loadStudentNames(papers, token) {
      const promises = papers.map(paper => {
        return axios.get('http://localhost:8082/api/system/user', {
          params: { userId: paper.studentId },
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        .then(userResponse => {
          return {
            ...paper,
            studentName: userResponse.data.name || '未知学生'
          }
        })
        .catch(() => {
          return {
            ...paper,
            studentName: '未知学生'
          }
        })
      })

      return Promise.all(promises)
    },
    viewPaper(paper) {
      this.currentPaper = paper
      this.gradeForm.score = null
      this.gradeForm.comment = ''
      this.viewDialogVisible = true
    },
    gradePaper(paper) {
      this.currentPaper = paper
      this.gradeForm.score = null
      this.gradeForm.comment = ''
      this.viewDialogVisible = true
    },
    confirmGrade() {
      if (!this.gradeForm.score) {
        this.$message.warning('请输入评分')
        return
      }

      const token = localStorage.getItem('token')
      const paperId = this.currentPaper.id

      axios.post('http://localhost:8082/api/thesis/teacher/grade-paper', null, {
        params: {
          paperId: paperId,
          score: this.gradeForm.score,
          comment: this.gradeForm.comment
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(response => {
        this.$message.success('评分成功')
        this.viewDialogVisible = false
        this.loadPapers()
      })
      .catch(error => {
        console.error('Failed to grade paper:', error)
        this.$message.error('评分失败，请重试')
      })
    },
    downloadFile(fileId) {
      const token = localStorage.getItem('token')
      const downloadUrl = `http://localhost:8082/api/file/${fileId}?token=${token}`

      const link = document.createElement('a')
      link.href = downloadUrl
      link.target = '_self'
      link.download = ''
      link.style.display = 'none'

      document.body.appendChild(link)
      link.click()

      setTimeout(() => {
        document.body.removeChild(link)
      }, 100)
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString('zh-CN')
    },
    getStatusText(status) {
      const statusMap = {
        'PENDING': '待评分',
        'GRADED': '已评分'
      }
      return statusMap[status] || status
    },
    getStatusType(status) {
      const typeMap = {
        'PENDING': 'warning',
        'GRADED': 'success'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>

<style scoped>
.teacher-papers {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
