import { defineStore } from 'pinia';

export const useGameStore = defineStore('game', {
  state: () => ({
    gameId: null,
  }),
  actions: {
    setGameId(id) {
      this.gameId = id;
    },
    getGameId() {
      return this.gameId;
    },
  },
});