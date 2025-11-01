<template>
  <v-container class="d-flex align-center justify-center" style="min-height: 70vh">
    <v-card width="400" class="pa-6">
      <div class="text-h6 mb-4">Admin Login</div>

      <!-- Formular: Enter trimite, fără reload de pagină -->
      <form @submit.prevent="submit">
        <v-text-field
          v-model="u"
          label="Username"
          autocomplete="username"
          :disabled="loading"
          class="mb-3"
        />
        <v-text-field
          v-model="p"
          label="Password"
          type="password"
          autocomplete="current-password"
          :disabled="loading"
        />

        <v-btn
          type="submit"
          block
          color="primary"
          class="mt-4"
          :loading="loading"
          :disabled="loading"
        >
          Login
        </v-btn>
      </form>

      <div v-if="err" class="text-error mt-3">{{ err }}</div>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import router from '@/router'
import { Auth, setToken } from '@/api'

const u = ref('')
const p = ref('')
const err = ref('')
const loading = ref(false)

async function submit() {
  if (!u.value || !p.value) {
    err.value = 'Completează username și parola'
    return
  }
  err.value = ''
  loading.value = true
  try {
    const { data } = await Auth.login(u.value, p.value)
    setToken(data.token)
    router.push('/') // redirect după login
  } catch {
    err.value = 'Credențiale invalide'
  } finally {
    loading.value = false
  }
}
</script>
