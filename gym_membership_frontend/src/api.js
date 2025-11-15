// src/api.js
import axios from 'axios'

const BASE = (import.meta.env.VITE_API_URL || 'http://localhost:8080') + '/api'

const api = axios.create({
  baseURL: BASE,
  timeout: 70000,
})

// --- Token helpers ---
export const getToken = () => localStorage.getItem('token')
export const setToken = (t) => {
  if (t) {
    localStorage.setItem('token', t)
    api.defaults.headers.common.Authorization = `Bearer ${t}`
  }
}
export const clearToken = () => {
  // curăță TOT ce ar putea rămâne
  localStorage.removeItem('token')
  localStorage.removeItem('jwt') // în caz că a mai rămas din versiunea veche
  delete api.defaults.headers.common.Authorization
}

// --- Attach token on each request (în afară de login) ---
api.interceptors.request.use((cfg) => {
  const t = getToken()
  if (t) cfg.headers.Authorization = `Bearer ${t}`
  else delete cfg.headers.Authorization
  return cfg
})

// --- Errors ---
api.interceptors.response.use(
  (r) => r,
  (e) => {
    const msg = e?.response?.data?.message || e?.response?.data || e?.message || 'Request failed'
    console.error('API error:', msg)
    return Promise.reject(e)
  },
)

// păstrăm sesiunea la refresh
;(function preloadToken() {
  const t = getToken()
  if (t) api.defaults.headers.common.Authorization = `Bearer ${t}`
})()

// ---------- Auth ----------
export const Auth = {
  login(username, password) {
    return api.post('/auth/login', { username, password }) // <-- cheile corecte
  },
}

// ---------- CRUD ----------
export const MembershipTypes = {
  all: () => api.get('/membership-types').then((r) => r.data),
  create: (payload) => api.post('/membership-types', payload).then((r) => r.data),
  update: (id, payload) => api.put(`/membership-types/${id}`, payload).then((r) => r.data),
  delete: (id) => api.delete(`/membership-types/${id}`).then((r) => r.data),
}

export const Trainers = {
  all: () => api.get('/trainers').then((r) => r.data),
  create: (payload) => api.post('/trainers', payload).then((r) => r.data),

  update: (id, payload) => api.put(`/trainers/${id}`, payload).then((r) => r.data),
  delete: (id) => api.delete(`/trainers/${id}`).then((r) => r.data),
}

export const Members = {
  all: () => api.get('/members').then((r) => r.data),
  create: (payload) => api.post('/members', payload).then((r) => r.data),
  assignMembership: (payload) =>
    api.post('/members/assign-membership', payload).then((r) => r.data),
  assignTrainer: ({ memberId, trainerId }) =>
    api.post('/members/assign-trainer', { memberId, trainerId }).then((r) => r.data),

  // 👇 NOI
  removeTrainer: (memberId) =>
    api.post('/members/assign-trainer', { memberId, trainerId: null }).then((r) => r.data),

  delete: (id) => api.delete(`/members/${id}`).then((r) => r.data),
}

// ---------- Analytics ----------
export const Analytics = {
  membersPerTrainer: () => api.get('/custom/members-per-trainer').then((r) => r.data),
  membershipStats: () => api.get('/custom/membership-stats-by-type').then((r) => r.data),
}

export default api
