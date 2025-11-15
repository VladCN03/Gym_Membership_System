<template>
  <v-container class="pa-6">
    <div class="d-flex justify-space-between align-center mb-6">
      <h1 class="text-h4">Membership Types</h1>
      <v-btn color="primary" @click="openCreateDialog">Add Membership Type</v-btn>
    </div>

    <!-- MEMBERSHIP TYPES TABLE -->
    <v-data-table
      :items="types"
      :headers="headers"
      item-key="id"
      class="elevation-1"
      :loading="loading"
    >
      <!-- price formatting -->
      <!-- eslint-disable-next-line vue/valid-v-slot -->
      <template #item.price="{ item }">
        {{ formatPrice(item.price) }}
      </template>

      <!-- actions -->
      <!-- eslint-disable-next-line vue/valid-v-slot -->
      <template #item.actions="{ item }">
        <v-btn icon="mdi-pencil" size="small" variant="text" @click="openEditDialog(item)" />
        <v-btn
          icon="mdi-delete"
          size="small"
          variant="text"
          color="red"
          @click="deleteType(item)"
        />
      </template>
    </v-data-table>

    <!-- CREATE / EDIT DIALOG -->
    <v-dialog v-model="dialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5">
          {{ editedType.id ? 'Edit Membership Type' : 'Add Membership Type' }}
        </v-card-title>

        <v-card-text>
          <v-row dense>
            <v-col cols="12">
              <v-text-field v-model="editedType.type" label="Name" outlined dense required />
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="editedType.price"
                label="Price"
                type="number"
                outlined
                dense
                min="0"
              />
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions class="justify-end">
          <v-btn variant="text" @click="closeDialog">Cancel</v-btn>
          <v-btn color="primary" :loading="saving" @click="saveType"> Save </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { MembershipTypes } from '@/api'

const types = ref([])
const loading = ref(false)
const saving = ref(false)
const dialog = ref(false)

const editedType = ref({
  id: null,
  type: '',
  price: 0,
})

const headers = [
  { title: 'ID', key: 'id', sortable: true, width: 70 },
  { title: 'Name', key: 'type' },
  { title: 'Price', key: 'price' },
  { title: 'Actions', key: 'actions', sortable: false, width: 120 },
]

// === LOAD DATA ===
async function loadAll() {
  loading.value = true
  try {
    types.value = await MembershipTypes.all()
  } catch (err) {
    console.error(err)
    alert('Failed to load membership types. Check backend.')
  } finally {
    loading.value = false
  }
}

onMounted(loadAll)

// === HELPERS ===
function formatPrice(value) {
  if (value == null) return '—'
  const n = Number(value)
  if (Number.isNaN(n)) return String(value)
  return n.toFixed(2)
}

// === CRUD ===
function openCreateDialog() {
  editedType.value = {
    id: null,
    type: '',
    price: 0,
  }
  dialog.value = true
}

function openEditDialog(item) {
  editedType.value = {
    id: item.id,
    type: item.type,
    price: item.price,
  }
  dialog.value = true
}

function closeDialog() {
  dialog.value = false
}

async function saveType() {
  if (!editedType.value.type) {
    alert('Name is required.')
    return
  }

  saving.value = true
  try {
    const payload = {
      ...editedType.value,
      price: Number(editedType.value.price ?? 0),
    }

    if (payload.id) {
      await MembershipTypes.update(payload.id, payload)
    } else {
      await MembershipTypes.create(payload)
    }

    dialog.value = false
    await loadAll()
  } catch (err) {
    console.error(err)
    alert('Failed to save membership type. Check backend.')
  } finally {
    saving.value = false
  }
}

async function deleteType(item) {
  if (!confirm(`Delete membership type "${item.type}"?`)) return
  try {
    await MembershipTypes.delete(item.id)
    await loadAll()
  } catch (err) {
    console.error(err)
    alert('Failed to delete membership type.')
  }
}
</script>
