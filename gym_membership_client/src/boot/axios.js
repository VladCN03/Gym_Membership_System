import { boot } from 'quasar/wrappers'
import axios from 'axios'

function isNativePlatform() {
  // Capacitor injectează window.Capacitor în aplicația nativă
  return typeof window !== 'undefined' && !!window.Capacitor
}

// ⚠️ MODIFICĂ IP-UL DACĂ FOLOSEȘTI TELEFON REAL
let base = 'http://localhost:8080' // pentru browser pe PC

if (isNativePlatform()) {
  // pentru EMULATOR Android:
  base = 'http://10.0.2.2:8080'

  // dacă rulezi pe TELEFON REAL în aceeași rețea Wi-Fi:
  // base = 'http://192.168.X.X:8080'  // IP-ul PC-ului tău
}

const api = axios.create({
  baseURL: base + '/api',
  timeout: 15000,
})

// 🔐 interceptor: adaugă token-ul JWT la fiecare request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

export default boot(({ app }) => {
  app.config.globalProperties.$axios = axios
  app.config.globalProperties.$api = api
})

export { api }
