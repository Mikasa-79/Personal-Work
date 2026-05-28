<template>
  <section class="order-detail-view">
    <h1>订单详情</h1>

    <div v-if="loading">正在加载订单详情...</div>
    <div v-else-if="order">
      <div class="order-card">
        <p><strong>订单 ID：</strong>{{ order.id }}</p>
        <p><strong>对应需求：</strong>{{ order.requestId }}</p>
        <p><strong>请求方：</strong>{{ order.requesterId }}</p>
        <p><strong>服务方：</strong>{{ order.providerId }}</p>
        <p><strong>当前状态：</strong>{{ order.status }}</p>
      </div>

      <div class="actions">
        <button v-if="showConfirm" @click="handleConfirm">确认订单</button>
        <button v-if="showStart" @click="handleStart">开始服务</button>
        <button v-if="showComplete" @click="handleComplete">完成订单</button>
      </div>
    </div>

    <div v-else class="notice">未找到订单详情。</div>

    <p class="message success" v-if="successMessage">{{ successMessage }}</p>
    <p class="message error" v-if="errorMessage">{{ errorMessage }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { authUser, completeOrder, confirmOrder, getOrder, OrderResponse, startOrder } from '../api';

const route = useRoute();
const order = ref<OrderResponse | null>(null);
const loading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');

const currentUser = authUser;
const orderId = Number(route.params.orderId ?? 0);

const showConfirm = ref(false);
const showStart = ref(false);
const showComplete = ref(false);

function updateActions() {
  if (!order.value || !currentUser.value) {
    showConfirm.value = false;
    showStart.value = false;
    showComplete.value = false;
    return;
  }
  showConfirm.value = order.value.status === 'ACCEPTED' && currentUser.value.userId === order.value.requesterId;
  showStart.value = order.value.status === 'CONFIRMED' && currentUser.value.userId === order.value.providerId;
  showComplete.value = (order.value.status === 'IN_PROGRESS' || order.value.status === 'CONFIRMED') &&
    (currentUser.value.userId === order.value.requesterId || currentUser.value.userId === order.value.providerId);
}

async function loadOrder() {
  loading.value = true;
  errorMessage.value = '';
  successMessage.value = '';
  try {
    order.value = await getOrder(orderId);
    updateActions();
  } catch (error) {
    errorMessage.value = '无法加载订单详情。';
  } finally {
    loading.value = false;
  }
}

async function handleConfirm() {
  if (!order.value) return;
  errorMessage.value = '';
  successMessage.value = '';
  try {
    order.value = await confirmOrder(order.value.id);
    successMessage.value = '订单已确认。';
    updateActions();
  } catch (error) {
    errorMessage.value = '确认失败，请稍后重试。';
  }
}

async function handleStart() {
  if (!order.value) return;
  errorMessage.value = '';
  successMessage.value = '';
  try {
    order.value = await startOrder(order.value.id);
    successMessage.value = '服务已开始。';
    updateActions();
  } catch (error) {
    errorMessage.value = '开始失败，请稍后重试。';
  }
}

async function handleComplete() {
  if (!order.value) return;
  errorMessage.value = '';
  successMessage.value = '';
  try {
    order.value = await completeOrder(order.value.id);
    successMessage.value = '订单已完成。';
    updateActions();
  } catch (error) {
    errorMessage.value = '完成订单失败，请稍后重试。';
  }
}

onMounted(loadOrder);
</script>

<style scoped>
.order-detail-view {
  background: white;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.06);
  max-width: 720px;
  margin: 0 auto;
}

.order-card {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 20px;
  background: #f8fafc;
  margin-bottom: 24px;
}

.order-card p {
  margin: 10px 0;
}

.actions {
  display: flex;
  gap: 12px;
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
