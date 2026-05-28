<template>
  <section class="request-detail-view">
    <h1>需求详情</h1>

    <div v-if="loading">正在加载需求信息...</div>
    <div v-else-if="request">
      <div class="detail-card">
        <h2>{{ request.title }}</h2>
        <p><strong>分类：</strong>{{ request.category }}</p>
        <p><strong>描述：</strong>{{ request.description }}</p>
        <p><strong>地点：</strong>{{ request.location }}</p>
        <p><strong>期望时间：</strong>{{ formatDate(request.expectedTime) }}</p>
        <p><strong>报酬：</strong>{{ request.reward }} 元</p>
        <p><strong>发布者：</strong>{{ request.publisherNickname }}</p>
        <p><strong>状态：</strong>{{ request.status }}</p>
      </div>

      <div class="actions">
        <button
          v-if="canAccept"
          :disabled="submitting"
          @click="onAccept(request.id)">
          接单
        </button>
      </div>

      <p class="message success" v-if="successMessage">{{ successMessage }}</p>
      <p class="message error" v-if="errorMessage">{{ errorMessage }}</p>
    </div>

    <div v-else class="notice">未找到该需求。</div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { authUser, getRequest, HelpRequestResponse, acceptRequest } from '../api';

const route = useRoute();
const router = useRouter();
const request = ref<HelpRequestResponse | null>(null);
const loading = ref(false);
const submitting = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

const currentUser = computed(() => authUser.value);
const canAccept = computed(() => {
  return !!request.value && !!currentUser.value && request.value.publisherId !== currentUser.value.userId && request.value.status === 'OPEN';
});

async function loadRequest() {
  loading.value = true;
  errorMessage.value = '';
  try {
    const id = Number(route.params.requestId ?? 0);
    request.value = await getRequest(id);
  } catch (error) {
    errorMessage.value = '无法加载需求，请稍后重试。';
  } finally {
    loading.value = false;
  }
}

function formatDate(value: string) {
  return new Date(value).toLocaleString();
}

async function onAccept(requestId: number) {
  if (!currentUser.value) {
    errorMessage.value = '请先登录后进行接单。';
    return;
  }
  submitting.value = true;
  errorMessage.value = '';
  successMessage.value = '';
  try {
    const order = await acceptRequest(requestId);
    successMessage.value = '接单成功，正在跳转订单详情。';
    router.push(`/orders/${order.id}`);
  } catch (error) {
    errorMessage.value = '接单失败，请确认该需求仍可接单。';
  } finally {
    submitting.value = false;
  }
}

onMounted(loadRequest);
</script>

<style scoped>
.request-detail-view {
  background: white;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.06);
  max-width: 760px;
  margin: 0 auto;
}

.detail-card {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 24px;
  background: #f8fafc;
}

.detail-card h2 {
  margin-top: 0;
}

.detail-card p {
  margin: 10px 0;
}

.actions {
  margin-top: 20px;
}

button {
  padding: 12px 18px;
  border: none;
  border-radius: 10px;
  background-color: #3b82f6;
  color: white;
  font-weight: 700;
  cursor: pointer;
}

button:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
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
}

.success {
  color: #16a34a;
}

.error {
  color: #dc2626;
}
</style>
