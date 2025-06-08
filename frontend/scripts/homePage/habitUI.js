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

         this.habitListEl.querySelectorAll('.delete-habit').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const habitId = btn.getAttribute('data-id');
            if (confirm('Sigur vrei să ștergi acest habit?')) {
                await this.habitService.deleteHabit(habitId);
                this.loadHabits();
            }
        });
    });
        
         // după ce randezi lista de habits
         this.habitListEl.querySelectorAll('.habit-toggle').forEach(toggle => {
            toggle.addEventListener('click', async (e) => {
                const habitId = toggle.getAttribute('data-id');
                await this.habitService.toggleHabitCompletion(habitId);
                // Reîncarcă lista pentru a reflecta schimbarea
                this.loadHabits();
            });
        });
    }
      static showNewHabitModal() {
        const habitModal = document.getElementById('habit-modal');
        if (habitModal) {
            habitModal.style.display = 'block';
        }
    }

    static hideNewHabitModal() {
        const habitModal = document.getElementById('habit-modal');
        if (habitModal) {
            habitModal.style.display = 'none';
        }
    }

    
    
}

document.addEventListener('DOMContentLoaded', function() {

    const deleteHabitBtns = document.querySelectorAll('.delete-habit');


    if( deleteHabitBtns) {

    }

    const addHabitBtn = document.getElementById('add-habit-btn');
    if (addHabitBtn) {
        addHabitBtn.addEventListener('click', () => {
            habitUi.showNewHabitModal();
        });
    }

    const closeHabitModal = document.getElementById('close-habit-modal');
    if (closeHabitModal) {
        closeHabitModal.addEventListener('click', () => {
            habitUi.hideNewHabitModal();
        });
    }

    const habitModal = document.getElementById('habit-modal');
    if (habitModal) {
        habitModal.addEventListener('click', function(event) {
            if (event.target === habitModal) {
                habitUi.hideNewHabitModal();
            }
        });
    }

    const habitForm = document.getElementById('habit-form');
    if (habitForm) {
        habitForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const name = document.getElementById('habit-name').value;
            const subject = document.getElementById('habit-subject').value;
            const time = document.getElementById('habit-time').value;

            const daysChecked = Array.from(habitForm.querySelectorAll('input[type="checkbox"]:checked'))
                .map(cb => cb.value);

         
            const dayOfWeek = daysChecked.join(',');

          
            const habitData = {
                name,
                subject,
                time, 
                dayOfWeek,
                recurring: true 
            };

            try {
                await window.habitServiceInstance.createHabit(habitData);
                habitUi.hideNewHabitModal();
                if (window.habitUIInstance) {
                    window.habitUIInstance.loadHabits();
                }
            } catch (err) {
                alert('Eroare la salvarea habitului!');
            }
        });
    }
});


