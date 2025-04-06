import api from './api'

export const requestHistoryService = {
  // GET /borrowrequests/{gameCopyId}/accepted
  async findAcceptedBorrowingRequests(gameCopyId) {
    try {
      const response = await api.get(`/borrowrequests/${gameCopyId}/accepted`)
      return response.data
    } catch (error) {
      console.error(`Error fetching accepted borrowing requests for gameCopyId ${gameCopyId}:`, error)
      throw error
    }
  }
}
