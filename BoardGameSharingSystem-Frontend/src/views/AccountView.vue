<template>
  <div class="account-settings">
    <div class="container">
      <!-- Header -->
      <div class="header">
        <DiceIcon class="header__icon" />
        <h1 class="header__title">Account Settings</h1>
      </div>
      
      <div class="layout">
        <!-- Sidebar Navigation -->
        <div class="sidebar">
          <div class="sidebar__wrapper">
            <div class="sidebar__texture"></div>
            <div class="sidebar__header">
              <h2 class="sidebar__title">
                <User class="sidebar__title-icon" />
                Settings Menu
              </h2>
            </div>
            <nav class="sidebar__nav">
              <button
                v-for="item in navItems"
                :key="item.id"
                @click="activeSection = item.id"
                :class="[
                  'sidebar__nav-item',
                  activeSection === item.id ? 'sidebar__nav-item--active' : '',
                  item.id === 'delete' ? 'sidebar__nav-item--danger' : ''
                ]"
              >
                <component :is="item.icon" class="sidebar__nav-icon" v-if="item.icon !== 'Warning'" />
                <div v-else class="sidebar__nav-icon sidebar__nav-icon--warning">⚠️</div>
                <span>{{ item.label }}</span>
              </button>
            </nav>
          </div>
        </div>
        
        <!-- Main Content -->
        <div class="content">
          <!-- Profile Section -->
          <div v-if="activeSection === 'profile'" class="card">
            <div class="card__texture"></div>
            <div class="card__header">
              <h2 class="card__title">Profile Information</h2>
              <p class="card__subtitle">Update your personal information</p>
            </div>
            <div class="card__content">
              <div class="form-group">
                <label for="name" class="form-label">Name</label>
                <input 
                  id="name" 
                  v-model="profile.username"
                  :placeholder="usernamePlaceholder"
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label for="email" class="form-label">Email</label>
                <input 
                  id="email" 
                  type="email" 
                  v-model="profile.email"
                  :placeholder="emailPlaceholder"
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input 
                  id="password" 
                  type="password" 
                  v-model="profile.password" 
                  placeholder="••••••••" 
                  class="form-input"
                />
              </div>
            </div>
            <div class="card__footer">
              <button class="btn btn--primary" @click="updateUserProfile">
                Save Changes
              </button>
            </div>
          </div>
          
          <!-- Account Type Section -->
          <div v-if="activeSection === 'account-type'" class="card">
            <div class="card__texture"></div>
            <div class="card__header">
              <h2 class="card__title">Account Type</h2>
              <p class="card__subtitle">Choose your account type</p>
            </div>
            <div class="card__content">
              <div class="account-toggle">
                <div class="account-toggle__info">
                  <div class="account-toggle__icon-wrapper">
                    <User class="account-toggle__icon" />
                  </div>
                  <div>
                    <p class="account-toggle__title">{{ isGameOwner ? "Game Owner" : "Player" }}</p>
                    <p class="account-toggle__description">
                      {{ isGameOwner 
                        ? "You can list games for others to borrow" 
                        : "You can borrow games from others" }}
                    </p>
                  </div>
                </div>
                <label class="toggle">
                  <input 
                    type="checkbox" 
                    :checked="isGameOwner" 
                    @change="toggleAccountType"
                    class="toggle__input"
                  />
                  <span class="toggle__slider"></span>
                </label>
              </div>
            </div>
          </div>
          
          <!-- Games Section (Only for Game Owners) -->
          <div v-if="activeSection === 'games' && isGameOwner" class="card">
            <div class="card__texture"></div>
            <div class="card__header" style="display: flex; justify-content: space-between; align-items: center;">
              <div>
                <h2 class="card__title">My Games</h2>
                <p class="card__subtitle">Manage your board game collection</p>
              </div>
              <!-- <button class="btn btn--add btn--with-icon">
                <Package class="btn__icon" /> Add New Game
              </button> -->
                <button class="btn btn--primary btn--with-icon" @click="showAddGameModal = true">
                  <Package class="btn__icon" /> Add New Game
                </button>
            </div>
            <div class="card__content">
              <div class="games-list">
                <div v-for="(gameCopy, index) in ownedGames" :key="index" class="game-item">
                  <div class="game-item__wrapper">
                    <div class="game-item__info">
                      <div class="game-item__icon-wrapper">
                        <Dice class="game-item__icon" />
                      </div>
                      <div>
                        <p class="game-item__title">{{ gameCopy.game.title }}</p>
                        <p class="game-item__details">
                          <span 
                            :class="[
                              'game-item__status',
                              'game-item__players'
                            ]"
                          >
                          {{ gameCopy.game.minNumPlayers === gameCopy.game.maxNumPlayers ? gameCopy.game.minNumPlayers + ' players': gameCopy.game.minNumPlayers + '-' + gameCopy.game.maxNumPlayers + ' players' }}
                          </span>
                          <template v-if="gameCopy.game.description">• {{ gameCopy.game.description }}</template>
                        </p>
                      </div>
                    </div>
                    <button class="btn btn--danger"  @click="removeGameCopy(gameCopy.id, index)">
                      Delete
                    </button>
                  </div>
                  <div v-if="index < ownedGames.length - 1" class="separator"></div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Events Section -->
          <div v-if="activeSection === 'events'" class="card">
            <div class="card__texture"></div>
            <div class="card__header">
              <h2 class="card__title">Event History</h2>
              <p class="card__subtitle">Events you've participated in</p>
            </div>
            <div class="card__content">
              <div class="events-list">
                <div v-for="(event, index) in events" :key="index" class="event-item">
                  <div class="event-item__wrapper">
                    <div class="event-item__info">
                      <div class="event-item__icon-wrapper">
                        <Calendar class="event-item__icon" />
                      </div>
                      <div>
                        <p class="event-item__title">{{ event.Name }}</p>
                        <div class="event-item__details">
                          <Clock class="event-item__details-icon" />
                          {{ event.FormattedDate }}
                          • {{ event.Location }} • {{ event.ContactEmail }}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div v-if="index < events.length - 1" class="separator"></div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Delete Account Section -->
          <div v-if="activeSection === 'delete'" class="card">
            <div class="card__texture"></div>
            <div class="card__header">
              <h2 class="card__title">Delete Account</h2>
              <p class="card__subtitle">Permanently delete your account and all associated data</p>
            </div>
            <div class="card__content">
              <div class="warning">
                <p class="warning__text">
                  Warning: This action cannot be undone. All your data, including game listings, borrowing history, and event participation will be permanently removed.
                </p>
              </div>
              <button 
                @click="showDeleteConfirm = true" 
                class="btn btn--danger"
              >
                Delete Account
              </button>
              
              <!-- Delete Confirmation Dialog -->
              <div v-if="showDeleteConfirm" class="modal">
                <div class="modal__overlay" @click="showDeleteConfirm = false"></div>
                <div class="modal__content">
                  <h3 class="modal__title">Are you absolutely sure?</h3>
                  <p class="modal__text">
                    This action cannot be undone. This will permanently delete your
                    account and remove your data from our servers.
                  </p>
                  <div class="modal__actions">
                    <button 
                      @click="showDeleteConfirm = false" 
                      class="btn btn--outline"
                    >
                      Cancel
                    </button>
                    <button class="btn btn--danger" @click="deleteAccount">
                      Delete Account
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Add Game Modal -->
  <div v-if="showAddGameModal" class="modal">
    <div class="modal__overlay" @click="closeAddGameModal"></div>
    <div class="modal__content modal__content--large">
      <div class="modal__header">
        <h3 class="modal__title">Add Game to Your Collection</h3>
        <button class="modal__close" @click="closeAddGameModal">×</button>
      </div>
      
      <div class="modal__body">
        <div class="tabs">
          <button 
            :class="['tabs__tab', { 'tabs__tab--active': !isAddingNewGame }]" 
            @click="isAddingNewGame = false"
          >
            Select Existing Game
          </button>
          <button 
            :class="['tabs__tab', { 'tabs__tab--active': isAddingNewGame }]" 
            @click="isAddingNewGame = true"
          >
            Add New Game
          </button>
        </div>
        
        <!-- Select Existing Game -->
        <div v-if="!isAddingNewGame" class="form-section">
          <div class="form-group">
            <label for="existingGame" class="form-label">Select a Game</label>
            <select id="existingGame" v-model="selectedExistingGame" class="form-select">
              <option value="" disabled>-- Select a game --</option>
              <option v-for="game in availableGames" :key="game.id" :value="game.id">
                {{ game.title }}
              </option>
            </select>
          </div>
          
          <div v-if="selectedExistingGame" class="game-preview">
            <div class="game-preview__image">
              <img 
                :src="getSelectedGameDetails().pictureURL || defaultGameImage" 
                alt="Game cover" 
                class="game-preview__img"
                @error="handleImageError"
              />
            </div>
            <div class="game-preview__details">
              <h4 class="game-preview__title">{{ getSelectedGameDetails().title }}</h4>
              <p class="game-preview__players">{{ getSelectedGameDetails().minNumPlayers }}-{{ getSelectedGameDetails().maxNumPlayers }} Players</p>
              <p class="game-preview__description">{{ getSelectedGameDetails().description }}</p>
            </div>
          </div>
          
          <div class="form-message">
            <p>Didn't find the game you were looking for? <button class="link-button" @click="isAddingNewGame = true">Add one!</button></p>
          </div>
        </div>
        
        <!-- Add New Game -->
        <div v-else class="form-section">
          <div class="form-group">
            <label for="newGameTitle" class="form-label">Game Title</label>
            <input id="newGameTitle" v-model="newGame.title" class="form-input" placeholder="Enter game title" />
          </div>
          
          <div class="form-row">
            <div class="form-group form-group--half">
              <label for="minPlayers" class="form-label">Min Players</label>
              <input 
                id="minNumPlayers" 
                type="number" 
                v-model="newGame.minNumPlayers" 
                min="1" 
                max="99" 
                class="form-input" 
              />
            </div>
            <div class="form-group form-group--half">
              <label for="maxNumPlayers" class="form-label">Max Players</label>
              <input 
                id="maxNumPlayers" 
                type="number" 
                v-model="newGame.maxNumPlayers" 
                min="1" 
                max="99" 
                class="form-input" 
              />
            </div>
          </div>
          
          <div class="form-group">
            <label for="description" class="form-label">Description</label>
            <textarea 
              id="description" 
              v-model="newGame.description" 
              class="form-textarea" 
              rows="3" 
              placeholder="Enter game description"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label for="imageUrl" class="form-label">Image URL</label>
            <input 
              id="imageUrl" 
              v-model="newGame.pictureURL" 
              class="form-input" 
              placeholder="https://example.com/game-image.jpg" 
            />
          </div>
          
          <div class="image-preview">
            <div class="image-preview__container">
              <img 
                :src="newGame.pictureURL || defaultGameImage" 
                alt="Game cover preview" 
                class="image-preview__img"
                @error="handleImageError"
              />
            </div>
            <p v-if="!newGame.pictureURL" class="image-preview__placeholder">Enter an image URL to see a preview</p>
          </div>
        </div>
      </div>
      
      <div class="modal__footer">
        <button class="btn btn--outline" @click="closeAddGameModal">Cancel</button>
        <button 
          class="btn btn--primary" 
          @click="addGameToCollection"
          :disabled="!canAddGame"
        >
          Add to Collection
        </button>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore'
