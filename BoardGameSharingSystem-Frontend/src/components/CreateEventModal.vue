<!-- components/CreateEventModal.vue -->
<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-2xl p-8 w-[400px]">
      <h2 class="text-2xl font-bold text-center mb-6">Create Event</h2>

      <form class="space-y-4">
        <!-- Date Range -->
        <div>
          <label class="block font-medium">Date</label>
          <input type="date" v-model="startDate" class="input" />
          <input type="date" v-model="endDate" class="input mt-1" />
        </div>

        <!-- Time Pickers -->
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block font-medium">Start Time</label>
            <input type="time" v-model="startTime" class="input" />
          </div>
          <div>
            <label class="block font-medium">End Time</label>
            <input type="time" v-model="endTime" class="input" />
          </div>
        </div>

        <!-- Location -->
        <div>
          <label class="block font-medium">Location</label>
          <input type="text" v-model="location" class="input" />
        </div>

        <!-- Participants -->
        <div>
          <label class="block font-medium">Number of Participants</label>
          <input type="number" v-model="participants" class="input" />
        </div>

        <!-- Board Game -->
        <div>
          <label class="block font-medium">Board Game</label>
          <select v-model="selectedGame" class="input">
            <option value="">Select Game</option>
            <option value="Chess">Chess</option>
            <option value="Catan">Catan</option>
            <option value="Uno">Uno</option>
          </select>
        </div>

        <!-- Description -->
        <div>
          <label class="block font-medium">Description</label>
          <input type="text" v-model="description" class="input" />
        </div>

        <!-- Buttons -->
        <div class="flex justify-between mt-6">
          <button type="button" @click="cancel" class="bg-gray-300 text-white px-6 py-2 rounded-full">Cancel</button>
          <button type="submit" @click.prevent="create" class="bg-black text-white px-6 py-2 rounded-full">Create</button>
        </div>

        <!-- Footer -->
        <p class="text-sm text-center mt-4">
          Game Not Found?
          <span class="text-black font-semibold cursor-pointer underline">Add it</span>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Emit function to communicate with parent
const emit = defineEmits(['close'])

const startDate = ref('')
const endDate = ref('')
const startTime = ref('')
const endTime = ref('')
const location = ref('')
const participants = ref('')
const selectedGame = ref('')
const description = ref('')

const cancel = () => {
  emit('close') // Close modal
}

const create = () => {
  // Handle form submission logic
  console.log({
    startDate: startDate.value,
    endDate: endDate.value,
    startTime: startTime.value,
    endTime: endTime.value,
    location: location.value,
    participants: participants.value,
    selectedGame: selectedGame.value,
    description: description.value
  })
  emit('close') // Optionally close modal after creation
}
</script>

<style scoped>
.input {
  @apply w-full px-3 py-2 mt-1 border rounded-md border-gray-300;
}
</style>
