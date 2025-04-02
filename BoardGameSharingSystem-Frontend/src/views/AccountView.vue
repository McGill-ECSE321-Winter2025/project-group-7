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
                    v-model="profile.name" 
                    class="form-input"
                  />
                </div>
                <div class="form-group">
                  <label for="email" class="form-label">Email</label>
                  <input 
                    id="email" 
                    type="email" 
                    v-model="profile.email" 
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
                      v-model="isGameOwner" 
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
                <button class="btn btn--add btn--with-icon">
                  <Package class="btn__icon" /> Add New Game
                </button>
              </div>
              <div class="card__content">
                <div class="games-list">
                  <div v-for="(game, index) in ownedGames" :key="index" class="game-item">
                    <div class="game-item__wrapper">
                      <div class="game-item__info">
                        <div class="game-item__icon-wrapper">
                          <img v-if="game.pictureUrl" :src= game.pictureUrl alt="Game Icon" class="game-item__icon" />
                          <Dice v-else class="game-item__icon" />
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
              <!--<div class="card__footer">
                <button class="btn btn--primary btn--with-icon">
                  <Package class="btn__icon" /> Add New Game
                </button>
              </div>-->
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
                          <p class="event-item__title">{{ event.title }}</p>
                          <div class="event-item__details">
                            <Clock class="event-item__details-icon" /> {{ event.date }} • {{ event.location }}
                          </div>
                        </div>
                      </div>
                      <span 
                        :class="[
                          'event-item__status',
                          event.status === 'Attended' ? 'event-item__status--attended' : 
                          event.status === 'Cancelled' ? 'event-item__status--cancelled' : 
                          'event-item__status--pending'
                        ]"
                      >
                        {{ event.status }}
                      </span>
                    </div>
                    <div v-if="index < events.length - 1" class="separator"></div>
                  </div>
                </div>
              </div>
              <div class="card__footer">
                <button class="btn btn--primary">
                  View All Events
                </button>
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
                      <button class="btn btn--danger">
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
    </div>
  </template>
  
  <script>
  import { ref, computed, watch } from 'vue'
  import { 
    Calendar, 
    Clock, 
    Dice1Icon as Dice, 
    Dice1Icon as DiceIcon,
    Gamepad2, 
    Package, 
    Settings, 
    User, 
    Users 
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
      Users
    },
    setup() {
      const isGameOwner = ref(false)
      const activeSection = ref('profile')
      const showDeleteConfirm = ref(false)
      
      const profile = ref({
        name: 'Meeple Master',
        email: 'meeple@boardgames.com',
        password: ''
      })
      
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
      
      const events = ref([
        {
          title: 'Weekly Game Night',
          date: 'March 20, 2025',
          location: 'Community Center',
          status: 'Attended'
        },
        {
          title: 'Catan Tournament',
          date: 'February 15, 2025',
          location: 'Board Game Cafe',
          status: 'Attended'
        },
        {
          title: 'RPG Weekend',
          date: 'January 10, 2025',
          location: 'Game Store',
          status: 'Cancelled'
        }
      ])
      
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
      
      return {
        isGameOwner,
        activeSection,
        profile,
        ownedGames,
        events,
        navItems,
        showDeleteConfirm
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
  
  .form-input:focus {
    box-shadow: 0 0 0 2px rgba(93, 64, 55, 0.5);
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
  
  .btn--primary {
    background-color: #5d4037;
    color: white;
  }
  
  .btn--primary:hover {
    background-color: #3e2723;
  }

  .btn--add {
    background-color: #81cb81;
    color: white;
  }

  .btn--add:hover{
    background-color: #5dac5d;
  }
  
  .btn--outline {
    background-color: transparent;
    border: 1px solid #8d6e63;
    color: rgba(59, 24, 4, 1);
  }
  
  .btn--outline:hover {
    background-color: #8d6e63;
    color: white;
  }
  
  .btn--danger {
    background-color: #ef4444;
    color: white;
  }
  
  .btn--danger:hover {
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
  }
  
  .modal__title {
    font-size: 1.125rem;
    font-weight: 700;
    color: rgba(59, 24, 4, 1);
    margin-bottom: 0.5rem;
  }
  
  .modal__text {
    color: rgba(93, 64, 55, 0.8);
    margin-bottom: 1.5rem;
  }
  
  .modal__actions {
    display: flex;
    justify-content: flex-end;
    gap: 0.5rem;
  }
  </style>