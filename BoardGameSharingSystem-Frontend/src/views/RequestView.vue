<template>
  <div v-if="isGameOwner">
    <main>
      <div class="search">
        <input
          type="search"
          v-model="searchTerm"
          placeholder="Search Requests"
        />
        <img id="searchIconImg" src="@/images/search.png" alt="Search Icon" />
      </div>
      <div class="searchFilters">
        <label>
          <input type="checkbox" v-model="filterByName" />
          by Name
        </label>
        <label>
          <input type="checkbox" v-model="filterByGame" />
          by Game
        </label>
        <label>
          <input type="checkbox" v-model="filterByDate" />
          by Date
        </label>
      </div>

      <button id="historyButton" @click="goToHistory">History</button>

      <table id="requestTable">
        <thead>
          <tr>
            <th>Borrower</th>
            <th>Game</th>
            <th>Start Date</th>
            <th>End Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="request in filteredRequests" :key="request.id">
            <td>{{ request.borrowerName }}</td>
            <td>{{ request.gameTitle }}</td>
            <td>{{ request.startDate }}</td>
            <td>{{ request.endDate }}</td>
            <td>
              <button v-if="request.status !== 'Accepted'" @click="acceptRequest(request.id)">Accept</button>
              <button v-if="request.status !== 'Declined'" @click="declineRequest(request.id)">Decline</button>
            </td>
          </tr>
        </tbody>
      </table>
    </main>
  </div>
  <div v-else>
    <p>This page is only available for Game Owners.</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { requestService } from '@/services/requestService'
import { useAuthStore } from '@/stores/authStore'
import { gameOwningService } from '@/services/gameOwningService'
import { gameCopyService } from '@/services/gameCopyService'

const router = useRouter()
const authStore = useAuthStore()

const searchTerm = ref('')
const filterByName = ref(false)
const filterByGame = ref(false)
const filterByDate = ref(false)
const requests = ref([])
const isGameOwner = ref(false)

const checkGameOwnerStatus = async () => {
  try {
    const result = await gameOwningService.findGameOwner(authStore.user.id)
    if (!result) {
      isGameOwner.value = false
    } else {
      isGameOwner.value = result.userId !== -1
      console.log('result.userId:', result.userId);
    }
  } catch (error) {
    console.error('Error checking game owner status:', error)
    isGameOwner.value = false
  }
}

const fetchPendingRequestsForOwnedGames = async () => {
  try {
    const gameCopies = await gameCopyService.findGameCopiesForGameOwner(authStore.user.id)
    let allRequests = []
    for (const copy of gameCopies) {
      const pendingRequests = await requestService.findPendingRequests(copy.id)
      allRequests = allRequests.concat(pendingRequests)
    }
    requests.value = allRequests
  } catch (error) {
    console.error('Error fetching pending requests for owned games:', error)
  }
}

const filteredRequests = computed(() => {
  const term = searchTerm.value.toLowerCase()
  return requests.value.filter(request => {
    return (
      (!filterByName.value ||
        (request.borrowerName && request.borrowerName.toLowerCase().includes(term))) &&
      (!filterByGame.value ||
        (request.gameTitle && request.gameTitle.toLowerCase().includes(term))) &&
      (!filterByDate.value ||
        (request.startDate && request.startDate.includes(term)) ||
        (request.endDate && request.endDate.includes(term)))
    )
  })
})

const acceptRequest = async (id) => {
  try {
    await requestService.acceptRequest(id)
    requests.value = requests.value.filter(r => r.id !== id)
  } catch (error) {
    console.error(`Error accepting request ${id}:`, error)
  }
}

const declineRequest = async (id) => {
  try {
    await requestService.declineRequest(id)
    requests.value = requests.value.filter(r => r.id !== id)
  } catch (error) {
    console.error(`Error declining request ${id}:`, error)
  }
}

const goToHistory = () => {
  router.push('/request-historys')
}

onMounted(async () => {
  await checkGameOwnerStatus()
  if (isGameOwner.value) {
    await fetchPendingRequestsForOwnedGames()
  }
})
</script>

<style scoped>
main {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  color: rgb(230, 204, 189);
  font-family: "Mansalva", sans-serif;
  padding: 1em;
}

.search {
  position: relative;
  margin-top: 1em;
  display: flex;
  align-items: center;
  gap: 0.5em;
}

.search input[type="search"] {
  height: 4em;
  width: 25em;
  border-radius: 10em;
  border: none;
  outline: none;
  padding-left: 2em;
  background-color: rgba(59, 24, 4, 0.9);
  caret-color: rgb(230, 204, 189);
  color: rgb(230, 204, 189);
  font-family: "Mansalva", sans-serif;
  font-size: 1.1rem;
}

.search input[type="search"]::placeholder {
  color: rgb(230, 204, 189);
}

#searchIconImg {
  position: absolute;
  left: 26.5em;
  top: 1.5em;
  width: 1.6em;
  height: 1.6em;
  pointer-events: none;
}

.searchFilters {
  margin-top: 1em;
  display: flex;
  gap: 1em;
  align-items: center;
}

.searchFilters label {
  color: rgb(255, 235, 123);
  font-size: 1.1rem;
  text-shadow: 1px 1px 0.2rem rgba(0, 0, 0, 0.9);
}

.searchFilters label input[type="checkbox"] {
  margin-right: 0.5em;
}

#requestTable {
  margin-top: 1em;
  border-radius: 1em;
  border-style: solid;
  border-color: rgba(134, 73, 37, 0.9);
  width: 60%;
  border-spacing: 0;
  background-color: rgba(59, 24, 4, 0.9);
  mix-blend-mode: add;
  font-size: 1.1rem;
  color: rgb(230, 204, 189);
}

#requestTable th,
#requestTable td {
  border: 0.2em solid rgba(134, 73, 37, 0.9);
  padding: 0.5em;
  text-align: center;
}

button {
  background-color: rgba(145, 84, 49, 0.9);
  border-radius: 10em;
  border: 0.1em solid grey;
  padding: 0em 1rem;
  color: rgb(230, 204, 189);
  font-family: "Mansalva", sans-serif;
  font-size: 1.1rem;
  text-shadow: 1px 1px 0.2rem rgba(0, 0, 0, 0.9);
  cursor: pointer;
  margin: 0 0.3em;
}

button:hover {
  background-color: rgba(172, 117, 86, 0.9);
}

button:active {
  background-color: rgba(77, 43, 24, 0.9);
}

.noContent {
  margin-top: 1em;
  color: rgb(230, 204, 189);
}
</style>
