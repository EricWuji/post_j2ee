// src/api/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080', // 所有请求的基础 URL
    timeout: 10000, // 请求超时时间
})

// 请求拦截器：自动添加 Bearer Token
request.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器：统一处理错误
request.interceptors.response.use(
    (response) => {
        const res = response.data

        // 假设后端约定：code === 200 表示成功
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message || 'Error'))
        }

        return res // 直接返回 data 部分，方便使用
    },
    (error) => {
        console.error('请求异常:', error)
        ElMessage.error(error.response?.data?.message || '网络异常，请稍后再试')
        return Promise.reject(error)
    }
)

export default request