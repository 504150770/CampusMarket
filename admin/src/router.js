import Vue from "vue"
import VueRouter from "vue-router"
import Main from './views/Main.vue'

Vue.use(VueRouter);

const routes = [
    {
        path: '/',
        name: 'Main',
        component: Main,
        meta: {
            loginRequire: true
        },
        children: [
            {
                path: '/',
                name: 'Home',
                component: () => import('@/views/Home'),
                meta: {
                   loginRequire: true
                }
            },
            {
                path: '/user_list',
                name: 'UserList',
                component: () => import('@/views/UserList'),
                meta: {
                    loginRequire: true
                }
            },
            {
                path: '/category_list',
                name: 'CategoryList',
                component: () => import('@/views/CategoryList'),
                meta: {
                    loginRequire: true
                }
            },
            {
                path: '/product_list',
                name: 'ProductList',
                component: () => import('@/views/ProductList'),
                meta: {
                    loginRequire: true
                }
            },
            {
                path: '/store_list',
                name: 'StoreList',
                component: () => import('@/views/StoreList'),
                meta: {
                    loginRequire: true
                }
            },
            {
                path: '/order_list',
                name: 'OrderList',
                component: () => import('@/views/OrderList'),
                meta: {
                    loginRequire: true
                }
            }
        ]
    }, {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login')
    }];

const router = new VueRouter({
    mode: "history",
    base: process.env.BASE_URL,
    routes
});

// 导航栏中用到了路由 防止重复点同一个菜单给出错误提示
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
};

export default router;
