<template>

<main>
        <button id = "create" @click="openCreateReview">Create Borrow Request</button>
        <h1 id = title>Select a Game Owner</h1>
    <div>
      <section id = s1>
        <table id = t>
            <thead>
            <tr>
                <th>Select</th>
                <th>Username</th>
            </tr>
            </thead>
            <tbody>
                        <tr v-for="(entry, index) in gameOwners":key = "index">
                            <td><input type="radio" :value="entry.id" v-model="selectedUserId" name = "selectedUser"></td>
                            <td>{{ entry.userName }}</td>
                        </tr>
                    </tbody>
                </table>
                </section>
</div>

<div v-if = "showPopup" class="modal-overlay">
            <div class = "modal-content">
                <form>
                    <div id = "start">
                    <p><label for="date1" id = date21>Select a start date for the lending:</label></p>
                    <input type="date" v-model="selectedDate1" id="date1" />
                    </div>

                    <div id = "end">
                    <p><label for="date2" id = date22>Select an end date for the lending:</label></p>
                    <p><input type="date" v-model="selectedDate2" id="date2" /></p>
                </div>
                    <button id = "Submit" @click.prevent = "createBorrowRequest">Create Request</button>
                    <button id = "Cancel" @click.prevent = "closeCreateReview">Cancel</button>
                </form> <br>
            </div>
            
        </div>
</main>
  </template>

  <style>

#date21, #date22 {
    font-family: "Mansalva", sans-serif;
    font-size: 150%;



}

#date1, #date2 {
    font-size: 130%;
    font-family: "Mansalva", sans-serif;


}

#start {
    margin-top: 4em;
    margin-bottom: 2em;
}

#end {
    margin-bottom: 4em;
    margin-top: 2em;
}

#Submit {
    margin-right: 2em;

}



button {
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

#create {
    margin-top: 5em;
    margin-left: 90em;

}

h1 {
    margin-top:-4em;
    margin-left: 2em;
    font-family: "Mansalva", sans-serif;

}

#t {
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
    margin-top: 2rem;
    padding: 2rem;
    font-size: 1.1rem;

}



#s1 table{

    width: 100%;
    border-collapse: collapse;
}

#s1 th, #s1 td {
    padding: 10px;
    border: 1px solid rgba(134, 73, 37, 0.9);
    text-align: center;
}

#s1 th {
    font-weight: bold;
    background-color: rgba(134, 73, 37, 0.9);
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-content {
    background-color: #cf8d4e;
    width: 50%; /* Adjust width */
    max-width: 500px;
    padding: 2em;
    border-radius: 10px;
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.3);
    text-align: center;
}
</style>
<script setup>
import backgroundImg from '@/assets/jessica-woulfe-harvest-witch-interior-jw2.jpg';
import placeholderImg from '@/assets/istockphoto-1147544807-612x612.jpg';
import plantImg from '@/assets/pixelated-green-greenery-sprout-leaf-260nw-2466520193-removebg-preview1.png';
import { reviewService } from '@/services/reviewService';
import { requestService } from '@/services/requestService';
import { gameCopyService } from '@/services/gameCopyService';
import {ref, onMounted, computed} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';
import { useGameStore } from '@/stores/gameStore';

    const authStore = useAuthStore();
    const gameStore = useGameStore();
    const router = useRouter();
    const route = useRoute();
    const gameId = route.params.gameId;
    console.log(gameId);
    const error = ref(null);
    const showPopup = ref(false);
    const gameOwners = ref([]);
    const formattedDate1 = ref('');
    const formattedDate2 = ref('');
    const selectedUserId = ref('');
    const currentUserId = computed(() => authStore.user.id);
    const selectedDate1 = ref('');
    const selectedDate2 = ref('');

    
    const openCreateReview = () => {
        console.log(selectedUserId.value)
        if ((selectedUserId.value.length == 0)) {
                console.log('No selection made')
                alert('Please select a value from the menu.');

            }
        else {
        showPopup.value = true;}
    }

    const closeCreateReview = () => {
        showPopup.value = false;
    }

    const fetchGameOwners = async() => {
        try {
            let fetchedGameCopies = await gameCopyService.findGameCopiesFromGame(gameId);
            gameOwners.value = fetchedGameCopies;
        
    } catch (err) {
        error.value = 'Failed to load game owners. Please try again later.';
        console.error('Error loading game owners:', err);
    }
}
onMounted(() => {
        fetchGameOwners();
    })



    const createBorrowRequest = async() => {
        try {

            if ((!selectedUserId)) {
                console.log('No selection made')
                alert('Please select a value from the menu.');

            }
           else {
            let startDateObj = new Date(selectedDate1.value);
            let endDateObj = new Date(selectedDate2.value);

            requestService.createRequest(selectedUserId.value, currentUserId.value, {
                startDate: startDateObj,
                endDate: endDateObj,
            })

        } }catch(err) {
            error.value = 'Failed to load game. Please try again later.'
            console.error('Error loading game:', err)

        }



    }



</script>