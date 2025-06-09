class HeatmapService {
    constructor() {
        this.apiUrl = 'http://localhost:8081/api';
        this.currentDate = new Date();
        this.activityData = new Map(); // Cache for activity data
    }

    getAuthHeader() {
        const token = localStorage.getItem('token');
        return {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    }

    // Fetch activity data for a specific month
    async fetchActivityData(year, month) {
        try {
            const response = await fetch(`${this.apiUrl}/activity/heatmap?year=${year}&month=${month}`, {
                headers: this.getAuthHeader()
            });

            if (!response.ok) {
                throw new Error('Failed to fetch activity data');
            }

            const data = await response.json();
            
            // Cache the data
            const key = `${year}-${month}`;
            this.activityData.set(key, data);
            
            return data;
        } catch (error) {
            console.error('Error fetching activity data:', error);
            // Return mock data for development
            return this.generateMockData(year, month);
        }
    }

    // Generate mock activity data for development
    generateMockData(year, month) {
        const data = {};
        const daysInMonth = new Date(year, month, 0).getDate();
        
        for (let day = 1; day <= daysInMonth; day++) {
            const date = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            // Random activity level (0-4)
            const tasksCompleted = Math.floor(Math.random() * 6);
            const habitsCompleted = Math.floor(Math.random() * 4);
            const totalActivity = tasksCompleted + habitsCompleted;
            
            data[date] = {
                tasks_completed: tasksCompleted,
                habits_completed: habitsCompleted,
                total_activity: totalActivity,
                level: this.getActivityLevel(totalActivity)
            };
        }
        
        return data;
    }

    // Convert activity count to heatmap level (0-4)
    getActivityLevel(activityCount) {
        if (activityCount === 0) return 0;
        if (activityCount <= 2) return 1;
        if (activityCount <= 4) return 2;
        if (activityCount <= 7) return 3;
        return 4;
    }

    // Get cached data or fetch if not available
    async getActivityData(year, month) {
        const key = `${year}-${month}`;
        if (this.activityData.has(key)) {
            return this.activityData.get(key);
        }
        return await this.fetchActivityData(year, month);
    }
}