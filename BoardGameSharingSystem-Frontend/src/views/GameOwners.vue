<template>
    <main>
        <button id = "create">Create Borrow Request</button>
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
                        <tr>
                            <td>JohnDoe</td>
                            <td>4/5</td>
                           
                        </tr>
                        <tr>
                            <td>JaneSmith</td>
                            <td>5/5</td>
                            
                        </tr>
                        <tr>
                            <td>SamPlayer</td>
                            <td>3/5</td>
                            
                        </tr>
                    </tbody>
                </table>
                </section>
</div>

<div v-if = "showPopup" class="modal-overlay">
            <div class = "modal-content">
                <form>
                    <<div>
                        </div>

                        <div>
                            <b-dropdown v-model="selectedValueYr1" id="dropdown-11" text="Dropdown Button" class="m-md-2">
                                <b-dropdown-item value = "2025">2025</b-dropdown-item>
                                <b-dropdown-item value = "2026">2026</b-dropdown-item>
                                <b-dropdown-item value = "2027">2027</b-dropdown-item>
                                <b-dropdown-divider></b-dropdown-divider>
                            </b-dropdown>
                            </div>

                            <div>
                                <b-dropdown v-model="selectedValueM1" id="dropdown-12" text="Dropdown Button" class="m-md-2">
                                    <b-dropdown-item value = "01">01</b-dropdown-item>
                                    <b-dropdown-item value = "02">02</b-dropdown-item>
                                    <b-dropdown-item value = "03">03</b-dropdown-item>
                                    <b-dropdown-item value = "04">04</b-dropdown-item>
                                    <b-dropdown-item value = "05">05</b-dropdown-item>
                                    <b-dropdown-item value = "06">06</b-dropdown-item>
                                    <b-dropdown-item value = "07">07</b-dropdown-item>
                                    <b-dropdown-item value = "08">08</b-dropdown-item>
                                    <b-dropdown-item value = "09">09</b-dropdown-item>
                                    <b-dropdown-item value = "10">10</b-dropdown-item>
                                    <b-dropdown-item value = "11">11</b-dropdown-item>
                                    <b-dropdown-item value = "12">12</b-dropdown-item>
                                    <b-dropdown-divider></b-dropdown-divider>
                                </b-dropdown>
                            </div> 

                            <div>
                                <b-dropdown v-model="selectedValueD1" id="dropdown-13" text="Dropdown Button" class="m-md-2">
                                    <b-dropdown-item value = "01">01</b-dropdown-item>
                                    <b-dropdown-item value = "02">02</b-dropdown-item>
                                    <b-dropdown-item value = "03">03</b-dropdown-item>
                                    <b-dropdown-item value = "04">04</b-dropdown-item>
                                    <b-dropdown-item value = "05">05</b-dropdown-item>
                                    <b-dropdown-item value = "06">06</b-dropdown-item>
                                    <b-dropdown-item value = "07">07</b-dropdown-item>
                                    <b-dropdown-item value = "08">08</b-dropdown-item>
                                    <b-dropdown-item value = "09">09</b-dropdown-item>
                                    <b-dropdown-item value = "10">10</b-dropdown-item>
                                    <b-dropdown-item value = "11">11</b-dropdown-item>
                                    <b-dropdown-item value = "12">12</b-dropdown-item>
                                    <b-dropdown-item value = "13">13</b-dropdown-item>
                                    <b-dropdown-item value = "14">14</b-dropdown-item>
                                    <b-dropdown-item value = "15">15</b-dropdown-item>
                                    <b-dropdown-item value = "16">16</b-dropdown-item>
                                    <b-dropdown-item value = "17">17</b-dropdown-item>
                                    <b-dropdown-item value = "18">18</b-dropdown-item>
                                    <b-dropdown-item value = "19">19</b-dropdown-item>
                                    <b-dropdown-item value = "20">20</b-dropdown-item>
                                    <b-dropdown-item value = "21">21</b-dropdown-item>
                                    <b-dropdown-item value = "22">22</b-dropdown-item>
                                    <b-dropdown-item value = "23">23</b-dropdown-item>
                                    <b-dropdown-item value = "24">24</b-dropdown-item>
                                    <b-dropdown-item value = "25">25</b-dropdown-item>
                                    <b-dropdown-item value = "26">26</b-dropdown-item>
                                    <b-dropdown-item value = "27">27</b-dropdown-item>
                                    <b-dropdown-item value = "28">28</b-dropdown-item>
                                    <b-dropdown-item value = "29">29</b-dropdown-item>
                                    <b-dropdown-item value = "30">30</b-dropdown-item>
                                    <b-dropdown-item value = "31">31</b-dropdown-item>


                                    <b-dropdown-divider></b-dropdown-divider>
                                </b-dropdown>
                                </div> 

                                <div>
                            <b-dropdown v-model="selectedValueYr2" id="dropdown-21" text="Dropdown Button" class="m-md-2">
                                <b-dropdown-item value = "2025">2025</b-dropdown-item>
                                <b-dropdown-item value = "2026">2026</b-dropdown-item>
                                <b-dropdown-item value = "2027">2027</b-dropdown-item>
                                <b-dropdown-divider></b-dropdown-divider>
                            </b-dropdown>
                            </div>

                            <div>
                                <b-dropdown v-model="selectedValueM2" id="dropdown22" text="Dropdown Button" class="m-md-2">
                                    <b-dropdown-item value = "01">01</b-dropdown-item>
                                    <b-dropdown-item value = "02">02</b-dropdown-item>
                                    <b-dropdown-item value = "03">03</b-dropdown-item>
                                    <b-dropdown-item value = "04">04</b-dropdown-item>
                                    <b-dropdown-item value = "05">05</b-dropdown-item>
                                    <b-dropdown-item value = "06">06</b-dropdown-item>
                                    <b-dropdown-item value = "07">07</b-dropdown-item>
                                    <b-dropdown-item value = "08">08</b-dropdown-item>
                                    <b-dropdown-item value = "09">09</b-dropdown-item>
                                    <b-dropdown-item value = "10">10</b-dropdown-item>
                                    <b-dropdown-item value = "11">11</b-dropdown-item>
                                    <b-dropdown-item value = "12">12</b-dropdown-item>
                                    <b-dropdown-divider></b-dropdown-divider>
                                </b-dropdown>
                            </div> 

                            <div>
                                <b-dropdown v-model="selectedValueD2" id="dropdown-23" text="Dropdown Button" class="m-md-2">
                                    <b-dropdown-item value = "01">01</b-dropdown-item>
                                    <b-dropdown-item value = "02">02</b-dropdown-item>
                                    <b-dropdown-item value = "03">03</b-dropdown-item>
                                    <b-dropdown-item value = "04">04</b-dropdown-item>
                                    <b-dropdown-item value = "05">05</b-dropdown-item>
                                    <b-dropdown-item value = "06">06</b-dropdown-item>
                                    <b-dropdown-item value = "07">07</b-dropdown-item>
                                    <b-dropdown-item value = "08">08</b-dropdown-item>
                                    <b-dropdown-item value = "09">09</b-dropdown-item>
                                    <b-dropdown-item value = "10">10</b-dropdown-item>
                                    <b-dropdown-item value = "11">11</b-dropdown-item>
                                    <b-dropdown-item value = "12">12</b-dropdown-item>
                                    <b-dropdown-item value = "13">13</b-dropdown-item>
                                    <b-dropdown-item value = "14">14</b-dropdown-item>
                                    <b-dropdown-item value = "15">15</b-dropdown-item>
                                    <b-dropdown-item value = "16">16</b-dropdown-item>
                                    <b-dropdown-item value = "17">17</b-dropdown-item>
                                    <b-dropdown-item value = "18">18</b-dropdown-item>
                                    <b-dropdown-item value = "19">19</b-dropdown-item>
                                    <b-dropdown-item value = "20">20</b-dropdown-item>
                                    <b-dropdown-item value = "21">21</b-dropdown-item>
                                    <b-dropdown-item value = "22">22</b-dropdown-item>
                                    <b-dropdown-item value = "23">23</b-dropdown-item>
                                    <b-dropdown-item value = "24">24</b-dropdown-item>
                                    <b-dropdown-item value = "25">25</b-dropdown-item>
                                    <b-dropdown-item value = "26">26</b-dropdown-item>
                                    <b-dropdown-item value = "27">27</b-dropdown-item>
                                    <b-dropdown-item value = "28">28</b-dropdown-item>
                                    <b-dropdown-item value = "29">29</b-dropdown-item>
                                    <b-dropdown-item value = "30">30</b-dropdown-item>
                                    <b-dropdown-item value = "31">31</b-dropdown-item>


                                    <b-dropdown-divider></b-dropdown-divider>
                                </b-dropdown>
                                </div> 

                    <button id = "Submit" @click.prevent = "submitReview">Create Request</button>
                    <button id = "Cancel" @click.prevent = "closeCreateReview">Cancel</button>
                </form> <br>
            </div>
            
        </div>
