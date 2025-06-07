class habitUi{  
    constructor(habitService) {
        this.habitService = habitService;
        this.habitListEl = document.getElementById('habit-list'); // <-- adaugă această linie

     
    }

     async loadHabits() {
        try {
            this.habits = await this.habitService.fetchHabits();
            this.renderHabits();
        } catch (error) {
            console.error('Failed to load habits:', error);
        }
    }

    renderHabits() {
        console.log("Rendering habits:", this.habits);
        if (!this.habitListEl) return;
        this.habitListEl.innerHTML = this.habits.map(habit => `
            <div class="habit-item">
                <div class="habit-info">
                    <div class="habit-icon"><i class="fas fa-book"></i></div>
                    <div class="habit-details">
                        <div class="task-name">${habit.name || 'Untitled Habit'}</div>
                        <div class="task-meta">
                            <span><i class="fas fa-clock"></i> ${habit.time || ''}</span>
                            <span><i class="fas fa-calendar-day"></i> ${habit.days || ''}</span>
                        </div>
                    </div>
                </div>
                <div class="habit-actions">
                    <div class="habit-toggle" data-id="${habit.id}"></div>
                    <button class="action-icon delete-habit" data-id="${habit.id}"><i class="fas fa-trash-alt"></i></button>
                </div>
            </div>
        `).join('');
        // Poți adăuga aici event listeners pentru toggle/delete dacă vrei
    }
}