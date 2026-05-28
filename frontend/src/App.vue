<template>
  <div class="app-shell">
    <NavBar />
    <main>
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import NavBar from './components/NavBar.vue';
</script>

<style>
body {
  margin: 0;
  font-family: Inter, system-ui, sans-serif;
  background: #f4f7fb;
}

.app-shell {
  min-height: 100vh;
}

main {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px;
}
</style>
