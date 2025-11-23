const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      // când intri pe '/', te redirecționează la /login
      { path: '', redirect: '/login' },

      // toate paginile noastre sunt copii ale layout-ului
      { path: 'login', component: () => import('pages/LoginPage.vue') },
      { path: 'register', component: () => import('pages/RegisterPage.vue') },
      { path: 'home', component: () => import('pages/HomePage.vue') },
      {
        path: '/membership',
        component: () => import('pages/BuyMembershipPage.vue'),
      },
      {
        path: '/trainer',
        component: () => import('pages/AssignTrainerPage.vue'),
      },
      {
        path: '/stats',
        component: () => import('pages/StatsPage.vue'),
      },
      {
        path: '/ai',
        component: () => import('src/pages/AiPage.vue'),
      },
    ],
  },

  // fallback pentru rute greșite
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
]

export default routes
