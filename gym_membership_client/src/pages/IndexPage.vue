<template>
  <q-page padding class="bg-grey-2">
    <div class="q-mb-lg">
      <div class="text-h5 text-primary text-weight-bold">
        AI Trainer & Membership Recommendation
      </div>
      <div class="text-caption text-grey-7 q-mt-xs">
        Select a member and let the AI suggest the best trainer & membership.
      </div>
    </div>

    <!-- SELECT MEMBER -->
    <q-card flat bordered class="q-pa-md q-mb-lg">
      <q-select
        v-model="selectedMember"
        :options="memberOptions"
        option-value="id"
        option-label="name"
        emit-value
        map-options
        label="Select member"
        outlined
        dense
        :loading="loadingMembers"
        :disable="loadingMembers"
        hint="Members are loaded from Spring Boot backend"
      />

      <div class="q-mt-md">
        <q-btn
          color="primary"
          label="Ask AI"
          :disable="!selectedMember || loadingAi"
          :loading="loadingAi"
          @click="getAiRecommendation"
        />
      </div>
    </q-card>

    <!-- RESULT -->
    <q-card v-if="result" flat bordered class="q-pa-md">
      <div class="text-subtitle1 text-weight-bold q-mb-sm">Recommendation result</div>

      <q-list dense>
        <q-item>
          <q-item-section top>
            <q-item-label caption>Membership Type ID</q-item-label>
            <q-item-label>{{ result.membershipTypeId ?? '—' }}</q-item-label>
          </q-item-section>
        </q-item>

        <q-item>
          <q-item-section top>
            <q-item-label caption>Trainer ID</q-item-label>
            <q-item-label>{{ result.trainerId ?? '—' }}</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>

      <q-separator spaced />

      <q-item>
        <q-item-section>
          <q-item-label caption>AI rationale</q-item-label>
          <q-item-label class="q-mt-xs">
            {{ result.rationale }}
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-card>

    <!-- ERROR -->
    <q-banner v-if="errorMessage" class="q-mt-lg" rounded dense inline-actions color="negative">
      {{ errorMessage }}
      <template #action>
        <q-btn flat label="Close" @click="errorMessage = ''" />
      </template>
    </q-banner>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const members = ref([])
const memberOptions = ref([])
const selectedMember = ref(null)

const loadingMembers = ref(false)
const loadingAi = ref(false)

const result = ref(null)
const errorMessage = ref('')

async function loadMembers() {
  loadingMembers.value = true
  errorMessage.value = ''
  try {
    const res = await api.get('/members')
    members.value = res.data
    memberOptions.value = members.value.map((m) => ({
      id: m.id,
      name: m.name,
    }))
  } catch (err) {
    console.error(err)
    errorMessage.value = 'Failed to load members. Check backend / network.'
  } finally {
    loadingMembers.value = false
  }
}

async function getAiRecommendation() {
  if (!selectedMember.value) return

  loadingAi.value = true
  errorMessage.value = ''
  result.value = null

  try {
    const res = await api.post(`/ai/recommend/${selectedMember.value}`)
    result.value = res.data
  } catch (err) {
    console.error(err)
    errorMessage.value = 'AI request failed. Check backend logs or network.'
  } finally {
    loadingAi.value = false
  }
}

onMounted(() => {
  loadMembers()
})
</script>
