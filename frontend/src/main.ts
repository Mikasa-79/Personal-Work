import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { loadAuthToken } from './api';

loadAuthToken();

createApp(App).use(router).mount('#app');
