<template>
  <v-container>
    <v-card class="pa-4 mb-4">
      <v-card-title>Adaugă Membru</v-card-title>
      <v-card-text>
        <v-text-field v-model="form.name" label="Name" />
        <v-text-field v-model="form.email" label="Email" />
        <v-select
          v-model="form.trainerId"
          :items="trainers"
          item-title="name"
          item-value="id"
          label="Trainer"
        />
        <v-btn color="primary" @click="saveMember">Salvează</v-btn>
      </v-card-text>
    </v-card>

    <v-card>
      <v-card-title>Lista Membri</v-card-title>
      <v-data-table
        :headers="headers"
        :items="members"
        :loading="loading"
        loading-text="Loading items..."
      >
        <!-- eslint-disable-next-line vue/valid-v-slot -->
        <template #item.membershipType="{ item }">
          {{ item.membershipType?.type ?? '-' }}
        </template>

        <!-- eslint-disable-next-line vue/valid-v-slot -->
        <template #item.trainer="{ item }">
          {{ item.trainer?.name ?? '-' }}
        </template>

        <!-- 👇 NOU: butoanele de acțiuni -->
        <!-- eslint-disable-next-line vue/valid-v-slot -->
        <template #item.actions="{ item }">
          <div class="flex gap-2">
            <v-btn color="orange" variant="flat" size="small" @click="onClearTrainer(item.id)">
              Remove Trainer
            </v-btn>

            <v-btn color="error" variant="flat" size="small" @click="onDeleteMember(item.id)">
              Delete
            </v-btn>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <v-card class="mt-4 pa-4">
      <v-card-title>Asignează Abonament</v-card-title>
      <v-card-text>
        <v-select
          v-model="assign.memberId"
          :items="members"
          :item-title="(m) => m.fullName || m.name || m.email"
          item-value="id"
          label="Member"
        />
        <v-select
          v-model="assign.membershipTypeId"
          :items="membershipTypes"
          :item-title="(it) => `${it.type} · ${it.price}`"
          item-value="id"
          label="Membership Type"
        />
        <v-btn color="primary" @click="assignMembership">Asignează</v-btn>
      </v-card-text>
    </v-card>

    <!-- MODIFICĂ TRAINER -->
    <v-card class="mt-4 pa-4">
      <v-card-title>Modifică Trainer</v-card-title>
      <v-card-text>
        <v-select
          v-model="updateTrainer.memberId"
          :items="members"
          :item-title="(m) => m.fullName || m.name || m.email"
          item-value="id"
          label="Member"
        />
        <v-select
          v-model="updateTrainer.trainerId"
          :items="trainers"
          item-title="name"
          item-value="id"
          label="New Trainer"
        />
        <v-btn color="primary" @click="updateMemberTrainer">Actualizează</v-btn>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Members, Trainers, MembershipTypes } from '@/api'

const members = ref([])
const trainers = ref([])
const membershipTypes = ref([])
const loading = ref(false)
const form = ref({ name: '', email: '', trainerId: null })
const assign = ref({ memberId: null, membershipTypeId: null })

const updateTrainer = ref({
  memberId: null,
  trainerId: null,
})

const headers = [
  { title: 'ID', key: 'id' },
  { title: 'Name', key: 'name' }, // sau 'name' la tine
  { title: 'Email', key: 'email' },
  { title: 'Membership', key: 'membershipType' },
  { title: 'Trainer', key: 'trainer' },
  { title: 'Actions', key: 'actions', sortable: false, width: 180 }, // 👈 nou
]

async function load() {
  loading.value = true
  try {
    const [memRes, trainRes, typeRes] = await Promise.all([
      Members.all(),
      Trainers.all(),
      MembershipTypes.all(),
    ])
    members.value = memRes
    trainers.value = trainRes
    membershipTypes.value = typeRes
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function saveMember() {
  if (!form.value.name || !form.value.email) return
  await Members.create({
    name: form.value.name,
    email: form.value.email,
    trainerId: form.value.trainerId ?? null, // optional
  })
  form.value = { name: '', email: '', trainerId: null }
  await load()
}

async function assignMembership() {
  if (!assign.value.memberId || !assign.value.membershipTypeId) return
  await Members.assignMembership(assign.value)
  assign.value = { memberId: null, membershipTypeId: null }
  await load()
}

async function updateMemberTrainer() {
  if (!updateTrainer.value.memberId || !updateTrainer.value.trainerId) return
  try {
    await Members.assignTrainer({
      memberId: +updateTrainer.value.memberId,
      trainerId: +updateTrainer.value.trainerId,
    })
    updateTrainer.value = { memberId: null, trainerId: null }
    await load()
  } catch (e) {
    console.error('API error la modificare trainer:', e?.response?.data || e)
  }
}

const busy = ref(false)

async function onClearTrainer(id) {
  if (!id) return
  try {
    busy.value = true
    await Members.removeTrainer(id) // trimite {memberId: id, trainerId: null}
    await load()
  } catch (e) {
    console.error('clear trainer failed:', e?.response?.data || e)
  } finally {
    busy.value = false
  }
}

async function onDeleteMember(id) {
  if (!id) return
  const ok = confirm('Ștergi acest membru?')
  if (!ok) return
  try {
    busy.value = true
    await Members.delete(id)
    await load()
  } catch (e) {
    console.error('delete member failed:', e?.response?.data || e)
  } finally {
    busy.value = false
  }
}

onMounted(load)
</script>