import { userService } from '@/services/userService';
import { eventService } from '@/services/eventService'
import { registrationService } from '@/services/registrationService'
import { gameCopyService } from '@/services/gameCopyService'
import { gameOwningService } from '@/services/gameOwningService'
import { gameService } from '@/services/gameService';
import { eventGameService } from '@/services/eventGameService';
import {Toast, useToast } from 'primevue'; 

import { 
  Calendar, 
  Clock, 
  Dice1Icon as Dice, 
  Dice1Icon as DiceIcon,
  Gamepad2, 
  Package, 
  Settings, 
  User, 
  Users ,
} from 'lucide-vue-next'

export default {
  name: 'AccountSettings',
  components: {
    Calendar,
    Clock,
    Dice,
    DiceIcon,
    Gamepad2,
    Package,
    Settings,
    User,
    Users,
  },

  setup() {
    const router = useRouter();
    const authStore = useAuthStore();
    const currentUserId = computed(() => authStore.user.id);
    const currentUserName = computed(() => authStore.user?.username ?? '');
    const currentUserEmail = computed(() => authStore.user?.userEmail ?? '');
    const isGameOwner = ref(false);
    const activeSection = ref('profile');
    const showDeleteConfirm = ref(false);
    const showAddGameModal = ref(false);
    const isAddingNewGame = ref(false);
    const selectedExistingGame = ref('');
    const checked = ref(false);
    checked.value = isGameOwner.value;
    
    const error = ref(null);
    const toast = useToast();
    const events = ref([]); //for event history
    const availableGames = ref([]); //for all games
    const ownedGames = ref([]); //for user game copies
    const defaultGameImage = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2YzZTllNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMjAiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGRvbWluYW50LWJhc2VsaW5lPSJtaWRkbGUiIGZpbGw9IiM4ZDZlNjMiPkJvYXJkIEdhbWUgSW1hZ2U8L3RleHQ+PC9zdmc+'

    // ----------------------------- PROFILE TAB -----------------------------------------
      // Directly bind the profile fields to authStore properties
      const profile = ref ({
        username: authStore.user.username  || '',
        email: authStore.user.userEmail  || '',
        password: ''  // Do not store passwords in plain text!
      });
      
      // Computed placeholders to show the current user's data
      const usernamePlaceholder = computed(() => authStore.user.username || 'Enter your name');
      const emailPlaceholder = computed(() => authStore.user.userEmail || 'Enter your email');

      // Function for updating profile fields
      const updateUserProfile = async () => {
        console.log('authStore.user.username:', authStore.user.username);
        console.log('authStore.user.userEmail:', authStore.user.userEmail);
        console.log('profile.value.username:',profile.value.username);
        try {
          await userService.updateUserAccount(currentUserId.value, {
            username: profile.value.username,
            email: profile.value.email,
            password: profile.value.password
          });

          // Reset password field after successful update
          profile.value.password = ''; 

          authStore.$patch((state) => {
            state.user.username = profile.value.username;
            state.user.userEmail = profile.value.email;
        });

          // Update placeholders to reflect changes
          profile.value.username = authStore.user.username ?? '';
          profile.value.email = authStore.user.userEmail ?? '';
        } catch (err) {
          console.error("Error updating profile:", err);
        }
      };


    // ----------------------------- ACCOUNT TYPE TAB -----------------------------------------
    // Function to toggle account type
    const toggleAccountType = async () => {
      try {
        // Refresh isGameOwner status from backend
        const response = await gameOwningService.findGameOwner(currentUserId.value);
        isGameOwner.value = response.isGameOwner;

        // Toggling to PLAYER (user is already a game owner)
        if (isGameOwner.value) {
          await userService.toggleUserToPlayer(authStore.user.id);
          isGameOwner.value = false;
        } 
        
        // Toggling to GAME OWNER (user is currently a player)
        else {
          try {
            await userService.toggleUserToGameOwner(authStore.user.id);
            isGameOwner.value = true;
          } catch (err) {
            const errors = err.response?.data?.errors || [];

            if (Array.isArray(errors) && errors.some(e => e.includes("gameOwner has no associated games"))) {
              showAddGameModal.value = true; // Trigger modal

              // Wait for user action to add game before continuing
              await new Promise(resolve => {
                // You should resolve the promise after the modal has closed and the game is added
                const modalClosed = () => {
                  closeAddGameModal();
                  resolve();  // Resolve when modal is closed
                };
                // Listen for modal close event
                watch(showAddGameModal, (newVal) => {
                  if (!newVal) {
                    modalClosed();
                  }
                });
              });

              await userService.toggleUserToGameOwner(authStore.user.id);
              isGameOwner.value = true;

            } else {
              throw err; // Unknown error, bubble it up
            }
          }
        }

      } catch (error) {
        console.error("Error toggling account type:", error);
      }
    };


    // ----------------------------- MY GAMES TAB -----------------------------------------    
    //Handles displaying my games (game copies related to the current user)
    const fetchUserGameCopies = async () => {
      try{
        error.value = null

        const userGameCopies = await gameCopyService.findGameCopiesForGameOwner(currentUserId.value)
        const games = await Promise.all(
          userGameCopies.map(async (copy) => {
            try {
            const game = await gameService.findGamebyId(copy.gameId);
            return {
              ...copy,       
              game: game     
            };
            } catch (gameErr) {
              console.error(`Failed to fetch game with ID ${copy.gameId}`, gameErr);
              return null; 
            }
          })
        );

        ownedGames.value = games
      } catch (err){
        error.value = 'Failed to load your games. Please try again later.'
        console.error("Error loading user's game copies:", err)
        toast.add({ severity: 'error', summary: 'Could not load your games', detail: 'Please try again later', });
      }
    }

    watch(activeSection, (newSection) => {
      if (newSection === 'games') {
      fetchUserGameCopies()
      findAllGames()
    }})

    //Handles deleting your game copies (index and gameCopy r good...)
    const removeGameCopy = async (gameCopyId, index) => {
      try {    
        const response = await gameCopyService.removeGameCopyFromGameOwner(`/gameCopies/${gameCopyId}`) //gets an error here for some reason (since next line never reached..)
        console.log("Backend delete response:", response); // Log the response to verify
        if (response) {
          ownedGames.splice(index, 1); // remove it from the array to update UI
        }
        return response.data
      } catch (error) {
        console.error('Failed to delete game copy:', error)
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete your game. Try again.' });
      }
    }

    //Refetch game list from backend
    /*watch(ownedGames, async (newOwnedGames) => {
      try {
        // After the ownedGames list changes, let's fetch fresh data from the backend
        await fetchUserGameCopies();
      } catch (err) {
        console.error("Error during watch update:", err);
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to load your games after update.' });
      }
    }, { immediate: true });*/

    //ADD GAME FEATURE
    //Handles displaying all available games in "select a game" drop down
    const findAllGames = async () => {
      try{
        let fetchedGames = await gameService.findAllGames()
        availableGames.value = fetchedGames; 
        
      } catch (err){
        error.value = 'Failed to load available games. PLease try later'
        console.error('Error loading games', err)
        show()
      }
    }

    const show = () => {
      toast.add({ severity: 'error', summary: 'Could not load available games', detail: 'Please try again later', });
    }

    onMounted( () =>{
      findAllGames()
    })
  
    
    // Get details of the selected existing game
    const getSelectedGameDetails = () => {
      return availableGames.value.find(game => game.id === selectedExistingGame.value) || {}
    }

    const newGame = ref({
      title: '',
      minNumPlayers: 2,
      maxNumPlayers: 4,
      description: '',
      pictureURL: ''
    })

    // Computed property to check if the form is valid for adding a game
    const canAddGame = computed(() => {
      if (isAddingNewGame.value) {
        return (
          newGame.value.title.trim() !== '' && 
          newGame.value.minNumPlayers > 0 && 
          newGame.value.maxNumPlayers >= newGame.value.minNumPlayers
        )
      } else {
        return selectedExistingGame.value !== ''
      }
    })
  
  
    // Function to close the add game modal and reset form
    const closeAddGameModal = () => {
      showAddGameModal.value = false
      isAddingNewGame.value = false
      selectedExistingGame.value = ''
      newGame.value = {
        title: '',
        minNumPlayers: 2,
        maxNumPlayers: 4,
        pictureURL: '',
        description: ''
      }
    }

  
    // Handles adding game to user's collection
    const addGameToCollection = async () => {
      //Adding New Game Tab
      if (isAddingNewGame.value) {
        // Add the new game to owned games
        /*ownedGames.value.push({
          game: {
            title: newGame.value.title,
            minNumPlayers: newGame.value.minNumPlayers,
            maxNumPlayers: newGame.value.maxNumPlayers,
            description: newGame.value.description
          }
        })
        
        // Also add to available games for future selection
        availableGames.value.push({
          id: availableGames.value.length + 1,
          title: newGame.value.title,
          minNumPlayers: newGame.value.minNumPlayers,
          maxNumPlayers: newGame.value.maxNumPlayers,
          description: newGame.value.description,
          pictureURL: newGame.value.pictureURL || 'https://via.placeholder.com/200x200?text=No+Image'
        })*/

        try{
          console.log('title:',newGame.value.title)
          console.log('min:',newGame.value.minNumPlayers)
          console.log('max:',newGame.value.maxNumPlayers)
          console.log('desc:',newGame.value.description)
          console.log('pictureURL:',newGame.value.pictureURL)
          console.log('id:',newGame.value)
          const newGameResponse = await gameService.createGame(newGame.value);
          const gameOwnerId = currentUserId.value;
          const gameId = newGameResponse.id;
          await gameCopyService.addGameCopyToGameOwner(gameOwnerId, gameId);
          ownedGames.value.push({
            game: newGameResponse
          });

          closeAddGameModal();
        } catch (error) {
          console.error('Error adding new game:', error);
          toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to add new game. Please try again later.' });
        }
      } 
      
      //Adding Pre-existing Game (selected)
      else {
        // Add the selected existing game to owned games
        try {
          const gameId = selectedExistingGame.value;
          const gameOwnerId = currentUserId.value;

          await gameCopyService.addGameCopyToGameOwner(gameOwnerId, gameId);

          const selectedGame = getSelectedGameDetails();
          ownedGames.value.push({
            game: selectedGame});

          closeAddGameModal(); // Reset modal and form

        } catch (error) {
          // Handle backend errors
          const msg = error?.response?.data?.message || "Something went wrong while adding the game.";
          toast.add({
          severity: 'error',
          summary: 'Could not add game',
          detail: msg});
          console.error('Error adding existing game copy:', error);
        }

        // Close the modal
        closeAddGameModal()
      }
    }

    // ----------------------------- EVENTS TAB -----------------------------------------
    //Handles event history (WORK IN PROGRESS):
    //Left to test: 
    // 1. if it displays events user is registered for (not just the ones they created)
    const formatDate = (dateStr) => new Date(dateStr).toLocaleDateString(undefined, {
    weekday: 'short', year: 'numeric', month: 'short', day: 'numeric'
    })

    const fetchEvents = async () => {
      try {
        error.value = null

        const userRegistrations = await registrationService.findRegistrationByParticipant(currentUserId.value)
        const formattedEvents = await Promise.all(userRegistrations.map(async (registration) => {
        const event = await eventService.findEventById(registration.eventId) 

        //Initialize API failure response
        let gameTitle = "Game Not Found"
        let creatorName = "User Not Found"
        let eventFormattedDate = "UnknownDate - UnknownDate"

        // Fetch game title
        try {
          const games = await eventGameService.findEventGamesByEvent(event.id)
          if (games.length > 0) {
            gameTitle = games[0].gameTitle
          }
        } catch (e) {
          console.warn(`Error Fetching Games for Event: ${e}`)
        }

        // Fetch creator name
        try {
          const user = await userService.findUserAccount(event.creatorId)
          creatorName = user.name
        } catch (e) {
          console.warn(`Error Fetching Creator for Event: ${e}`)
        }

        // Format event date
        if (event.startDate === event.endDate) {
          eventFormattedDate = formatDate(event.startDate)
        } else {
          eventFormattedDate = `${formatDate(event.startDate)} - ${formatDate(event.endDate)}`
        }

        return {
          ...event,
          Name: `${gameTitle} by ${creatorName}`,
          FormattedDate: eventFormattedDate,
          Location: event.location,
          ContactEmail: event.contactEmail,
          endDate: event.endDate
        }}))
      
        //Filter only past events
        const currentDate = new Date()
        const pastEvents = formattedEvents.filter(event => new Date(event.endDate) < currentDate)
        events.value = pastEvents

      } catch (err) {
        error.value = 'Failed to load events. Please try again later.'
        console.error('Error loading events:', err)
      }
    }
    

    // ----------------------------- DELETE ACCOUNT TAB -----------------------------------------
    // Handles delete account:
    const deleteAccount = async () => {
      console.log('User ID:', currentUserId.value);

      try {
        console.log(currentUserId.value)
        await userService.deleteUserAccount(currentUserId.value);
        // logout
        authStore.logout();
        router.push('/');
        
      } catch (error) {
        console.error('Failed to delete account:', error);
      }
    };
    
    // Only include the games section in navigation if user is a game owner
    const navItems = computed(() => {
      const items = [
        {
          id: 'profile',
          label: 'Profile',
          icon: User
        },
        {
          id: 'account-type',
          label: 'Account Type',
          icon: Settings
        },
        {
          id: 'events',
          label: 'Events',
          icon: Calendar
        },
        {
          id: 'delete',
          label: 'Delete Account',
          icon: 'Warning'
        }
      ]
      
      // Only add the games navigation item if the user is a game owner
      if (isGameOwner.value) {
        items.splice(2, 0, {
          id: 'games',
          label: 'My Games',
          icon: Dice
        })
      }
      
      return items
    })
    


    // Watch for changes in isGameOwner and update activeSection if needed
    watch(isGameOwner, (newValue, oldValue) => {
      // If user switches from game owner to player and was on the games section,
      // redirect them to profile
      if (!newValue && oldValue && activeSection.value === 'games') {
        activeSection.value = 'profile'
      }
    })

    // Function to handle image loading errors
    const handleImageError = (event) => {
      event.target.src = defaultGameImage;
    }
    
    onMounted(() => {
    console.log("mounted")
    if (!authStore.user.isAuthenticated) {
      router.push('/')
    } 
    else{
      fetchEvents()
    }
    })

    return {
      authStore,
      currentUserName,
      currentUserEmail,
      usernamePlaceholder,
      emailPlaceholder,
      updateUserProfile,
      isGameOwner,
      activeSection,
      profile,
      ownedGames,
      events,
      navItems,
      showDeleteConfirm,
      toggleAccountType,
      deleteAccount,
      showAddGameModal,
      isAddingNewGame,
      selectedExistingGame,
      availableGames,
      newGame,
      canAddGame,
      getSelectedGameDetails,
      closeAddGameModal,
      addGameToCollection,
      handleImageError,
      defaultGameImage,
      removeGameCopy
    }
  }
}
</script>

