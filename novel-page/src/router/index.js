import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },

  {
    path: '/pages/crawler',
    name: 'Crawler',
    component: () => import('../views/Crawler.vue')
  },
  {
    path: '/pages/setting',
    name: 'setting',
    component: () => import('../views/Setting.vue')
  },
  {
    path: '/pages/novels',
    name: 'novels',
    component: () => import('../views/Novels.vue')
  }
];

const router = createRouter({
  history: createWebHistory('/'),
  routes: routes
})
export default router
