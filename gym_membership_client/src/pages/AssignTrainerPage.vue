<template>
  <q-page class="bg-grey-10 column q-pa-md items-center">
    <!-- HEADER -->
    <div class="q-mt-xl q-mb-md text-center">
      <div class="text-h5 text-white text-weight-bold">Gym App</div>
      <div class="text-subtitle2 text-grey-4 q-mt-xs">Choose your trainer</div>
    </div>

    <!-- CARD ASSIGN TRAINER -->
    <q-card
      class="bg-grey-9 text-white rounded-borders shadow-4"
      style="min-width: 320px; max-width: 420px"
    >
      <q-card-section>
        <div class="text-subtitle1 text-center text-weight-bold">Assign trainer</div>
        <div class="text-caption text-center text-grey-4 q-mt-xs">
          Pick a trainer for your workouts
        </div>
      </q-card-section>

      <q-card-section>
        <div v-if="loadingTrainers" class="row justify-center q-my-md">
          <q-spinner size="lg" />
        </div>

        <div v-else>
          <q-list bordered separator class="rounded-borders bg-grey-8 text-white">
            <q-item v-for="t in trainers" :key="t.id" clickable @click="selectedTrainerId = t.id">
              <q-item-section>
                <q-item-label class="text-subtitle2">
                  {{ t.name }}
                </q-item-label>
                <q-item-label caption>
                  {{ t.email }}
                </q-item-label>
              </q-item-section>

              <q-item-section side>
                <q-radio v-model="selectedTrainerId" :val="t.id" />
              </q-item-section>
            </q-item>
          </q-list>

          <div v-if="!trainers.length" class="text-caption text-grey q-mt-md">
            No trainers available. Please contact the gym admin.
          </div>
        </div>
      </q-card-section>

      <q-card-actions align="around" class="q-pb-md q-pt-none">
        <q-btn
          color="pink-5"
          class="full-width"
          :disable="!selectedTrainerId || saving"
          :loading="saving"
          label="Assign trainer"
          @click="assignTrainer"
        />
      </q-card-actions>
    </q-card>

    <q-btn flat color="primary" label="Back to home" class="q-mt-lg" @click="goHome" />
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'

const router = useRouter()
const $q = useQuasar()

const trainers = ref([])
const loadingTrainers = ref(false)
const selectedTrainerId = ref(null)
const saving = ref(false)

const memberId = ref(localStorage.getItem('memberId') || null)
const email = ref(localStorage.getItem('email') || '')

// ----------------- helpers -----------------
async function loadTrainers() {
  loadingTrainers.value = true
  try {
    // GET /api/trainers (baseURL are deja /api în boot/axios.js)
    const { data } = await api.get('/trainers')
    trainers.value = data
  } catch (err) {
    console.error('LOAD TRAINERS ERROR', err)
    $q.notify({ type: 'negative', message: 'Could not load trainers' })
  } finally {
    loadingTrainers.value = false
  }
}

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

async function assignTrainer() {
  if (!selectedTrainerId.value) {
    $q.notify({ type: 'warning', message: 'Select a trainer first' })
    return
  }

  if (!memberId.value) {
    await ensureMemberId()
  }

  if (!memberId.value) {
    $q.notify({
      type: 'negative',
      message: 'Member id not found. Try to login again or contact admin.',
    })
    return
  }

  saving.value = true
  try {
    // POST /api/members/assign-trainer
    await api.post('/members/assign-trainer', {
      memberId: Number(memberId.value),
      trainerId: selectedTrainerId.value,
    })

    $q.notify({ type: 'positive', message: 'Trainer assigned successfully' })
    router.push('/home')
  } catch (err) {
    console.error('ASSIGN TRAINER ERROR', err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.error || 'Could not assign trainer',
    })
  } finally {
    saving.value = false
  }
}

function goHome() {
  router.push('/home')
}

onMounted(() => {
  ensureMemberId()
  loadTrainers()
})
</script>

<style scoped>
.rounded-borders {
  border-radius: 18px;
}
</style>