<style scoped>
/* Base styles */
.account-settings {
  min-height: 100vh;
  background-image: url('/wood-texture.svg');
  background-repeat: repeat;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem 1rem;
}

/* Header */
.header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
  background-color: rgba(59, 24, 4, 1);
  color: white;
  padding: 1rem;
  border-radius: 0.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.header__icon {
  height: 1.5rem;
  width: 1.5rem;
}

.header__title {
  font-size: 1.5rem;
  font-weight: 700;
}

/* Layout */
.layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

@media (min-width: 768px) {
  .layout {
    grid-template-columns: 1fr 3fr;
  }
}

/* Sidebar */
.sidebar {
  position: sticky;
  top: 1.5rem;
}

.sidebar__wrapper {
  position: relative;
  border: none;
  background-color: #8d6e63;
  color: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 0.5rem;
  overflow: hidden;
}

.sidebar__texture {
  position: absolute;
  inset: 0;
  background-image: url('/wood-grain.svg');
  opacity: 0.2;
  pointer-events: none;
}

.sidebar__header {
  background-color: rgba(59, 24, 4, 1);
  border-bottom: 1px solid #3e2723;
  padding: 1rem;
}

.sidebar__title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
}

.sidebar__title-icon {
  height: 1.25rem;
  width: 1.25rem;
}

