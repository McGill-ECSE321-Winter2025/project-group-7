<template>
    <main>
        <div class="title-container" :style="{ backgroundImg: `url(${backgroundImg})` }">   
            <h1 class="game-title">
                <b>{{ gameTitle }}</b>
            </h1>
        </div>
        <section id="all">
            <section id = playerNb> {{ gameMinPlayer }} - {{ gameMaxPlayer }}  players</section>
            <div class="stuff"> 
                <Button id="borrowButton" class = "details-button" @click="goToGameOwners"> 
                    Borrow Game
                </button>
                <img :src="gameURL || placeholderImg" alt="Game Image" id="imageGame" v-if="placeholderImg" />
                <p id = "desc">{{ gameDesc }}</p>
            </div>
            <div>
                <h2 class="yellow-h2"><b>Latest Reviews</b></h2>
                <button id="createReview" @click="openCreateReview">Create Review</button>
                <section id="reviews">
                    <table border="1">
                        <thead id = allReviews>
                            <tr>
                                <th>Username</th>
                                <th>Rating</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(entry, index) in gameReviews" :key="index">
                                <td>{{ entry.username }}</td>
                                <td>{{ entry.rating }}</td>
                                <td>{{ entry.comment }}</td>
                            </tr>
                        </tbody>
                    </table>
            </section>
            </div>
        </section>
        <div v-if = "showPopup" class="modal-overlay">
            <div class = "modal-content">
                <form>
                    <label for = "rating" id = "r"> Rating:</label>
                    <div class="number-rating">
                        <input type="radio" id="num1" name="rating" value="1"    >
                        <label for="num1">1</label>

                        <input type="radio" id="num2" name="rating" value="2">
                        <label for="num2">2</label>

                        <input type="radio" id="num3" name="rating" value="3">
                        <label for="num3">3</label>

                        <input type="radio" id="num4" name="rating" value="4">
                        <label for="num4">4</label>

                        <input type="radio" id="num5" name="rating" value="5">
                        <label for="num5">5</label>
                    </div>
                    <label for = "desc" id = "d"> Description:</label><br>
                    <textarea id="descN" v-model = createdDesc name="desc" rows="4" cols="50"> </textarea>
                    <button id = "Submit" @click.prevent = "submitReview">Submit</button>
                    <button id = "Cancel" @click.prevent = "closeCreateReview">Cancel</button>
                </form> <br>
            </div>
            
        </div>
    </main>
</template>

<script setup>
import backgroundImg from '@/assets/jessica-woulfe-harvest-witch-interior-jw2.jpg';
import placeholderImg from '@/assets/istockphoto-1147544807-612x612.jpg';
import { reviewService } from '@/services/reviewService';
import {ref, onMounted, computed} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import axios from 'axios';
import { gameService } from '@/services/gameService';
import { useAuthStore } from '@/stores/authStore';
import { useGameStore } from '@/stores/gameStore';
import { userService } from '@/services/userService';



const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const gameStore = useGameStore();
const gameId = route.params.gameId;
const userId = computed(() => authStore.user.id);
console.log(userId.value);
const username = computed(() => authStore.user.name);
const showPopup = ref(false);
const error = ref(null);
let gameReviews = ref([]);
let users = ref([]);
const gameMaxPlayer = ref(0);
const gameMinPlayer = ref(0);
const gameDesc = ref('');
const gameURL = ref('');
const gameTitle = ref('');



const goToGameOwners = () => {
  router.push({ name: 'GameOwner', params: { gameId } });
};

const findAllReviews = async () => {
  try {
    let fetchedReviews = await reviewService.findAllReviewsOfGame(gameId);
    gameReviews.value = [];
    for (let review of fetchedReviews) {
      const user = await userService.findUserAccount(review.userId); 
      review.username = user ? user.name : 'Unknown User';
      gameReviews.value.push(review);
    }

  } catch (err) {
    error.value = 'Failed to load reviews. Please try again later.';
    console.error('Error loading reviews:', err);
  }
}

