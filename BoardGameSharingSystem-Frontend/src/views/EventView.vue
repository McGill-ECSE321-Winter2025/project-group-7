<template>
    <main>
        <div class="search">
            <input type="search" name="eventName" id="eventSearch" v-model="searchString" placeholder="Search Events">
            <img id="searchIconImg" src="@/images/search.png">
        </div>
        <div class="searchFilters">
            <input type="checkbox" name="FilterByRegistered" id="registeredEventCheckbox" v-model="filterByRegistered">
            <label for="registeredEventCheckbox">Filter By Registered Events</label>
        </div>
        <button id="createEventButton" @click="createEvent">Create An Event</button>
        <p v-if="error" class="error-message">{{ error }}</p>
        <table v-if="filteredEvents && filteredEvents.length > 0" id="eventTable">
            <tr v-for="(event, index) in filteredEvents" :key="event.id">
                <td class="eventTableDataContainer">
                    <div class="eventBox">
                        <p class="eventName">{{event.eventName}}</p>
                        <div>    
                            <p class="eventCapacity">{{event.eventCapacity}}</p>
                            <button v-if="event.eventHasCapacity && !event.eventIsRegistered && !event.eventIsCreator" class="registerToEventButton" @click="registerToEvent(event.id)">Register</button>
                            <button v-if="event.eventIsRegistered && !event.eventIsCreator" class="dangerButton" @click="deregisterFromEvent(event.id)">Deregister</button>
                            <button v-if="event.eventIsCreator" class="updateEvent" @click="updateEvent(event)">Update</button>
                            <button v-if="event.eventIsCreator" class="dangerButton" @click="cancelEvent(event.id)">Cancel</button>
                        </div>
                    </div>
                    
                    <div class="eventDetails">
                        <p class="eventContactEmail">{{ event.contactEmail }}</p>
                        <p class="eventDateTime">{{event.eventFormattedDateTime}}</p>
                        <div class="eventDetailsLeft">
                            <p class="eventLocation">{{event.location}}</p>
                            <p class="eventDescription">{{event.description}}</p>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
        <div v-else class="noContent">
            No events found,<br> Create one!
        </div>

        <!-- Popup Modal -->
        <CreateEventModal v-if="showModal" :event="selectedEvent" @close="closeModal" />

    </main>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router';
import { eventService } from '@/services/eventService'
import { userService } from '@/services/userService'
import { eventGameService } from '@/services/eventGameService'
import { registrationService } from '@/services/registrationService'
import { useAuthStore } from '@/stores/authStore';
import CreateEventModal from '@/components/CreateEventModal.vue'

const showModal = ref(false)

const router = useRouter();
const events = ref([])
const error = ref(null)
const searchString = ref('')
const authStore = useAuthStore();
const currentUserId = computed(() => authStore.user.id);
const selectedEvent = ref(null);
const filterByRegistered = ref(false)

const filteredEvents = computed(() => {
    const now = new Date()
    return events.value.filter(event => {
        const eventEnd = new Date(`${event.endDate}T${event.endTime}`)
        if (eventEnd <= now) return false
        if (filterByRegistered.value && !event.eventIsRegistered) return false
        if (!searchString.value) return true
        const query = searchString.value.toLowerCase()
        return Object.values(event).some(val => String(val).toLowerCase().includes(query))
    })
})

const formatDate = (dateStr) => new Date(dateStr).toLocaleDateString(undefined, {
  weekday: 'short', year: 'numeric', month: 'short', day: 'numeric'
})

const formatTime = (timeStr) => {
  const dateObj = new Date(`1970-01-01T${timeStr}`)
  return dateObj.toLocaleTimeString(undefined, { hour: 'numeric', minute: '2-digit' })
}

const fetchEvents = async () => {
    try {
    error.value = null
    const fetchedEvents = await eventService.findAllEvents()
    const formattedEvents = await Promise.all(fetchedEvents.map(async (event) => {
        let gameTitle = "Game Not Found"
        let creatorName = "User Not Found"
        let eventCapacity = "N/A"
        let eventHasCapacity = false
        let eventFormattedDateTime = "UnknownTime UnknownDate - UnknownTime UnknownDate"
        let eventIsRegistered = false
        let eventIsCreator = event.creatorId == currentUserId.value

        try {
          const games = await eventGameService.findEventGamesByEvent(event.id)
          if (games.length > 0) gameTitle = games[0].gameTitle
        } catch (e) { console.warn(`Error Fetching Games: ${e}`) }

        try {
          const user = await userService.findUserAccount(event.creatorId)
          creatorName = user.name
        } catch (e) { console.warn(`Error Fetching User: ${e}`) }

        try {
          const registrations = await registrationService.findRegistrationByEvent(event.id)
          eventCapacity = `${registrations.length}/${event.maxNumParticipants}`
          eventHasCapacity = registrations.length < event.maxNumParticipants
          registrations.forEach(r => { if (r.userId == currentUserId.value) eventIsRegistered = true })
        } catch (e) { console.warn(`Error Fetching Registrations: ${e}`) }

        eventFormattedDateTime = `${formatTime(event.startTime)} ${formatDate(event.startDate)} - ${formatTime(event.endTime)} ${formatDate(event.endDate)}`

        return {
          ...event,
          eventName: `${gameTitle} by ${creatorName}`,
          eventCapacity,
          eventHasCapacity,
          eventFormattedDateTime,
          eventIsRegistered,
          eventIsCreator
        }
    }))
    events.value = formattedEvents
  } catch (err) {
    error.value = 'Failed to load events. Please try again later.'
    console.error('Error loading events:', err)
  }
}