.sidebar__nav {
  display: flex;
  flex-direction: column;
  border-top: 1px solid rgba(62, 39, 35, 0.2);
}

.sidebar__nav-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  width: 100%;
  padding: 0.75rem 1rem;
  font-size: 0.875rem;
  text-align: left;
  transition: background-color 0.2s;
  color: rgba(255, 255, 255, 0.9);
  border: none;
  background: transparent;
  cursor: pointer;
  border-bottom: 1px solid rgba(62, 39, 35, 0.2);
}

.sidebar__nav-item:hover {
  background-color: rgba(93, 64, 55, 0.3);
}

.sidebar__nav-item--active {
  background-color: rgba(59, 24, 4, 1);
  color: white;
}

.sidebar__nav-item--danger {
  color: rgb(252, 165, 165);
}

.sidebar__nav-icon {
  height: 1rem;
  width: 1rem;
}

.sidebar__nav-icon--warning {
  height: 1rem;
  width: 1rem;
  color: rgb(252, 165, 165);
}

/* Card */
.card {
  position: relative;
  border: none;
  background-color: #efebe9;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 0.5rem;
  overflow: hidden;
}

.card__texture {
  position: absolute;
  inset: 0;
  background-image: url('/parchment.svg');
  opacity: 0.1;
  pointer-events: none;
}

