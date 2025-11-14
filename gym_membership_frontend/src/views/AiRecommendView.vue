<template>
  <v-container class="pa-6">
    <h1 class="text-h4 mb-6">AI Trainer & Membership Recommendation</h1>

    <v-row>
      <v-col cols="12" md="6">
        <v-select
          v-model="selectedMember"
          :items="members"
          item-title="name"
          item-value="id"
          label="Select Member"
          outlined
          dense
        />
      </v-col>

      <v-col cols="12" md="6" class="d-flex align-center">
        <v-btn
          color="primary"
          :loading="loading"
          :disabled="!selectedMember"
          @click="getAiRecommendation"
        >
          Ask AI
        </v-btn>
      </v-col>
    </v-row>

    <v-divider class="my-6"></v-divider>

    <div v-if="result">
      <v-card class="pa-4">
        <h2 class="text-h5 mb-4">Recommendation Result</h2>

        <p><strong>Membership Type ID:</strong> {{ result.membershipTypeId }}</p>
        <p><strong>Trainer ID:</strong> {{ result.trainerId }}</p>

        <v-alert type="info" border="start" color="blue" class="mt-4">
          {{ result.rationale }}
        </v-alert>
      </v-card>
    </div>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api' // Axios instance

const members = ref([])
const selectedMember = ref(null)
const loading = ref(false)
const result = ref(null)

onMounted(async () => {
  const res = await api.get('/members')
  members.value = res.data
})

async function getAiRecommendation() {
  loading.value = true
  try {
    const res = await api.post(`/ai/recommend/${selectedMember.value}`)
    result.value = res.data
  } catch (err) {
    console.error(err)
    alert('AI request failed. Check backend logs.')
  }
  loading.value = false
}
</script>
