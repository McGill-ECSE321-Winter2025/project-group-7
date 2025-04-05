
export const useGameStore = defineStore('game', {
    state: () => ({
        currentGameId : null
    }),

    actions: {
        setCurrentGameId(id) {
            this.currentGameId = id;

        }

        
    },
    getters: {
        getCurrentGameId: (state) => state.currentGameId,
    }


})