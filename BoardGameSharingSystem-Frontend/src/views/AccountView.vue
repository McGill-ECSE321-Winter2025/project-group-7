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
                    v-model="authStore.user.username"
                    class="form-input"
                  />
                </div>
                <div class="form-group">
                  <label for="email" class="form-label">Email</label>
                  <input 
                    id="email" 
                    type="email" 
                    v-model="authStore.user.userEmail"
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
                <button class="btn btn--primary">
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
              <div class="card__footer">
                <button class="btn btn--primary">
                  Save Changes
                </button>
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
                  <div v-for="(game, index) in ownedGames" :key="index" class="game-item">
                    <div class="game-item__wrapper">
                      <div class="game-item__info">
                        <div class="game-item__icon-wrapper">
                          <Dice class="game-item__icon" />
                        </div>
                        <div>
                          <p class="game-item__title">{{ game.title }}</p>
                          <p class="game-item__details">
                            <span 
                              :class="[
                                'game-item__status',
                                'game-item__players'
                              ]"
                            >
                            {{ game.numMinPlayers === game.numMaxPlayers ? game.numMinPlayers + ' players': game.numMinPlayers + '-' + game.numMaxPlayers + ' players' }}
                            </span>
                            <template v-if="game.description">• {{ game.description }}</template>
                          </p>
                        </div>
                      </div>
                      <button class="btn btn--danger">
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
                          <p class="event-item__title">{{ event.game }}</p>
                          <div class="event-item__details">
                            <Clock class="event-item__details-icon" />
                            {{ event.startDate }}
                            <template v-if="event.startDate !== event.endDate"> - {{ event.endDate }}</template>
                            • {{ event.location }} • {{ event.contactEmail }}
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
                  :src="getSelectedGameDetails().imageUrl || defaultGameImage" 
                  alt="Game cover" 
                  class="game-preview__img"
                  @error="handleImageError"
                />
              </div>
              <div class="game-preview__details">
                <h4 class="game-preview__title">{{ getSelectedGameDetails().title }}</h4>
                <p class="game-preview__players">{{ getSelectedGameDetails().minPlayers }}-{{ getSelectedGameDetails().maxPlayers }} Players</p>
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
                  id="minPlayers" 
                  type="number" 
                  v-model="newGame.minPlayers" 
                  min="1" 
                  max="99" 
                  class="form-input" 
                />
              </div>
              <div class="form-group form-group--half">
                <label for="maxPlayers" class="form-label">Max Players</label>
                <input 
                  id="maxPlayers" 
                  type="number" 
                  v-model="newGame.maxPlayers" 
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
                v-model="newGame.imageUrl" 
                class="form-input" 
                placeholder="https://example.com/game-image.jpg" 
              />
            </div>
            
            <div class="image-preview">
              <div class="image-preview__container">
                <img 
                  :src="newGame.imageUrl || defaultGameImage" 
                  alt="Game cover preview" 
                  class="image-preview__img"
                  @error="handleImageError"
                />
              </div>
              <p v-if="!newGame.imageUrl" class="image-preview__placeholder">Enter an image URL to see a preview</p>
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
  import { ref, computed, watch } from 'vue'
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '@/stores/authStore'
  import { userService } from '@/services/userService';
  import { gameOwningService } from '@/services/gameOwningService';

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
      const currentUserName = computed(() => authStore.user.username);
      const currentUserEmail = computed(() => authStore.user.userEmail);
      const isGameOwner = ref(null);
      const activeSection = ref('profile');
      const showDeleteConfirm = ref(false);
      const showAddGameModal = ref(false);
      const isAddingNewGame = ref(false);
      const selectedExistingGame = ref('');
      
      
      // Directly bind the profile fields to authStore properties
      const profile = ref({
        name: currentUserName,
        email: currentUserEmail,
        password: '' // Do not store passwords in plain text!
      });

      // Function to toggle account type
      const toggleAccountType = async () => {
        try {

          // find the gameOwnerResponse to update isGameOwner

          /* WILL ONLY WORK ONCE DIAGLOG BOX FOR ADDING GAME WHEN TOGGLING FOR THE FIRST TIME IS IMPLEMENTED
          SO UNCOMMENT THIS BLOCK ONCE ADD GAME IS IMPLEMENTED 
          AND REMOVE : "isGameOwner.value = ref(true);" BELOW 

          //const response = await gameOwningService.findGameOwner(currentUserId.value);
          //isGameOwner.value = response.isGameOwner; 
          //console.log("Checkbox is now:", isGameOwner.value);
          */
          isGameOwner.value = ref(true);
          if (checked) {
            await userService.toggleUserToGameOwner(authStore.user.id);
            console.log("Checkbox is now:", isGameOwner.value);
          } 
          // // else if it failed because of no games
          // // Uncomment this when add game dialog exists
          // else if (error.response?.data?.message?.includes("gameOwner has no associated games")) {
          //   triggerAddGameDialog(); // Replace with your actual dialog trigger + make sure it actually 
                                      // adds game by calling api
          // //re-try toggle
          //   await userService.toggleUserToGameOwner(authStore.user.id);
          //   console.log("Checkbox is now:", isGameOwner.value);
          // }
          else {
            await userService.toggleUserToPlayer(authStore.user.id);
            console.log("Checkbox is now:", isGameOwner.value);
          }

        // Refresh user data after toggle
        const updatedUser = await userService.getUser(authStore.user.id);
        authStore.user = updatedUser.data; // Update the store with new user data
        } catch (error) {
        console.error("Error toggling account type:", error);
    }
  };
      
      const ownedGames = ref([
        {
          title: 'Catan',
          numMinPlayers: 2,
          numMaxPlayers: 4,
          description: "Multiplayer board game designed by Klaus Teuber",
          pictureUrl: null
        },
        {
          title: 'Ticket to Ride',
          numMinPlayers: 2,
          numMaxPlayers: 5,
          description: null,
          pictureUrl: null
        },
        {
          title: 'Pandemic',
          numMinPlayers: 2,
          numMaxPlayers: 5,
          description: "As members of an elite disease control team, you must keep four deadly diseases at bay",
          pictureUrl: null
        },
        {
          title: 'Chess',
          numMinPlayers: 2,
          numMaxPlayers: 2,
          description: "Strategy board game for two players",
          pictureUrl: "/test-image2.jpg"
        },
        
      ])

      const availableGames = ref([
        {
          id: 1,
          title: 'Gloomhaven',
          minPlayers: 1,
          maxPlayers: 4,
          description: 'Gloomhaven is a cooperative game of tactical combat, battling monsters and advancing a player\'s own individual goals in a persistent and changing world.',
          imageUrl: 'https://cf.geekdo-images.com/sZYp_3BTDGjh2unaZfZmuA__imagepage/img/pBaOL7vV402nn1I5dHsdSKsFHqA=/fit-in/900x600/filters:no_upscale():strip_icc()/pic2437871.jpg'
        },
        {
          id: 2,
          title: 'Wingspan',
          minPlayers: 1,
          maxPlayers: 5,
          description: 'Wingspan is a competitive, medium-weight, card-driven, engine-building board game from Stonemaier Games.',
          imageUrl: 'https://cf.geekdo-images.com/yLZJCVLlIx4c7eJEWUNJ7w__imagepage/img/uIjeoKgHMcRtzRSR4MoUYl3nXxs=/fit-in/900x600/filters:no_upscale():strip_icc()/pic4458123.jpg'
        },
        {
          id: 3,
          title: 'Terraforming Mars',
          minPlayers: 1,
          maxPlayers: 5,
          description: 'Compete with rival CEOs to make Mars habitable and build your corporate empire.',
          imageUrl: 'https://cf.geekdo-images.com/wg9oOLcsKvDesSUdZQ4rxw__imagepage/img/FS1RE8Ue6nk1pNbPI3l-OSapQGc=/fit-in/900x600/filters:no_upscale():strip_icc()/pic3536616.jpg'
        },
        {
          id: 4,
          title: 'Scythe',
          minPlayers: 1,
          maxPlayers: 5,
          description: 'Asymmetric strategy game set in an alternate-history 1920s period where players compete for territory and resources.',
          imageUrl: 'https://cf.geekdo-images.com/7k_nOxpO9OGIjhLq2BUZdA__imagepage/img/zoz-t_z9nqqxL7OwQenbqp9PRl8=/fit-in/900x600/filters:no_upscale():strip_icc()/pic3163924.jpg'
        },
        {
          id: 5,
          title: 'Azul',
          minPlayers: 2,
          maxPlayers: 4,
          description: 'Tile placement game where players compete to create the most beautiful mosaic.',
          imageUrl: 'https://cf.geekdo-images.com/tz19PfklMdAdjxV9WArraA__imagepage/img/K3OydSGTbGOb6FrwPjpYT0BaUbI=/fit-in/900x600/filters:no_upscale():strip_icc()/pic3718275.jpg'
        }
      ])
    
      const newGame = ref({
        title: '',
        minPlayers: 2,
        maxPlayers: 4,
        description: '',
        imageUrl: ''
      })

      const defaultGameImage = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2YzZTllNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMjAiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGRvbWluYW50LWJhc2VsaW5lPSJtaWRkbGUiIGZpbGw9IiM4ZDZlNjMiPkJvYXJkIEdhbWUgSW1hZ2U8L3RleHQ+PC9zdmc+'
      
      const events = ref([
        {
          game: 'Monopoly',
          startDate: 'March 20, 2025',
          endDate: 'March 21, 2025',
          startTime: '2PM',
          endTime: '3PM',
          location: 'Community Center',
          status: 'Attended',
          contactEmail: 'sniggy_the_piggy@gmail.com'
        },
        {
          game: 'Catan',
          startDate: 'February 15, 2025',
          endDate: 'February 19, 2025',
          startTime: '2PM',
          endTime: '3PM',
          location: 'Board Game Cafe',
          status: 'Attended',
          contactEmail: 'peter_parker@gmail.com'
        },
        {
          game: 'UNO',
          startDate: 'January 10, 2025',
          endDate: 'January 10, 2025',
          startTime: '2PM',
          endTime: '3PM',
          location: 'Game Store',
          status: 'Cancelled',
          contactEmail: 'tom_loves_zendaya@gmail.com'
        }
      ])

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
      
      // Computed property to check if the form is valid for adding a game
      const canAddGame = computed(() => {
        if (isAddingNewGame.value) {
          return (
            newGame.value.title.trim() !== '' && 
            newGame.value.minPlayers > 0 && 
            newGame.value.maxPlayers >= newGame.value.minPlayers
          )
        } else {
          return selectedExistingGame.value !== ''
        }
      })
    
      // Function to get details of the selected existing game
      const getSelectedGameDetails = () => {
        return availableGames.value.find(game => game.id === selectedExistingGame.value) || {}
      }
    
      // Function to close the add game modal and reset form
      const closeAddGameModal = () => {
        showAddGameModal.value = false
        isAddingNewGame.value = false
        selectedExistingGame.value = ''
        newGame.value = {
          title: '',
          minPlayers: 2,
          maxPlayers: 4,
          description: '',
          imageUrl: ''
        }
      }
    
      // Function to add a game to the collection
      const addGameToCollection = () => {
        if (isAddingNewGame.value) {
          // Add the new game to owned games
          ownedGames.value.push({
            title: newGame.value.title,
            status: 'Available',
            borrower: null,
            dueDate: null
          })
          
          // Also add to available games for future selection
          availableGames.value.push({
            id: availableGames.value.length + 1,
            title: newGame.value.title,
            minPlayers: newGame.value.minPlayers,
            maxPlayers: newGame.value.maxPlayers,
            description: newGame.value.description,
            imageUrl: newGame.value.imageUrl || 'https://via.placeholder.com/200x200?text=No+Image'
          })
        } else {
          // Add the selected existing game to owned games
          const selectedGame = getSelectedGameDetails()
          ownedGames.value.push({
            title: selectedGame.title,
            status: 'Available',
            borrower: null,
            dueDate: null
          })
        }
        
        // Close the modal
        closeAddGameModal()
      }

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
      
      return {
        authStore,
        currentUserName,
        currentUserEmail,
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
        defaultGameImage
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
  }

  .form-select,
  .form-textarea {
    width: 100%;
    padding: 0.5rem 0.75rem;
    border: 1px solid #8d6e63;
    border-radius: 0.375rem;
    outline: none;
    transition: box-shadow 0.2s;
    background-color: white;
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