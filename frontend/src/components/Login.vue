<template>
  <div class="login-container">
    <div class="login-form">
      <h2>用户登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
              id="username"
              v-model="formData.username"
              type="text"
              placeholder="请输入用户名"
              required
          />
          <div v-if="errors.username" class="error">{{ errors.username }}</div>
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input
              id="password"
              v-model="formData.password"
              type="password"
              placeholder="请输入密码"
              required
          />
          <div v-if="errors.password" class="error">{{ errors.password }}</div>
        </div>

        <div v-if="loginError" class="error global-error">{{ loginError }}</div>

        <button type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

// 表单数据
const formData = ref({
  username: '',
  password: ''
})

// 错误信息
const errors = ref({})
const loginError = ref('')
const loading = ref(false)

// 登录处理
const handleLogin = async () => {
  // 重置错误
  errors.value = {}
  loginError.value = ''

  // 简单前端验证
  if (!formData.value.username.trim()) {
    errors.value.username = '用户名不能为空'
  }
  if (!formData.value.password) {
    errors.value.password = '密码不能为空'
  }

  // 如果有前端错误，不提交
  if (Object.keys(errors.value).length > 0) return

  loading.value = true

  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: formData.value.username,
      password: formData.value.password
    })

    // 登录成功，假设后端返回 { token: 'xxx', user: {} }
    console.log('登录成功', response.data)

    // 通常这里会保存 token 到 localStorage 或 vuex/pinia
    // localStorage.setItem('token', response.data.token)

    // 跳转到首页或其他页面
    // router.push('/dashboard')

    alert('登录成功！')
  } catch (err) {
    console.error('登录失败', err)
    if (err.response && err.response.data && err.response.data.message) {
      loginError.value = err.response.data.message
    } else {
      loginError.value = '登录失败，请检查用户名或密码'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  width: 350px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
}

.form-group input:focus {
  border-color: #409eff;
  outline: none;
}

.error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

.global-error {
  margin-bottom: 20px;
  text-align: center;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover:not(:disabled) {
  background-color: #66b1ff;
}

button:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}
</style>