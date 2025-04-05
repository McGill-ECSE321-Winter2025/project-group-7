<template>
  <main>
    <h1 class="title">Recent Lending History</h1>
    <button id="backButton" @click="goToRequests">Back to Requests</button>
    <table id="historyTable">
      <thead>
        <tr>
          <th>Borrower</th>
          <th>Game</th>
          <th>Start Date</th>
          <th>End Date</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(entry, index) in lendingHistory" :key="index">
          <td>{{ entry.borrower }}</td>
          <td>{{ entry.game }}</td>
          <td>{{ entry.startDate }}</td>
          <td>{{ entry.endDate }}</td>
          <td>{{ entry.status }}</td>
        </tr>
      </tbody>
    </table>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { requestHistoryService } from '@/services/requestHistoryService'

const router = useRouter()
const lendingHistory = ref([])

const fetchHistory = async () => {
  try {
    lendingHistory.value = await requestHistoryService.findRequestHistory()
  } catch (error) {
    console.error('Error fetching lending history:', error)
  }
}

const goToRequests = () => {
  router.push('/requests')
}

onMounted(() => {
  fetchHistory()
})
</script>

<style scoped>
main {
  padding: 2em;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: rgb(230, 204, 189);
  font-family: "Mansalva", sans-serif;
}

.title {
  margin-bottom: 1em;
  font-size: 2.5em;
  text-align: center;
  color: rgb(255, 235, 123);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.7);
}

#backButton {
  margin-bottom: 1em;
  padding: 0.5em 1em;
  background-color: rgba(145, 84, 49, 0.9);
  border: none;
  border-radius: 10em;
  color: rgb(230, 204, 189);
  font-family: "Mansalva", sans-serif;
  font-size: 1.1rem;
  cursor: pointer;
  text-shadow: 1px 1px 0.2rem rgba(0, 0, 0, 0.9);
}

#backButton:hover {
  background-color: rgba(172, 117, 86, 0.9);
}

#backButton:active {
  background-color: rgba(77, 43, 24, 0.9);
}

#historyTable {
  width: 60%;
  border-collapse: collapse;
  border-radius: 1em;
  background-color: rgba(59, 24, 4, 0.9);
  color: rgb(230, 204, 189);
  mix-blend-mode: add;
  font-size: 1.1rem;
  border-spacing: 0;
  margin-top: 1em;
}

#historyTable th,
#historyTable td {
  border: 0.2em solid rgba(134, 73, 37, 0.9);
  padding: 0.5em;
  text-align: center;
}

#historyTable th {
  background-color: rgba(97, 42, 7, 0.9);
}
</style>
