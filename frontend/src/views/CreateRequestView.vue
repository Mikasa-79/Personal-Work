<template>
  <section class="create-request-view">
    <h1>发布需求</h1>

    <form @submit.prevent="submitRequest" class="request-form">
      <label>
        标题
        <input v-model="title" type="text" required minlength="4" />
      </label>

      <label>
        分类
        <input v-model="category" type="text" placeholder="如：搬运、辅导、跑腿" />
      </label>

      <label>
        描述
        <textarea v-model="description" required rows="5"></textarea>
      </label>

      <label>
        地点
        <input v-model="location" type="text" required />
      </label>

      <label>
        期望时间
        <input v-model="expectedTime" type="datetime-local" required />
      </label>

      <label>
        报酬（元）
        <input v-model.number="reward" type="number" min="0" />
      </label>

      <button type="submit" :disabled="submitting">提交需求</button>
    </form>

    <p class="message success" v-if="successMessage">{{ successMessage }}</p>
    <p class="message error" v-if="errorMessage">{{ errorMessage }}</p>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { createRequest, HelpRequestPayload } from '../api';

const router = useRouter();
const title = ref('');
const category = ref('');
const description = ref('');
const location = ref('');
const expectedTime = ref('');
const reward = ref<number | null>(null);
const submitting = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

function normalizeLocalDateTime(value: string) {
  if (!value) {
    return value;
  }
  // datetime-local returns yyyy-MM-ddTHH:mm, backend expects seconds as well
  return value.length === 16 ? `${value}:00` : value;
}

async function submitRequest() {
  errorMessage.value = '';
  successMessage.value = '';
  if (!expectedTime.value) {
    errorMessage.value = '请填写期望时间。';
    return;
  }

  const payload: HelpRequestPayload = {
    title: title.value,
    description: description.value,
    location: location.value,
    expectedTime: normalizeLocalDateTime(expectedTime.value),
    reward: reward.value ?? 0,
    category: category.value,
  };

  submitting.value = true;
  try {
    await createRequest(payload);
    successMessage.value = '需求已发布，正在跳转到需求列表。';
    setTimeout(() => router.push('/requests'), 800);
  } catch (error: any) {
    errorMessage.value = error?.response?.data || '发布失败，请稍后重试。';
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.create-request-view {
  background: white;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.06);
  max-width: 760px;
  margin: 0 auto;
}

.request-form label {
  display: block;
  margin-bottom: 18px;
  font-weight: 600;
}

.request-form input,
.request-form textarea {
  width: 100%;
  padding: 12px 14px;
  margin-top: 8px;
  border: 1px solid #d8dce5;
  border-radius: 12px;
  font-size: 1rem;
}

button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 14px 20px;
  border: none;
  border-radius: 12px;
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
</style>
