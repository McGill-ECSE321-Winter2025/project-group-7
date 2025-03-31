<script setup>
import { useRoute, RouterLink, RouterView } from 'vue-router'
const route = useRoute()
</script>

<template>
  <header>
    <!--The navigation bar-->
      <nav v-if="!$route.meta.hideNavbar">
        
        <div class="nav-left">
          <RouterLink to="/games">Game</RouterLink>
          <RouterLink to="/events">Event</RouterLink>
          <RouterLink to="/requests">Request</RouterLink>
        </div>

      <!-- Account icon and Link on the right -->
        <div class="nav-right">

          <RouterLink to="/users">
            <img src="@/images/Account-icon.png" alt="Account Icon" class="nav-icon" />
          </RouterLink>

          <RouterLink to="/login">Sign Out</RouterLink>
        </div>
        </nav>
  </header>

  <RouterView />
</template>

<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
  padding: 2rem;
}

/* .logo {
  display: block;
  margin: 0 auto 2rem;
} */

nav {
  display: flex;
  position: fixed; /* Keeps it at the top even when scrolling */
  align-items: center; /* Align items vertically */
  top: -3.5rem;
  left: 50%;
  transform: translateX(-50%); /* Shift it back by half of its width */
  width: max-content;/* Shrinks to fit content */
  font-size: 55px;
  text-align: center;
  z-index: 1000; /* Ensures it stays above other elements */
  gap: 1.5rem; /* Adds spacing between links */
} 

.nav-center {
  position: absolute;
  display: flex;
  align-items: center;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  margin-top: 2rem;
  color: rgb(230, 204, 189);
  background-color: rgba(2, 1, 0, 0.411);
  mix-blend-mode: add;
  gap: 1rem;
  justify-content: center;
  font-size: 2.5rem;
}


.nav-right {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-left: auto;
  font-size: 2.5rem;
}

nav .nav-icon {
  width: 150px; /* Adjust to the desired size */
  height: 150px; /* Same as width to keep it circular */
  border-radius: 50%; /* Ensures it remains circular */
  object-fit: cover; /* Ensures image is cropped to fit inside the circle */
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
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

#pageContent {
  width: 100%;
  height: 100%;
}
@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  nav {
    text-align: center;
    font-size: 1rem;

    padding: 1rem 0;
    margin-top: 0;
  }
}

</style>

<style>
#app {
  /* Default background */
  background-image: url('@/images/GameNest-app-bg.png'); 
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  min-height: 100vh;
  min-width: 100vw; 
  width: 100%;

  /* Allow views to override */
  transition: background 0.3s ease-in-out;
}
</style>