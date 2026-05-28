<template>
  <header class="nav-bar">
    <div class="brand">CampusHub</div>
    <nav>
      <router-link to="/">首页</router-link>
      <router-link v-if="currentUser" to="/requests">需求列表</router-link>
      <router-link v-if="currentUser" to="/requests/new">发布需求</router-link>
      <router-link v-if="currentUser" to="/orders">我的订单</router-link>
      <router-link v-if="currentUser" to="/notifications">通知</router-link>
      <router-link v-if="!currentUser" to="/login">登录</router-link>
      <router-link v-if="!currentUser" to="/register">注册</router-link>
      <router-link v-if="currentUser" to="/profile">个人资料</router-link>
      <router-link v-if="currentUser?.admin" to="/admin">管理员</router-link>
      <a v-if="currentUser" href="#" @click.prevent="handleLogout">登出</a>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { authUser, logout } from '../api';

const router = useRouter();
const currentUser = computed(() => authUser.value);

async function handleLogout() {
  await logout();
  router.push('/login');
}
</script>

<style scoped>
.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  background: white;
  border-bottom: 1px solid #eaecf0;
}

.brand {
  font-weight: 800;
  font-size: 1.1rem;
}

nav {
  display: flex;
  gap: 18px;
}

a,
router-link {
  color: #334155;
  text-decoration: none;
}

a.router-link-active,
.router-link-active {
  color: #3b82f6;
  font-weight: 700;
}
</style>
