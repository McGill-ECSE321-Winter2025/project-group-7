<template>
    <main>
        <div class="title-container" :style="{ backgroundImg: `url(${backgroundImg})` }">
            
            <h1>
                <b>{{ gameTitle }}</b>
            </h1>
        
        </div>
        <div class="stuff">
            <section id = playerNb> {{minPlayers}} - {{ maxPlayers }}</section>
            <button id="borrowButton"> Borrow Game</button>
           
            <img :src="pictureURL" alt="Game Image" id="imageGame" v-if="pictureURL" />
            <img :src = "plantImg" id="plantImg"/>
            
            <p id = "desc">{{ description }}</p>

        </div>
        <div>
            <h2><b>Latest Reviews</b></h2>
            <button id="createReview" @click="openCreateReview">Create Review</button>
            <section id="reviews">
                <div id = "r1">
                    <p>Username</p>
                    <p>Description</p>
                </div>

            </section>

        </div>

        <div v-if = "showPopup" class="modal-overlay">
            <div class = "modal-content">
                <form>
                    <div class="stars">
                        <label for="rating"> Set Rating:</label>

                        <label class="star-checkbox">
                        <input type = "checkbox" id = "1star" name="1star" value = "1star">
                        <font-awesome-icon :icon="['fas', 'star']" />
                        </label>

                        <label class="star-checkbox">
                        <input type = "checkbox" id = "2star" name="2star" value = "2star">
                        <font-awesome-icon :icon="['fas', 'star']" />
                        </label>

                        <label class="star-checkbox">
                        <input type = "checkbox" id = "3star" name="3star" value = "3star">
                        <font-awesome-icon :icon="['fas', 'star']" />
                        </label>

                        <label class="star-checkbox">
                        <input type = "checkbox" id = "4star" name="4star" value = "4star">
                        <font-awesome-icon :icon="['fas', 'star']" />
                        </label>

                        <label class="star-checkbox">
                        <input type = "checkbox" id = "5star" name="5star" value = "5star">
                        <font-awesome-icon :icon="['fas', 'star']" />
                        </label>
                    
                    </div>

                    <label for = "desc"> Description:</label><br>
                    <textarea id="descN" v-model = createdDesc name="desc" rows="4" cols="50"> </textarea>
                    <button id = "Submit" @click.prevent = "submitReview">Submit</button>
                    <button id = "Cancel" @click.prevent = "closeCreateReview">Cancel</button>
                </form> <br>
            </div>
            
        </div>
    </main>
</template>

<script>
import backgroundImg from '@/assets/jessica-woulfe-harvest-witch-interior-jw2.jpg';
import placeholderImg from '@/assets/istockphoto-1147544807-612x612.jpg';
import plantImg from '@/assets/pixelated-green-greenery-sprout-leaf-260nw-2466520193-removebg-preview1.png';
import { reviewService } from '@/services/reviewService';
import {ref, onMounted} from 'vue';
import {useRoute} from 'vue-router';
import axios from 'axios';


