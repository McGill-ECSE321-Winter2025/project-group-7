import api from './api'

export const requestService = {
  // GET /borrowrequests/{gameCopyId}/pending
  async findPendingRequests(gameCopyId) {
    try {
      const response = await api.get(`/borrowrequests/${gameCopyId}/pending`)
      return response.data
    } catch (error) {
      console.error(`Error fetching pending borrow requests for gameCopyId ${gameCopyId}:`, error)
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
  },

  // POST /borrowrequests
  async createRequest(gameCopyId, borrowerId, borrowRequest) {
    try {
      const response = await api.post('/borrowrequests', borrowRequest, {
        params: {
          gameCopyId: gameCopyId,
          borrowerId: borrowerId
        }
      })
      return response.data
    } catch (error) {
      console.error('Error creating borrowing request:', error)
      throw error
    }
    },
}


