<script setup>

import Card from 'primevue/card';
import { Button } from 'primevue';
</script>

<template>
  <h1 class="game-title">Games</h1>
  <div class="game-card-wrapper">
    <div class="game-card-content">
      <div class="flip-card">
        <div class="flip-card-inner">
          <div class="flip-card-front">
    <Card style="width: 20rem; overflow: hidden" class="game-card">
        <template #header>
          <img src="/test-image2.jpg" alt="game image1" class="game-images">
        </template>
        <template #title>Game</template>
        <template #subtitle>Min-Max Players </template>
        <template #content>
            <p class="m-0">
              This is a short description about the game 
            </p>
        </template>
        <template #footer>This is a footer</template>
    </Card>
    </div>
    <div class="flip-card-back">
      <Card style="width: 20rem; overflow: hidden" class="game-card">
        <template #header>
        </template>
        <template #title>Game</template>
        <template #subtitle>Min-Max Players </template>
        <template #content>
            <p class="m-0">
              This is a short description about the game 
            </p>
            <Button>View Details</Button>
        </template>
    </Card>
    </div>
        </div>
      </div>
    </div>  
    <div class="game-card-content">
      <div class="flip-card">
        <div class="flip-card-inner">
          <div class="flip-card-front">
    <Card style="width: 20rem; overflow: hidden" class="game-card">
        <template #header>
          <img src="/test-image2.jpg" alt="game image1" class="game-images">
        </template>
        <template #title>Game</template>
        <template #subtitle>Min-Max Players </template>
        <template #content>
            <p class="m-0">
              This is a short description about the game 
            </p>
        </template>
        <template #footer>This is a footer</template>
    </Card>
    </div>
    <div class="flip-card-back">
      <Card style="width: 20rem; overflow: hidden" class="game-card">
        <template #header>
        </template>
        <template #title>Game</template>
        <template #subtitle>Min-Max Players </template>
        <template #content>
            <p class="m-0">
              This is a short description about the game 
            </p>
            <Button>View Details</Button>
        </template>
    </Card>
    </div>
        </div>
      </div>
    </div>    
  </div>
  
</template>

<style>
 .game-card-wrapper {
  position: relative;
  max-width: 200px;
  margin-top: 10vh;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 20px;
} 


.game-card-content {
  cursor: pointer;
  height: 100%;
  transition: transform 0.2s ease;
  margin-bottom: 0;
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

.game-title {
  width: auto;
  text-align: left;
  position: fixed;
  margin-left: 0;
  margin-bottom: 10vh;
  margin-top: 10vh;
}

.game-images{
  max-height: 100px;
}

.game-card{
  height: 300px ;
}


</style>