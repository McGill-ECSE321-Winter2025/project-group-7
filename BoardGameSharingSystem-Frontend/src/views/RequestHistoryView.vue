<template>
  <main>
    <h1 class="title">Recent Lending History</h1>
    <table id="historyTable">
      <thead>
        <tr>
          <th>Borrower </th>
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

<script>
export default {
  data() {
    return {
      lendingHistory: [
        { borrower: "Yeonjun", game: "Splendor", startDate: "2025-05-01", endDate: "2025-05-10", status: "Returned" },
        { borrower: "Soobin", game: "Catan", startDate: "2025-04-12", endDate: "2025-04-20", status: "Overdue" },
        { borrower: "Beomgyu", game: "Carcassonne", startDate: "2025-03-01", endDate: "2025-03-10", status: "Returned" },
      ],
    };
  },
};
</script>

<style scoped>
main {
  padding: 2em;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: rgb(230, 204, 189);
}

.title {
  margin-bottom: 1em;
  font-size: 2em;
  text-align: center;
  color: rgb(230, 204, 189);
}

#historyTable {
  width: 80%;
  border-collapse: collapse;
  background-color: rgba(59, 24, 4, 0.9);
  color: rgb(230, 204, 189);
}

th, td {
  border: 1px solid #aaa;
  padding: 0.75em;
  text-align: center;
}

th {
  background-color: rgba(97, 42, 7, 0.9);
}
</style>
