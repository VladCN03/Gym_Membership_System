import { createRouter, createWebHistory } from 'vue-router'

const Login = () => import('@/views/LoginView.vue')
const Dashboard = () => import('@/views/DashboardView.vue')
const MembershipTypes = () => import('@/views/MembershipTypesView.vue')
const Trainers = () => import('@/views/TrainersView.vue')
const Members = () => import('@/views/MembersView.vue')
const Stats = () => import('@/views/StatsView.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', component: Login },
    // rute protejate:
    { path: '/', component: Dashboard, meta: { auth: true } },
    { path: '/membership-types', component: MembershipTypes, meta: { auth: true } },
    { path: '/trainers', component: Trainers, meta: { auth: true } },
    { path: '/members', component: Members, meta: { auth: true } },
    { path: '/stats', name: 'stats', component: Stats },
  ],
})

// guard global
router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta?.auth
  const token = localStorage.getItem('token')

  if (requiresAuth && !token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }
  // dacă ești deja logat și vii pe /login, trimite-l în dashboard
  if (to.path === '/login' && token) {
    return next('/')
  }
  next()
})

export default router
