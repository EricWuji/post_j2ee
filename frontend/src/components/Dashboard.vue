<template>
  <div class="dashboard-container">
    <el-container>
      <!-- 头部 -->
      <el-header style="display: flex; justify-content: space-between; align-items: center; padding: 0 20px; height: 60px; background-color: #66b1ff">
        <h2 style="margin: 0; font-size: 20px; color: #303133;">论坛仪表盘</h2>
        <div class="header-right">
          <span class="user-role">角色：{{ userRole }}</span>
          <el-button type="danger" @click="handleLogout" size="small">登出</el-button>
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

      <!-- 用户搜索区域 -->
      <el-card shadow="hover" style="margin: 20px 10px; padding: 20px;">
        <h3>搜索用户</h3>
        <el-input
            v-model="searchUsername"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="searchUser"
            style="max-width: 300px; margin-bottom: 16px;"
        >
          <template #suffix>
            <el-button type="primary" :loading="searching" @click="searchUser" circle>
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>

        <!-- 搜索结果 -->
        <!-- 搜索结果 -->
        <div v-if="searching" style="padding: 20px; text-align: center;">
          <el-skeleton :rows="3" animated />
        </div>

        <div v-else-if="searchResult.length === 0" style="padding: 20px; text-align: center; color: #999;">
          <el-empty description="未找到匹配用户" />
        </div>

        <div v-else class="search-results-grid">
          <el-card
              v-for="(user, index) in searchResult"
              :key="index"
              shadow="hover"
              class="user-result-card"
          >
            <div class="user-info">
              <h4>👤 {{ user.username }}</h4>
              <p>🎯 角色：{{ user.role || '未知' }}</p>

              <div class="user-actions">
                <!-- 第一行：查看帖子 + 封禁用户 -->
                <div class="top-buttons">
                  <el-button type="primary" size="small" @click="viewUserPosts(user.username)">
                    查看TA的帖子
                  </el-button>
                  <el-button v-if="userRole === 'admin'" type="danger" size="small" @click.stop="showBanDialog(user)">
                    封禁用户
                  </el-button>
                </div>

                <!-- 第二行：解封用户 + 设置版主 -->
                <div class="bottom-buttons">
                  <el-button v-if="userRole === 'admin'" type="success" size="small" @click.stop="showUnbanDialog(user)" style="padding: 0px 23px">
                    解封用户
                  </el-button>
                  <el-button v-if="userRole === 'admin'" type="warning" size="small" @click.stop="showSetModeratorDialog(user)" style="margin-left: 15px">
                    设置版主
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </el-card>

      <!-- 封禁用户对话框 -->
      <el-dialog
          v-model="banDialogVisible"
          title="封禁用户"
          width="400px"
          :close-on-click-modal="false"
      >
        <div v-if="loadingAvailableForums" style="padding: 20px; text-align: center;">
          <el-skeleton :rows="3" animated />
        </div>

        <div v-else>
          <p>用户：<strong>{{ selectedUser?.username }}</strong></p>
          <p>请选择要封禁的板块：</p>
          <el-checkbox-group v-model="selectedForumIds">
            <div
                v-for="forum in availableForums"
                :key="forum.forumId"
                style="margin: 8px 0;"
            >
              <el-checkbox :label="forum.forumId" border>
                {{ forum.forumName }}
              </el-checkbox>
            </div>
          </el-checkbox-group>
        </div>

        <template #footer>
          <el-button @click="banDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmBan" :loading="false">
            确认封禁
          </el-button>
        </template>
      </el-dialog>

      <!-- 解封用户对话框 -->
      <el-dialog
          v-model="unbanDialogVisible"
          title="解封用户"
          width="400px"
          :close-on-click-modal="false"
      >
        <div v-if="loadingAvailableForums" style="padding: 20px; text-align: center;">
          <el-skeleton :rows="3" animated />
        </div>

        <div v-else>
          <p>用户：<strong>{{ selectedUserForUnban?.username }}</strong></p>
          <p>请选择要解封的板块：</p>
          <el-checkbox-group v-model="selectedForumIdsForUnban">
            <div
                v-for="forum in banForums"
                :key="forum.forumId"
                style="margin: 8px 0;"
            >
              <el-checkbox :label="forum.forumId" border>
                {{ forum.forumName }}
              </el-checkbox>
            </div>
          </el-checkbox-group>
        </div>

        <template #footer>
          <el-button @click="unbanDialogVisible = false">取消</el-button>
          <el-button type="success" @click="confirmUnban" :loading="false">
            确认解封
          </el-button>
        </template>
      </el-dialog>

      <!-- 设置版主对话框 -->
      <el-dialog
          v-model="moderatorDialogVisible"
          title="设置版主"
          width="400px"
          :close-on-click-modal="false"
      >
        <div v-if="loadingModeratorForums" style="padding: 20px; text-align: center;">
          <el-skeleton :rows="3" animated />
        </div>

        <div v-else>
          <p>用户：<strong>{{ selectedUserForModerator?.username }}</strong></p>
          <p>请选择要设置版主的板块：</p>
          <el-checkbox-group v-model="selectedModeratorForumIds">
            <div
                v-for="forum in availableModeratorForums"
                :key="forum.forumId"
                style="margin: 8px 0;"
            >
              <el-checkbox :label="forum.forumId" border>
                {{ forum.forumName }}
              </el-checkbox>
            </div>
          </el-checkbox-group>
        </div>

        <template #footer>
          <el-button @click="moderatorDialogVisible = false">取消</el-button>
          <el-button type="warning" @click="confirmSetModerator" :loading="false">
            确认设置
          </el-button>
        </template>
      </el-dialog>
      <!-- 我的帖子 -->
      <h3 style="margin-top: 40px;margin-left: 20px">我的帖子</h3>
      <el-card style="margin-left: 10px" shadow="hover" class="my-posts-card">
        <div v-if="postsLoading" style="padding: 30px; text-align: center;">
          <el-skeleton :rows="4" animated />
        </div>

        <div v-else-if="myPosts.length === 0" style="padding: 40px; text-align: center; color: #999;">
          <el-empty description="你还没有发布任何帖子">
            <el-button type="primary" size="small" @click="goToFirstForum">
              去发帖
            </el-button>
          </el-empty>
        </div>

        <div v-else class="posts-grid">
          <div
              v-for="post in myPosts"
              :key="post.postId"
              class="post-item"
              @click="viewPostDetail(post)"
          >
            <h4 v-if="post.deleted" class="post-title">{{ post.postTitle + "  已删除"}}</h4>
            <h4 v-else class="post-title">{{ post.postTitle }}</h4>
            <p class="post-content">
              {{ post.postContent.length > 80 ? post.postContent.substring(0, 80) + '...' : post.postContent }}
            </p>
            <div class="post-meta">
              <span class="forum-name">板块：{{ post.forumName || '未知' }}</span>
              <div class="actions" v-if="!post.deleted">
                <el-button type="text" size="small" @click.stop="viewPostDetail(post)" style="font-size: 15px;margin-bottom: 4px">
                  查看详情
                </el-button>
                <el-button type="danger" text size="small" @click.stop="deletePost(post.postId)" style="font-size: 15px;margin-bottom: 4px" v-if="!post.deleted">
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import { Search } from '@element-plus/icons-vue'

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
    await router.push('/login')
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

  await fetchMyPosts()
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
    await router.push('/login')
  }
}

