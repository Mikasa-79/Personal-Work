import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import RegisterView from '../views/RegisterView.vue';
import ProfileView from '../views/ProfileView.vue';
import AdminView from '../views/AdminView.vue';
import RequestsView from '../views/RequestsView.vue';
import CreateRequestView from '../views/CreateRequestView.vue';
import RequestDetailView from '../views/RequestDetailView.vue';
import OrderListView from '../views/OrderListView.vue';
import OrderDetailView from '../views/OrderDetailView.vue';
import NotificationsView from '../views/NotificationsView.vue';
import { authUser, loadAuthToken } from '../api';
const routes = [
    { path: '/', name: 'Home', component: HomeView },
    { path: '/login', name: 'Login', component: LoginView },
    { path: '/register', name: 'Register', component: RegisterView },
    { path: '/requests', name: 'Requests', component: RequestsView, meta: { requiresAuth: true } },
    { path: '/requests/new', name: 'CreateRequest', component: CreateRequestView, meta: { requiresAuth: true } },
    { path: '/requests/:requestId', name: 'RequestDetail', component: RequestDetailView, props: true, meta: { requiresAuth: true } },
    { path: '/orders', name: 'Orders', component: OrderListView, meta: { requiresAuth: true } },
    { path: '/orders/:orderId', name: 'OrderDetail', component: OrderDetailView, props: true, meta: { requiresAuth: true } },
    { path: '/notifications', name: 'Notifications', component: NotificationsView, meta: { requiresAuth: true } },
    { path: '/profile', name: 'Profile', component: ProfileView, meta: { requiresAuth: true } },
    { path: '/admin', name: 'Admin', component: AdminView, meta: { requiresAuth: true, requiresAdmin: true } },
];
const router = createRouter({
    history: createWebHistory(),
    routes,
});
router.beforeEach(to => {
    const token = loadAuthToken();
    const isLoggedIn = !!token && !!authUser.value;
    if (to.meta.requiresAuth && !isLoggedIn) {
        return { name: 'Login', query: { redirect: to.fullPath } };
    }
    if (to.meta.requiresAdmin && !authUser.value?.admin) {
        return { name: 'Home' };
    }
    if ((to.name === 'Login' || to.name === 'Register') && isLoggedIn) {
        return { name: 'Requests' };
    }
    return true;
});
export default router;
//# sourceMappingURL=index.js.map