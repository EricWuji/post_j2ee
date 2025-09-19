<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 style="text-align: center; margin-bottom: 30px;">用户登录</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
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
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await axios.post('http://localhost:8080/api/auth/login', {
        username: loginForm.username,
        password: loginForm.password
      })

      // 检查响应结构
      if (res.data.code === 200 && res.data.data?.token) {
        const { token, username } = res.data.data
        localStorage.setItem('token', token)
        localStorage.setItem('username', username) // 👈 存储用户名，方便 Dashboard 使用
        ElMessage.success('登录成功！')
        router.push('/dashboard')
      } else {
        ElMessage.error(res.data.message || '登录失败')
      }
    } catch (error) {
      ElMessage.error('登录失败，请检查用户名或密码')
      console.error(error)
    } finally {
      loading.value = false
    }
  })
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