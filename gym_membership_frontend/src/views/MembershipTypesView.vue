<template>
  <v-container>
    <!-- Adaugă tip abonament -->
    <v-card class="pa-4 mb-4">
      <v-card-title>Adaugă Tip Abonament</v-card-title>
      <v-card-text>
        <!-- folosește 'type' în form -->
        <v-text-field v-model="form.type" label="Name" />
        <v-text-field v-model="form.price" label="Price" type="number" />
        <v-btn color="primary" @click="saveType">Salvează</v-btn>
      </v-card-text>
    </v-card>

    <!-- Lista tipuri abonament -->
    <v-card>
      <v-card-title>Lista Tipuri de Abonament</v-card-title>
      <v-data-table
        :headers="headers"
        :items="types"
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

    <!-- Editează abonament -->
    <v-card class="mt-4 pa-4">
      <v-card-title>Editează Tip Abonament</v-card-title>
      <v-card-text>
        <v-select
          v-model="editId"
          :items="types"
          item-title="type"
          item-value="id"
          label="Alege Abonament"
          @update:model-value="prefill"
          clearable
        />
        <!-- folosește 'type' și la edit -->
        <v-text-field v-model="editForm.type" label="Name" />
        <v-text-field v-model="editForm.price" label="Price" type="number" />
        <div class="mt-2">
          <v-btn color="primary" :disabled="!editId" @click="onUpdate">Actualizează</v-btn>
          <v-btn variant="text" @click="resetEdit">Resetează</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { MembershipTypes } from '@/api'

const types = ref([])
const loading = ref(false)
const form = ref({ type: '', price: 0 }) // ← consistent

const editId = ref(null)
const editForm = ref({ type: '', price: 0 }) // ← consistent

const headers = [
  { title: 'ID', key: 'id', width: 60 },
  { title: 'Name', key: 'type' },
  { title: 'Price', key: 'price' },
  { title: 'Actions', key: 'actions', sortable: false, width: 160 },
]

async function load() {
  loading.value = true
  try {
    types.value = await MembershipTypes.all()
  } finally {
    loading.value = false
  }
}

async function saveType() {
  if (!form.value.type || form.value.price === null || form.value.price === '') return
  await MembershipTypes.create({
    type: form.value.type,
    price: Number(form.value.price),
  })
  form.value = { type: '', price: 0 }
  await load()
}

function startEdit(item) {
  editId.value = item.id
  editForm.value = { type: item.type, price: item.price }
}

function prefill(id) {
  const t = types.value.find((x) => x.id === id)
  if (t) startEdit(t)
}

function resetEdit() {
  editId.value = null
  editForm.value = { type: '', price: 0 }
}

async function onUpdate() {
  if (!editId.value) return
  await MembershipTypes.update(editId.value, {
    type: editForm.value.type,
    price: Number(editForm.value.price),
  })
  resetEdit()
  await load()
}

async function onDelete(id) {
  const ok = confirm('Sigur vrei să ștergi acest tip de abonament?')
  if (!ok) return
  await MembershipTypes.delete(id)
  if (editId.value === id) resetEdit()
  await load()
}

onMounted(load)
</script>