const fetchGameDetails = async () => {
        try {
            let fetchedGameDetails = await gameService.findGamebyId(gameId);
            gameMaxPlayer.value = fetchedGameDetails.maxNumPlayers;
            gameMinPlayer.value = fetchedGameDetails.minNumPlayers;
            gameDesc.value = fetchedGameDetails.description;
            gameURL.value = fetchedGameDetails.pictureURL;
            gameTitle.value = fetchedGameDetails.title;
            
        }
        catch(err) {
            error.value = 'Failed to load game. Please try again later.'
            console.error('Error loading game:', err)

        }

    }

    onMounted(() => {
        console.log("mounted")
        if (!authStore.user.isAuthenticated) {
            router.push('/')
        }
        else {
        findAllReviews();
        fetchGameDetails();}
    })

    const openCreateReview = () => {
        showPopup.value = true;
    }

    const closeCreateReview = () => {
        showPopup.value = false;
    }

    const submitReview = async () => {
  try {
    var checked_rating = document.querySelector('input[name="rating"]:checked');
    var newDesc = document.getElementById("descN").value;
    console.log(newDesc);
    console.log(checked_rating);
    if (!checked_rating)  {
        console.log('5');
      alert('You must select a rating for your review.');
    } else if (newDesc.trim().length === 0) {
      alert('You must include a comment.');
    } 
    else {
      await reviewService.createReview({
        rating: checked_rating.value,
        comment: newDesc.trim(),
      }, userId.value, gameId);
      findAllReviews();
    }
  } catch (err) {
    error.value = 'Failed to create the review. Please try again later.';
    console.error('Error creating review:', err);
  }
};





    
</script>

<style>

#allReviews {
    font-size: 125%;
    text-align: center;
    align-items: center;
    margin-top: 8em;



}

.game-title {
    margin-left: 1.5em;
    margin-top: 0.2em;
    font-family: "Mansalva", sans-serif;
    color: rgb(255, 235, 123);
    font-size: 300%;
    text-shadow: 1px 1px 0.2rem rgba(0, 0, 0, 0.9);
}


.title-container {
    top: 0em;
    left: 0em;
    width: 200em;
    height: 5em;
    background-size: cover;
    text-align: left;
    padding-bottom: 1em;
}

#createReview {

    margin-top: -100em;
    margin-left: 70em;
}

#r {
    font-family: "Mansalva", sans-serif;
    font-size: 106%;
}

#d {
    font-family: "Mansalva", sans-serif;
    font-size: 106%;
    margin-top: 10%;
}
#reviews {
    font-family: "Mansalva", sans-serif;
    align-items: center;
    background-color: rgba(59, 24, 4, 0.7);
    border-color: rgba(134, 73, 37, 0.5);
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    font-size: 2rem;
    color: rgb(230, 204, 189);
    margin-top: 0.7rem; 
    padding: 2rem;
    font-size: 1.1rem;
}

#reviews table {
    width: 100%;
    border-collapse: collapse;
}

#reviews th, #reviews td {
    padding: 10px;
    border: 1px solid rgba(134, 73, 37, 0.9);
    text-align: center;
}

#reviews th {
    font-weight: bold;
    background-color: rgba(134, 73, 37, 0.9);
}


#plantImg {
    z-index:10;
    position:absolute;
    margin-top: 5.8em;
    margin-left: -94em;
}

h1 {
    margin-left:2em;
    margin-top: -1em;
    font-family: "Mansalva", sans-serif;
    color: white;
}

#updateReview {
    margin-top: 38em;
    margin-left: 75em;


}

#deleteReview {
    margin-top: 38em;
    margin-left: 65em;
}


#imageGame{
    margin-top: 1em;
    margin-left: 5em;
    width: 25em;
    height:25em;

}


input[type="text"], textarea {
    border-radius: 10px;
    box-shadow: 0 0 0 1.5px #000;
    border: 5px solid transparent;

}

body {
    background-color: #cf8d4e;

}

#borrowButton {
    margin-top: -1em;
    margin-left: 70em;
    

}



h2 {

    font-size: 200%;
    font-family: "Mansalva", sans-serif;
    font-weight: bold;
    margin-left:4em;
    margin-top:10em;
}

.stuff {
    
    margin-bottom: 1em;


}

