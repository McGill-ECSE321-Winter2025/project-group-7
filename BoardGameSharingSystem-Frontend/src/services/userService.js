import api from './api'

export const userService = {
    // GET /users/{id}
    async findUserAccount(id) {
      try {
        const response = await api.get(`/users/${id}`)
        return response.data
      } catch (error) {
        console.error('Error fetching users:', error)
        throw error
      }
    },
  
    // POST /users
    async createUserAccount(userData) {
      try {
        const response = await api.post('/users', userData)
        return response.data
      } catch (error) {
        console.error('Error creating user:', error)
        throw error
      }
    },
  
    // DELETE /events/{id}
    async deleteUserAccount(userData) {
      try {
        const response = await api.delete(`/users/${id}`)
        return response.data
      } catch (error) {
        console.error('Error deleting user:', error)
        throw error
      }
    },
  }