</main>
  </template>

  <style>

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
    margin-top:-2em;
    margin-left: 5em;
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
    margin-top: 2rem;  /* Adjusted to push the reviews down, instead of using top */
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

</style>

<script setup>
import backgroundImg from '@/assets/jessica-woulfe-harvest-witch-interior-jw2.jpg';
import placeholderImg from '@/assets/istockphoto-1147544807-612x612.jpg';
import plantImg from '@/assets/pixelated-green-greenery-sprout-leaf-260nw-2466520193-removebg-preview1.png';
import { reviewService } from '@/services/reviewService';
import {ref, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';

    const authStore = useAuthStore();
    const router = useRoute();
    const gameId = ref(router.params.gameId);
    const error = ref(null);
    const showPopup = ref(false);
    let gameOwners = ref([]);
    const selectedYR1 = ref('');
    const selectedD1 = ref('');
    const selectedM1 = ref('');
    const selectedYR2 = ref('');
    const selectedD2 = ref('');
    const selectedM2 = ref('');
    const formattedDate1 = ref('');
    const formattedDate2 = red('');
    const currentUserId = computed(() => authStore.user.id);
    
    
    const fetchGameOwners = async() => {
        try {
            let fetchedGameCopies = await gameCopyService.findGameCopiesFromGame(gameId);
            gameOwners.value = fetchedGameCopies.data.map(gameCopy => {
            return {
                userId: gameCopy.userId,
                userName: gameCopy.userName,
                gameCopyId: gameCopy.id
            };


        });
    } catch (err) {
        error.value = 'Failed to load game owners. Please try again later.';
        console.error('Error loading game owners:', err);
    }
}

    const createBorrowRequest = async() => {
        try {

            if ((!selectedValue1.value || !selectedValue2.value || !selectedValue3.value)) {
                console.log('No selection made')
                alert('Please select a value from the dropdown.');

            }
           else {
            const dateStr1 = `${selectedYR1.value}-${selectedM1.value}-${selectedD1.value}`;
            const dateStr2 = `${selectedYR2.value}-${selectedM2.value}-${selectedD2.value}`;

            const date1 = new Date(dateStr1);
            const date2 = new Date(dateStr2);

            formattedDate1.value = date1.toISOString().split('T')[0];
            formattedDate2.value = date2.toISOString().split('T')[0];

            requestService.createBorrowRequest()

        } }catch(err) {
            error.value = 'Failed to load game. Please try again later.'
            console.error('Error loading game:', err)

        }



    }

    const openCreateReview = () => {
        showPopup.value = true;
    }

    const closeCreateReview = () => {
        showPopup.value = false;
    }



</script>

//get the dateObj to make rq
//get the 

