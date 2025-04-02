<template>
    <main>
        <div class="title-container" :style="{ backgroundImage: `url(${backgroundImage})` }">
            
            <h1>
                <b>Game Name</b>
            </h1>
        
        </div>
        <div class="stuff">
            <section id = "playerNb"> Player Nb</section>
            <button id="borrowButton"> Borrow Game</button>
           
            <img :src="placeholderImg" id="imageGame"/>
            <img :src = "plantImg" id="plantImg"/>
            
            <p id = "desc">[Description of the game]Excepteur efficient emerging, minim veniam anim aute carefully curated Ginza conversation exquisite perfect nostrud nisi intricate Content. Qui  international first-class nulla ut. Punctual adipisicing, essential lovely queen tempor eiusmod irure. Exclusive izakaya charming Scandinavian impeccable aute quality of life soft power pariatur Melbourne occaecat discerning. Qui wardrobe aliquip, et Porter destination Toto remarkable officia Helsinki excepteur Basset hound. Zürich sleepy perfect consectetur.</p>

        </div>
        <div>
            <h2><b>Latest Reviews</b></h2>
            <button id = "createReview">Create Review</button>
            <button id = "updateReview"> Update Review</button>
            <button id = "deleteReview"> Delete Review</button>
            <section id="reviews">
                <div id = "r1">
                    <p>Username</p>
                    <p>Description</p>
                </div>

            </section>

        </div>
    </main>
</template>

<script>
import backgroundImg from '@/assets/jessica-woulfe-harvest-witch-interior-jw2.jpg';
import placeholderImg from '@/assets/istockphoto-1147544807-612x612.jpg';

import plantImg from '@/assets/pixelated-green-greenery-sprout-leaf-260nw-2466520193-removebg-preview1(1).png';


export default {
    data() {
        return {
            backgroundImage: backgroundImg,
            placeholderImg: placeholderImg,
            plantImg : plantImg
        };
    }
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

</style>