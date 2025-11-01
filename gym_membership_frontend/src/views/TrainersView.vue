<template>
  <v-container>
    <!-- Adaugă trainer -->
    <v-card class="pa-4 mb-4">
      <v-card-title>Adaugă Trainer</v-card-title>
      <v-card-text>
        <v-text-field v-model="form.name" label="Name" />
        <v-text-field v-model="form.email" label="Email" />
        <v-btn color="primary" @click="saveTrainer">Salvează</v-btn>
      </v-card-text>
    </v-card>

    <!-- Lista traineri -->
    <v-card>
      <v-card-title>Lista Traineri</v-card-title>
      <v-data-table
        :headers="headers"
        :items="trainers"
        :loading="loading"
        loading-text="Loading items..."
      >
        <!-- eslint-disable-next-line vue/valid-v-slot -->
        <template #item.actions="{ item }">
          <div class="d-flex gap-2">
            <v-btn size="small" color="error" variant="outlined" @click="onDelete(item.id)">
              Delete
            </v-btn>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <!-- Editează trainer -->
    <v-card class="mt-4 pa-4">
      <v-card-title>Editează Trainer</v-card-title>
      <v-card-text>
        <v-select
          v-model="editId"
          :items="trainers"
          item-title="name"
          item-value="id"
          label="Alege Trainer"
          @update:model-value="prefill"
          clearable
        />
        <v-text-field v-model="editForm.name" label="Name" />
        <v-text-field v-model="editForm.email" label="Email" />
        <div class="mt-2">
          <v-btn color="primary" :disabled="!editId" @click="onUpdate"> Actualizează </v-btn>
          <v-btn variant="text" @click="resetEdit">Resetează</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Trainers } from '@/api'

const trainers = ref([])
const loading = ref(false)
const form = ref({ name: '', email: '' })

// câmpuri pentru editare
const editId = ref(null)
const editForm = ref({ name: '', email: '' })

const headers = [
  { title: 'ID', key: 'id', width: 60 },
  { title: 'Name', key: 'name' },
  { title: 'Email', key: 'email' },
  { title: 'Actions', key: 'actions', sortable: false, width: 160 },
]

// ----------------- FUNCȚII -----------------
async function load() {
  loading.value = true
  try {
    trainers.value = await Trainers.all()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function saveTrainer() {
  if (!form.value.name || !form.value.email) return
  await Trainers.create(form.value)
  form.value = { name: '', email: '' }
  await load()
}

function startEdit(item) {
  editId.value = item.id
  editForm.value = { name: item.name, email: item.email }
}

function prefill(id) {
  const t = trainers.value.find((x) => x.id === id)
  if (t) startEdit(t)
}

function resetEdit() {
  editId.value = null
  editForm.value = { name: '', email: '' }
}

async function onUpdate() {
  if (!editId.value) return
  await Trainers.update(editId.value, editForm.value)
  resetEdit()
  await load()
}

async function onDelete(id) {
  const ok = confirm('Sigur vrei să ștergi acest trainer?')
  if (!ok) return
  await Trainers.delete(id)
  if (editId.value === id) resetEdit()
  await load()
}

onMounted(load)
</script>
