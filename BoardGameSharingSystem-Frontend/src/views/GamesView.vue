<script setup>

import Card from 'primevue/card';
import { Button, Toast, useToast } from 'primevue';
import { onMounted, ref } from 'vue';
import { gameService } from '@/services/gameService';
import { useAuthStore } from '@/stores/authStore';
import { useRouter } from 'vue-router';

let games = ref([]);
const error = ref(null);
const toast = useToast();
const authStore = useAuthStore();
const router = useRouter();

const findAllGames = async () => {
  try{
    let fetchedGames = await gameService.findAllGames()
    games.value = fetchedGames;
  }catch (err){
    error.value = 'Failed to load available games. PLease try later'
    console.error('Error loading games', err)
    show()
  }
}

onMounted( () =>{
  if (!authStore.user.isAuthenticated) {
        router.push('/')
    } else {
      findAllGames()
    }
})

const show = () => {
    toast.add({ severity: 'error', summary: 'Could not load available games', detail: 'Please try again later', });
};

</script>

<template class="top">
  <Toast />
  <h1 class="game-title"></h1>
  <div class="game-card-wrapper">
    <div class="game-card-content" v-for="game in games">
      <div class="flip-card">
        <div class="flip-card-inner">
          <div class="flip-card-front">
    <Card style="width: 20rem; overflow: hidden" class="game-card">
        <template #header>
          <img :src="game.pictureURL" :alt="game.title" class="game-images">
        </template>
    </Card>
    </div>
    <div class="flip-card-back">
      <Card style="width: 20rem; overflow: hidden" class="game-card game-card-back">
        <template #title class="game-card-title">{{ game.title }}</template>
        <template #content>
          <p class="card-text">Min # Players:</p>
          <p class="card-text-center">{{ game.minNumPlayers }}</p>
          <p class="card-text">Max # Players:</p>
          <p class="card-text-center">{{ game.maxNumPlayers }}</p>
            <p class="description">
              {{ game.description }} 
            </p>
            <div class="button-link-wrapper">
            <Button class="details-button">
              <RouterLink class="link" :to="{name:'game',params:{gameId:game.id}}">
                View details
              </RouterLink>
            </Button>
            </div>
        </template>
    </Card>
    </div>
        </div>
      </div>
    </div>    
  </div>
  
</template>

<style scoped>
 .game-card-wrapper {
  position: relative;
  max-width: auto;
  margin-top: 10vh;
  margin-bottom: 20vh;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  flex-direction: row;
  gap: 20px;
  top: 10vh;
} 


.game-card-content {
  cursor: pointer;
  height: 100%;
  transition: transform 0.2s ease;
  margin-bottom: 0;
  margin-left: 10vh;
  margin-right: 5vh;
  text-align: center;
}

.flip-card {
  width: 20rem;
  height: 20rem;
  perspective: 1000px; /* Remove this if you don't want the 3D effect */
  /* border: 1px solid #f1f1f1; */
  margin-top: 0;
  margin-bottom: 0;
  margin-right: 0;
  margin-left: 0;
}

.flip-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  text-align: center;
  transition: transform 0.8s;
  transform-style: preserve-3d;
}

.flip-card:hover .flip-card-inner {
  transform: rotateY(180deg);
}

.flip-card-front, .flip-card-back {
  position: absolute;
  width: 100%;
  height: 100%;  
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
}

.flip-card-back {
  transform: rotateY(180deg);
}

.game-card{
  height: 300px ;
}

.game-images{
  width: 100%;
  max-width: 20rem;
  height: auto;
  object-fit: cover;
}

.game-card-back{
  background-color: #602809;
  font-family: "Mansalva", sans-serif;
  color: beige;
}

.card-text{
  text-align: left;
}

.details-button{
  margin-top: 10vh;
  background-color:transparent ;
  border-color: white;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
  padding: 0%;
  width: 100%;
}

.details-button:hover{
  background-color:beige;
  color: beige;
}

.card-text-center{
  text-align: center;
}

.game-title{
  color: #602809;
  font-family:  "Mansalva", sans-serif;
  margin-top: 17.5vh;
  background-color: rgba(221, 216, 201, 0.7);
  border-radius: 5%;
  border-top-width: 0;
}

.description{
  text-align: left;
  max-height: 10%;
}

.game-card-title{
  height: 10%;
}

.link{
  display: block;
  width: 100%; /* Make the link take up full width of the button */
  text-align: center;
  background-color: transparent; /* Make the link behave like a button */
  color: white; /* Set color to white to match button text color */
  text-decoration: none; /* Remove underline */
  padding: 0;
  height: 100%;
  padding: 0;
}

.button-link-wrapper {
  display: flex;
  width: 100%;
  justify-content: center; /* Center the button and link wrapper */
}


</style>