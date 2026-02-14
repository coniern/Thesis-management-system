<template>
  <div class="admin-settings">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
        </div>
      </template>
      <el-form :model="settings" label-width="120px">
        <el-form-item label="系统名称">
          <el-input v-model="settings.systemName" placeholder="请输入系统名称" />
        </el-form-item>
        <el-form-item label="系统版本">
          <el-input v-model="settings.systemVersion" placeholder="请输入系统版本" />
        </el-form-item>
        <el-form-item label="最大学生数">
          <el-input-number v-model="settings.maxStudents" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="论文提交截止日期">
          <el-date-picker
            v-model="settings.paperDeadline"
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
          <el-button @click="resetSettings">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'AdminSettings',
  data() {
    return {
      settings: {
        systemName: '毕业论文管理系统',
        systemVersion: '1.0.0',
        maxStudents: 50,
        paperDeadline: '2026-06-30 23:59:59'
      }
    }
  },
  methods: {
    saveSettings() {
      this.$message.success('设置保存成功')
    },
    resetSettings() {
      this.settings = {
        systemName: '毕业论文管理系统',
        systemVersion: '1.0.0',
        maxStudents: 50,
        paperDeadline: '2026-06-30 23:59:59'
      }
      this.$message.info('已重置为默认设置')
    }
  }
}
</script>

<style scoped>
.admin-settings {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>