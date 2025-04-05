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
  }
}