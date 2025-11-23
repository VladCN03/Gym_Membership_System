<template>
  <q-page class="bg-grey-10 column q-pa-md items-center">
    <!-- HEADER -->
    <div class="q-mt-lg q-mb-md text-center">
      <div class="text-h5 text-white text-weight-bold">Gym App</div>
      <div class="text-subtitle2 text-grey-4 q-mt-xs">AI Coach</div>
    </div>

    <!-- CARD AI -->
    <q-card class="bg-grey-9 text-white rounded-borders shadow-4 ai-card">
      <q-card-section class="text-center">
        <q-icon name="smart_toy" size="50px" color="pink-5" />
        <div class="text-h6 text-weight-bold q-mt-sm">AI Recommendations</div>
        <div class="text-caption text-grey-4">Personalized guidance based on your profile</div>
      </q-card-section>

      <q-separator dark />

      <q-card-section>
        <!-- LOADING -->
        <div v-if="loading" class="row justify-center q-my-lg">
          <q-spinner size="40px" color="pink-5" />
        </div>

        <!-- REZULTAT AI -->
        <div v-else-if="aiText" class="q-pa-sm">
          <div class="ai-response text-grey-2">
            {{ aiText }}
          </div>
        </div>

        <!-- EMPTY STATE -->
        <div v-else class="text-grey-5 text-center q-my-md">
          Press the button to generate recommendations.
        </div>
      </q-card-section>

      <q-card-actions align="center">
        <q-btn
          color="pink-5"
          label="Generate AI Advice"
          class="full-width"
          :loading="loading"
          @click="askAi"
        />
      </q-card-actions>
    </q-card>

    <q-btn flat color="primary" label="Back to home" class="q-mt-xl full-width" @click="goHome" />
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'

const router = useRouter()
const $q = useQuasar()

const loading = ref(false)
const aiText = ref(null)

const memberId = ref(localStorage.getItem('memberId') || null)
const email = ref(localStorage.getItem('email') || '')

// dacă nu avem memberId în localStorage, îl căutăm după email
async function ensureMemberId() {
  if (memberId.value || !email.value) return

  try {
    const { data } = await api.get('/members/by-email', {
      params: { email: email.value },
    })
    memberId.value = data.id
    localStorage.setItem('memberId', data.id)
  } catch (err) {
    console.error('LOAD MEMBER BY EMAIL ERROR', err)
    $q.notify({
      type: 'negative',
      message: 'Cannot load your profile (member id). Please contact admin.',
    })
  }
}

async function askAi() {
  aiText.value = null
  loading.value = true

  try {
    // ne asigurăm că avem memberId
    if (!memberId.value) {
      await ensureMemberId()
    }

    if (!memberId.value) {
      loading.value = false
      $q.notify({
        type: 'negative',
        message: 'Member id not found. Try to login again or contact admin.',
      })
      return
    }

    // apel corect la backend: /api/ai/recommend/{memberId}
    const { data } = await api.post(`/ai/recommend/${memberId.value}`)

    // 🔥 aici extragem DOAR rationale
    let text =
      data.rationale || // <- primul câmp folosit
      data.recommendation ||
      data.text ||
      data.advice ||
      null

    if (!text) {
      // fallback, doar pentru debugging
      text = JSON.stringify(data, null, 2)
    }

    aiText.value = text
  } catch (err) {
    console.error('AI ERROR', err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.error || 'AI request failed',
    })
  } finally {
    loading.value = false
  }
}

function goHome() {
  router.push('/home')
}
</script>

<style scoped>
.rounded-borders {
  border-radius: 18px;
}

.ai-card {
  width: 100%;
  max-width: 420px;
}

.ai-response {
  white-space: pre-line;
  line-height: 1.45;
  font-size: 15px;
}
</style>