#descN {
    text-align: justify;
    color: rgb(230, 204, 189);
    font-family: "Mansalva", sans-serif;
    font-size: 106%;
    border-radius: 2em;
    width: 100%;
    height: 6em;
    max-width: none;
    outline: none;
    background-color: #8d6e63;
}

#Submit {
    position: relative;
    margin-top: 2em;
    margin-left: -1em;
}

#Cancel {
    position: relative;
    margin-top: 2em;
    margin-left: 1em;

}

#playerNb {

    text-align: center;
    display: grid;
    padding: 0.7em;
    margin-top: 0.6em;
    margin-left:3.4em;
    width: 8.5em;
    height: 3em;
    font-size: 106%;
    border-radius: 3em;
    border: none;
    font-family: "Mansalva", sans-serif;
    outline: none; 
    box-shadow: none;

    color: rgb(230, 204, 189);
    font-family: "Mansalva", sans-serif;
    font-size: 1.1rem;
    text-shadow: 1px 1px 0.2rem rgba(0, 0, 0, 0.9);
    color: white;
    font-family: "Mansalva", sans-serif;
    background-color: rgba(145, 84, 49, 0.9);
    mix-blend-mode:add;
    border-radius: 10em;
    border-style: solid;
    border-color: grey;
    border-width: 0.1em;
}
.star-checkbox input {
    opacity:0;
    
}

#num1{
    font-family: "Mansalva", sans-serif;
    font-size: 106%;
    margin-left: -2em;


}

#num2 {
    font-family: "Mansalva", sans-serif;
    font-size: 106%;
    margin-left: 2em
}

#num3 {
    font-family: "Mansalva", sans-serif;
    font-size: 106%;
    margin-left: 2em
}

#num4 {
    font-family: "Mansalva", sans-serif;
    font-size: 106%;
    margin-left: 2em

}

#num5 {
    font-family: "Mansalva", sans-serif;
    font-size: 106%;
    margin-left: 2em;

}

.star-checkbox{
    font-size: 100%;
    color:lightgray;
    cursor: pointer;
    transition: color 0.3s;

}

.star-checkbox :hover + svg {
    color: #d99058;
}

.star-checkbox input:checked + svg {
    color: #d99058;
}

textarea {
    resize : none;
}
#desc {
    text-align:center;
    align-items: center;
    align-items: top;
    margin-top: -20em;
    margin-left:30em;
    max-width: 30%;
    color: #FFFFFF;
    font-family: "Mansalva", sans-serif;
    font-size: 106%;

    padding: 1.5em; /* add padding to make it look like a text box */
    background-color: rgba(59, 24, 4, 0.9); /* warm background, similar to your theme */
    border-style: solid;
    border-color: rgba(134, 73, 37, 0.9);
    width: 60%;
    border-spacing: 0;
    mix-blend-mode:add;
    border-radius: 1em; /* rounded corners */
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.3); /* optional shadow for depth */
}

button{
    background-color: rgba(145, 84, 49, 0.9);
    mix-blend-mode:add;
    border-radius: 10em;
    border-style: solid;
    border-color: grey;
    border-width: 0.1em;
    padding: 0em 1rem;
    color: rgb(230, 204, 189);
    font-family: "Mansalva", sans-serif;
    font-size: 1.1rem;
    text-shadow: 1px 1px 0.2rem rgba(0, 0, 0, 0.9);
}

button:hover{
    background-color: rgba(172, 117, 86, 0.9);
    mix-blend-mode:add;
}
button:active{
    background-color: rgba(77, 43, 24, 0.9);
    mix-blend-mode:add;
}



.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent background */
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-content {
    background-color: #cf8d4e;
    width: 50%; /* Adjust width */
    max-width: 500px;
    padding: 2em; /* Reduce padding to fit textarea */
    border-radius: 10px;
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.3);
    text-align: center;
}

.yellow-h2 {
    color: rgb(255, 235, 123);
    font-size: 200%;
    text-shadow: 1px 1px 0.2rem rgba(0, 0, 0, 0.9);
}

.description-block {
    display: flex;
    flex-direction: column;
    align-items: flex-start; /* aligns both playerNb and desc to the left */
    margin: 1em 0;
}

</style>