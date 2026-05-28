<template>
  <div class="auth-screen">
    <h1>登录 CampusHub</h1>
    <form @submit.prevent="submitLogin">
      <label>
        学号
        <input v-model="studentNo" required minlength="6" />
      </label>
      <label>
        密码
        <input v-model="password" type="password" required minlength="6" />
      </label>
      <button type="submit">登录</button>
    </form>
    <p>
      还没有账号？
      <router-link to="/register">去注册</router-link>
    </p>
    <p class="error" v-if="errorMessage">{{ errorMessage }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login, setAuthData } from '../api';

const studentNo = ref('');
const password = ref('');
const errorMessage = ref('');
const router = useRouter();

async function submitLogin() {
  errorMessage.value = '';
  try {
    const result = await login({ studentNo: studentNo.value, password: password.value });
    setAuthData(result);
    router.push('/');
  } catch (error) {
    errorMessage.value = '登录失败，请检查学号和密码。';
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
  background-color: #3b82f6;
  color: white;
  font-weight: 700;
  cursor: pointer;
}

button:hover {
  background-color: #2563eb;
}

.error {
  margin-top: 16px;
  color: #dc2626;
}
</style>