export default {

    setup() {
        const route = useRoute();
        const gameId = ref(route.params.gameId);
        const gameTitle = ref('');
        const minPlayers = ref(0);
        const maxPlayers = ref(0);
        const pictureURL = ref('');
        const description = ref('');
        const reviews = ref([]);
        const createdDesc = ref('');
        const createdRating = ref('');
        const showPopup = ref(false);
        
        const fetchGameDetails = async () => {
            try {
                const response = await axios.get(`http://localhost:8090/api/games/${gameId.value}`);
                gameTitle.value = response.data.title;
                minPlayers.value = response.data.minNumPlayers;
                maxPlayers.value = response.data.maxNumPlayers;
                pictureURL.value = response.data.pictureURL;
                description.value = response.data.description;
            }

            catch(error) {
                console.error('Error fetching game details:', error);
            }

        }

        const fetchReviews = async() => {
            try {
                const response = await axios.get(`http://localhost:8090/api/reviews?gameId=${gameId.value}`);
                reviews.value = response.data;

            }

            catch(error) {
                console.error('Error fetching reviews:', error);
            }

        }


        const submitReview = async() => {
            try {
                const reviewData = {comment: createdDesc, rating: createdRating}
                const response = await axios.post(`http://localhost:8090/api/reviews?gameId=${gameId.value}&reviewerId=${reviewerId.value}`, reviewData);
                await fetchReviews();
            }

            catch(error) {
                console.error('Error submitting review:', error);
            }

        }

        const openCreateReview = () => {
            showPopup.value = true;
        }

        const closeCreateReview = () => {
            showPopup.value = false;
        }

        onMounted(async () => {
        await fetchGameDetails();
        await fetchReviews();
        })

        return {
            gameTitle,
            minPlayers,
            maxPlayers,
            pictureURL,
            reviews,
            description,
            backgroundImg,
            placeholderImg,
            plantImg,
            createdDesc,
            createdRating,
            showPopup,
            openCreateReview,
            closeCreateReview,
            submitReview,
        }
    },

}


    
</script>

<style>


.title-container {
    position: fixed;
    top: 0em;
    left: 0em;
    width: 200em;
    height: 5em;
    background-size: cover;
    text-align: left;
    padding-bottom: 1em;
}

#createReview {
    position:fixed;
    top: 38em;
    left: 85em;
}

#plantImg {
    z-index:10;
    position:absolute;
    top: 5.8em;
    left: 14em;
}

h1 {
    position: absolute;
    bottom: 0em; 
    left: 3em;
    margin: 0em;
    font-family: 'Kantumruy Pro', sans-serif;
    color: white;

}

#r1 {
    position: fixed;
    width: 25em;
    left: 5%;
    top: 95%;
    outline: 0.1em solid white; 
    padding: 1%; 
    border-radius: 0.6em;

}

#updateReview {
    position: fixed;
    top: 38em;
    left: 75em;


}

#deleteReview {
    position: fixed;
    top: 38em;
    left: 65em;
}


#imageGame{
    position:fixed;
    top: 12em;
    left: 5em;
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
    position: fixed;
    top: 6em;
    left: 85em;
    

}

h2 {
    position:fixed;
    top: 87%;
    font-family: 'Kantumruy Pro', sans-serif;
    font-weight: bold;
    left:4em;
}

#descN {
    text-align: justify;
    color: #14252E;
    font-family: 'Inter', sans-serif;
    font-size: 106%;
    border-radius: 2em;
    width: 100%;
    height: 6em;
    max-width: none;
    outline: none;
}

#Submit {
    position: relative;
    top: 2em;
    left: -1em;
}

#Cancel {
    position: relative;
    top: 2em;
    left: 1em;

}

#playerNb {
    position:fixed;
    text-align: center;
    display: grid;
    padding: 0.7em;
    top: 6em;
    left: 5em;
    width: 8.5em;
    height: 3em;
    font-size: 106%;
    border-radius: 3em;
    border: none;
    font-family: 'Inter', sans-serif;
    outline: none; 
    box-shadow: none;
    background-color: #14252E;
    color: white
}
.star-checkbox input {
    opacity:0;
    
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
    text-align:justify;
    position:fixed;
    top: 22.5%;
    left: 32em;
    max-width: 55%;
    color: #FFFFFF;
    font-family: 'Inter', sans-serif;
    font-size: 106%;

}

button {
    width: 8.5em;
    height: 3em;
    font-size: 106%;
    border-radius: 3em;
    border: none;
    font-family: 'Inter', sans-serif;
    outline: none; 
    box-shadow: none;
    background-color: #14252E;
    color: white
}

button:hover {
    background-color: darkseagreen;

}

button:focus {
    outline: none; 
    box-shadow: none; 
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


</style>