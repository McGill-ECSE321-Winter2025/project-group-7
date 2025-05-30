<script setup>
import { useRoute, useRouter, RouterLink, RouterView } from 'vue-router'
import {ref, computed, watch} from 'vue'
import { useAuthStore } from './stores/authStore';
import { gameOwningService } from './services/gameOwningService';
const route = useRoute()
const router = useRouter()
function goToHomePage() {
  router.push('/games')
}
const authStore = useAuthStore()
console.log('isGameOwner (from store):', authStore.user.isGameOwner);
const isGameOwner = computed(() => authStore.user.isGameOwner);
const currentUserId = ref(authStore.user.id) // ensure user is loaded before this
watch(
  () => authStore.user.id,
  async (newId) => {
    if (authStore.user.isAuthenticated && newId) {
      try {
        const result = await gameOwningService.findGameOwner(newId)
        authStore.user.isGameOwner = result?.userId !== -1
        console.log('Game owner status updated:', authStore.user.isGameOwner)
      } catch (err) {
        console.error('Failed to fetch game owner status:', err)
        authStore.user.isGameOwner = false
      }
    }
  },
  { immediate: true }
)
</script>

<template>
  <header v-if="!$route.meta.hideNavbar">
    <!--The navigation bar-->
      <nav>
        <img class="logo" src="@/images/GameNest-logo.png" @click="goToHomePage">
        <div class="nav-left">
          <RouterLink to="/games">Game</RouterLink>
          <RouterLink to="/events">Event</RouterLink>
          <RouterLink v-if="isGameOwner" to="/requests">Request</RouterLink>
        </div>

      <!-- Account icon and Link on the right -->
        <div class="nav-right">

          <RouterLink to="/users">
            <img src="@/images/Account-icon.png" alt="Account Icon" class="nav-icon" />
          </RouterLink>

          <RouterLink to="/">Sign Out</RouterLink>
        </div>
      </nav>
  </header>

  <RouterView id="pageContent"/>
</template>

<style scoped>
header {
  display: flex;
  position: fixed;
  place-items: center;
  max-height: 6.5rem;
  width: 100%;
  background-color: rgba(221, 216, 201, 0.7);
  mix-blend-mode: additive;
  z-index: 1000;
}

/* .logo {
  display: block;
  margin: 0 auto 2rem;
} */

nav {
  display: flex;
  justify-content: space-between;
  align-items: center; /* Align items vertically */
  width: 100%;/* Shrinks to fit content */
  font-size: 55px;
  text-align: center;
} 

.logo{
  width: 20rem;
  height: auto;
}
.nav-left {
  display: flex;
  align-items: center;
  text-align: center;
  margin-top: 0rem;
  color: rgb(230, 204, 189);
  mix-blend-mode: add;
  gap: 1rem;
  justify-content: center;
  font-size: 2.5rem;
}


.nav-right {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding-right: 0rem;
  margin-top: 0rem;
  font-size: 2.5rem;
}

.nav-icon {
  width: 60px; /* Adjust to the desired size */
  height: 60px; /* Same as width to keep it circular */
  margin-top: 1rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  font-family: "Mansalva", sans-serif;
  color: #602809;
  display: inline-block;
  padding: 0 1rem;
}

nav a:first-of-type {
  border: 0;
}

#pageContent {
  margin-top: 10rem;
  width: 100%;
  height: 100%;
}
</style>
