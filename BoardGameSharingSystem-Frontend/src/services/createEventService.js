import api from './api' // your Axios instance

export const eventService = {
    async createEvent(eventData) {
        const response = await api.post('/events', eventData)
        return response.data
    },
    async findAllEvents() {
        const response = await api.get('/events')
        return response.data
    }
}