const createEvent = () => {
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedEvent.value = null
  fetchEvents();
}

const updateEvent = (event) => {
  selectedEvent.value = { ...event }; // Make a copy to avoid mutating the original
  showModal.value = true;
}

const registerToEvent = async (eventId) => {
    try{
        await registrationService.registerParticipantToEvent(eventId, currentUserId.value)
        fetchEvents()
    } catch (e){
        error.value = 'Failed to register. Please try again.'
        console.error(e)
    }
}

const deregisterFromEvent = async (eventId) => {
    try{
        await registrationService.deregisterParticipantFromEvent(eventId, currentUserId.value)
        fetchEvents()
    } catch (e){
        error.value = 'Failed to deregister. Please try again.'
        console.error(e)
    }
}

const cancelEvent = async (eventId) => {
    try {
        await eventService.deleteEvent(eventId)
        fetchEvents()
    } catch (e){
      error.value = 'Failed to cancel event. Please try again.'
      console.error(e)
    }
}

onMounted(() => {
    if (!authStore.user.isAuthenticated) {
        router.push('/')
    } else {
        fetchEvents()
    }
})

</script>

<style scoped>
td{
    border-top: 0.2em solid rgba(134, 73, 37, 0.9);
    width: 100%;
    color:rgb(230, 204, 189);
}
tr:first-child td{
    border-top: none;
}
main{
    flex: 1;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    min-height: 100%;
    color: rgb(85, 40, 17);
    font-family: "Mansalva", sans-serif;
}
table{
    border-radius: 1em;
    border-style: solid;
    border-color: rgba(134, 73, 37, 0.9);
    width: 60%;
    border-spacing: 0;
    background-color: rgba(59, 24, 4, 0.9);
    mix-blend-mode:add;
    margin-top: 1%;
    font-size: 1.1rem;
}
.eventTableDataContainer{
    padding: 1rem;
    width: 100%;
}
.eventBox{
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    align-items: flex-start;
}
.eventBox div{
    display: flex;
}
.eventBox div p {
    margin-right: 1em;
}
.eventDescription{
    word-wrap: break-word;
    word-break: break-all;
    overflow-wrap: break-word;
}
button{
    background-color: rgba(145, 84, 49, 0.9);
    height: auto;
    width: auto;
    mix-blend-mode:add;
    border-radius: 10em;
    border-style: solid;
    border-color: grey;
    border-width: 0.1em;
    padding: 0em 1rem;
    color: rgb(230, 204, 189);
    font-family: "Mansalva", sans-serif;
    font-size: 1.1rem;
    text-shadow: 1px 1px 0.2rem rgba(0, 0, 0, 0.9);
}
.registerToEventButton{
    color: rgb(230, 204, 189);
}
.dangerButton{
    background-color: rgba(189, 0, 0, 0.9);
    color: rgb(230, 204, 189);
}
.dangerButton:hover{
    background-color: rgba(226, 12, 12, 0.9);
}
.dangerButton:active{
    background-color: rgba(255, 149, 149, 0.9);
}
.updateEvent {
    margin-right: 1rem;
}
button:hover{
    background-color: rgba(172, 117, 86, 0.9);
    mix-blend-mode:add;
}
button:active{
    background-color: rgba(77, 43, 24, 0.9);
    mix-blend-mode:add;
}
.search{
    margin-top: 1%;
    position: relative;
}
#searchIconImg{
    position: absolute;  
    left: 26.5em;
    top: 1.5em;
    width: 1.6em;
    height: 1.6em;
    pointer-events: none;
}
#eventSearch{
    height: 4em;
    width: 25em;
    border-radius: 10em;
    border-style: none;
    outline: none;
    text-indent: 2em;
    background-color: rgba(59, 24, 4, 0.9);
    caret-color: rgb(230, 204, 189);
    color: rgb(230, 204, 189);
    mix-blend-mode:add;
    font-family: "Mansalva", sans-serif;
    font-size: 1.1rem;
}
#eventSearch::placeholder{
    color: rgb(230, 204, 189);
}
#eventSearch::-webkit-search-cancel-button{
    margin-right: 3em;
    display: none;
}
#registeredEventCheckbox{
    accent-color: rgba(59, 24, 4, 0.9);
    mix-blend-mode:add;
    margin: 1rem;
}
label[for="registeredEventCheckbox"]{
    color: rgb(255, 235, 123);
    font-size: 1.1rem;
    text-shadow: 1px 1px 0.2rem rgba(0, 0, 0, 0.9);
}
.eventName{
    text-decoration: underline;
}
.eventName:hover{
    text-decoration: underline;
}
.eventCapacity{
    font-weight: bold;
}
.eventLocation{
    font-style: italic;
    color: rgb(228, 187, 140);
}
.noContent{
    background-color: rgba(59, 24, 4, 0.9);
    flex: 1;
    width: 60%;
    margin: 5vh;
    border-radius: 1em;
    border-style: solid;
    border-color: rgba(134, 73, 37, 0.9);
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2rem;
    color: rgb(230, 204, 189);
}
.error-message{
    color: rgb(209, 15, 15);
    text-shadow: 1px 1px 0.2rem rgba(255, 195, 66, 0.9);
    font-size: 1.1rem;
    font-weight: bold;
}
</style>
