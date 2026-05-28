<template>
  <section class="order-list-view">
    <h1>我的订单</h1>

    <div v-if="loading">正在加载订单...</div>
    <div v-else>
      <div v-if="orders.length === 0" class="notice">你当前没有订单。</div>
      <div v-else class="orders-grid">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <h2>订单 #{{ order.id }}</h2>
          <p><strong>对应需求：</strong>{{ order.requestId }}</p>
          <p><strong>订单状态：</strong>{{ order.status }}</p>
          <router-link :to="`/orders/${order.id}`">查看详情</router-link>
        </div>
      </div>
    </div>

    <p class="message error" v-if="errorMessage">{{ errorMessage }}</p>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { listOrders, OrderResponse } from '../api';

const orders = ref<OrderResponse[]>([]);
const loading = ref(false);
const errorMessage = ref('');

async function loadOrders() {
  loading.value = true;
  errorMessage.value = '';
  try {
    orders.value = await listOrders();
  } catch (error) {
    errorMessage.value = '无法加载订单，请稍后重试。';
  } finally {
    loading.value = false;
  }
}

onMounted(loadOrders);
</script>

<style scoped>
.order-list-view {
  background: white;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.06);
  max-width: 980px;
  margin: 0 auto;
}

.orders-grid {
  display: grid;
  gap: 18px;
}

.order-card {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 20px;
  background: #f8fafc;
}

.order-card h2 {
  margin-top: 0;
}

.order-card a {
  display: inline-block;
  margin-top: 12px;
  padding: 10px 16px;
  border-radius: 10px;
  background: #3b82f6;
  color: white;
  text-decoration: none;
  font-weight: 700;
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
