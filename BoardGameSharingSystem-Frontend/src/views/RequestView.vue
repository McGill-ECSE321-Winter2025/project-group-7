<template>
  <main>
    <!-- Search bar and filters -->
    <div class="search">
      <input type="search" v-model="searchTerm" placeholder="Search Requests">
      <button @click="performSearch">Search</button>
    </div>
    <div class="searchFilters">
      <label><input type="checkbox" v-model="searchByName">by Name</label>
      <label><input type="checkbox" v-model="searchByGame">by Game</label>
      <label><input type="checkbox" v-model="searchByDate">by Date</label>
    </div>

    <!-- History button -->
    <button id="historyButton" @click="goToHistory">History</button>

    <!-- Request table -->
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
          <td>{{ request.borrower }}</td>
          <td>{{ request.game }}</td>
          <td>{{ request.startDate }}</td>
          <td>{{ request.endDate }}</td>
          <td>
            <button @click="acceptRequest(request.id)">Accept</button>
            <button @click="deleteRequest(request.id)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
  </main>
</template>

<script>
export default {
  data() {
    return {
      searchTerm: '',
      searchByName: false,
      searchByGame: false,
      searchByDate: false,
      requests: [
        { id: 1, borrower: 'Alice', game: 'Catan', startDate: '2025-04-01', endDate: '2025-04-10' },
        { id: 2, borrower: 'Bob', game: 'Splendor', startDate: '2025-04-02', endDate: '2025-04-08' },
        // Add more dummy requests
      ]
    };
  },
  computed: {
    filteredRequests() {
      return this.requests.filter(req => {
        const term = this.searchTerm.toLowerCase();
        return (
          (!this.searchByName || req.borrower.toLowerCase().includes(term)) &&
          (!this.searchByGame || req.game.toLowerCase().includes(term)) &&
          (!this.searchByDate || req.startDate.includes(term) || req.endDate.includes(term))
        );
      });
    }
  },
  methods: {
    performSearch() {
      console.log('Search triggered:', this.searchTerm);
    },
    acceptRequest(id) {
      console.log('Accepted request with ID:', id);
    },
    deleteRequest(id) {
      console.log('Deleted request with ID:', id);
    },
    goToHistory() {
      this.$router.push('/request-historys');
    }
  }
};
</script>

<style scoped>
main {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  color: rgb(230, 204, 189);
}
.search, .searchFilters {
  margin-top: 1em;
  display: flex;
  gap: 1em;
  align-items: center;
}
#requestTable {
  margin-top: 1em;
  border-collapse: collapse;
  width: 80%;
  background-color: rgba(59, 24, 4, 0.9);
  color: rgb(230, 204, 189);
  mix-blend-mode: add;
}
#requestTable th, #requestTable td {
  border: 1px solid grey;
  padding: 0.5em;
  text-align: center;
}
button {
  margin: 0 0.3em;
  padding: 0.4em 1em;
  border-radius: 10em;
  border: 1px solid grey;
  background-color: rgba(59, 24, 4, 0.9);
  color: rgb(230, 204, 189);
  mix-blend-mode: add;
  cursor: pointer;
}
button:hover {
  background-color: rgba(145, 84, 49, 0.9);
}
</style>
