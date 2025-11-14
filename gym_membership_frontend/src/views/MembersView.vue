<template>
  <v-container class="pa-6">
    <div class="d-flex justify-space-between align-center mb-6">
      <h1 class="text-h4">Members</h1>
      <v-btn color="primary" @click="openCreateDialog">Add Member</v-btn>
    </div>

    <!-- MEMBERS TABLE -->
    <v-data-table
      :items="members"
      :headers="headers"
      item-key="id"
      class="elevation-1"
      :loading="loading"
    >
      <!-- membership type name -->
      <!-- eslint-disable-next-line vue/valid-v-slot -->
      <template #item.membershipType="{ item }">
        {{ item.membershipType ? item.membershipType.type : '—' }}
      </template>

      <!-- trainer name -->
      <!-- eslint-disable-next-line vue/valid-v-slot -->
      <template #item.trainer="{ item }">
        {{ item.trainer ? item.trainer.name : '—' }}
      </template>

      <!-- celelalte câmpuri sunt string-uri simple, se afișează direct -->

      <!-- eslint-disable-next-line vue/valid-v-slot -->
      <template #item.actions="{ item }">
        <v-btn icon="mdi-pencil" size="small" variant="text" @click="openEditDialog(item)" />
        <v-btn
          icon="mdi-delete"
          size="small"
          variant="text"
          color="red"
          @click="deleteMember(item)"
        />
      </template>
    </v-data-table>

    <!-- CREATE / EDIT DIALOG -->
    <v-dialog v-model="dialog" max-width="700px">
      <v-card>
        <v-card-title class="text-h5">
          {{ editedMember.id ? 'Edit Member' : 'Add Member' }}
        </v-card-title>

        <v-card-text>
          <v-row dense>
            <v-col cols="12" md="6">
              <v-text-field v-model="editedMember.name" label="Name" outlined dense required />
            </v-col>

            <v-col cols="12" md="6">
              <v-text-field
                v-model="editedMember.email"
                label="Email"
                outlined
                dense
                type="email"
                required
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-select
                v-model="editedMember.membershipTypeId"
                :items="membershipTypes"
                item-title="type"
                item-value="id"
                label="Membership type"
                outlined
                dense
                clearable
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-select
                v-model="editedMember.trainerId"
                :items="trainers"
                item-title="name"
                item-value="id"
                label="Trainer"
                outlined
                dense
                clearable
              />
            </v-col>

            <!-- NEW FIELDS -->
            <v-col cols="12" md="6">
              <v-select
                v-model="editedMember.goal"
                :items="goalOptions"
                item-title="label"
                item-value="value"
                label="Goal"
                outlined
                dense
                clearable
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-select
                v-model="editedMember.experience"
                :items="experienceOptions"
                item-title="label"
                item-value="value"
                label="Experience level"
                outlined
                dense
                clearable
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-select
                v-model="editedMember.budgetTier"
                :items="budgetOptions"
                item-title="label"
                item-value="value"
                label="Budget tier"
                outlined
                dense
                clearable
              />
            </v-col>

            <v-col cols="12" md="6">
              <v-select
                v-model="editedMember.schedule"
                :items="scheduleOptions"
                item-title="label"
                item-value="value"
                label="Preferred schedule"
                outlined
                dense
                clearable
              />
            </v-col>
          </v-row>
        </v-card-text>

        <v-card-actions class="justify-end">
          <v-btn variant="text" @click="closeDialog">Cancel</v-btn>
          <v-btn color="primary" :loading="saving" @click="saveMember"> Save </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import { ref, onMounted } from 'vue'
import api from '@/api'

