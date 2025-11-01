<template>
  <v-container>
    <!-- Members per Trainer -->
    <v-card class="mb-6">
      <v-card-title>Members per Trainer</v-card-title>
      <v-card-text>
        <v-data-table
          :headers="trainerHeaders"
          :items="trainerStats"
          :loading="loadingTrainers"
          loading-text="Loading items..."
          class="mb-2"
        />
      </v-card-text>
    </v-card>

    <!-- Memberships by Type -->
    <v-card>
      <v-card-title>Memberships by Type</v-card-title>
      <v-card-text>
        <v-data-table
          :headers="typeHeaders"
          :items="typeStats"
          :loading="loadingTypes"
          loading-text="Loading items..."
        />
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Analytics } from '@/api'

const trainerStats = ref([])
const typeStats = ref([])

const loadingTrainers = ref(false)
const loadingTypes = ref(false)

const trainerHeaders = [
  { title: 'Trainer', key: 'trainer' },
  { title: 'Members', key: 'count', align: 'end' },
]

const typeHeaders = [
  { title: 'Membership Type', key: 'type' },
  { title: 'Members', key: 'count', align: 'end' },
]

// helper pt. a extrage primul key existent dintr-o listă de alternative
const firstKey = (obj, options, fallback = null) => {
  for (const k of options) if (Object.prototype.hasOwnProperty.call(obj, k)) return obj[k]
  return fallback
}

// normalizări ca să accepte diverse forme din backend
function normalizeTrainerRow(r) {
  return {
    trainer: firstKey(r, ['trainer', 'trainerName', 'name']),
    count: Number(firstKey(r, ['count', 'membersCount', 'memberCount', 'total', 'members'], 0)),
  }
}

function normalizeTypeRow(r) {
  return {
    type: firstKey(r, ['type', 'membershipType', 'name']),
    count: Number(firstKey(r, ['count', 'membersCount', 'total', 'members'], 0)),
  }
}

async function loadTrainers() {
  loadingTrainers.value = true
  try {
    const rows = await Analytics.membersPerTrainer()
    trainerStats.value = Array.isArray(rows) ? rows.map(normalizeTrainerRow) : []
  } finally {
    loadingTrainers.value = false
  }
}

async function loadTypes() {
  loadingTypes.value = true
  try {
    const rows = await Analytics.membershipStats()
    typeStats.value = Array.isArray(rows) ? rows.map(normalizeTypeRow) : []
  } finally {
    loadingTypes.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadTrainers(), loadTypes()])
})
</script>
