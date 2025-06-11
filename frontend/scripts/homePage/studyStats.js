class StudyStats {
    constructor(tasks, habits) {
        this.tasks = tasks || [];
        this.habits = habits || [];
    }
     getAuthHeader() {
        const token = localStorage.getItem('token');
        return {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    }


    async fetchStudyStats() {
    const response = await fetch(`http://localhost:8081/api/stats/study`, {
        method: 'GET',
        headers: this.getAuthHeader()
    });
    if (!response.ok) throw new Error('Failed to fetch stats');
    return await response.json();
}

    // 1. Tasks This Week
    getTasksThisWeek() {
        const now = new Date();
        const startOfWeek = new Date(now);
        startOfWeek.setDate(now.getDate() - now.getDay()); // duminică
        startOfWeek.setHours(0,0,0,0);

        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 7);

        return this.tasks.filter(task => {
            if (!task.deadline) return false;
            const deadline = new Date(task.deadline);
            return deadline >= startOfWeek && deadline < endOfWeek;
        }).length;
    }

    // 2. Average Difficulty
    getAvgDifficulty() {
        const difficulties = this.tasks.map(t => t.difficulty).filter(d => d != null);
        if (!difficulties.length) return 0;
        return (difficulties.reduce((a, b) => a + b, 0) / difficulties.length).toFixed(1);
    }
}