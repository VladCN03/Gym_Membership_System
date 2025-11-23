<template>
  <q-page class="bg-grey-10 column q-pa-md">
    <!-- HEADER -->
    <div class="q-mt-lg q-mb-md text-center">
      <div class="text-h5 text-white text-weight-bold">Gym App</div>
      <div class="text-subtitle2 text-grey-4 q-mt-xs">Welcome, {{ displayName }}</div>
    </div>

    <!-- WRAPPER pentru carduri (same width) -->
    <div class="cards-wrapper">
      <!-- CARD CU BUTOANE (YOUR SERVICES) -->
      <q-card class="bg-grey-9 text-white rounded-borders shadow-4">
        <q-card-section>
          <div class="text-subtitle2 text-weight-bold">YOUR SERVICES</div>
        </q-card-section>

        <q-card-section>
          <div class="row justify-around q-gutter-md q-mb-md">
            <!-- Buy Membership -->
            <div class="column items-center">
              <q-btn
                round
                unelevated
                size="lg"
                color="pink-5"
                icon="credit_card"
                @click="goBuyMembership"
              />
              <div class="text-caption text-uppercase q-mt-sm text-grey-3 text-center">
                Buy<br />membership
              </div>
            </div>

            <!-- Assign Trainer -->
            <div class="column items-center">
              <q-btn
                round
                unelevated
                size="lg"
                color="pink-5"
                icon="person_search"
                @click="goAssignTrainer"
              />
              <div class="text-caption text-uppercase q-mt-sm text-grey-3 text-center">
                Assign<br />trainer
              </div>
            </div>
          </div>

          <div class="row justify-around q-gutter-md">
            <!-- View Stats -->
            <div class="column items-center">
              <q-btn round unelevated size="lg" color="pink-5" icon="insights" @click="goStats" />
              <div class="text-caption text-uppercase q-mt-sm text-grey-3 text-center">
                View<br />stats
              </div>
            </div>

            <!-- AI Coach -->
            <div class="column items-center">
              <q-btn round unelevated size="lg" color="pink-5" icon="smart_toy" @click="goAi" />
              <div class="text-caption text-uppercase q-mt-sm text-grey-3 text-center">
                AI<br />coach
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>

      <!-- PROFILE CARD, MAI JOS + ACEEAȘI LĂȚIME -->
      <q-card class="bg-grey-9 text-white rounded-borders shadow-4 q-mt-xl">
        <q-card-section>
          <div class="text-subtitle2 text-weight-bold">YOUR PROFILE</div>
          <div class="text-caption text-grey-4 q-mt-xs">Current membership and trainer</div>
        </q-card-section>

        <q-card-section>
          <div v-if="profileLoading" class="row justify-center q-my-md">
            <q-spinner size="md" />
          </div>

          <div v-else>
            <div class="q-mb-sm">
              <div class="text-caption text-grey-4">Membership</div>
              <div class="text-body2">
                {{ displayMembership }}
              </div>
            </div>

            <q-separator dark inset class="q-my-sm" />

            <div>
              <div class="text-caption text-grey-4">Trainer</div>
              <div class="text-body2">
                {{ displayTrainer }}
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>
    </div>

    <!-- LOGOUT jos -->
    <div class="row justify-center q-mt-auto q-mb-md">
      <q-btn outline color="negative" label="Logout" class="full-width" @click="logout" />
    </div>
  </q-page>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'

const router = useRouter()
const $q = useQuasar()

// Numele afisat – il luam din localStorage (numele salvat la register) sau fallback la email
const displayName = computed(() => {
  const name = localStorage.getItem('name')
  if (name && name.trim().length > 0) return name
  return 'Member'
})

// ---- PROFILE STATE ----
const profileLoading = ref(false)
const profile = ref(null)

const displayMembership = computed(() => {
  if (!profile.value || !profile.value.membershipName) {
    return 'No active membership'
  }
  return profile.value.membershipName
})

const displayTrainer = computed(() => {
  if (!profile.value || !profile.value.trainerName) {
    return 'No trainer assigned'
  }
  return profile.value.trainerName
})

// ---- LOAD PROFILE FROM BACKEND ----
async function loadProfile() {
  const email = localStorage.getItem('email')
  if (!email) {
    profileLoading.value = false
    return
  }

  profileLoading.value = true
  try {
    const { data } = await api.get('/members/by-email', {
      params: { email },
    })
    profile.value = data
  } catch (err) {
    console.error('LOAD PROFILE ERROR', err)
    $q.notify({
      type: 'negative',
      message: 'Could not load your profile',
    })
  } finally {
    profileLoading.value = false
  }
}

onMounted(() => {
  loadProfile()
})

function logout() {
  localStorage.clear()
  router.push('/login')
}

// Navigare spre pagini (le vom implementa ulterior)
function goBuyMembership() {
  router.push('/membership') // o să facem o pagină / route pentru asta
}

function goAssignTrainer() {
  router.push('/trainer') // viitoare pagină de asignare trainer
}

function goStats() {
  router.push('/stats') // viitoare pagină cu statisticile
}

function goAi() {
  router.push('/ai') // viitoare pagină pentru AI recommendations
}
</script>

<style scoped>
.rounded-borders {
  border-radius: 18px;
}

/* ambele carduri (services + profile) vor avea aceeași lățime */
.cards-wrapper {
  width: 100%;
  max-width: 420px;
  margin: 0 auto; /* centrează pe orizontală */
}
</style>
