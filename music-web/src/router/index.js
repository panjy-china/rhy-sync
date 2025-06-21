import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const Home = () => import('@/views/home/Home.vue')


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [    
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/user/Login.vue'),
    },
    {
      path: '/',
      name: 'home',
      component: Home,
      children: [
        {
          path: '/song',
          name: 'song',
          component: () => import('@/views/music/Song.vue'),
        }, {
          path: '/singer',
          name: 'singer',
          component: () => import('@/views/singer/Singer.vue'),
        },{
          path: '/vedio',
          name: 'vedio',
          component: () => import('@/views/vedio/Vedio.vue'),
        }
      ],
    },
  ],
})

// 导航守卫
router.beforeEach((to) => {
  const userStore = useUserStore()
  //判断用户是否登录
  if (to.name !== 'login' && !userStore.token) {
    return { name: 'login' }
  }
  // return true
})

export default router
