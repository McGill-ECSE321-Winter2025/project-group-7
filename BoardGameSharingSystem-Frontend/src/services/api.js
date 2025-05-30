import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/',
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use(
  (config) => {
    const userId = localStorage.getItem('userId')
    if (userId) {
      config.headers['User-Id'] = userId
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

export default api