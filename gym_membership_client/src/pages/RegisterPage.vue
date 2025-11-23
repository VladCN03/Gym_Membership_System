<template>
  <q-page class="bg-grey-10 column q-pa-md items-center">
    <!-- HEADER -->
    <div class="q-mt-xl q-mb-md text-center">
      <div class="text-h5 text-white text-weight-bold">Gym App</div>
      <div class="text-subtitle2 text-grey-4 q-mt-xs">Create your account</div>
    </div>

    <!-- CARD REGISTER -->
    <q-card
      class="bg-grey-9 text-white rounded-borders shadow-4"
      style="min-width: 320px; max-width: 420px"
    >
      <q-card-section>
        <div class="text-subtitle1 text-center text-weight-bold">Create account</div>
      </q-card-section>

      <q-card-section class="q-pt-md">
        <q-input v-model="name" label="Name" outlined dense dark class="q-mb-md">
          <template v-slot:prepend>
            <q-icon name="person" />
          </template>
        </q-input>

        <q-input v-model="email" label="Email" type="email" outlined dense dark class="q-mb-md">
          <template v-slot:prepend>
            <q-icon name="email" />
          </template>
        </q-input>

        <q-input
          v-model="password"
          label="Password"
          type="password"
          outlined
          dense
          dark
          class="q-mb-md"
        >
          <template v-slot:prepend>
            <q-icon name="lock" />
          </template>
        </q-input>

        <!-- câmpuri opționale -->
        <q-select
          v-model="goal"
          :options="goalOptions"
          label="Goal"
          outlined
          dense
          dark
          emit-value
          map-options
          class="q-mb-md"
        >
          <template v-slot:prepend>
            <q-icon name="flag" />
          </template>
        </q-select>

        <q-select
          v-model="experience"
          :options="experienceOptions"
          label="Experience"
          outlined
          dense
          dark
          emit-value
          map-options
          class="q-mb-md"
        >
          <template v-slot:prepend>
            <q-icon name="fitness_center" />
          </template>
        </q-select>

        <q-select
          v-model="budgetTier"
          :options="budgetOptions"
          label="Budget tier"
          outlined
          dense
          dark
          emit-value
          map-options
          class="q-mb-md"
        >
          <template v-slot:prepend>
            <q-icon name="payments" />
          </template>
        </q-select>

        <q-select
          v-model="schedule"
          :options="scheduleOptions"
          label="Schedule"
          outlined
          dense
          dark
          emit-value
          map-options
        >
          <template v-slot:prepend>
            <q-icon name="schedule" />
          </template>
        </q-select>
      </q-card-section>

      <q-card-actions align="around" class="q-pb-md q-pt-none column q-gutter-sm">
        <q-btn
          color="pink-5"
          label="Register"
          class="full-width"
          :loading="loading"
          @click="onSubmit"
        />
        <q-btn
          flat
          color="grey-3"
          label="Back to login"
          size="sm"
          class="full-width"
          @click="goLogin"
        />
      </q-card-actions>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'

const router = useRouter()
const $q = useQuasar()

const name = ref('')
const email = ref('')
const password = ref('')

const goal = ref(null)
const experience = ref(null)
const budgetTier = ref(null)
const schedule = ref(null)

const goalOptions = [
  { label: 'Weight loss', value: 'weight_loss' },
  { label: 'Muscle gain', value: 'muscle_gain' },
  { label: 'Maintenance', value: 'maintenance' },
  { label: 'Performance', value: 'performance' },
]

const experienceOptions = [
  { label: 'Beginner', value: 'beginner' },
  { label: 'Intermediate', value: 'intermediate' },
  { label: 'Advanced', value: 'advanced' },
]

const budgetOptions = [
  { label: 'Low', value: 'low' },
  { label: 'Medium', value: 'medium' },
  { label: 'High', value: 'high' },
  { label: 'Premium', value: 'premium' },
]

const scheduleOptions = [
  { label: 'Morning', value: 'morning' },
  { label: 'Lunch', value: 'lunch' },
  { label: 'Evening', value: 'evening' },
  { label: 'Flexible', value: 'flexible' },
]

const loading = ref(false)

function goLogin() {
  router.push('/login')
}

async function onSubmit() {
  if (!email.value || !password.value || !name.value) {
    $q.notify({ type: 'negative', message: 'Completează nume, email și parolă' })
    return
  }

  loading.value = true
  try {
    const payload = {
      name: name.value,
      email: email.value,
      password: password.value,
      goal: goal.value || null,
      experience: experience.value || null,
      budgetTier: budgetTier.value || null,
      schedule: schedule.value || null,
    }

    const { data } = await api.post('/auth/register', payload)

    localStorage.setItem('token', data.token)
    localStorage.setItem('email', data.email)
    localStorage.setItem('role', data.role)
    localStorage.setItem('name', data.name || name.value)
    if (data.memberId) {
      localStorage.setItem('memberId', data.memberId)
    }

    $q.notify({ type: 'positive', message: 'Account created' })
    router.push('/home')
  } catch (err) {
    console.error('REGISTER ERROR', api.defaults.baseURL, err?.message, err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.error || err.message || 'Register failed',
    })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.rounded-borders {
  border-radius: 18px;
}
</style>
