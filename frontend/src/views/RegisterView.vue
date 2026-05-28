<template>
  <div class="auth-screen">
    <h1>注册 CampusHub</h1>
    <form @submit.prevent="submitRegister">
      <label>
        学号
        <input v-model="studentNo" required minlength="6" />
      </label>
      <label>
        昵称
        <input v-model="nickname" required minlength="2" />
      </label>
      <label>
        密码
        <input v-model="password" type="password" required minlength="8" />
      </label>
      <button type="submit">注册</button>
    </form>
    <p>
      已有账号？
      <router-link to="/login">去登录</router-link>
    </p>
    <p class="error" v-if="errorMessage">{{ errorMessage }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { register, setAuthData } from '../api';

const studentNo = ref('');
const nickname = ref('');
const password = ref('');
const errorMessage = ref('');
const router = useRouter();

async function submitRegister() {
  errorMessage.value = '';
  try {
    const result = await register({ studentNo: studentNo.value, nickname: nickname.value, password: password.value });
    setAuthData(result);
    router.push('/');
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const detail = error.response?.data;
      const status = error.response?.status;
      errorMessage.value = typeof detail === 'string'
        ? `${detail} (${status})`
        : `注册失败，请检查输入后重试。(${status ?? 'unknown'})`;
    } else {
      errorMessage.value = '注册失败，请检查输入后重试。';
    }
  }
}
</script>

<style scoped>
.auth-screen {
  max-width: 420px;
  margin: 32px auto;
  padding: 28px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 18px 44px rgba(0, 0, 0, 0.08);
}

label {
  display: block;
  margin-bottom: 18px;
  font-weight: 600;
}

input {
  width: 100%;
  padding: 10px 12px;
  margin-top: 8px;
  border: 1px solid #d8dce5;
  border-radius: 10px;
}

button {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 10px;
  background-color: #10b981;
  color: white;
  font-weight: 700;
  cursor: pointer;
}

button:hover {
  background-color: #059669;
}

.error {
  margin-top: 16px;
  color: #dc2626;
}
</style>
