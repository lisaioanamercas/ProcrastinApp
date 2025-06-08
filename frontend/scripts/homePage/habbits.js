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

    async createHabit(habitData) {
        console.log("Creating habit with data:", habitData);

        try {
            const response = await fetch(this.apiUrl, {
                method: 'POST',
                headers: this.getAuthHeader(),
                body: JSON.stringify(habitData)
            });

            console.log("Response received:", response.status, response.statusText);

            if (response.status === 401) {
                alert("Your session has expired. Please log in again.");
                window.location.href = 'login.html';
                return null;
            }

            if (!response.ok) {
                const errorText = await response.text();
                console.log("Error details:", errorText);
                throw new Error(`Failed to create habit: ${response.status}`);
            }

            const responseText = await response.text();
            let newHabit;
            if (responseText.trim()) {
                try {
                    newHabit = JSON.parse(responseText);
                    console.log("Habit created on server:", newHabit);
                    this.habits.push(newHabit);
                    return newHabit;
                } catch (e) {
                    console.error("Error parsing response JSON:", e);
                    throw e;
                }
            } else {
                throw new Error("Empty response from server");
            }
        } catch (error) {
            console.error('Network error creating habit:', error);
            throw error;
        }
    }
    async deleteHabit(habitId) {
    try {
        const response = await fetch(`${this.apiUrl}/${habitId}`, {
            method: 'DELETE',
            headers: this.getAuthHeader()
        });
        if (!response.ok) {
            throw new Error(`Failed to delete habit: ${response.status}`);
        }
        // Opțional: șterge habitul din this.habits local
        this.habits = this.habits.filter(h => h.id !== habitId);
        return true;
    } catch (error) {
        console.error('Error deleting habit:', error);
        return false;
    }
} 
    async toggleHabitCompletion(habitId) {
    try {
        const response = await fetch(`${this.apiUrl}/${habitId}/toggle`, {
            method: 'PATCH',
            headers: this.getAuthHeader()
        });
        if (!response.ok) {
            throw new Error(`Failed to toggle habit: ${response.status}`);
        }
        return true;
    } catch (error) {
        console.error('Error toggling habit:', error);
        return false;
    }
}
}