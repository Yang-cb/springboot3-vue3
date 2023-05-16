import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    // 配置路由
    routes: [
        {
            // 一级路由
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            // 子路由
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/components/welcome/Login.vue')
                }, {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/components/welcome/Register.vue')
                }, {
                    path: 'resetPassword',
                    name: 'welcome-resetPassword',
                    component: () => import('@/components/welcome/resetPassword.vue')
                }
            ],
        },
        {
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue')
        }
    ]
})

export default router
