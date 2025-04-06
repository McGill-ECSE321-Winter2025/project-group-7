import api from './api'

export const userService = {
    // GET /users/{id}
    async findUserAccount(id) {
      try {
        const response = await api.get(`/users/${id}`)
        return response.data
      } catch (error) {
        console.error('Error fetching user:', error)
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
  
    // DELETE /users/{id}
    async deleteUserAccount(id) {
      try {
        const response = await api.delete(`/users/${id}`)
        return response.data
      } catch (error) {
        console.error('Error deleting user:', error)
        throw error
      }
    },

    // PUT /users/{id}
    async updateUserAccount(id) {
      try {
        const response = await api.put(`/users/${id}`)
        return response.data
      } catch (error) {
        console.error('Error updating account details:', error)
        throw error
      }
    },

    // POST /users/login
    async login(userData) {
      try {
        const response = await api.post('/users/login', userData)
        return response.data
      } catch (error) {
        console.error('Error logging in the user:', error)
        throw error
      }
    },

    // PUT /users/{id}/toPlayer
    async toggleUserToPlayer(id) {
      try {
        const response = await api.put(`/users/${id}/toPlayer`)
        return response.data
      } catch (error) {
        console.error('Error toggling user to player:', error)
        throw error
      }
    },

    // PUT /users/{id}/toGameOwner
    async toggleUserToGameOwner(id) {
      try {
        const response = await api.put(`/users/${id}/toGameOwner`)
        return response.data
      } catch (error) {
        console.error('Error toggling user to game owner:', error)
        throw error
      }
    },
  }