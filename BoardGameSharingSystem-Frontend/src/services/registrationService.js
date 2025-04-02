import api from './api'

export const registrationService = {
  // GET /registrations/fromEvent
  async findRegistrationByEvent(eventId) {
    try {
      const response = await api.get('/registrations/fromEvent', {params: { eventId }})
      return response.data
    } catch (error) {
      console.error('Error fetching registrations:', error)
      throw error
    }
  },
}