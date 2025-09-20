<template>
  <div class="post-detail-container">
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="post-header">
        <el-button type="primary" @click="goBack" plain size="small">
          <el-icon><ArrowLeft /></el-icon>
          返回论坛
        </el-button>
        <h2 style="margin-left:10px; margin-bottom:5px">帖子详情</h2>
        <div class="spacer"></div>
      </el-header>

      <el-main class="post-main">
        <!-- 帖子内容卡片 -->
        <el-card class="post-content-card" shadow="hover">
          <div v-if="loading">
            <el-skeleton :rows="4" animated />
          </div>
          <div v-else>
            <h1 class="title">{{ postData.postTitle }}</h1>
            <div class="meta">
              <span>作者：{{ postData.username }}</span>
              <span class="dot">·</span>
              <span>发布时间：刚刚</span> <!-- 后续可替换为真实时间 -->
            </div>
            <el-divider />
            <div class="content" v-html="formattedContent"></div>
          </div>
        </el-card>

        <!-- 发表评论区域 -->
        <el-card class="comment-form-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <span>发表评论</span>
          </template>
          <el-form
              :model="commentForm"
              :rules="commentRules"
              ref="commentFormRef"
              label-width="0"
              style="padding: 20px;"
          >
            <el-form-item prop="commentContent">
              <el-input
                  v-model="commentForm.commentContent"
                  type="textarea"
                  :rows="4"
                  placeholder="写下你的评论..."
                  maxlength="500"
                  show-word-limit
                  clearable
              />
            </el-form-item>
            <el-form-item>
              <el-button
                  type="primary"
                  :loading="submittingComment"
                  @click="submitComment"
                  :disabled="submittingComment"
              >
                {{ submittingComment ? '发布中...' : '发布评论' }}
              </el-button>
              <el-button @click="resetCommentForm" :disabled="submittingComment">清空</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 评论列表 -->
        <!-- 评论列表 -->
        <el-card class="comment-list-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <span>评论列表（{{ comments.length }}）</span>
          </template>

          <div v-if="commentsLoading" style="padding: 40px; text-align: center;">
            <el-skeleton :rows="3" animated />
          </div>

          <div v-else-if="comments.length === 0" style="padding: 40px; text-align: center; color: #999;">
            <el-empty description="暂无评论，快抢沙发吧！" />
          </div>

          <div v-else class="comments-container">
            <div
                v-for="(comment, index) in comments"
                :key="index"
                class="comment-item"
            >
              <div class="comment-header">
                <span class="author" style="font-size: 20px">{{ comment.username || '匿名用户' }}</span>
                <span class="time" style="font-size: 20px">刚刚</span>
              </div>
              <div class="comment-body" style="font-size: 30px">
                {{ comment.commentContent }}
              </div>
            </div>
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import {ref, onMounted, reactive, computed} from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElSkeleton, ElEmpty } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const route = useRoute()

// 获取 postId 参数
const postId = ref(route.params.postId)

// 帖子数据
const postData = ref({})
const loading = ref(true)

// 评论相关
const comments = ref([])
const commentsLoading = ref(false)

// 评论表单
const commentForm = reactive({
  commentContent: ''
})

const commentRules = {
  commentContent: [
    { required: true, message: '请输入评论内容', trigger: 'blur' },
    { max: 500, message: '评论不能超过500字', trigger: 'blur' }
  ]
}

const commentFormRef = ref()
const submittingComment = ref(false)

// 返回上一页
const goBack = () => {
  router.go(-1) // 或者 router.push(`/forum/${route.query.forumId}`)
}

// 获取帖子详情
const fetchPostDetail = async () => {
  try {
    const res = await request.get('/api/post/details', {
      params: {
        post_id: postId.value
      }
    })
    if (res.code === 200) {
      postData.value = res.data || {}
    } else {
      ElMessage.warning(res.message || '获取帖子详情失败')
    }
  } catch (error) {
    ElMessage.error('获取帖子详情失败，请稍后重试')
    console.error('获取帖子详情异常:', error)
  } finally {
    loading.value = false
  }
}

// 获取评论列表
const fetchComments = async () => {
  commentsLoading.value = true
  try {
    const res = await request.get('/api/comment/details', {
      params: {
        postId: postId.value
      }
    })
    if (res.code === 200) {
      comments.value = Array.isArray(res.data) ? res.data : []
    } else {
      ElMessage.warning(res.message || '获取评论失败')
      comments.value = []
    }
  } catch (error) {
    ElMessage.error('获取评论失败，请稍后重试')
    comments.value = []
    console.error('获取评论异常:', error)
  } finally {
    commentsLoading.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!commentFormRef.value) return

  await commentFormRef.value.validate(async (valid) => {
    if (!valid) return

    submittingComment.value = true
    try {
      const res = await request.post('/api/comment/add', {
        postId: postId.value,
        commentContent: commentForm.commentContent
      })

      if (res.code === 200) {
        ElMessage.success('评论发布成功！')
        resetCommentForm()
        await fetchComments() // ✅ 自动刷新评论列表
      } else {
        ElMessage.warning(res.message || '评论发布失败')
      }
    } catch (error) {
      ElMessage.error('网络错误，请稍后再试')
      console.error('评论提交失败:', error)
    } finally {
      submittingComment.value = false
    }
  })
}

// 清空评论表单
const resetCommentForm = () => {
  commentForm.commentContent = ''
  if (commentFormRef.value) {
    commentFormRef.value.clearValidate()
  }
}

// 格式化内容（可选：支持换行）
const formattedContent = computed(() => {
  return postData.value.postContent?.replace(/\n/g, '<br/>') || ''
})

onMounted(() => {
  if (!postId.value) {
    ElMessage.error('帖子ID无效')
    router.go(-1)
    return
  }
  fetchPostDetail()
  fetchComments()
})
</script>

<script>
// 非 setup 语法用于 computed（或你也可以用 setup 里 import { computed }）
export default {
  name: 'PostDetail'
}
</script>

<style scoped>
.post-detail-container {
  height: 100vh;
}

.post-header {
  display: flex;
  align-items: center;
  background-color: #66b1ff;
  color: white;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.post-header h2 {
  margin: 0;
  font-weight: 500;
}

.spacer {
  flex: 1;
}

.post-main {
  background-color: #f9f9f9;
  padding: 20px;
}

.post-content-card :deep(.el-card__body) {
  padding: 30px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
  line-height: 1.4;
}

.meta {
  font-size: 14px;
  color: #888;
  margin-bottom: 16px;
}

.dot {
  margin: 0 8px;
  color: #ccc;
}

.content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  word-break: break-word;
}

.comment-form-card :deep(.el-card__body) {
  padding: 0;
}

.comment-list-card :deep(.el-card__body) {
  padding: 20px;
}

.comments-container {
  margin-top: 10px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.author {
  font-weight: 500;
  color: #409eff;
}

.time {
  margin-left: 8px;
}

.comment-body {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  word-break: break-word;
}

/* 响应式 */
@media (max-width: 768px) {
  .post-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    padding: 15px;
  }

  .post-main {
    padding: 15px;
  }

  .title {
    font-size: 20px;
  }

  .content {
    font-size: 15px;
  }
}

/* ============ 优化后的评论列表样式 ============ */

.comments-container {
  margin-top: 10px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.author {
  font-weight: 500;
  color: #409eff;
  cursor: pointer;
  transition: color 0.2s;
}

.author:hover {
  color: #66b1ff;
}

.time {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.comment-body {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  text-align: center;
  word-break: break-word;
  margin: 0;
}
</style>