import api from './api'

export const gameService = {
  // POST /{gameOwnerId}/{gameId}
  async addGameCopyToGameOwner(gameOwnerId, gameId) {
    try {
      const response = await api.post(`/${gameOwnerId}/${gameId}`)
      return response.data
    } catch (error) {
      console.error('Error creating game copy:', error)
      throw error
    }
  },

  // DELETE /{gameCopyId}
  async removeGameCopyFromGameOwner(gameCopyId) {
    try {
      const response = await api.delete(`/${gameCopyId}`)
      return response.data
    } catch (error) {
      console.error('Error deleting game copy:', error)
      throw error
    }
  },

  // GET /forOwner
  async findGameCopiesForGameOwner(gameOwnerId) {
    try {
      const response = await api.get(`/forOwner?gameOwnerId=${gameOwnerId}`)
      return response.data
    } catch (error) {
      console.error('Error fetching game copies from game owner:', error)
      throw error
    }
  },

    // GET /forGame
    async findGameCopiesFromGame(gameId) {
      try {
        const response = await api.get(`/forGame?gameId=${gameId}`)
        return response.data
      } catch (error) {
        console.error('Error fetching game copies from game:', error)
        throw error
      }
    },
}