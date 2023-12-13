import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AccountViewVue from '../views/AccountView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: HomeView
    },
    {
      path: '/movements/:name',
      name: 'movements',
      // route level code-splitting
      // this generates a separate chunk (Holders.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/MovementsView.vue')
    },
    {
      path: '/accounts/:name',
      name: 'accounts',
      component: () => AccountViewVue
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/homepage/:name',
      name: 'homepage',
      component: () => import('../views/HomepageView.vue')
    },
    {
      path: '/payments/:name',
      name: 'payments',
      component: () => import('../views/PaymentsView.vue')
    }
  ]
})

export default router
