import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import vuetify from './plugins/vuetify'

createApp(App).use(router).use(createPinia()).use(vuetify).mount('#app')
