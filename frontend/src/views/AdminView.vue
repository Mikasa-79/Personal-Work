<template>
  <section class="admin-view">
    <h1>管理员控制台</h1>

    <div class="admin-grid">
      <div class="admin-card">
        <h2>待审核请求</h2>
        <div v-if="loadingRequests">正在加载...</div>
        <div v-else-if="pendingRequests.length === 0">当前没有待审核请求。</div>
        <div v-else class="request-list">
          <div v-for="request in pendingRequests" :key="request.id" class="request-item">
            <div>
              <strong>{{ request.title }}</strong>
              <p>发布者：{{ request.publisherNickname }}（{{ request.publisherId }}）</p>
              <p>状态：{{ request.status }} / {{ request.auditStatus }}</p>
            </div>
            <div class="request-actions">
              <button @click="approve(request.id)">通过</button>
              <button class="reject" @click="reject(request.id)">驳回</button>
            </div>
          </div>
        </div>
      </div>

      <div class="admin-card">
        <h2>用户管理</h2>
        <div v-if="loadingUsers">正在加载...</div>
        <div v-else-if="users.length === 0">当前没有用户。</div>
        <div v-else class="user-list">
          <div v-for="user in users" :key="user.id" class="user-item">
            <div>
              <strong>{{ user.nickname }}（{{ user.studentNo }}）</strong>
              <p>角色：{{ user.role }}，信用：{{ user.creditScore }}，状态：{{ user.status }}</p>
            </div>
            <div class="user-actions">
              <button v-if="user.status !== 'DISABLED'" @click="toggleUser(user)">禁用</button>
              <button v-else @click="toggleUser(user)">启用</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <p class="message success" v-if="successMessage">{{ successMessage }}</p>
    <p class="message error" v-if="errorMessage">{{ errorMessage }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {
  approveRequest,
  disableUser,
  enableUser,
  listPendingRequests,
  listUsers,
  rejectRequest,
} from '../api';

const pendingRequests = ref<Array<{ id: number; title: string; status: string; auditStatus: string; publisherId: number; publisherNickname: string }>>([]);
const users = ref<Array<{ id: number; studentNo: string; nickname: string; role: string; admin: boolean; creditScore: number; status: string }>>([]);
const loadingRequests = ref(true);
const loadingUsers = ref(true);
const errorMessage = ref('');
const successMessage = ref('');

async function loadData() {
  loadingRequests.value = true;
  loadingUsers.value = true;
  errorMessage.value = '';
  successMessage.value = '';
  try {
    const [requests, userList] = await Promise.all([listPendingRequests(), listUsers()]);
    pendingRequests.value = requests;
    users.value = userList;
  } catch (error) {
    errorMessage.value = '加载管理员数据失败，请检查权限或重新登录。';
  } finally {
    loadingRequests.value = false;
    loadingUsers.value = false;
  }
}

async function approve(requestId: number) {
  errorMessage.value = '';
  successMessage.value = '';
  try {
    await approveRequest(requestId);
    successMessage.value = '请求已通过审批。';
    await loadData();
  } catch (error) {
    errorMessage.value = '审批失败，请稍后重试。';
  }
}

async function reject(requestId: number) {
  const reason = window.prompt('请输入驳回原因：');
  if (!reason) {
    return;
  }
  errorMessage.value = '';
  successMessage.value = '';
  try {
    await rejectRequest(requestId, reason);
    successMessage.value = '请求已驳回。';
    await loadData();
  } catch (error) {
    errorMessage.value = '驳回失败，请稍后重试。';
  }
}

async function toggleUser(user: { id: number; status: string }) {
  errorMessage.value = '';
  successMessage.value = '';
  try {
    if (user.status === 'DISABLED') {
      await enableUser(user.id);
      successMessage.value = `用户 ${user.id} 已启用。`;
    } else {
      await disableUser(user.id);
      successMessage.value = `用户 ${user.id} 已禁用。`;
    }
    await loadData();
  } catch (error) {
    errorMessage.value = '操作失败，请稍后重试。';
  }
}

onMounted(loadData);
</script>

<style scoped>
.admin-view {
  background: white;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.06);
  max-width: 980px;
  margin: 0 auto;
}

.admin-grid {
  display: grid;
  gap: 24px;
}

.admin-card {
  background: #f8fafc;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #e2e8f0;
}

.request-item,
.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 14px;
  border-bottom: 1px solid #e2e8f0;
}

.request-item:last-child,
.user-item:last-child {
  border-bottom: none;
}

.request-actions button,
.user-actions button {
  min-width: 92px;
  padding: 10px 14px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 700;
}

.request-actions button {
  background: #14b8a6;
  color: white;
}

.request-actions .reject {
  background: #ef4444;
}

.user-actions button {
  background: #3b82f6;
  color: white;
}

.message {
  margin-top: 18px;
  font-weight: 600;
}

.success {
  color: #16a34a;
}

.error {
  color: #dc2626;
}
</style>
