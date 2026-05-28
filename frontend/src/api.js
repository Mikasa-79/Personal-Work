import axios from 'axios';
import { ref } from 'vue';
const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL ?? '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});
export const authUser = ref(null);
export function setAuthToken(token) {
    if (token) {
        api.defaults.headers.common.Authorization = `Bearer ${token}`;
        localStorage.setItem('campushub_token', token);
    }
    else {
        delete api.defaults.headers.common.Authorization;
        localStorage.removeItem('campushub_token');
    }
}
export function setAuthData(response) {
    setAuthToken(response.token);
    localStorage.setItem('campushub_refresh_token', response.refreshToken);
    authUser.value = {
        userId: response.userId,
        studentNo: response.studentNo,
        nickname: response.nickname,
        role: response.role,
        admin: response.admin,
        creditScore: response.creditScore,
        status: response.status,
    };
    localStorage.setItem('campushub_user', JSON.stringify(authUser.value));
}
export function clearAuth() {
    setAuthToken(null);
    authUser.value = null;
    localStorage.removeItem('campushub_user');
    localStorage.removeItem('campushub_refresh_token');
}
export function loadAuthToken() {
    const token = localStorage.getItem('campushub_token');
    if (token) {
        api.defaults.headers.common.Authorization = `Bearer ${token}`;
    }
    const userJson = localStorage.getItem('campushub_user');
    if (userJson) {
        try {
            authUser.value = JSON.parse(userJson);
        }
        catch {
            authUser.value = null;
        }
    }
    return token;
}
function getStoredRefreshToken() {
    return localStorage.getItem('campushub_refresh_token');
}
let refreshPromise = null;
api.interceptors.response.use(response => response, async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;
    const refresh = getStoredRefreshToken();
    const isRefreshRequest = originalRequest?.url?.includes('/auth/refresh');
    if (status === 401 && refresh && originalRequest && !originalRequest._retry && !isRefreshRequest) {
        originalRequest._retry = true;
        try {
            refreshPromise = refreshPromise ?? refreshToken(refresh);
            const refreshed = await refreshPromise;
            setAuthData(refreshed);
            originalRequest.headers = originalRequest.headers ?? {};
            originalRequest.headers.Authorization = `Bearer ${refreshed.token}`;
            return api(originalRequest);
        }
        catch (refreshError) {
            clearAuth();
            return Promise.reject(refreshError);
        }
        finally {
            refreshPromise = null;
        }
    }
    if (status === 401) {
        clearAuth();
    }
    return Promise.reject(error);
});
export async function login(payload) {
    const response = await api.post('/auth/login', payload);
    return response.data;
}
export async function register(payload) {
    const response = await api.post('/auth/register', payload);
    return response.data;
}
export async function logout() {
    try {
        await api.post('/auth/logout');
    }
    finally {
        clearAuth();
    }
}
export async function refreshToken(refreshToken) {
    const response = await api.post('/auth/refresh', { refreshToken });
    return response.data;
}
export async function getCurrentUser() {
    const response = await api.get('/users/me');
    authUser.value = {
        userId: response.data.id,
        studentNo: response.data.studentNo,
        nickname: response.data.nickname,
        college: response.data.college,
        contact: response.data.contact,
        role: response.data.role,
        admin: response.data.admin,
        creditScore: response.data.creditScore,
        status: response.data.status,
    };
    localStorage.setItem('campushub_user', JSON.stringify(authUser.value));
    return response.data;
}
export async function updateProfile(payload) {
    const response = await api.put('/users/me', payload);
    authUser.value = {
        userId: response.data.id,
        studentNo: response.data.studentNo,
        nickname: response.data.nickname,
        college: response.data.college,
        contact: response.data.contact,
        role: response.data.role,
        admin: response.data.admin,
        creditScore: response.data.creditScore,
        status: response.data.status,
    };
    localStorage.setItem('campushub_user', JSON.stringify(authUser.value));
    return response.data;
}
export async function listRequests() {
    const response = await api.get('/requests');
    return response.data;
}
export async function getRequest(requestId) {
    const response = await api.get(`/requests/${requestId}`);
    return response.data;
}
export async function createRequest(payload) {
    const response = await api.post('/requests', payload);
    return response.data;
}
export async function listOrders() {
    const response = await api.get('/orders');
    return response.data;
}
export async function getOrder(orderId) {
    const response = await api.get(`/orders/${orderId}`);
    return response.data;
}
export async function acceptRequest(requestId) {
    const response = await api.post(`/orders/${requestId}/accept`);
    return response.data;
}
export async function confirmOrder(orderId) {
    const response = await api.post(`/orders/${orderId}/confirm`);
    return response.data;
}
export async function startOrder(orderId) {
    const response = await api.post(`/orders/${orderId}/start`);
    return response.data;
}
export async function completeOrder(orderId) {
    const response = await api.post(`/orders/${orderId}/complete`);
    return response.data;
}
export async function submitReview(orderId, payload) {
    const response = await api.post(`/orders/${orderId}/reviews`, payload);
    return response.data;
}
export async function listNotifications() {
    const response = await api.get('/notifications');
    return response.data;
}
export async function markNotificationRead(id) {
    const response = await api.post(`/notifications/${id}/read`);
    return response.data;
}
export async function listPendingRequests() {
    const response = await api.get('/admin/requests/pending');
    return response.data;
}
export async function approveRequest(requestId) {
    const response = await api.post(`/admin/requests/${requestId}/approve`);
    return response.data;
}
export async function rejectRequest(requestId, reason) {
    const response = await api.post(`/admin/requests/${requestId}/reject`, { reason });
    return response.data;
}
export async function listUsers() {
    const response = await api.get('/admin/users');
    return response.data;
}
export async function disableUser(userId) {
    const response = await api.post(`/admin/users/${userId}/disable`);
    return response.data;
}
export async function enableUser(userId) {
    const response = await api.post(`/admin/users/${userId}/enable`);
    return response.data;
}
export default api;
//# sourceMappingURL=api.js.map