.card__header {
  background-color: #8d6e63;
  color: white;
  border-bottom: 1px solid rgba(59, 24, 4, 1);
  padding: 1rem;
}

.card__title {
  font-size: 1.25rem;
  font-weight: 600;
}

.card__subtitle {
  font-size: 0.875rem;
  color: #efebe9;
}

.card__content {
  padding: 1.5rem;
}

.card__footer {
  background-color: #d7ccc8;
  border-top: 1px solid rgba(141, 110, 99, 0.2);
  padding: 1rem;
}

/* Form elements */
.form-group {
  margin-bottom: 1rem;
}

.form-group--half {
width: calc(50% - 0.5rem);
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-label {
  display: block;
  color: rgba(59, 24, 4, 1);
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.form-input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #8d6e63;
  border-radius: 0.375rem;
  outline: none;
  transition: box-shadow 0.2s;
  background-color: rgba(93, 64, 55, 0.5);
}

.form-select,
.form-textarea {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #8d6e63;
  border-radius: 0.375rem;
  outline: none;
  transition: box-shadow 0.2s;
  background-color: rgba(93, 64, 55, 0.5);
}

.form-textarea {
  resize: vertical;
  min-height: 5rem;
}

.form-input:focus {
  box-shadow: 0 0 0 2px rgba(93, 64, 55, 0.5);
}

.form-select:focus,
.form-textarea:focus {
  box-shadow: 0 0 0 2px rgba(93, 64, 55, 0.5);
}

.form-select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%235d4037' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem;
  padding-right: 2.5rem;
}

