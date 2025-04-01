<script setup>

import Card from 'primevue/card';
import { Button } from 'primevue';
</script>

<template>
  <h1 class="game-title">Browse Games</h1>
  <div class="game-card-wrapper">
    <div class="game-card-content">
      <div class="flip-card">
        <div class="flip-card-inner">
          <div class="flip-card-front">
    <Card style="width: 20rem; overflow: hidden" class="game-card">
        <template #header>
          <img src="/test-image2.jpg" alt="game image1" class="game-images">
        </template>
    </Card>
    </div>
    <div class="flip-card-back">
      <Card style="width: 20rem; overflow: hidden" class="game-card game-card-back">
        <template #title class="game-card-title">Game Title</template>
        <template #content>
          <p class="card-text">Min # Players:</p>
          <p class="card-text-center">Min # Players:</p>
          <p class="card-text">Max # Players:</p>
          <p class="card-text-center">Min # Players:</p>
            <p class="description">
              This is a short description about the game and it is getting increasingly longer so i have to deal with it 
            </p>
            <Button class="details-button">View Details</Button>
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
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  flex-direction: row;
  gap: 20px;
} 


.game-card-content {
  cursor: pointer;
  height: 100%;
  transition: transform 0.2s ease;
  margin-bottom: 0;
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
  height: 15%;
}

.game-card-title{
  height: 10%;
}



</style>