export default {
  setup() {
    const members = ref([])
    const membershipTypes = ref([])
    const trainers = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialog = ref(false)

    const editedMember = ref({
      id: null,
      name: '',
      email: '',
      membershipTypeId: null,
      trainerId: null,
      goal: null,
      experience: null,
      budgetTier: null,
      schedule: null,
    })

    const headers = [
      { title: 'ID', key: 'id', sortable: true },
      { title: 'Name', key: 'name' },
      { title: 'Email', key: 'email' },
      { title: 'Membership', key: 'membershipType' }, // <── cheie = membershipType (obiect)
      { title: 'Trainer', key: 'trainer' }, // <── cheie = trainer (obiect)
      { title: 'Goal', key: 'goal' },
      { title: 'Experience', key: 'experience' },
      { title: 'Budget', key: 'budgetTier' },
      { title: 'Schedule', key: 'schedule' },
      { title: 'Actions', key: 'actions', sortable: false },
    ]

    const goalOptions = [
      { label: 'Weight loss', value: 'weight_loss' },
      { label: 'Muscle gain', value: 'muscle_gain' },
      { label: 'Strength', value: 'strength' },
      { label: 'Endurance', value: 'endurance' },
      { label: 'Flexibility', value: 'flexibility' },
      { label: 'General fitness', value: 'general_fitness' },
    ]

    const experienceOptions = [
      { label: 'Beginner', value: 'beginner' },
      { label: 'Intermediate', value: 'intermediate' },
      { label: 'Advanced', value: 'advanced' },
    ]

    const budgetOptions = [
      { label: 'Low', value: 'low' },
      { label: 'Medium', value: 'medium' },
      { label: 'High', value: 'high' },
    ]

    const scheduleOptions = [
      { label: 'Morning', value: 'morning' },
      { label: 'Afternoon', value: 'afternoon' },
      { label: 'Evening', value: 'evening' },
    ]

    // === LOAD DATA ===
    async function loadAll() {
      loading.value = true
      try {
        const [mRes, mtRes, tRes] = await Promise.all([
          api.get('/members'),
          api.get('/membership-types'),
          api.get('/trainers'),
        ])

        members.value = mRes.data
        membershipTypes.value = mtRes.data
        trainers.value = tRes.data
      } catch (err) {
        console.error(err)
        alert('Failed to load data. Check backend.')
      } finally {
        loading.value = false
      }
    }

    onMounted(loadAll)

    // === CRUD ===
    function openCreateDialog() {
      editedMember.value = {
        id: null,
        name: '',
        email: '',
        membershipTypeId: null,
        trainerId: null,
        goal: null,
        experience: null,
        budgetTier: null,
        schedule: null,
      }
      dialog.value = true
    }

    function openEditDialog(item) {
      // mapăm obiectele nested la id-uri
      editedMember.value = {
        id: item.id,
        name: item.name,
        email: item.email,
        membershipTypeId: item.membershipType ? item.membershipType.id : null,
        trainerId: item.trainer ? item.trainer.id : null,
        goal: item.goal,
        experience: item.experience,
        budgetTier: item.budgetTier,
        schedule: item.schedule,
      }
      dialog.value = true
    }

    function closeDialog() {
      dialog.value = false
    }

    async function saveMember() {
      saving.value = true
      try {
        const payload = { ...editedMember.value }

        if (payload.id) {
          await api.put(`/members/${payload.id}`, payload)
        } else {
          await api.post('/members', payload)
        }

        dialog.value = false
        await loadAll()
      } catch (err) {
        console.error(err)
        alert('Failed to save member. Check backend.')
      } finally {
        saving.value = false
      }
    }

    async function deleteMember(item) {
      if (!confirm(`Delete member "${item.name}"?`)) return
      try {
        await api.delete(`/members/${item.id}`)
        await loadAll()
      } catch (err) {
        console.error(err)
        alert('Failed to delete member.')
      }
    }

    return {
      members,
      membershipTypes,
      trainers,
      loading,
      saving,
      dialog,
      editedMember,
      headers,
      goalOptions,
      experienceOptions,
      budgetOptions,
      scheduleOptions,
      openCreateDialog,
      openEditDialog,
      closeDialog,
      saveMember,
      deleteMember,
    }
  },
}
</script>
