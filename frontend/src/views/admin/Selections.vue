<template>
  <div class="admin-selections">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>师生互选管理</span>
        </div>
      </template>
      <el-table :data="selections" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="teacherName" label="教师姓名" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="viewSelection(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="showApproveDialog(scope.row)">审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="师生互选详情"
      width="500px"
    >
      <el-form label-width="80px">
        <el-form-item label="选择ID">
          {{ selectedSelection.id }}
        </el-form-item>
        <el-form-item label="学生姓名">
          {{ selectedSelection.studentName }}
        </el-form-item>
        <el-form-item label="学生信息">
          {{ selectedSelection.studentInfo }}
        </el-form-item>
        <el-form-item label="教师姓名">
          {{ selectedSelection.teacherName }}
        </el-form-item>
        <el-form-item label="教师信息">
          {{ selectedSelection.teacherInfo }}
        </el-form-item>
        <el-form-item label="绑定状态">
          <el-tag :type="getStatusType(selectedSelection.status)">{{ selectedSelection.status }}</el-tag>
        </el-form-item>
        <el-form-item label="申请时间">
          {{ selectedSelection.createdAt }}
        </el-form-item>
        <el-form-item label="审批时间">
          {{ selectedSelection.approvedAt || '未审批' }}
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="approveDialogVisible"
      title="审批师生互选"
      width="400px"
    >
      <el-form label-width="80px">
        <el-form-item label="选择ID">
          {{ selectedSelection.id }}
        </el-form-item>
        <el-form-item label="学生姓名">
          {{ selectedSelection.studentName }}
        </el-form-item>
        <el-form-item label="教师姓名">
          {{ selectedSelection.teacherName }}
        </el-form-item>
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(selectedSelection.status)">{{ selectedSelection.status }}</el-tag>
        </el-form-item>
        <el-form-item label="审批状态" required>
          <el-radio-group v-model="approveStatus">
            <el-radio label="已批准">已批准</el-radio>
            <el-radio label="已拒绝">已拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input
            type="textarea"
            v-model="approveComment"
            placeholder="请输入审批意见"
            rows="3"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="approveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApproval">提交审批</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AdminSelections',
  data() {
    return {
      selections: [],
      viewDialogVisible: false,
      approveDialogVisible: false,
      selectedSelection: {},
      approveStatus: '',
      approveComment: ''
    }
  },
  mounted() {
    this.loadSelections()
  },
  methods: {
    loadSelections() {
      // 模拟数据
      this.selections = [
        {
          id: 1,
          studentName: '李学生',
          studentInfo: '计算机科学与技术专业，2020级',
          teacherName: '张老师',
          teacherInfo: '计算机科学与技术系，教授',
          status: '待审批',
          createdAt: '2026-01-01 00:00:00',
          approvedAt: null
        },
        {
          id: 2,
          studentName: '王学生',
          studentInfo: '软件工程专业，2020级',
          teacherName: '李老师',
          teacherInfo: '软件工程系，副教授',
          status: '已批准',
          createdAt: '2026-01-02 00:00:00',
          approvedAt: '2026-01-03 10:00:00'
        }
      ]
    },
    viewSelection(selection) {
      this.selectedSelection = { ...selection }
      this.viewDialogVisible = true
    },
    showApproveDialog(selection) {
      this.selectedSelection = { ...selection }
      this.approveStatus = selection.status === '待审批' ? '已批准' : selection.status
      this.approveComment = ''
      this.approveDialogVisible = true
    },
    async submitApproval() {
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 更新本地数据
        const index = this.selections.findIndex(item => item.id === this.selectedSelection.id)
        if (index !== -1) {
          this.selections[index].status = this.approveStatus
          this.selections[index].approvedAt = new Date().toLocaleString()
        }
        
        this.$message.success('审批成功')
        this.approveDialogVisible = false
      } catch (error) {
        console.error('审批失败:', error)
        this.$message.error('审批失败，请重试')
      }
    },
    getStatusType(status) {
      switch (status) {
        case '已批准':
          return 'success'
        case '已拒绝':
          return 'danger'
        case '待审批':
          return 'warning'
        default:
          return ''
      }
    }
  }
}
</script>

<style scoped>
.admin-selections {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>