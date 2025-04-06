import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from 'axios'

const axiosClient = axios.create({
    baseURL: "http://localhost:8080",
  });

export const useAuthStore = defineStore('auth', () => {
    const user = ref({
        id: null,
        username: null,
        userEmail: null,
        isAuthenticated: false,
    });

    async function login(username, email, password) {
        console.log(username);
        console.log(email);
        console.log(password);
        try {
            const response = await axiosClient.post('/users/login', {
                username: username,
                email: email,
                password: password
            });

            // Extract relevant data from the response
            const { id, name, email: returnedEmail } = response.data;

            // Update the store with the user's data
            user.value.id = id;
            user.value.username = name;
            user.value.userEmail = returnedEmail;
            user.value.isAuthenticated = true;

        } catch (error) {
            console.error('Login failed:', error.response?.data?.errors || error.message);
            const errorMsg = error.response?.data?.errors?.[0] || error.message || 'Invalid credentials';
            throw new Error(errorMsg);
        }
    }

    function logout() {
        user.value.id = null;
        user.value.username = null;
        user.value.userEmail = null;
        user.value.isAuthenticated = false;
    }

    return { user, login, logout }
}, {
    persist: true
});
