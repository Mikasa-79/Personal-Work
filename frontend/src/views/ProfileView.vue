<template>
  <section class="profile-view">
    <h1>个人资料</h1>

    <div v-if="loading">正在加载用户信息...</div>
    <div v-else>
      <div class="profile-card">
        <p><strong>学号：</strong>{{ profile?.studentNo }}</p>
        <p><strong>昵称：</strong>{{ profile?.nickname }}</p>
        <p><strong>角色：</strong>{{ profile?.role }}</p>
        <p><strong>管理员：</strong>{{ profile?.admin ? '是' : '否' }}</p>
        <p><strong>信用分：</strong>{{ profile?.creditScore }}</p>
        <p><strong>状态：</strong>{{ profile?.status ?? 'ACTIVE' }}</p>
      </div>

      <form @submit.prevent="saveProfile" class="profile-form">
        <label>
          昵称
          <input v-model="nickname" required minlength="2" />
        </label>
        <label>
          学院
          <input v-model="college" />
        </label>
        <label>
          联系方式
          <input v-model="contact" />
        </label>
        <button type="submit">保存</button>
      </form>

      <p class="message success" v-if="successMessage">{{ successMessage }}</p>
      <p class="message error" v-if="errorMessage">{{ errorMessage }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { getCurrentUser, ProfileResponse, updateProfile } from '../api';

const profile = ref<ProfileResponse | null>(null);
const nickname = ref('');
const college = ref('');
const contact = ref('');
const loading = ref(true);
const errorMessage = ref('');
const successMessage = ref('');

async function loadProfile() {
  loading.value = true;
  errorMessage.value = '';
  try {
    const current = await getCurrentUser();
    profile.value = current;
    nickname.value = current.nickname;
    college.value = current.college ?? '';
    contact.value = current.contact ?? '';
  } catch (error) {
    errorMessage.value = '无法加载用户信息，请重新登录后重试。';
  } finally {
    loading.value = false;
  }
}

async function saveProfile() {
  errorMessage.value = '';
  successMessage.value = '';
  try {
    const updated = await updateProfile({
      nickname: nickname.value,
      college: college.value,
      contact: contact.value,
    });
    profile.value = updated;
    successMessage.value = '资料已保存。';
  } catch (error) {
    errorMessage.value = '保存失败，请稍后重试。';
  }
}

onMounted(loadProfile);
</script>

<style scoped>
.profile-view {
  background: white;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.06);
  max-width: 720px;
  margin: 0 auto;
}

.profile-card {
  background: #f8fafc;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #e2e8f0;
  margin-bottom: 24px;
}

.profile-card p {
  margin: 10px 0;
}

.profile-form label {
  display: block;
  margin-bottom: 18px;
  font-weight: 600;
}

.profile-form input {
  width: 100%;
  padding: 10px 12px;
  margin-top: 8px;
  border: 1px solid #d8dce5;
  border-radius: 10px;
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

button:hover {
  background-color: #2563eb;
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
