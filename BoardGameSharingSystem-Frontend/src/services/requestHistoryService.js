import api from './api'

export const requestHistoryService = {
  // GET /borrowrequests/all
  async findRequestHistory() {
    try {
      const response = await api.get('/borrowrequests/all')
      const history = response.data.filter(request => 
        request.requestStatus === "Accepted" || request.requestStatus === "Declined"
      )
      return history
    } catch (error) {
      console.error("Error fetching request history:", error)
      throw error
    }
  }
}
