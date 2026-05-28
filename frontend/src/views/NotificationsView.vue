<template>
  <section class="notifications-view">
    <h1>通知中心</h1>

    <div v-if="loading">正在加载通知...</div>
    <div v-else>
      <div v-if="notifications.length === 0" class="notice">当前没有通知。</div>
      <ul v-else class="notification-list">
        <li v-for="notification in notifications" :key="notification.id" :class="{ unread: !notification.readFlag }">
          <div class="notification-meta">
            <span class="notification-type">{{ notification.type }}</span>
            <span>{{ notification.title }}</span>
          </div>
          <p>{{ notification.content }}</p>
          <button v-if="!notification.readFlag" @click="markRead(notification.id)">标记为已读</button>
        </li>
      </ul>
    </div>

    <p class="message error" v-if="errorMessage">{{ errorMessage }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { listNotifications, markNotificationRead, NotificationResponse } from '../api';

const notifications = ref<NotificationResponse[]>([]);
const loading = ref(false);
const errorMessage = ref('');

function formatDate(value: string) {
  return new Date(value).toLocaleString();
}

async function loadNotifications() {
  loading.value = true;
  errorMessage.value = '';
  try {
    notifications.value = await listNotifications();
  } catch (error) {
    errorMessage.value = '无法加载通知，请稍后重试。';
  } finally {
    loading.value = false;
  }
}

async function markRead(notificationId: number) {
  errorMessage.value = '';
  try {
    await markNotificationRead(notificationId);
    await loadNotifications();
  } catch (error) {
    errorMessage.value = '标记为已读失败，请重试。';
  }
}

onMounted(loadNotifications);
</script>

<style scoped>
.notifications-view {
  background: white;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.06);
  max-width: 980px;
  margin: 0 auto;
}

.notification-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 16px;
}

.notification-list li {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 20px;
  background: #f8fafc;
}

.notification-list li.unread {
  border-color: #2563eb;
  background: #eff6ff;
}

.notification-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  color: #334155;
}

.notification-type {
  font-weight: 700;
}

button {
  margin-top: 12px;
  padding: 10px 14px;
  border: none;
  border-radius: 10px;
  background-color: #3b82f6;
  color: white;
  font-weight: 700;
  cursor: pointer;
}

.notice {
  padding: 20px;
  border-radius: 14px;
  background: #e2e8f0;
  color: #334155;
}

.message {
  margin-top: 18px;
  font-weight: 600;
  color: #dc2626;
}
</style>
