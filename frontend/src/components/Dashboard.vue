<template>
  <div class="dashboard-container">
    <el-container>
      <!-- 头部 -->
      <el-header>
        <h2 style="margin-bottom: 20px">论坛仪表盘</h2>
        <div class="header-right">
          <span class="user-role">角色：{{ userRole }}</span>
          <el-button type="danger" @click="handleLogout" style="margin-bottom:8px">登出</el-button>
        </div>
      </el-header>

      <!-- 主体内容 -->
      <el-main>
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <h3>欢迎来到论坛系统！</h3>
          <p>当前用户：<strong>{{ username || '未知用户' }}</strong></p>
          <p>身份：<strong>{{ userRole }}</strong></p>
        </el-card>

        <!-- 论坛列表 -->
        <h3>论坛板块</h3>
        <el-row :gutter="20">
          <el-col :span="6" v-for="forum in forums" :key="forum.forumId">
            <el-card shadow="hover" @click="goToForum(forum.forumId)" class="forum-card">
              <div class="forum-item">
                <h4 class="forum-name">{{ forum.forumName }}</h4>
                <p class="desc">{{ forum.forumDesc }}</p>
                <el-button type="primary" size="small" plain>进入</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()

// 用户信息
const username = ref('')
const userRole = ref('')

// 论坛列表
const forums = ref([])

// 初始化数据
onMounted(async () => {
  const token = localStorage.getItem('token')
  const savedUsername = localStorage.getItem('username')

  if (!token) {
    router.push('/login')
    return
  }

  // 设置用户名
  username.value = savedUsername || '未知用户'

  // 获取用户角色
  try {
    const roleRes = await request.get('/api/user/role')
    userRole.value = roleRes.data || 'normal user'
  } catch (error) {
    console.warn('获取用户角色失败', error)
    userRole.value = 'normal user'
  }

  // 获取论坛列表
  try {
    const forumRes = await request.get('/api/forum/list')
    console.log(forumRes)
    forums.value = forumRes.data || []
  } catch (error) {
    console.error('获取论坛列表失败', error)
    ElMessage.error('加载论坛失败')
  }
})

// 跳转到指定论坛
const goToForum = (forumId) => {
  router.push(`/forum/${forumId}`)
}

// 登出处理
const handleLogout = async () => {
  try {
    await request.post('/api/auth/logout')
  } catch (error) {
    console.warn('登出请求失败，但仍清除本地状态', error)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    ElMessage.success('已成功登出')
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

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-role {
  font-size: 14px;
  background-color: rgba(255, 255, 255, 0.2);
  padding: 4px 8px;
  border-radius: 4px;
}

.el-main {
  background-color: #f5f5f5;
  padding: 20px;
}

/* 统一论坛卡片大小 */
.forum-card {
  min-height: 180px; /* 固定最小高度 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.forum-card:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.forum-item {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: center;
}

.forum-name {
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-wrap: break-word;
}

.desc {
  font-size: 14px;
  color: #666;
  margin: 8px 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-wrap: break-word;
}

/* 按钮居中 */
.el-button {
  margin-top: 8px;
}
</style>