// 我的帖子相关
const myPosts = ref([])
const postsLoading = ref(false)
// 用户搜索相关
const searchUsername = ref('')
const searchResult = ref([])
const searching = ref(false)
// 封禁相关
const availableForums = ref([]) // 可管理的板块
const banDialogVisible = ref(false) // 封禁对话框显隐
const selectedUser = ref(null) // 当前要封禁的用户
const selectedForumIds = ref([]) // 选中的板块ID
const loadingAvailableForums = ref(false)

const unbanDialogVisible = ref(false) // 解封对话框显隐
const selectedUserForUnban = ref(null) // 当前要解封的用户
const selectedForumIdsForUnban = ref([]) // 选中的板块ID（解封用）

const banForums = ref([])           // 用户被封禁的板块
const loadingBanForums = ref(false) // 加载状态

// 设置版主相关
const moderatorDialogVisible = ref(false)
const selectedUserForModerator = ref(null)
const availableModeratorForums = ref([]) // 可设置版主的板块
const selectedModeratorForumIds = ref([]) // 用户选中的板块ID
const loadingModeratorForums = ref(false)

// 获取我的帖子
const fetchMyPosts = async () => {
  postsLoading.value = true
  try {
    const res = await request.get('/api/post/lists')
    console.log(res)
    if (res.code === 200) {
      myPosts.value = Array.isArray(res.data) ? res.data : []
      console.log(myPosts.value)
    } else {
      ElMessage.warning(res.message || '获取我的帖子失败')
      myPosts.value = []
    }
  } catch (error) {
    ElMessage.error('获取我的帖子失败，请稍后重试')
    myPosts.value = []
    console.error('获取我的帖子异常:', error)
  } finally {
    postsLoading.value = false
  }
}

// 跳转到第一个论坛（用于“去发帖”）
const goToFirstForum = () => {
  if (forums.value.length > 0) {
    router.push(`/forum/${forums.value[0].forumId}`)
  } else {
    ElMessage.warning('暂无可用论坛')
  }
}

