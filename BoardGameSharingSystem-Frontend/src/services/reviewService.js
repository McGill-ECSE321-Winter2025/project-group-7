import api from './api'

export const reviewService = {
    //
    async createReview(reviewData, userId, gameId) {
        try {
            const response = await api.post(`/reviews/${userId}/${gameId}`, reviewData,);
            return response.data;
        } catch (error) {
            console.error('Error creating review:', error);
            throw error;
        }
    },

    async findAllReviewsOfGame(gameId) {
        try {
            const response = await api.get(`/reviews/${gameId}`)
            return response.data;
        }
        catch(error) {
            console.error('Error gathering reviews:', error);
            throw error;
        }
    },

    async findGame(id) {
        try {
            const response = await api.get(`games/${id}`)
            return response.data

        }

        catch(error) {
            console.error('Error obtaining game:', error)
            throw error

        }

    }


}

