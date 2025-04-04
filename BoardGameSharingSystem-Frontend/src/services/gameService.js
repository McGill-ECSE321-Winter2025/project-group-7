import api from './api'

export const gameService = {
  // POST /games
  async createGame(gameData) {
    try {
      const response = await api.post('/games', gameData)
      return response.data
    } catch (error) {
      console.error('Error creating game:', error)
      throw error
    }
  },

  // PUT /games/{gameId}
  async updateGame(gameId, gameData) {
    try {
      const response = await api.put(`/games/${gameId}`, gameData)
      return response.data
    } catch (error) {
      console.error('Error updating game:', error)
      throw error
    }
  },

  // GET /games
  async findAllGames() {
    try {
      const response = await api.get(`/games`)
      return response.data
    } catch (error) {
      console.error('Error fetching games:', error)
      throw error
    }
  },

    // GET /games/{gameId}
    async findGamebyId(gameId) {
      try {
        const response = await api.get(`/games/${gameId}`)
        return response.data
      } catch (error) {
        console.error('Error fetching game:', error)
        throw error
      }
    },
}