// 查看帖子详情
const viewPostDetail = (post) => {
  router.push({
    name: 'PostDetail',
    params: { postId: post.postId }
  })
}

// 删除帖子
const deletePost = async (postId) => {
  try {
    await ElMessageBox.confirm(
        '确定要删除这个帖子吗？删除后不可恢复。',
        '提示',
        {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          center: true
        }
    )

    const res = await request.delete('/api/post/delete', {
      params: {
        post_id: postId
      }
    })

    if (res.code === 200) {
      ElMessage.success('帖子删除成功！')
      // ✅ 自动刷新我的帖子列表
      await fetchMyPosts()
    } else {
      ElMessage.warning(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除帖子失败，请稍后重试')
      console.error('删除帖子异常:', error)
    }
  }
}

// 搜索用户
const searchUser = async () => {
  if (!searchUsername.value.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }

  searching.value = true
  searchResult.value = [] // ✅ 清空为数组

  try {
    const res = await request.get('/api/user/find', {
      params: {
        username: searchUsername.value.trim()
      }
    })

    if (res.code === 200 && Array.isArray(res.data)) {
      searchResult.value = res.data // ✅ 直接赋值数组
    } else {
      searchResult.value = []
      ElMessage.warning(res.message || '未找到匹配用户')
    }
  } catch (error) {
    searchResult.value = []
    ElMessage.error('搜索失败，请稍后重试')
    console.error('搜索用户异常:', error)
  } finally {
    searching.value = false
  }
}

// 查看该用户帖子（预留功能）
const viewUserPosts = (username) => {
  ElMessage.info('功能待开发：跳转到用户帖子列表页')
  console.log('查看用户帖子:', username)
}

// 获取当前管理员可管理的板块
const fetchAvailableForums = async (user) => {
  if (availableForums.value.length > 0) return // 已加载过则不再请求

  loadingAvailableForums.value = true
  try {
    const res = await request.get('/api/user/available', {
      params: { user_id: user?.accountId } // ✅ 使用传入的 user
    })
    console.log(res)
    if (res.code === 200 && Array.isArray(res.data)) {
      availableForums.value = res.data
    } else {
      availableForums.value = []
      ElMessage.warning('暂无可管理板块')
    }
  } catch (error) {
    availableForums.value = []
    ElMessage.error('获取可管理板块失败')
    console.error('获取可管理板块异常:', error)
  } finally {
    loadingAvailableForums.value = false
  }
}

// 获取用户当前被封禁的板块（用于解封）
const fetchBanForums = async (user) => {
  if (!user?.accountId) {
    ElMessage.warning('用户信息不完整')
    return
  }

  loadingAvailableForums.value = true // 复用加载状态，也可新增如 loadingBanForums
  try {
    const res = await request.get('/api/user/ban_forums', {
      params: {
        user_id: user.accountId // ✅ 注意：参数名是 user_id
      }
    })

    if (res.code === 200 && Array.isArray(res.data)) {
      banForums.value = res.data
      selectedForumIdsForUnban.value = res.data.map(f => f.forumId)
    } else {
      availableForums.value = []
      selectedForumIdsForUnban.value = []
      ElMessage.warning('该用户暂无被封禁的板块')
    }
  } catch (error) {
    availableForums.value = []
    selectedForumIdsForUnban.value = []
    ElMessage.error('获取被封禁板块失败')
    console.error('获取被封禁板块异常:', error)
  } finally {
    loadingAvailableForums.value = false
  }
}


// 显示封禁对话框
// 显示封禁对话框
const showBanDialog = async (user) => {
  if (userRole.value !== 'admin') {
    ElMessage.warning('只有管理员可以封禁用户')
    return
  }

  // 先获取可管理板块，传入当前用户
  await fetchAvailableForums(user) // ✅ 传入 user

  selectedUser.value = user
  selectedForumIds.value = [] // 清空上次选择
  banDialogVisible.value = true
}

// 显示解封对话框
// 显示解封对话框
const showUnbanDialog = async (user) => {
  if (userRole.value !== 'admin') {
    ElMessage.warning('只有管理员可以解封用户')
    return
  }

  // ✅ 改为获取用户当前被封禁的板块
  await fetchBanForums(user)

  selectedUserForUnban.value = user
  // selectedForumIdsForUnban 已在 fetchBanForums 中设置
  unbanDialogVisible.value = true
}

// 执行封禁
const confirmBan = async () => {
  if (selectedForumIds.value.length === 0) {
    ElMessage.warning('请选择至少一个板块')
    return
  }

  try {
    const res = await request.post('/api/user/ban', {
      userId: selectedUser.value.accountId,
      forumId: selectedForumIds.value
    })

    if (res.code === 200) {
      ElMessage.success('用户封禁成功！')
      banDialogVisible.value = false
      selectedUser.value = null
      selectedForumIds.value = []
    } else {
      ElMessage.warning(res.message || '封禁失败')
    }
  } catch (error) {
    ElMessage.error('封禁操作失败，请稍后重试')
    console.error('封禁用户异常:', error)
  }
}

// 执行解封
const confirmUnban = async () => {
  if (selectedForumIdsForUnban.value.length === 0) {
    ElMessage.warning('请选择至少一个板块')
    return
  }

  try {
    const res = await request.post('/api/user/unban', {
      userId: selectedUserForUnban.value.accountId,
      forumId: selectedForumIdsForUnban.value
    })

    if (res.code === 200) {
      ElMessage.success('用户解封成功！')
      unbanDialogVisible.value = false
      selectedUserForUnban.value = null
      selectedForumIdsForUnban.value = []
    } else {
      ElMessage.warning(res.message || '解封失败')
    }
  } catch (error) {
    ElMessage.error('解封操作失败，请稍后重试')
    console.error('解封用户异常:', error)
  }
}

// 获取可设置为版主的板块
const fetchAvailableModeratorForums = async (user) => {
  if (!user?.accountId) {
    ElMessage.warning('用户信息不完整')
    return
  }

  loadingModeratorForums.value = true
  try {
    const res = await request.get('/api/user/available-moderator', {
      params: {
        user_id: user.accountId
      }
    })

    if (res.code === 200 && Array.isArray(res.data)) {
      availableModeratorForums.value = res.data
    } else {
      availableModeratorForums.value = []
      ElMessage.warning(res.message || '暂无可设置版主的板块')
    }
  } catch (error) {
    availableModeratorForums.value = []
    ElMessage.error('获取可设置版主板块失败')
    console.error('获取可设置版主板块异常:', error)
  } finally {
    loadingModeratorForums.value = false
  }
}

// 显示设置版主对话框
const showSetModeratorDialog = async (user) => {
  if (userRole.value !== 'admin') {
    ElMessage.warning('只有管理员可以设置版主')
    return
  }

  await fetchAvailableModeratorForums(user)

  if (availableModeratorForums.value.length === 0) {
    return // 已有提示，无需额外弹窗
  }

  selectedUserForModerator.value = user
  selectedModeratorForumIds.value = [] // 清空上次选择
  moderatorDialogVisible.value = true
}

// 确认设置版主
const confirmSetModerator = async () => {
  if (selectedModeratorForumIds.value.length === 0) {
    ElMessage.warning('请选择至少一个板块')
    return
  }

  try {
    const res = await request.post('/api/user/set_moderator', {
      userId: selectedUserForModerator.value.accountId,
      forumId: selectedModeratorForumIds.value
    })

    if (res.code === 200) {
      ElMessage.success('版主设置成功！')
      moderatorDialogVisible.value = false
      selectedUserForModerator.value = null
      selectedModeratorForumIds.value = []
    } else if (res.code === 400) {
      ElMessage.warning(res.message || '设置失败')
    } else {
      ElMessage.warning('设置版主失败')
    }
  } catch (error) {
    ElMessage.error('设置版主操作失败，请稍后重试')
    console.error('设置版主异常:', error)
  }
}

</script>

<style scoped>
.dashboard-container {
  height: 100vh;
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

/* ============ 我的帖子区域样式 ============ */

.my-posts-card {
  margin-left: 10px;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.posts-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  padding: 20px 0;
}

.post-item {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.post-item:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.post-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.4;
  word-break: break-word;
}

.post-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0 0 12px 0;
  word-break: break-word;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.forum-name {
  font-weight: 500;
  color: #409eff;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .posts-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .post-title {
    font-size: 15px;
  }

  .post-content {
    font-size: 13px;
  }
}

.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-role {
  font-size: 14px;
  background-color: rgba(255, 255, 255, 0.2);
  padding: 4px 8px;
  border-radius: 4px;
  color: #fff;
}
/* 危险按钮悬停变深红 */

/* ============ 用户搜索结果样式 ============ */

.search-results-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  margin-top: 16px;
}

.user-result-card {
  padding: 16px;
  background-color: #f0f9ff;
  border-left: 4px solid #409eff;
  transition: all 0.2s ease;
}

.user-result-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.user-info p {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .search-results-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .user-info h4 {
    font-size: 15px;
  }

  .user-info p {
    font-size: 13px;
  }
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 按钮容器：分两行 */
.user-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

/* 上面两个按钮：并排 */
.user-actions .top-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  width: 100%;
}

/* 下面两个按钮：并排 */
.user-actions .bottom-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  width: 100%;
}
</style>