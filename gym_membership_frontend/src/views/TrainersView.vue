<template>
  <v-container class="pa-6">
    <div class="d-flex justify-space-between align-center mb-6">
      <h1 class="text-h4">Trainers</h1>
      <v-btn color="primary" @click="openCreateDialog">Add Trainer</v-btn>
    </div>

    <!-- TRAINERS TABLE -->
    <v-data-table
      :items="trainers"
      :headers="headers"
      item-key="id"
      class="elevation-1"
      :loading="loading"
    >
      <!-- specialties text -->
      <!-- eslint-disable-next-line vue/valid-v-slot -->
      <template #item.specialties="{ item }">
        {{ item.specialties || '—' }}
      </template>

      <!-- eslint-disable-next-line vue/valid-v-slot -->
      <template #item.actions="{ item }">
        <v-btn icon="mdi-pencil" size="small" variant="text" @click="openEditDialog(item)" />
        <v-btn
          icon="mdi-delete"
          size="small"
          variant="text"
          color="red"
          @click="deleteTrainer(item)"
        />
      </template>
    </v-data-table>

    <!-- CREATE / EDIT DIALOG -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title class="text-h5">
          {{ editedTrainer.id ? 'Edit Trainer' : 'Add Trainer' }}
        </v-card-title>

        <v-card-text>
          <v-row dense>
            <v-col cols="12" md="6">
              <v-text-field v-model="editedTrainer.name" label="Name" outlined dense required />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="editedTrainer.email"
                label="Email"
                outlined
                dense
                type="email"
                required
              />
            </v-col>

            <v-col cols="12">
              <v-text-field
                v-model="editedTrainer.specialties"
                label="Specialties (comma separated)"
                outlined
                dense
                hint="e.g. weight_loss, strength_training, hypertrophy"
                persistent-hint
              />
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions class="justify-end">
          <v-btn variant="text" @click="closeDialog">Cancel</v-btn>
          <v-btn color="primary" :loading="saving" @click="saveTrainer"> Save </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Trainers } from '@/api'

const trainers = ref([])
const loading = ref(false)
const saving = ref(false)
const dialog = ref(false)

const editedTrainer = ref({
  id: null,
  name: '',
  email: '',
  specialties: '',
})

const headers = [
  { title: 'ID', key: 'id', sortable: true, width: 70 },
  { title: 'Name', key: 'name' },
  { title: 'Email', key: 'email' },
  { title: 'Specialties', key: 'specialties' },
  { title: 'Actions', key: 'actions', sortable: false, width: 120 },
]

// === LOAD DATA ===
async function loadAll() {
  loading.value = true
  try {
    trainers.value = await Trainers.all()
  } catch (err) {
    console.error(err)
    alert('Failed to load trainers. Check backend.')
  } finally {
    loading.value = false
  }
}

onMounted(loadAll)

// === CRUD ===
function openCreateDialog() {
  editedTrainer.value = {
    id: null,
    name: '',
    email: '',
    specialties: '',
  }
  dialog.value = true
}

function openEditDialog(item) {
  editedTrainer.value = {
    id: item.id,
    name: item.name,
    email: item.email,
    specialties: item.specialties || '',
  }
  dialog.value = true
}

function closeDialog() {
  dialog.value = false
}

async function saveTrainer() {
  if (!editedTrainer.value.name || !editedTrainer.value.email) {
    alert('Name and email are required.')
    return
  }

  saving.value = true
  try {
    const payload = { ...editedTrainer.value }

    if (payload.id) {
      await Trainers.update(payload.id, payload)
    } else {
      await Trainers.create(payload)
    }

    dialog.value = false
    await loadAll()
  } catch (err) {
    console.error(err)
    alert('Failed to save trainer. Check backend.')
  } finally {
    saving.value = false
  }
}

async function deleteTrainer(item) {
  if (!confirm(`Delete trainer "${item.name}"?`)) return
  try {
    await Trainers.delete(item.id)
    await loadAll()
  } catch (err) {
    console.error(err)
    alert('Failed to delete trainer.')
  }
}
</script>
