<template>
  <div class="forum-container">
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="forum-header">
        <el-button type="primary" @click="goBack" plain>
          <el-icon><ArrowLeft /></el-icon>
          返回仪表盘
        </el-button>
        <h2 style="margin-left:10px;margin-bottom:5px">论坛板块 {{ forumName || '加载中...' }}</h2>
        <div class="spacer"></div>
      </el-header>

      <!-- 主体内容 -->
      <el-main class="forum-main">
        <!-- 论坛信息卡片 -->
        <el-card class="forum-info-card" shadow="hover">
          <template #header>
            <div class="clearfix">
              <span>板块信息</span>
            </div>
          </template>
          <div v-if="loading">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else>
            <p><strong>板块名称：</strong>{{ forumName }}</p>
            <p><strong>板块描述：</strong>{{ forumDesc }}</p>
          </div>
        </el-card>

        <!-- 发帖区域 -->
        <el-card class="post-form-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <span>发表新帖</span>
          </template>
          <el-form
              :model="postForm"
              :rules="postRules"
              ref="postFormRef"
              label-width="80px"
              style="padding: 20px;"
          >
            <el-form-item label="标题" prop="postTitle">
              <el-input
                  v-model="postForm.postTitle"
                  placeholder="请输入帖子标题"
                  maxlength="100"
                  show-word-limit
                  clearable
              />
            </el-form-item>
            <el-form-item label="内容" prop="postContent">
              <el-input
                  v-model="postForm.postContent"
                  type="textarea"
                  :rows="6"
                  placeholder="请输入帖子内容"
                  maxlength="5000"
                  show-word-limit
                  clearable
              />
            </el-form-item>
            <el-form-item>
              <el-button
                  type="primary"
                  :loading="submitting"
                  @click="submitPost"
                  :disabled="submitting"
              >
                {{ submitting ? '发布中...' : '立即发布' }}
              </el-button>
              <el-button @click="resetForm" :disabled="submitting">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 帖子列表 -->
        <!-- 帖子列表 -->
        <el-card class="post-list-card centered" shadow="never" style="margin-top: 20px;">
          <template #header>
            <div class="post-list-header">
              <span>帖子列表</span>
              <el-tag v-if="!postsLoading && posts.length > 0" effect="dark" size="small">
                {{ posts.length }} 篇
              </el-tag>
            </div>
          </template>

          <!-- 加载中状态 -->
          <div v-if="postsLoading" class="loading-container">
            <el-skeleton :rows="3" animated style="margin: 20px 0;" />
            <el-skeleton :rows="3" animated style="margin: 20px 0;" />
            <el-skeleton :rows="3" animated style="margin: 20px 0;" />
          </div>

          <!-- 空数据提示 -->
          <div v-else-if="posts.length === 0" class="empty-container">
            <el-empty
                description="暂无帖子，快去发布第一个吧！"
                image-style="width: 120px; height: 120px;"
            >
              <el-button type="primary" size="small" @click="postFormRef?.focus?.()">
                立即发帖
              </el-button>
            </el-empty>
          </div>

          <!-- 帖子列表渲染 -->
          <div v-else class="posts-list">
            <div
                v-for="(post, index) in posts"
                :key="index"
                class="post-item"
                @click="viewPostDetail(post)"
            >
              <h3 class="post-title">{{ post.postTitle }}</h3>
              <p class="post-content">{{ post.postContent }}</p>
              <div class="post-meta">
                <span class="author">{{ post.username || '匿名用户' }}</span>
                <span class="time">刚刚</span>
                <el-button type="text" size="small" @click.stop="viewPostDetail(post)">
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElSkeleton, ElEmpty } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const route = useRoute()

const forumId = ref(route.params.forumId)
const forumName = ref('')
const forumDesc = ref('')
const loading = ref(true)

// 帖子列表相关
const posts = ref([])
const postsLoading = ref(false)

// 发帖表单数据
const postForm = reactive({
  postTitle: '',
  postContent: ''
})

// 表单校验规则
const postRules = {
  postTitle: [
    { required: true, message: '请输入帖子标题', trigger: 'blur' },
    { max: 100, message: '标题不能超过100字', trigger: 'blur' }
  ],
  postContent: [
    { required: true, message: '请输入帖子内容', trigger: 'blur' },
    { max: 5000, message: '内容不能超过5000字', trigger: 'blur' }
  ]
}

