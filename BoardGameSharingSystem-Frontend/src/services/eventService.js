import api from './api'

export const eventService = {
  // GET /events
  async findAllEvents() {
    try {
      const response = await api.get('/events')
      return response.data
    } catch (error) {
      console.error('Error fetching events:', error)
      throw error
    }
  },

  // PUT /events/{id}
  async updateEvent(id, eventData) {
    try {
      const response = await api.put(`/events/${id}`, eventData)
      return response.data
    } catch (error) {
      console.error('Error updating event:', error)
      throw error
    }
  },

  // POST /events
  async createEvent(eventData) {
    try {
      const response = await api.post('/events', eventData)
      return response.data
    } catch (error) {
      console.error('Error creating event:', error)
      throw error
    }
  },

  // DELETE /events/{id}
  async deleteEvent(id) {
    try {
      const response = await api.delete(`/events/${id}`)
      return response.data
    } catch (error) {
      console.error('Error deleting event:', error)
      throw error
    }
  },
}