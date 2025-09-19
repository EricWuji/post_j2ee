<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 style="text-align: center; margin-bottom: 30px;">用户登录</h2>

      <!-- 登录/注册切换 -->
      <div style="text-align: center; margin-bottom: 20px;">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="登录" name="login"></el-tab-pane>
          <el-tab-pane label="注册" name="register"></el-tab-pane>
        </el-tabs>
      </div>

      <!-- 登录表单 -->
      <el-form
          v-if="activeTab === 'login'"
          :model="loginForm"
          :rules="rules"
          ref="loginFormRef"
          label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              @click="handleLogin"
              :loading="loading"
              style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册表单 -->
      <el-form
          v-else
          :model="registerForm"
          :rules="registerRules"
          ref="registerFormRef"
          label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input v-model="registerForm.code" placeholder="请输入验证码">
            <template #append>
              <el-button
                  :disabled="countdown > 0 || isSendingCode"
                  @click="sendCode"
              >
                {{ countdown > 0 ? `${countdown}s 后重发` : '获取验证码' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              @click="handleRegister"
              :loading="loading"
              style="width: 100%"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部链接（可选） -->
      <div style="text-align: center; margin-top: 20px;">
        <el-link
            v-if="activeTab === 'login'"
            type="primary"
            @click="activeTab = 'register'"
        >
          没有账号？去注册
        </el-link>
        <el-link
            v-else
            type="primary"
            @click="activeTab = 'login'"
        >
          已有账号？去登录
        </el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import request from "@/api/request.js";
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginFormRef = ref(null)
const registerFormRef = ref(null)
const loading = ref(false)
const activeTab = ref('login') // 默认为登录

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  code: ''
})

// 登录规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 注册规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 5, message: '验证码为5位数字', trigger: 'blur' }
  ]
}

// 登录处理
const handleLogin = async () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await request.post('/api/auth/login', {
        username: loginForm.username,
        password: loginForm.password
      })

      // ✅ 修复：res 就是 data，不需要 res.data
      if (res.code === 200 && res.data?.token) {
        const { token, username } = res.data
        localStorage.setItem('token', token)
        localStorage.setItem('username', username)
        ElMessage.success('登录成功！')
        router.push('/dashboard')
      } else {
        ElMessage.error(res.message || '登录失败')
      }
    } catch (error) {
      ElMessage.error('登录失败，请检查用户名或密码')
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

// 发送验证码
const isSendingCode = ref(false)
const countdown = ref(0)

const sendCode = async () => {
  try {
    await registerFormRef.value.validateField('email')

    console.log("here")

    isSendingCode.value = true
    const res = await request.get('/api/auth/ask-code', {
      params: {
        email: registerForm.email,
        type: "register"
      }
    })

    if (res.code === 200) {
      ElMessage.success('验证码已发送，请查收邮箱')
      startCountdown()
    } else {
      ElMessage.error(res.message || '发送失败')
    }
  } catch (error) {
    // 如果是表单校验错误，这里会进入 catch
    console.log('邮箱值：', registerForm.email)
    console.log('邮箱校验结果：', error)
    console.log('表单校验失败:', error)
    return // 不继续发送请求
  } finally {
    isSendingCode.value = false
  }
}

// 倒计时
const startCountdown = () => {
  countdown.value = 60
  const timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      clearInterval(timer)
    }
  }, 1000)
}

// 注册处理
const handleRegister = async () => {
  registerFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await request.post('/api/auth/register', { // 👈 路径也建议补全 /api/auth/register
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password,
        code: registerForm.code
      })

      // ✅ 修复：res.code
      if (res.code === 200) {
        ElMessage.success('注册成功！')
        activeTab.value = 'login'
        registerForm.username = ''
        registerForm.email = ''
        registerForm.password = ''
        registerForm.code = ''
      } else {
        ElMessage.error(res.message || '注册失败')
      }
    } catch (error) {
      ElMessage.error('注册失败，请稍后重试')
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

// tab切换事件（可选）
const handleTabClick = (tab) => {
  // 可以在这里做额外逻辑，比如清空表单等
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
  padding: 40px;
}
</style>