<template>
  <q-page class="bg-grey-10 column q-pa-md items-center">
    <!-- HEADER, la fel ca pe Home -->
    <div class="q-mt-xl q-mb-md text-center">
      <div class="text-h5 text-white text-weight-bold">Gym App</div>
      <div class="text-subtitle2 text-grey-4 q-mt-xs">Welcome back</div>
    </div>

    <!-- CARD LOGIN -->
    <q-card
      class="bg-grey-9 text-white rounded-borders shadow-4"
      style="min-width: 320px; max-width: 420px"
    >
      <q-card-section>
        <div class="text-subtitle1 text-center text-weight-bold">Login</div>
      </q-card-section>

      <q-card-section class="q-pt-md">
        <q-input v-model="email" type="email" label="Email" outlined dense dark class="q-mb-md">
          <template v-slot:prepend>
            <q-icon name="email" />
          </template>
        </q-input>

        <q-input v-model="password" type="password" label="Password" outlined dense dark>
          <template v-slot:prepend>
            <q-icon name="lock" />
          </template>
        </q-input>

        <!-- 🔥 Forgot password -->
        <div class="text-right q-mt-sm">
          <q-btn
            flat
            dense
            color="pink-4"
            label="Forgot password?"
            class="text-caption"
            @click="showForgotDialog = true"
          />
        </div>
      </q-card-section>

      <q-card-actions align="around" class="q-pb-md q-pt-none column q-gutter-sm">
        <q-btn
          color="pink-5"
          label="Login"
          class="full-width"
          :loading="loading"
          @click="doLogin"
        />
        <q-btn
          flat
          color="grey-3"
          label="Create account"
          size="sm"
          class="full-width"
          @click="goRegister"
        />
      </q-card-actions>
    </q-card>

    <!-- Setări server (Dev) -->
    <div class="q-mt-lg" style="width: 320px; max-width: 420px">
      <q-expansion-item
        dense
        dense-toggle
        expand-separator
        icon="settings"
        label="Setări Server (Dev)"
        header-class="bg-grey-8 text-grey-2 rounded-borders"
        class="shadow-2 overflow-hidden rounded-borders bg-grey-9 text-white"
      >
        <div class="q-pa-md">
          <div class="text-caption text-grey-3 q-mb-sm">
            URL Curent: <br />
            <span class="text-weight-bold text-pink-4">{{ currentApiUrl }}</span>
          </div>

          <q-input
            v-model="newIpAddress"
            label="Noua adresă (ex: 192.168.1.5:8080)"
            dense
            outlined
            dark
            color="pink-5"
            placeholder="http://..."
          />

          <div class="row q-mt-md justify-between">
            <q-btn color="negative" label="Reset" size="sm" flat @click="resetIp" />
            <q-btn color="pink-5" label="Setează IP" size="sm" unelevated @click="updateIp" />
          </div>
        </div>
      </q-expansion-item>
    </div>

    <!-- 🔐 Dialog schimbare parolă -->
    <q-dialog v-model="showForgotDialog" persistent>
      <q-card class="bg-grey-9 text-white" style="min-width: 320px; max-width: 380px">
        <q-card-section>
          <div class="text-subtitle1 text-weight-bold">Reset password</div>
          <div class="text-caption text-grey-4 q-mt-xs">Enter your email and the new password.</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input v-model="forgotEmail" type="email" label="Email" outlined dense class="q-mb-md">
            <template v-slot:prepend>
              <q-icon name="email" />
            </template>
          </q-input>

          <q-input
            v-model="forgotPassword"
            type="password"
            label="New password"
            outlined
            dense
            class="q-mb-md"
          >
            <template v-slot:prepend>
              <q-icon name="lock" />
            </template>
          </q-input>

          <q-input
            v-model="forgotPasswordConfirm"
            type="password"
            label="Confirm password"
            outlined
            dense
          >
            <template v-slot:prepend>
              <q-icon name="lock" />
            </template>
          </q-input>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md">
          <q-btn flat label="Cancel" color="grey-4" @click="closeForgotDialog" />
          <q-btn
            color="pink-5"
            label="Change password"
            :loading="forgotLoading"
            @click="confirmForgotPassword"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'boot/axios'

const router = useRouter()
const { proxy } = getCurrentInstance()
const $q = useQuasar()

const email = ref('')
const password = ref('')

const currentApiUrl = api.defaults.baseURL
const newIpAddress = ref('')
const loading = ref(false)

// --- Forgot password dialog state ---
const showForgotDialog = ref(false)
const forgotEmail = ref('')
const forgotPassword = ref('')
const forgotPasswordConfirm = ref('')
const forgotLoading = ref(false)

async function doLogin() {
  if (!email.value || !password.value) {
    $q.notify({ type: 'negative', message: 'Enter email and password' })
    return
  }

  loading.value = true
  try {
    const { data } = await api.post('/auth/login', {
      username: email.value,
      password: password.value,
    })

    localStorage.setItem('token', data.token)
    localStorage.setItem('email', data.email || email.value)
    localStorage.setItem('role', data.role || 'MEMBER')

    if (data.name) {
      localStorage.setItem('name', data.name)
    }
    if (data.memberId) {
      localStorage.setItem('memberId', data.memberId)
    }

    router.push('/home')
  } catch (err) {
    console.error('LOGIN ERROR', err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.error || 'Login failed',
    })
  } finally {
    loading.value = false
  }
}

function goRegister() {
  router.push('/register')
}

function updateIp() {
  if (!newIpAddress.value) return

  let urlToSet = newIpAddress.value
  if (!urlToSet.startsWith('http')) {
    urlToSet = 'http://' + urlToSet
  }
  proxy.$setApiUrl(urlToSet)
}

function resetIp() {
  proxy.$resetApiUrl()
}

function closeForgotDialog() {
  showForgotDialog.value = false
  forgotEmail.value = ''
  forgotPassword.value = ''
  forgotPasswordConfirm.value = ''
}

async function confirmForgotPassword() {
  if (!forgotEmail.value || !forgotPassword.value || !forgotPasswordConfirm.value) {
    $q.notify({ type: 'negative', message: 'Complete all fields' })
    return
  }

  if (forgotPassword.value !== forgotPasswordConfirm.value) {
    $q.notify({ type: 'negative', message: 'Passwords do not match' })
    return
  }

  forgotLoading.value = true
  try {
    await api.post('/auth/reset-password-simple', {
      email: forgotEmail.value,
      newPassword: forgotPassword.value,
    })

    $q.notify({ type: 'positive', message: 'Password changed successfully' })
    closeForgotDialog()

    // opțional: completăm email-ul pe pagina de login
    email.value = forgotEmail.value
  } catch (err) {
    console.error('RESET PASSWORD SIMPLE ERROR', err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.error || 'Could not change password',
    })
  } finally {
    forgotLoading.value = false
  }
}
</script>

<style scoped>
.rounded-borders {
  border-radius: 18px;
}
</style>
