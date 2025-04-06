<!-- components/CreateEventModal.vue -->
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
import { useAuthStore } from '@/stores/authStore';

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
const error = ref(null)
const authStore = useAuthStore();
const currentUserId = computed(() => authStore.user.id);
const games = ref([])
const modalTitle = computed(() => props.event ? 'Edit Event' : 'Create Event')
const submitButtonText = computed(() => props.event ? 'Update' : 'Create')

const props = defineProps({
  event: {
    type: Object,
    default: null
  }
})

watchEffect(() => {
  const newEvent = props.event
  if (!newEvent || games.value.length === 0) return

  console.log('Populating form from event...')

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
      // Updating
      await eventService.updateEvent(props.event.id, eventPayload)
      const currentGames = await eventGameService.findEventGamesByEvent(props.event.id)

      // Remove all current games (assuming one-to-one)
      for (const game of currentGames) {
        await eventGameService.removeGameFromEvent(props.event.id, game.id)
      }
      if (selectedGame.value && selectedGame.value.id) {
        await eventGameService.addGameToEvent(props.event.id, selectedGame.value.id)
      }
    } else {
      // Creating
      const createdEvent = await eventService.createEvent(eventPayload)
      if (selectedGame.value && selectedGame.value.id) {
        await eventGameService.addGameToEvent(createdEvent.id, selectedGame.value.id)
      }
      await registerToEvent(createdEvent.id)
    }

    cancel()
  } catch (e) {
    console.log(e)
    error.value = 'Something went wrong. Please try again.'
  }
}
const registerToEvent = async (eventId) => {
    try{
        await registrationService.registerParticipantToEvent(eventId, currentUserId.value)
    } catch (e){
        error.value = 'Failed to register Participant. Please try again later'
        console.error('Error register participant:', e)
    }
}

const fetchGames = async () => 
{
  try {
    const allGames = await gameService.findAllGames();
    games.value = allGames;
  } catch (error) {
    error.value = 'Failed to fetch Games. Please try again later'
    console.error('Error fetching games:', e)
  }
}
onMounted(() => {
  fetchGames()
})
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
