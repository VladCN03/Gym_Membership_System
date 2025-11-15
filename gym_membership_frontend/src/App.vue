<template>
  <v-app>
    <v-layout class="app-layout">
      <!-- Bara de sus -->
      <v-app-bar app color="primary" dark flat>
        <v-app-bar-nav-icon class="d-md-none" @click="drawer = !drawer" />
        <v-app-bar-title>Gym Admin</v-app-bar-title>
        <v-spacer />
        <v-btn variant="text" @click="logout">Logout</v-btn>
      </v-app-bar>

      <!-- Meniul lateral -->
      <v-navigation-drawer
        app
        v-model="drawer"
        :temporary="smAndDown"
        :rail="mdAndUp"
        expand-on-hover
      >
        <v-list density="compact" nav>
          <v-list-item to="/" title="Dashboard" prepend-icon="mdi-view-dashboard" />
          <v-list-item
            to="/membership-types"
            title="Membership Types"
            prepend-icon="mdi-card-account-details"
          />
          <v-list-item to="/trainers" title="Trainers" prepend-icon="mdi-dumbbell" />
          <v-list-item to="/members" title="Members" prepend-icon="mdi-account-group" />
          <v-list-item to="/stats" title="Stats" prepend-icon="mdi-chart-bar" />
          <v-list-item
            title="AI Recommender"
            prepend-icon="mdi-robot-outline"
            to="/ai"
          ></v-list-item>
        </v-list>
      </v-navigation-drawer>

      <!-- Zona principală, scrollabilă -->
      <v-main class="app-main">
        <div class="main-content">
          <router-view />
        </div>
      </v-main>
    </v-layout>
  </v-app>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useDisplay } from 'vuetify'
import { clearToken } from '@/api'
import { useRouter } from 'vue-router'

const { smAndDown, mdAndUp } = useDisplay()
const drawer = ref(mdAndUp.value)
const router = useRouter()

watch(smAndDown, (isSmall) => {
  if (isSmall) drawer.value = false
})
watch(mdAndUp, (isDesktop) => {
  if (isDesktop) drawer.value = true
})

function logout() {
  clearToken()
  router.replace('/login')
}
</script>

<style>
/* 1️⃣ Eliminăm scroll global */
html,
body,
#app {
  height: 100%;
  margin: 0;
  overflow: hidden;
}

/* 2️⃣ Structura generală */
.v-application,
.v-application--wrap,
.app-layout {
  height: 100%;
  overflow: hidden;
  background-color: #f6f7fb;
}

/* 3️⃣ Zona principală scrollabilă */
.app-main {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden; /* ascunde scrollul sub bară */
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: calc(var(--v-layout-top) + 16px) 16px 16px calc(var(--v-layout-left) + 16px);
  box-sizing: border-box;
}
</style>