.form-message {
  margin-top: 1.5rem;
  text-align: center;
  color: #5d4037;
}

/* Account toggle */
.account-toggle {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 0.5rem;
  border: 1px solid rgba(141, 110, 99, 0.2);
}

.account-toggle__info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.account-toggle__icon-wrapper {
  padding: 0.5rem;
  background-color: #8d6e63;
  color: white;
  border-radius: 9999px;
}

.account-toggle__icon {
  height: 1.25rem;
  width: 1.25rem;
}

.account-toggle__title {
  font-weight: 500;
  color: rgba(59, 24, 4, 1);
}

.account-toggle__description {
  font-size: 0.875rem;
  color: rgba(93, 64, 55, 0.8);
}

/* Toggle switch */
.toggle {
  position: relative;
  display: inline-block;
  width: 3rem;
  height: 1.5rem;
}

.toggle__input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle__slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  border-radius: 9999px;
  transition: background-color 0.2s;
}

.toggle__slider:before {
  position: absolute;
  content: "";
  height: 1rem;
  width: 1rem;
  left: 0.25rem;
  bottom: 0.25rem;
  background-color: white;
  border-radius: 50%;
  transition: transform 0.2s;
}

.toggle__input:checked + .toggle__slider {
  background-color: rgba(59, 24, 4, 1);
}

