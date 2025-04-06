<!-- components/CreateEventModal.vue -->
<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h2 class="modal-title">Create Event</h2>

      <form class="space-y-3">
        <!-- Date Range -->
        <div>
          <label class="modal-label">Date</label>
          <input type="date" v-model="startDate" class="modal-input" />
          <input type="date" v-model="endDate" class="modal-input mt-1" />
        </div>

        <!-- Time Pickers -->
        <div class="flex justify-between gap-4">
          <div class="w-1/2">
            <label class="modal-label">Start Time</label>
            <input type="time" v-model="startTime" class="modal-input" />
          </div>
          <div class="w-1/2">
            <label class="modal-label">End Time</label>
            <input type="time" v-model="endTime" class="modal-input" />
          </div>
        </div>

        <!-- Location -->
        <div>
          <label class="modal-label">Location</label>
          <input type="text" v-model="location" class="modal-input" />
        </div>

        <!-- Participants -->
        <div>
          <label class="modal-label">Number of Participants</label>
          <input type="number" v-model="participants" class="modal-input" />
        </div>

        <!-- Board Game -->
        <div>
          <label class="modal-label">Board Game</label>
          <select v-model="selectedGame" class="modal-input">
            <option value="">Select Game</option>
            <option value="Chess">Chess</option>
            <option value="Catan">Catan</option>
            <option value="Uno">Uno</option>
          </select>
        </div>

        <!-- Description -->
        <div>
          <label class="modal-label">Description</label>
          <input type="text" v-model="description" class="modal-input" />
        </div>

        <!-- Buttons -->
        <div class="flex justify-between mt-4">
          <button type="button" @click="cancel" class="modal-button cancel">Cancel</button>
          <button type="submit" @click.prevent="create" class="modal-button create">Create</button>
        </div>

        <!-- Footer -->
        <p class="text-sm text-center mt-3 text-[rgb(230,204,189)]">
          Game Not Found?
          <span class="underline cursor-pointer">Add it</span>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits } from 'vue'
import { eventService } from '@/services/eventService'

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
  emit('close')
}

const create = async () => {
  try {
    const eventData = {
      startDate: startDate.value,
      endDate: endDate.value,
      startTime: startTime.value,
      endTime: endTime.value,
      location: location.value,
      maxNumParticipants: parseInt(participants.value),
      description: description.value,
      contactEmail: 'testuser@example.com', // you can update this later
      creatorId: 1 // replace with the actual logged-in user id
    }

    const createdEvent = await eventService.createEvent(eventData)
    console.log('Event created:', createdEvent)
    emit('close')
  } catch (err) {
    console.error('Error creating event:', err)
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 50;
}

.modal-content {
  background-color: rgba(59, 24, 4, 0.9); /* matching event table */
  color: rgb(230, 204, 189); /* matching cream text */
  padding: 2rem;
  border-radius: 1.5rem;
  width: 420px;
  box-shadow: 0 0 15px rgba(0,0,0,0.5);
  mix-blend-mode: add;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 1rem;
  color: rgb(230, 204, 189);
}

.modal-label {
  font-weight: 500;
  display: block;
  margin-bottom: 0.25rem;
}

.modal-input {
  width: 100%;
  padding: 0.4rem 0.75rem;
  background-color: rgba(230, 204, 189, 0.1);
  border: 1px solid gray;
  border-radius: 0.5rem;
  color: rgb(230, 204, 189);
}

.modal-input::placeholder {
  color: #ccc;
}

.modal-button {
  border-radius: 9999px;
  padding: 0.5em 1.5em;
  font-weight: 600;
  border: 1px solid gray;
}

.modal-button.cancel {
  background-color: rgba(145, 84, 49, 0.8);
  color: white;
}

.modal-button.create {
  background-color: black;
  color: white;
}

.modal-button:hover {
  background-color: rgba(187, 128, 95, 0.9);
}
</style>
