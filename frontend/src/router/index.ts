import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AccountViewVue from '../views/AccountView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/movements/:id',
      name: 'movements',
      // route level code-splitting
      // this generates a separate chunk (Holders.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/MovementsView.vue')
    },
    {
      path: '/accounts/:name',
      name: 'accountsById',
      component: () => AccountViewVue
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/homepage/:id',
      name: 'homepage',
      component: () => import('../views/HomepageView.vue')
    },
    {
      path: '/payments/:id',
      name: 'payments',
      component: () => import('../views/PaymentsView.vue')
    }
  ]
})

export default router