.toggle__input:checked + .toggle__slider:before {
  transform: translateX(1.5rem);
}

/* Game items */
.games-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.game-item__wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 0.5rem;
  border: 1px solid rgba(141, 110, 99, 0.2);
}

.game-item__info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.game-item__icon-wrapper {
  padding: 0.5rem;
  background-color: #8d6e63;
  color: white;
  border-radius: 0.5rem;
}

.game-item__icon {
  height: 1.25rem;
  width: 1.25rem;
}

.game-item__title {
  font-weight: 500;
  color: rgba(59, 24, 4, 1);
}

.game-item__details {
  font-size: 0.875rem;
  color: rgba(93, 64, 55, 0.8);
}

.game-item__status {
  display: inline-block;
  padding: 0 0.5rem;
  font-size: 0.75rem;
  border-radius: 9999px;
  margin-right: 0.5rem;
}

.game-item__status--available {
  border: 1px solid #22c55e;
  color: #15803d;
}

.game-item__players {
  background-color: #8d6e63;
  color: white;
}

/* Event items */
.events-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.event-item__wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 0.5rem;
  border: 1px solid rgba(141, 110, 99, 0.2);
}

.event-item__info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.event-item__icon-wrapper {
  padding: 0.5rem;
  background-color: #8d6e63;
  color: white;
  border-radius: 0.5rem;
}

.event-item__icon {
  height: 1.25rem;
  width: 1.25rem;
}

.event-item__title {
  font-weight: 500;
  color: rgba(59, 24, 4, 1);
}

.event-item__details {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: rgba(93, 64, 55, 0.8);
}

.event-item__details-icon {
  height: 0.75rem;
  width: 0.75rem;
}

.event-item__status {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  border-radius: 9999px;
}

.event-item__status--attended {
  background-color: #dcfce7;
  color: #166534;
}

.event-item__status--cancelled {
  background-color: #fee2e2;
  color: #b91c1c;
}

.event-item__status--pending {
  background-color: #d7ccc8;
  color: rgba(59, 24, 4, 1);
}

/* Separator */
.separator {
  height: 1px;
  background-color: rgba(141, 110, 99, 0.2);
  margin: 1rem 0;
}

