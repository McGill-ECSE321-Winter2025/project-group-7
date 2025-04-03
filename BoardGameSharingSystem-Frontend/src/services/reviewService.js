import api from './api'

export const reviewService = {
    //
    async createReview(reviewData, reviewerId, gameId) {
        try {
            const response = await api.post('/reviews', reviewData, {params: {reviewerId: reviewerId, gameId: gameId}});
            return response.data}

        catch(error) {
            console.error('Error creating review:', error)
            throw error

        }

    },

    async findAllReviewsOfGame(id) {
        try {
            const response = await api.get('/reviews', {
                params: {id: id}
            });
            return response.data; }
        catch(error) {
            console.error('Error gathering reviews:', error)
            throw error
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

