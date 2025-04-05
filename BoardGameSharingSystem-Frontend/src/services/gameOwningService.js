import api from './api'

export const gameOwningService = {
    // PUT /gameowners/{id}
    async createGameOwner(id) {
      try {
        const response = await api.put(`/gameowners/${id}`)
        return response.data
      } catch (error) {
        console.error('Error creating game owner:', error)
        throw error
      }
    },

    // GET /gameowners/{id}
    async findGameOwner(id) {
        try {
          const response = await api.get(`/gameowners/${id}`)
          return response.data
        } catch (error) {
          console.error('Error fetching game owner:', error)
          throw error
        }
      },
    }  