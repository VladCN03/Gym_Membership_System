<template>
  <q-page class="bg-grey-10 column q-pa-md items-center">
    <!-- HEADER -->
    <div class="q-mt-lg q-mb-md text-center">
      <div class="text-h5 text-white text-weight-bold">Gym App</div>
      <div class="text-subtitle2 text-grey-4 q-mt-xs">Statistics</div>
    </div>

    <div class="stats-wrapper">
      <!-- CARD 1: MEMBERSHIP STATS -->
      <q-card class="bg-grey-9 text-white rounded-borders shadow-4">
        <q-card-section>
          <div class="text-subtitle2 text-weight-bold">Membership stats</div>
          <div class="text-caption text-grey-4 q-mt-xs">Members per membership type</div>
        </q-card-section>

        <q-card-section>
          <div v-if="loading" class="row justify-center q-my-md">
            <q-spinner size="md" />
          </div>

          <div v-else>
            <q-list
              v-if="membershipStats.length"
              bordered
              separator
              class="rounded-borders bg-grey-8 text-white"
            >
              <q-item v-for="m in membershipStats" :key="m.membershipTypeId">
                <q-item-section>
                  <q-item-label class="text-subtitle2">
                    {{ m.type }}
                  </q-item-label>
                  <q-item-label caption> {{ formatPrice(m.price) }} RON / month </q-item-label>
                </q-item-section>

                <!-- badge modern pentru număr -->
                <q-item-section side top>
                  <div class="count-wrapper">
                    <div class="count-label">Members</div>
                    <div class="count-badge">
                      {{ m.membersCount }}
                    </div>
                  </div>
                </q-item-section>
              </q-item>
            </q-list>

            <div v-else class="text-caption text-grey-5">No membership stats available.</div>
          </div>
        </q-card-section>
      </q-card>

      <!-- CARD 2: MEMBERS PER TRAINER -->
      <q-card class="bg-grey-9 text-white rounded-borders shadow-4 q-mt-xl">
        <q-card-section>
          <div class="text-subtitle2 text-weight-bold">Members per trainer</div>
          <div class="text-caption text-grey-4 q-mt-xs">How many members each trainer has</div>
        </q-card-section>

        <q-card-section>
          <div v-if="loading" class="row justify-center q-my-md">
            <q-spinner size="md" />
          </div>

          <div v-else>
            <q-list
              v-if="membersPerTrainer.length"
              bordered
              separator
              class="rounded-borders bg-grey-8 text-white"
            >
              <q-item v-for="t in membersPerTrainer" :key="t.trainerId">
                <q-item-section>
                  <q-item-label class="text-subtitle2">
                    {{ t.trainerName }}
                  </q-item-label>
                </q-item-section>

                <!-- același badge modern -->
                <q-item-section side top>
                  <div class="count-wrapper">
                    <div class="count-label">Members</div>
                    <div class="count-badge">
                      {{ t.membersCount }}
                    </div>
                  </div>
                </q-item-section>
              </q-item>
            </q-list>

            <div v-else class="text-caption text-grey-5">No trainer stats available.</div>
          </div>
        </q-card-section>
      </q-card>

      <!-- BACK BUTTON -->
      <q-btn flat color="primary" label="Back to home" class="q-mt-xl full-width" @click="goHome" />
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'

const router = useRouter()
const $q = useQuasar()

const loading = ref(false)
const membershipStats = ref([])
const membersPerTrainer = ref([])

function formatPrice(value) {
  if (value == null) return '-'
  const n = Number(value)
  if (Number.isNaN(n)) return String(value)
  return n.toFixed(2)
}

async function loadStats() {
  loading.value = true
  try {
    const [membershipRes, trainersRes] = await Promise.all([
      api.get('/custom/membership-stats-by-type'),
      api.get('/custom/members-per-trainer'),
    ])

    membershipStats.value = membershipRes.data || []
    membersPerTrainer.value = trainersRes.data || []
  } catch (err) {
    console.error('LOAD STATS ERROR', err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.error || 'Could not load stats',
    })
  } finally {
    loading.value = false
  }
}

function goHome() {
  router.push('/home')
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.rounded-borders {
  border-radius: 18px;
}

.stats-wrapper {
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
}

/* container pentru etichetă + badge */
.count-wrapper {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

/* text mic "Members" */
.count-label {
  font-size: 11px;
  color: #b0bec5; /* un gri mai vizibil */
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

/* badge modern pentru număr */
.count-badge {
  margin-top: 4px;
  min-width: 40px;
  padding: 4px 10px;
  border-radius: 999px;
  background: #ec407a; /* aproximativ pink-4 */
  color: #ffffff;
  font-weight: 700;
  font-size: 14px;
  text-align: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.4);
}
</style>
