import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

// 设置默认 baseURL（可选）
axios.defaults.baseURL = 'http://localhost:8080'

// 请求拦截器：自动添加 JWT
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
}, error => {
    return Promise.reject(error)
})

// 响应拦截器（可选）：统一处理错误
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            // Token 失效或未授权，跳转登录
            localStorage.removeItem('token')
            localStorage.removeItem('username')
            router.push('/login')
        }
        return Promise.reject(error)
    }
)

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')