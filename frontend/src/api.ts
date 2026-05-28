import axios from 'axios';
import { ref } from 'vue';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface AuthResponse {
  token: string;
  refreshToken: string;
  userId: number;
  studentNo: string;
  nickname: string;
  role: string;
  admin: boolean;
  creditScore: number;
  status?: string;
}

export interface LoginPayload {
  studentNo: string;
  password: string;
}

export interface RegisterPayload extends LoginPayload {
  nickname: string;
}

export interface UserProfile {
  userId: number;
  studentNo: string;
  nickname: string;
  college?: string;
  contact?: string;
  role: string;
  admin: boolean;
  creditScore: number;
  status?: string;
}

export interface ProfileResponse {
  id: number;
  studentNo: string;
  nickname: string;
  college?: string;
  contact?: string;
  role: string;
  admin: boolean;
  creditScore: number;
  status?: string;
}

export interface UpdateProfilePayload {
  nickname?: string;
  college?: string;
  contact?: string;
}

export interface HelpRequestPayload {
  title: string;
  description: string;
  location: string;
  expectedTime: string;
  reward?: number;
  category?: string;
}

export interface HelpRequestResponse {
  id: number;
  title: string;
  description: string;
  location: string;
  expectedTime: string;
  reward: number;
  category: string;
  status: string;
  publisherId: number;
  publisherNickname: string;
}

export interface OrderResponse {
  id: number;
  requestId: number;
  requesterId: number;
  providerId: number;
  status: string;
}

export interface ReviewPayload {
  rating: number;
  comment: string;
}

export interface NotificationResponse {
  id: number;
  type: string;
  title: string;
  content: string;
  readFlag: boolean;
  relatedId: number;
}

export interface AuditRequestResponse {
  id: number;
  title: string;
  status: string;
  auditStatus: string;
  publisherId: number;
  publisherNickname: string;
}

export interface UserAdminResponse {
  id: number;
  studentNo: string;
  nickname: string;
  role: string;
  admin: boolean;
  creditScore: number;
  status: string;
}

export const authUser = ref<UserProfile | null>(null);

export function setAuthToken(token: string | null) {
  if (token) {
    api.defaults.headers.common.Authorization = `Bearer ${token}`;
    localStorage.setItem('campushub_token', token);
  } else {
    delete api.defaults.headers.common.Authorization;
    localStorage.removeItem('campushub_token');
  }
}

export function setAuthData(response: AuthResponse) {
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
    } catch {
      authUser.value = null;
    }
  }
  return token;
}

function getStoredRefreshToken() {
  return localStorage.getItem('campushub_refresh_token');
}

let refreshPromise: Promise<AuthResponse> | null = null;

api.interceptors.response.use(
  response => response,
  async error => {
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
      } catch (refreshError) {
        clearAuth();
        return Promise.reject(refreshError);
      } finally {
        refreshPromise = null;
      }
    }

    if (status === 401) {
      clearAuth();
    }

    return Promise.reject(error);
  }
);

export async function login(payload: LoginPayload) {
  const response = await api.post<AuthResponse>('/auth/login', payload);
  return response.data;
}

export async function register(payload: RegisterPayload) {
  const response = await api.post<AuthResponse>('/auth/register', payload);
  return response.data;
}

export async function logout() {
  try {
    await api.post('/auth/logout');
  } finally {
    clearAuth();
  }
}

export async function refreshToken(refreshToken: string) {
  const response = await api.post<AuthResponse>('/auth/refresh', { refreshToken });
  return response.data;
}

export async function getCurrentUser() {
  const response = await api.get<ProfileResponse>('/users/me');
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

export async function updateProfile(payload: UpdateProfilePayload) {
  const response = await api.put<ProfileResponse>('/users/me', payload);
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
  const response = await api.get<HelpRequestResponse[]>('/requests');
  return response.data;
}

export async function getRequest(requestId: number) {
  const response = await api.get<HelpRequestResponse>(`/requests/${requestId}`);
  return response.data;
}

export async function createRequest(payload: HelpRequestPayload) {
  const response = await api.post<HelpRequestResponse>('/requests', payload);
  return response.data;
}

export async function listOrders() {
  const response = await api.get<OrderResponse[]>('/orders');
  return response.data;
}

export async function getOrder(orderId: number) {
  const response = await api.get<OrderResponse>(`/orders/${orderId}`);
  return response.data;
}

export async function acceptRequest(requestId: number) {
  const response = await api.post<OrderResponse>(`/orders/${requestId}/accept`);
  return response.data;
}

export async function confirmOrder(orderId: number) {
  const response = await api.post<OrderResponse>(`/orders/${orderId}/confirm`);
  return response.data;
}

export async function startOrder(orderId: number) {
  const response = await api.post<OrderResponse>(`/orders/${orderId}/start`);
  return response.data;
}

export async function completeOrder(orderId: number) {
  const response = await api.post<OrderResponse>(`/orders/${orderId}/complete`);
  return response.data;
}

export async function submitReview(orderId: number, payload: ReviewPayload) {
  const response = await api.post<OrderResponse>(`/orders/${orderId}/reviews`, payload);
  return response.data;
}

export async function listNotifications() {
  const response = await api.get<NotificationResponse[]>('/notifications');
  return response.data;
}

export async function markNotificationRead(id: number) {
  const response = await api.post<NotificationResponse>(`/notifications/${id}/read`);
  return response.data;
}

export async function listPendingRequests() {
  const response = await api.get<AuditRequestResponse[]>('/admin/requests/pending');
  return response.data;
}

export async function approveRequest(requestId: number) {
  const response = await api.post<HelpRequestResponse>(`/admin/requests/${requestId}/approve`);
  return response.data;
}

export async function rejectRequest(requestId: number, reason: string) {
  const response = await api.post<HelpRequestResponse>(`/admin/requests/${requestId}/reject`, { reason });
  return response.data;
}

export async function listUsers() {
  const response = await api.get<UserAdminResponse[]>('/admin/users');
  return response.data;
}

export async function disableUser(userId: number) {
  const response = await api.post<UserAdminResponse>(`/admin/users/${userId}/disable`);
  return response.data;
}

export async function enableUser(userId: number) {
  const response = await api.post<UserAdminResponse>(`/admin/users/${userId}/enable`);
  return response.data;
}

export default api;
