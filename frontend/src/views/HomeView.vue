<template>
  <section class="home-view">
    <h1>CampusHub</h1>
    <div v-if="currentUser">
      <p>欢迎，{{ currentUser.nickname }}！</p>
      <p>你可以访问个人资料、需求列表、订单和通知页面，也可以发布新需求。</p>
      <div class="links">
        <router-link to="/requests">需求列表</router-link>
        <router-link to="/requests/new">发布需求</router-link>
        <router-link to="/orders">我的订单</router-link>
        <router-link to="/notifications">通知</router-link>
        <router-link to="/profile">个人资料</router-link>
        <router-link v-if="currentUser.admin" to="/admin">管理员页面</router-link>
      </div>
    </div>
    <div v-else>
      <p>欢迎使用 CampusHub。请先登录或注册账号，然后即可访问受保护的服务。</p>
      <div class="links">
        <router-link to="/login">登录</router-link>
        <router-link to="/register">注册</router-link>
      </div>
    </div>
    <div class="cards">
      <div class="card">
        <h2>最新进展</h2>
        <p>目前前端已支持用户资料、管理员审核和用户启用/禁用功能。</p>
      </div>
      <div class="card">
        <h2>提示</h2>
        <p>如果您的账号是管理员，系统会在导航栏中显示“管理员”入口。</p>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { authUser } from '../api';

const currentUser = computed(() => authUser.value);
</script>

<style scoped>
.home-view {
  background: white;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.06);
}

.links {
  margin: 18px 0;
  display: flex;
  gap: 16px;
}

.links a {
  padding: 10px 16px;
  border-radius: 12px;
  background: #eff6ff;
  color: #1d4ed8;
  text-decoration: none;
  font-weight: 700;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 18px;
  margin-top: 24px;
}

.card {
  background: #f8fafc;
  padding: 20px;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
}

.card h2 {
  margin-top: 0;
}
</style>
