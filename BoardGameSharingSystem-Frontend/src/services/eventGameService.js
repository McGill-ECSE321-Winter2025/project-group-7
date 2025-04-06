import api from './api'

export const eventGameService = {
  // GET /eventGames/fromEvent
  async findEventGamesByEvent(eventId) {
    try {
      const response = await api.get('/eventGames/fromEvent', {params: {eventId}})
      return response.data
    } catch (error) {
      console.error('Error fetching eventGames:', error)
      throw error
    }
  },

  // PUT /eventGames/{eventId}/{gameId}
  async addGameToEvent(eventId, gameId) {
    try {
      const response = await api.put(`/eventGames/${eventId}/${gameId}`)
      return response.data
    } catch (error) {
      console.error('Error adding game to event:', error)
      throw error
    }
  }
}