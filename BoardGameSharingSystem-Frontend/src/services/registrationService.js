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

  // PUT /registrations/{eventId}/{participantId}
  async registerParticipantToEvent(eventId, participantId)
  {
    try{
      const response = await api.put(`/registrations/${eventId}/${participantId}`)
      return response.data
    } catch (error){
      console.error('Error registering participant:', error)
      throw error
    }
  },
  
  // DELETE /registrations/{eventId}/{participantId}
  async deregisterParticipantFromEvent(eventId, participantId)
  {
    try{
      const response = await api.delete(`/registrations/${eventId}/${participantId}`)
      return response.data
    } catch (error){
      console.error('Error deregistering participant:', error)
      throw error
    }
  },

    // GET /registrations/fromParticipant
    async findRegistrationByParticipant(participantId) {
      try {
        const response = await api.get('/registrations/fromParticipant', {params: { participantId }})
        return response.data
      } catch (error) {
        console.error('Error fetching registrations by participant:', error)
        throw error
      }
    }
}