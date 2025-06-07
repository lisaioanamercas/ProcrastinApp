class HabitService {
    constructor() {
        this.apiUrl = 'http://localhost:8081/api/habits';
        this.subjects = [];
    }

    getAuthHeader() {
        const token = localStorage.getItem('token');
        return {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    }

    async fetchHabits() {
        try {
            console.log("Fetching habits from API:", this.apiUrl);
            const response = await fetch(this.apiUrl, {
                method: 'GET',
                headers: this.getAuthHeader()
            });

            console.log("API Response status:", response.status);
            console.log("API Response headers:", response.headers.get('Content-Type'));

            if (!response.ok) {
                throw new Error(`API Error: ${response.status}`);
            }

            this.habits = await response.json();
            console.log("Habits loaded from API:", this.habits.length);
            return this.habits;
        } catch (error) {
            console.error('Error loading habits from API:', error);
            return [];
        }
    }
}