// 表单引用
const postFormRef = ref()

// 提交状态
const submitting = ref(false)

const goBack = () => {
  router.push('/dashboard')
}

// 获取论坛详情
const fetchForumDetails = async () => {
  try {
    const res = await request.get('/api/forum/details', {
      params: {
        forum_id: forumId.value
      }
    })
    forumName.value = res.data.forumName
    forumDesc.value = res.data.forumDesc
  } catch (error) {
    ElMessage.error('获取论坛信息失败，请稍后重试')
    console.error('获取论坛详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取帖子列表
const fetchPosts = async () => {
  postsLoading.value = true
  try {
    const res = await request.get('/api/forum/list_posts', {
      params: {
        forum_id: forumId.value
      }
    })
    if (res.code === 200) {
      posts.value = res.data || []
    } else {
      ElMessage.warning(res.message || '获取帖子列表失败')
      posts.value = []
    }
  } catch (error) {
    ElMessage.error('获取帖子列表失败，请检查网络或稍后再试')
    posts.value = []
    console.error('获取帖子列表异常:', error)
  } finally {
    postsLoading.value = false
  }
}

// 提交发帖
const submitPost = async () => {
  if (!postFormRef.value) return

  await postFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const res = await request.post('/api/post/add', {
        postTitle: postForm.postTitle,
        postContent: postForm.postContent,
        forumId: forumId.value
      })

      if (res.code === 200) {
        ElMessage.success('发帖成功！')
        resetForm()
        // ✅ 自动刷新帖子列表
        await fetchPosts()
      } else {
        ElMessage.warning(res.message || '发帖失败，请稍后重试')
      }
    } catch (error) {
      ElMessage.error('网络错误或服务器异常，请稍后再试')
      console.error('发帖请求失败:', error)
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  if (postFormRef.value) {
    postFormRef.value.resetFields()
  }
  postForm.postTitle = ''
  postForm.postContent = ''
}

// 模拟跳转到帖子详情（后续可扩展）
const viewPostDetail = (post) => {
  console.log('查看帖子详情:', post)
  ElMessage.info('功能待开发：跳转到帖子详情页')
}

onMounted(() => {
  console.log('当前进入的论坛ID:', forumId.value)
  fetchForumDetails()
  fetchPosts() // 初始化加载帖子列表
})
</script>

<style scoped>
.forum-container {
  height: 100vh;
}

.forum-header {
  display: flex;
  align-items: center;
  background-color: #66b1ff;
  color: white;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.forum-header h2 {
  margin: 0;
  font-weight: 500;
}

.spacer {
  flex: 1;
}

.forum-main {
  background-color: #f9f9f9;
  padding: 20px;
}

.post-form-card :deep(.el-card__body) {
  padding: 0;
}

.post-item-card :deep(.el-card__body) {
  padding: 15px;
}

.post-item h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #303133;
}

.meta {
  font-size: 13px;
  color: #888;
  margin: 0 0 10px 0;
}

.dot {
  margin: 0 5px;
  color: #ccc;
}

.content-preview {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 15px;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .forum-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    padding: 15px;
  }

  .forum-main {
    padding: 15px;
  }
}

.post-list-card.clean {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.post-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.posts-list {
  padding: 16px 0;
  list-style: none;
  margin: 0;
}

.post-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.post-item:last-child {
  border-bottom: none;
}

.post-item:hover {
  background-color: #f5f7fa;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.4;
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

.author {
  font-weight: 500;
  color: #409eff;
}

.time {
  margin-left: 8px;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .post-title {
    font-size: 16px;
  }

  .post-content {
    font-size: 13px;
  }

  .post-meta {
    flex-direction: column;
    gap: 4px;
  }
}

/* ============ 居中对齐版帖子列表 ============ */

.post-list-card.centered {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.post-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.posts-list {
  padding: 16px 0;
  list-style: none;
  margin: 0;
}

.post-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.post-item:last-child {
  border-bottom: none;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  text-align: center;
  line-height: 1.4;
}

.post-content {
  font-size: 14px;
  color: #606266;
  text-align: center;
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
  padding: 0 16px;
}

.author {
  font-weight: 500;
  color: #409eff;
}

.time {
  margin-left: 8px;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .post-title {
    font-size: 16px;
  }

  .post-content {
    font-size: 13px;
  }

  .post-meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>