/* Buttons */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
  border: none;
}

.btn:disabled {
opacity: 0.6;
cursor: not-allowed;
}

.btn--primary {
  background-color: #5d4037;
  color: white;
}

.btn--primary:hover:not(:disabled) {
  background-color: #3e2723;
}

/* .btn--add {
  background-color: rgb(160, 198, 34);
  color: white;
}

.btn--add:hover{
  background-color: rgb(135, 168, 29);
} */

.btn--outline {
  background-color: transparent;
  border: 1px solid #8d6e63;
  color: rgba(59, 24, 4, 1);
}

.btn--outline:hover:not(:disabled) {
  background-color: #8d6e63;
  color: white;
}

.btn--danger {
  background-color: #ef4444;
  color: white;
}

.btn--danger:hover:not(:disabled) {
  background-color: #dc2626;
}

.btn--with-icon {
  display: inline-flex;
  align-items: center;
}

.btn__icon {
  margin-right: 0.5rem;
  height: 1rem;
  width: 1rem;
}

/* Warning */
.warning {
  background-color: #fee2e2;
  border: 1px solid #fecaca;
  border-radius: 0.375rem;
  padding: 1rem;
  margin-bottom: 1rem;
}

.warning__text {
  color: #b91c1c;
  font-size: 0.875rem;
}

/* Modal */
.modal {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
}

.modal__overlay {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.modal__content {
  position: relative;
  background-color: #efebe9;
  padding: 1.5rem;
  border-radius: 0.5rem;
  max-width: 28rem;
  width: 100%;
  overflow: hidden;
}

.modal__content--large {
max-width: 40rem;
}

.modal__header {
  background-color: #8d6e63;
  color: white;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #5d4037;
}

.modal__title {
  font-size: 1.125rem;
  font-weight: 700;
  color: rgba(59, 24, 4, 1);
  margin-bottom: 0.5rem;
}

.modal__close {
background: none;
border: none;
color: white;
font-size: 1.5rem;
cursor: pointer;
line-height: 1;
padding: 0;
margin: 0;
}

.modal__body {
  padding: 1.5rem;
  max-height: 70vh;
  overflow-y: auto;
}
.modal__text {
  color: rgba(93, 64, 55, 0.8);
  margin-bottom: 1.5rem;
}
.modal__footer {
background-color: #d7ccc8;
border-top: 1px solid rgba(141, 110, 99, 0.2);
padding: 1rem;
display: flex;
justify-content: flex-end;
gap: 0.5rem;
}
.modal__actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}
/* Tabs */
.tabs {
display: flex;
border-bottom: 1px solid rgba(141, 110, 99, 0.2);
margin-bottom: 1.5rem;
}

.tabs__tab {
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  color: #5d4037;
  font-weight: 500;
  cursor: pointer;
  transition: border-color 0.2s;
}

.tabs__tab--active {
  border-bottom-color: #5d4037;
}

.tabs__tab:hover:not(.tabs__tab--active) {
  border-bottom-color: rgba(93, 64, 55, 0.3);
}

/* Form section */
.form-section {
  padding-bottom: 1rem;
}

/* Image preview */
.image-preview {
  margin-top: 1rem;
  border-radius: 0.25rem;
  overflow: hidden;
  max-width: 200px;
  border: 1px solid rgba(141, 110, 99, 0.3);
  background-color: #f5f5f5;
}

.image-preview__container {
  width: 100%;
  height: 150px;
  overflow: hidden;
}

.image-preview__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.image-preview__placeholder {
  padding: 0.5rem;
  text-align: center;
  font-size: 0.75rem;
  color: #5d4037;
  margin: 0;
  background-color: rgba(255, 255, 255, 0.5);
}

/* Game preview */
.game-preview {
  display: flex;
  gap: 1rem;
  margin: 1.5rem 0;
  padding: 1rem;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 0.5rem;
  border: 1px solid rgba(141, 110, 99, 0.3);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.game-preview__image {
  flex-shrink: 0;
  width: 120px;
  height: 120px;
  overflow: hidden;
  border-radius: 0.25rem;
  border: 1px solid rgba(141, 110, 99, 0.3);
  background-color: #f5f5f5;
}

.game-preview__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.game-preview__details {
  flex: 1;
}

.game-preview__title {
  font-weight: 600;
  color: #5d4037;
  margin: 0 0 0.25rem 0;
}

.game-preview__players {
  font-size: 0.875rem;
  color: #5d4037;
  margin: 0 0 0.5rem 0;
}

.game-preview__description {
  font-size: 0.875rem;
  color: rgba(93, 64, 55, 0.8);
  margin: 0;
}

/* Link button */
.link-button {
  background: none;
  border: none;
  padding: 0;
  color: #5d4037;
  font-weight: 600;
  text-decoration: underline;
  cursor: pointer;
}

.link-button:hover {
  color: #3e2723;
}
</style>