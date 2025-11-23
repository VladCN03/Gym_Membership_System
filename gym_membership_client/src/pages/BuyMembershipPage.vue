<template>
  <q-page class="bg-grey-10 column q-pa-md items-center">
    <!-- HEADER -->
    <div class="q-mt-xl q-mb-md text-center">
      <div class="text-h5 text-white text-weight-bold">Gym App</div>
      <div class="text-subtitle2 text-grey-4 q-mt-xs">Choose your membership</div>
    </div>

    <!-- CARD BUY MEMBERSHIP -->
    <q-card
      class="bg-grey-9 text-white rounded-borders shadow-4"
      style="min-width: 320px; max-width: 420px"
    >
      <q-card-section>
        <div class="text-subtitle1 text-center text-weight-bold">Buy membership</div>
        <div class="text-caption text-center text-grey-4 q-mt-xs">
          Choose a plan and activate it
        </div>
      </q-card-section>

      <q-card-section>
        <div v-if="loadingMemberships" class="row justify-center q-my-md">
          <q-spinner size="lg" />
        </div>

        <div v-else>
          <q-list bordered separator class="rounded-borders bg-grey-8 text-white">
            <q-item
              v-for="m in membershipTypes"
              :key="m.id"
              clickable
              @click="selectedMembershipId = m.id"
            >
              <q-item-section>
                <q-item-label class="text-subtitle2">
                  {{ m.type }}
                </q-item-label>
                <q-item-label caption class="text-grey-3">
                  {{ formatPrice(m.price) }} RON / month
                </q-item-label>
              </q-item-section>

              <q-item-section side>
                <q-radio v-model="selectedMembershipId" :val="m.id" color="pink-5" />
              </q-item-section>
            </q-item>
          </q-list>

          <div v-if="!membershipTypes.length" class="text-caption text-grey-4 q-mt-md">
            No memberships available. Please contact the gym admin.
          </div>
        </div>
      </q-card-section>

      <q-card-actions align="around" class="q-pb-md q-pt-none">
        <q-btn
          color="pink-5"
          class="full-width"
          :disable="!selectedMembershipId || saving"
          :loading="saving"
          label="Activate membership"
          @click="activateMembership"
        />
      </q-card-actions>
    </q-card>

    <q-btn flat color="grey-3" label="Back to home" class="q-mt-lg" @click="goHome" />
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useQuasar } from 'quasar'
import { api } from 'src/boot/axios'

const router = useRouter()
const $q = useQuasar()

const membershipTypes = ref([])
const loadingMemberships = ref(false)
const selectedMembershipId = ref(null)
const saving = ref(false)

const memberId = ref(localStorage.getItem('memberId') || null)
const email = ref(localStorage.getItem('email') || '')

function formatPrice(value) {
  if (value == null) return ''
  return Number(value).toFixed(2)
}

async function loadMembershipTypes() {
  loadingMemberships.value = true
  try {
    const { data } = await api.get('/membership-types')
    membershipTypes.value = data
  } catch (err) {
    console.error('LOAD MEMBERSHIPS ERROR', err)
    $q.notify({ type: 'negative', message: 'Could not load memberships' })
  } finally {
    loadingMemberships.value = false
  }
}

async function activateMembership() {
  if (!selectedMembershipId.value) {
    $q.notify({ type: 'warning', message: 'Select a membership first' })
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
    await api.post('/members/assign-membership', {
      memberId: Number(memberId.value),
      membershipTypeId: selectedMembershipId.value,
    })

    $q.notify({ type: 'positive', message: 'Membership activated successfully' })
    router.push('/home')
  } catch (err) {
    console.error('ACTIVATE MEMBERSHIP ERROR', err)
    $q.notify({
      type: 'negative',
      message: err.response?.data?.error || 'Could not activate membership',
    })
  } finally {
    saving.value = false
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

function goHome() {
  router.push('/home')
}

onMounted(() => {
  ensureMemberId()
  loadMembershipTypes()
})
</script>

<style scoped>
.rounded-borders {
  border-radius: 18px;
}
</style>
