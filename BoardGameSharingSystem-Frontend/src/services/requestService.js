import api from './api'

export const requestService = {
  // GET /borrowrequests/all
  async findAllRequests() {
    try {
      const response = await api.get('/borrowrequests/all')
      return response.data
    } catch (error) {
      console.error('Error fetching borrow requests:', error)
      throw error
    }
  },

  // PUT /borrowrequests/{id}/accept
  async acceptRequest(id) {
    try {
      const response = await api.put(`/borrowrequests/${id}/accept`)
      return response.data
    } catch (error) {
      console.error(`Error accepting request ${id}:`, error)
      throw error
    }
  },

  // DELETE /borrowrequests/{id}
  async declineRequest(id) {
    try {
      const response = await api.delete(`/borrowrequests/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error declining request ${id}:`, error)
      throw error
    }
  }
}