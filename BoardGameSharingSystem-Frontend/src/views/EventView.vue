<template>
  <main>
    <div class="search">
      <input type="search" name="eventName" id="eventSearch" placeholder="Search Events">
      <img id="searchIconImg" src="./search.png">
    </div>

    <div class="searchFilters">
      <input type="checkbox" name="FilterByRegistered" id="registeredEventCheckbox">
      <label for="registeredEventCheckbox">Filter By Registered Events</label>
    </div>

    <button id="createEventButton" @click="createEvent">Create An Event</button>

    <table id="eventTable">
      <tbody>
      <tr>
        <td class="eventTableDataContainer">
          <div class="eventBox">
            <p class="eventName">Event Name</p>
            <div>
              <p class="eventCapacity">8/9</p>
              <button class="registerToEventButton">Register</button>
            </div>
          </div>

          <div class="eventDetails">
            <p class="eventDateTime">9AM May 12, 2025 - 10AM May 12, 2025</p>
            <div class="eventDetailsLeft">
              <p class="eventLocation">Event Location</p>
              <p class="eventDescription">Event Description...</p>
            </div>
          </div>
        </td>
      </tr>
      </tbody>
    </table>

    <!-- Popup Modal -->
    <CreateEventModal v-if="showModal" @close="closeModal" />
  </main>
</template>

<script setup>
import { ref } from 'vue'
import CreateEventModal from '@/components/CreateEventModal.vue'

const showModal = ref(false)

const createEvent = () => {
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}
</script>

<style scoped>
td {
  border-top: 0.1em solid black;
  width: 100%;
}
tr:first-child td {
  border-top: none;
}
main {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100%;
  color: rgb(230, 204, 189);
}
table {
  border-radius: 1em;
  border-style: solid;
  border-color: green;
  width: 60%;
  border-spacing: 0;
  background-color: rgba(59, 24, 4, 0.9);
  mix-blend-mode: add;
  margin-top: 1%;
}
.eventTableDataContainer {
  padding: 1%;
  width: 100%;
}
.eventBox {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
}
.eventBox div {
  display: flex;
}
.eventBox div p {
  margin-right: 1em;
}
.eventDescription {
  word-wrap: break-word;
  word-break: break-all;
  overflow-wrap: break-word;
}
button {
  background-color: rgba(59, 24, 4, 0.9);
  color: rgb(230, 204, 189);
  mix-blend-mode: add;
  border-radius: 10em;
  border-style: solid;
  border-color: grey;
  border-width: 0.1em;
  padding: 0.5em;
}
.registerToEventButton {
  color: #32CD32;
}
button:hover {
  background-color: rgba(145, 84, 49, 0.9);
  mix-blend-mode: add;
}
button:active {
  background-color: rgba(187, 128, 95, 0.9);
  mix-blend-mode: add;
}
.search {
  margin-top: 1%;
  position: relative;
}
#searchIconImg {
  position: absolute;
  left: 20em;
  top: 1em;
  width: 1.6em;
  height: 1.6em;
  pointer-events: none;
  filter: brightness(10);
}
#eventSearch {
  height: 4em;
  width: 25em;
  border-radius: 10em;
  border-style: none;
  outline: none;
  text-indent: 2em;
  background-color: rgba(59, 24, 4, 0.9);
  caret-color: rgb(230, 204, 189);
  color: rgb(230, 204, 189);
  mix-blend-mode: add;
}
#eventSearch::-webkit-search-cancel-button {
  color: #bababa;
  margin-right: 5em;
}
#registeredEventCheckbox {
  margin-right: 0.2em;
  accent-color: rgba(59, 24, 4, 0.9);
  mix-blend-mode: add;
}
.eventName {
  text-decoration: underline;
}
.eventName:hover {
  text-decoration: underline;
}
.eventCapacity {
  font-weight: bold;
}
.eventLocation {
  font-style: italic;
  color: rgb(197, 102, 54);
}
</style>
