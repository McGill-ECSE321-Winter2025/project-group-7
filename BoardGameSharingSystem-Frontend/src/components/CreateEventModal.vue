<template>
  <div class="modal-overlay">
    <div class="modal-content">
      <h2 class="modal-title">{{ modalTitle }}</h2>

      <form class="space-y-3">
        <!-- Date Range -->
        <div>
          <label class="modal-label">Start Date</label>
          <input type="date" v-model="startDate" class="modal-input" />
          <label class="modal-label">End Date</label>
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
            <option v-for="game in games" :key="game.id" :value="game">
              {{ game.title }}
            </option>
          </select>
        </div>

        <!-- Contact Email -->
        <div>
          <label class="modal-label">Contact Email</label>
          <input type="text" v-model="contactEmail" class="modal-input" />
        </div>

        <!-- Description -->
        <div>
          <label class="modal-label">Description</label>
          <input type="text" v-model="description" class="modal-input" />
        </div>

        <!-- Buttons -->
        <p v-if="error" class="text-sm text-red-400 font-semibold">
          {{ error }}
        </p>

        <div class="flex justify-between mt-4">
          <button type="button" @click="cancel" class="modal-button cancel">Cancel</button>
          <button type="submit" @click.prevent="submit" class="modal-button create">{{ submitButtonText }}</button>
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
import { ref, computed, defineEmits, onMounted, watchEffect } from 'vue'
import { eventService } from '@/services/eventService'
import { registrationService } from '@/services/registrationService'
import { eventGameService } from '@/services/eventGameService'
import { gameService } from '@/services/gameService'
import { useAuthStore } from '@/stores/authStore'

const emit = defineEmits(['close'])

const startDate = ref('')
const endDate = ref('')
const startTime = ref('')
const endTime = ref('')
const location = ref('')
const participants = ref('')
const selectedGame = ref('')
const description = ref('')
const contactEmail = ref('')
const error = ref('')
const authStore = useAuthStore()
const currentUserId = computed(() => authStore.user.id)
const games = ref([])

const props = defineProps({
  event: {
    type: Object,
    default: null
  }
})

const modalTitle = computed(() => props.event ? 'Edit Event' : 'Create Event')
const submitButtonText = computed(() => props.event ? 'Update' : 'Create')

watchEffect(() => {
  const newEvent = props.event
  if (!newEvent || games.value.length === 0) return

  startDate.value = newEvent.startDate
  endDate.value = newEvent.endDate
  startTime.value = newEvent.startTime
  endTime.value = newEvent.endTime
  location.value = newEvent.location
  participants.value = newEvent.maxNumParticipants
  description.value = newEvent.description
  contactEmail.value = newEvent.contactEmail

  const gameTitle = newEvent.eventName?.split(' by ')[0]
  const gameMatch = games.value.find(g => g.title === gameTitle)
  selectedGame.value = gameMatch || ''
})

const cancel = () => {
  emit('close')
}

const submit = async () => {
  try {
    // Reset warnings
    error.value = ''

    // Validate required fields
    if (
        !startDate.value || !endDate.value || !startTime.value || !endTime.value ||
        !location.value || !participants.value || !contactEmail.value || !description.value
    ) {
      error.value = 'Please fill in all required fields.'
      return
    }

    // Validate participant count
    if (parseInt(participants.value) < 1) {
      error.value = 'Number of participants must be at least 1.'
      return
    }

    // Validate start/end datetime
    const start = new Date(`${startDate.value}T${startTime.value}`)
    const end = new Date(`${endDate.value}T${endTime.value}`)
    const now = new Date()

    if (start < now) {
      error.value = 'Start date and time cannot be in the past.'
      return
    }

    if (end <= start) {
      error.value = 'End date and time must be after the start date and time.'
      return
    }

    const eventPayload = {
      startDate: startDate.value,
      startTime: startTime.value,
      endDate: endDate.value,
      endTime: endTime.value,
      maxNumParticipants: participants.value,
      location: location.value,
      description: description.value,
      contactEmail: contactEmail.value,
      creatorId: currentUserId.value
    }

    if (props.event) {
      await eventService.updateEvent(props.event.id, eventPayload)

      try {
        const currentGames = await eventGameService.findEventGamesByEvent(props.event.id)
        for (const game of currentGames) {
          await eventGameService.removeGameFromEvent(props.event.id, game.id)
        }
      } catch (innerError) {
        console.warn('Warning while removing games:', innerError)
      }

      try {
        if (selectedGame.value && selectedGame.value.id) {
          await eventGameService.addGameToEvent(props.event.id, selectedGame.value.id)
        }
      } catch (innerError) {
        console.warn('Warning while adding game to event:', innerError)
      }

    } else {
      const createdEvent = await eventService.createEvent(eventPayload)
      if (selectedGame.value && selectedGame.value.id) {
        await eventGameService.addGameToEvent(createdEvent.id, selectedGame.value.id)
      }
      await registrationService.registerParticipantToEvent(createdEvent.id, currentUserId.value)
    }

    cancel()

  } catch (e) {
    console.error('Main submit error:', e)
    error.value = 'Something went wrong. Please try again.'
  }
}
const fetchGames = async () => {
  try{
    const allGames = await gameService.findAllGames()
    games.value = allGames
  } catch (e){
    error.value = 'Failed to fetch games. Please try again later.'
    console.error(e)
  }
}

onMounted(fetchGames)
</script>

<style scoped>

.text-red-400 {
  color: #f87171;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  z-index: 50;
}

.modal-content {
  position: relative;
  top: 15vh; /* 20% from top of the viewport */
  transform: translateY(0);
  background-color: rgba(59, 24, 4, 0.9);
  color: rgb(230, 204, 189);
  padding: 2rem;
  border-radius: 1.5rem;
  width: 420px;
  box-shadow: 0 0 15px rgba(0,0,0,0.5);
  mix-blend-mode: add;
}

.modal-content {
  max-height: 80vh;
  overflow-y: auto;
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
  margin-top: 0.5rem;
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