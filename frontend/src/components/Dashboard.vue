<template>
  <div class="dashboard-container">
    <el-container>
      <el-header>
        <h2>仪表盘</h2>
        <el-button type="danger" @click="handleLogout">登出</el-button>
      </el-header>
      <el-main>
        <el-card>
          <p>欢迎来到仪表盘！</p>
          <p>当前用户：{{ username || '未知用户' }}</p>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  import { ElMessage } from 'element-plus'

  const router = useRouter()
  const username = ref('')

  onMounted(() => {
  const token = localStorage.getItem('token')
  const savedUsername = localStorage.getItem('username') // 👈 直接读取保存的用户名

  if (!token) {
    router.push('/login')
    return
  }

  username.value = savedUsername || '未知用户'
  })

  const handleLogout = async () => {
    try {
      const token = localStorage.getItem('token')
      if (token) {
        await axios.post('http://localhost:8080/api/auth/logout')
      }
  } catch (error) {
    console.warn('登出请求失败，但仍清除本地状态', error)
  } finally {
    // 👇 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    ElMessage.success('已登出')
    router.push('/login')
  }
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #409eff;
  color: white;
  padding: 0 20px;
}

.el-main {
  background-color: #f5f5f5;
  padding: 20px;
}
</style>