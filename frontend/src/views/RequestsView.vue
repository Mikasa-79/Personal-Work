<template>
  <section class="requests-view">
    <h1>需求列表</h1>

    <div v-if="!currentUser" class="notice">
      请先登录后查看需求列表并接单。
    </div>

    <div v-if="currentUser">
      <div class="filters">
        <input v-model.trim="keyword" placeholder="搜索标题、描述或地点" />
        <select v-model="categoryFilter">
          <option value="">全部分类</option>
          <option value="EXPRESS_PICKUP">快递代取</option>
          <option value="STUDY_TUTORING">学习辅导</option>
          <option value="SECOND_HAND">二手交易</option>
          <option value="TEAM_UP">活动组队</option>
          <option value="OTHER">其他</option>
        </select>
        <select v-model="ownerFilter">
          <option value="all">全部需求</option>
          <option value="mine">我发布的</option>
          <option value="available">可接单</option>
        </select>
      </div>

      <div v-if="loading">加载中...</div>
      <div v-else>
        <div v-if="filteredRequests.length === 0" class="notice">没有符合条件的需求。</div>
        <div v-else class="request-list">
          <div v-for="item in filteredRequests" :key="item.id" class="request-card">
            <div class="request-header">
              <router-link :to="`/requests/${item.id}`" class="request-title">{{ item.title }}</router-link>
              <span>{{ item.status }} / {{ item.category }}</span>
            </div>
            <p>{{ item.description }}</p>
            <p><strong>地点：</strong>{{ item.location }}</p>
            <p><strong>期望时间：</strong>{{ formatDate(item.expectedTime) }}</p>
            <p><strong>报酬：</strong>{{ item.reward }} 元</p>
            <p><strong>发布者：</strong>{{ item.publisherNickname }}</p>
            <button
              :disabled="item.publisherId === currentUser.userId"
              @click="onAccept(item.id)">
              {{ item.publisherId === currentUser.userId ? '无法接自己需求' : '接单' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <p class="message success" v-if="successMessage">{{ successMessage }}</p>
    <p class="message error" v-if="errorMessage">{{ errorMessage }}</p>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { authUser, HelpRequestResponse, acceptRequest, listRequests } from '../api';

const currentUser = computed(() => authUser.value);
const requests = ref<HelpRequestResponse[]>([]);
const keyword = ref('');
const categoryFilter = ref('');
const ownerFilter = ref('all');
const loading = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const router = useRouter();

const filteredRequests = computed(() => {
  const searchText = keyword.value.toLowerCase();

  return requests.value.filter(item => {
    const matchesKeyword = !searchText ||
      item.title.toLowerCase().includes(searchText) ||
      item.description.toLowerCase().includes(searchText) ||
      item.location.toLowerCase().includes(searchText);
    const matchesCategory = !categoryFilter.value || item.category === categoryFilter.value;
    const isMine = item.publisherId === currentUser.value?.userId;
    const matchesOwner =
      ownerFilter.value === 'all' ||
      (ownerFilter.value === 'mine' && isMine) ||
      (ownerFilter.value === 'available' && !isMine && item.status === 'OPEN');

    return matchesKeyword && matchesCategory && matchesOwner;
  });
});

async function loadRequests() {
  loading.value = true;
  errorMessage.value = '';
  successMessage.value = '';
  try {
    requests.value = await listRequests();
  } catch (error) {
    errorMessage.value = '无法加载需求列表，请稍后重试。';
  } finally {
    loading.value = false;
  }
}

function formatDate(value: string) {
  return new Date(value).toLocaleString();
}

async function onAccept(requestId: number) {
  errorMessage.value = '';
  successMessage.value = '';
  try {
    const order = await acceptRequest(requestId);
    successMessage.value = '接单成功，正在跳转到订单详情。';
    router.push(`/orders/${order.id}`);
  } catch (error) {
    errorMessage.value = '接单失败，请确认该需求仍可接单。';
  }
}

onMounted(loadRequests);
</script>

<style scoped>
.requests-view {
  background: white;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.06);
  max-width: 980px;
  margin: 0 auto;
}

.request-list {
  display: grid;
  gap: 18px;
}

.filters {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 160px 140px;
  gap: 12px;
  margin-bottom: 20px;
}

.filters input,
.filters select {
  min-width: 0;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  padding: 11px 12px;
  font: inherit;
  background: white;
}

.request-card {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 20px;
  background: #f8fafc;
}

.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 16px;
}

button {
  margin-top: 16px;
  padding: 12px 16px;
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

.notice {
  padding: 20px;
  border-radius: 14px;
  background: #e2e8f0;
  color: #334155;
}

@media (max-width: 720px) {
  .filters {
    grid-template-columns: 1fr;
  }

  .request-header {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
