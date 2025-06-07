// Task UI - Handles UI rendering for tasks
class TaskUI {
    constructor(taskService, subjectService) {
        this.taskService = taskService;
        this.subjectService = subjectService;
        this.taskListEl = document.getElementById('task-list');
        this.taskModal = document.getElementById('task-modal');
        this.closeTaskModal = document.getElementById('close-task-modal');
        this.taskForm = document.getElementById('task-form');
        this.taskIdField = document.getElementById('task-id');
        this.taskModalTitle = document.getElementById('task-modal-title');
        this.addTaskBtn = document.getElementById('add-task-btn');
        this.filterButtons = document.querySelectorAll('.filter-btn');
        
        // Subject dropdown
        this.subjectDropdown = document.getElementById('task-subject');
        
        // Stats elements
        this.tasksCompletedEl = document.getElementById('tasks-completed');
        this.tasksTotalEl = document.getElementById('tasks-total');
        this.productivityScoreEl = document.getElementById('productivity-score');
        
        this.initEventListeners();
        this.loadSubjects();
    }

    // Load subjects for dropdown
    // Load subjects for dropdown
    async loadSubjects() {
        try {
            const subjects = await this.subjectService.fetchSubjects();
            console.log("Loaded subjects:", subjects);
            
            if (!this.subjectDropdown) {
                console.error("Subject dropdown not found in DOM");
                return;
            }
            
            // Clear existing options except the first one
            while (this.subjectDropdown.options.length > 1) {
                this.subjectDropdown.remove(1);
            }
            
            // Add subjects to dropdown
            subjects.forEach(subject => {
                const option = document.createElement('option');
                option.value = subject.name;
                option.textContent = subject.name;
                this.subjectDropdown.appendChild(option);
            });
            
            console.log("Subject dropdown populated with", subjects.length, "options");
        } catch (error) {
            console.error('Failed to load subjects:', error);
        }
    }
    
    initEventListeners() {
        // Add task button
        if (this.addTaskBtn) {
            this.addTaskBtn.addEventListener('click', () => this.openAddTaskModal());
        }
        
        // Close modal button
        if (this.closeTaskModal) {
            this.closeTaskModal.addEventListener('click', () => {
                this.taskModal.classList.remove('active');
            });
        }

        if (this.taskForm) {
        console.log("Setting up form submit listener");
        this.taskForm.addEventListener('submit', (e) => {
            console.log("Form submitted!");
            this.handleFormSubmit(e);
        });
    }
        
        // Close modal when clicking outside
        window.addEventListener('click', (e) => {
            if (e.target === this.taskModal) {
                this.taskModal.classList.remove('active');
            }
        });
        
        // Filter buttons
        if (this.filterButtons) {
            this.filterButtons.forEach(btn => {
                btn.addEventListener('click', () => {
                    this.filterButtons.forEach(b => b.classList.remove('active'));
                    btn.classList.add('active');
                    this.renderTasks(btn.dataset.filter);
                });
            });
        }
    }
    
    async loadTasks() {
        try {
            await this.taskService.fetchTasks();
            this.renderTasks();
            this.updateProgressSummary();
        } catch (error) {
            console.error('Failed to load tasks:', error);
        }
    }
    
    renderTasks(filter) {
        if (!this.taskListEl) return;
        
        const filteredTasks = this.taskService.getFilteredTasks(filter);

        this.taskListEl.innerHTML = filteredTasks.map(task => `
            <div class="task-item ${task.completed ? 'completed' : ''}">
                <div class="task-checkbox ${task.completed ? 'checked' : ''}" data-id="${task.id}"></div>
                <div class="task-details">
                    <div class="task-name">${task.name || task.title || 'Untitled Task'}</div>
                    <div class="task-meta">
                        <span><i class="fas fa-book"></i> ${task.subject_name || 'No subject'}</span>
                        <span><i class="fas fa-clock"></i> ${task.duration_minutes || 0} min</span>
                        <span><i class="fas fa-calendar"></i> ${new Date(task.deadline).toLocaleDateString()}</span>
                        <span><i class="fas fa-chart-line"></i> ${this.convertDifficultyToString(task.difficulty)}</span>
                    </div>
                </div>
                <div class="task-actions">
                    <button class="action-icon edit-task" data-id="${task.id}"><i class="fas fa-edit"></i></button>
                    <button class="action-icon delete-task" data-id="${task.id}"><i class="fas fa-trash-alt"></i></button>
                </div>
            </div>
            `).join('');

        // Add event listeners
        document.querySelectorAll('.task-checkbox').forEach(checkbox => {
            checkbox.addEventListener('click', () => this.toggleTaskCompletion(checkbox.dataset.id));
        });

        document.querySelectorAll('.edit-task').forEach(btn => {
            btn.addEventListener('click', () => this.openEditTaskModal(btn.dataset.id));
        });

        document.querySelectorAll('.delete-task').forEach(btn => {
            btn.addEventListener('click', () => this.deleteTask(btn.dataset.id));
        });
    }
    
    async toggleTaskCompletion(taskId) {
        try {
            await this.taskService.toggleTaskCompletion(taskId);
            this.renderTasks();
            this.updateProgressSummary();
        } catch (error) {
            console.error('Failed to toggle task:', error);
        }
    }
    
    async deleteTask(taskId) {
        if (confirm('Are you sure you want to delete this task?')) {
            try {
                await this.taskService.deleteTask(taskId);
                this.renderTasks();
                this.updateProgressSummary();
            } catch (error) {
                console.error('Failed to delete task:', error);
            }
        }
    }
    
    openEditTaskModal(taskId) {
        const task = this.taskService.tasks.find(t => t.id === parseInt(taskId));
        if (!task) return;
        
        this.taskIdField.value = task.id;
        document.getElementById('task-name').value = task.title || task.name || '';
        document.getElementById('task-description').value = task.description || '';
        document.getElementById('task-subject').value = task.subject_name || task.subject || '';
        document.getElementById('task-duration').value = task.duration_minutes || task.duration || '';
        
        // Convert numeric difficulty to string for the dropdown
        const difficultyString = this.convertDifficultyToString(task.difficulty);
        document.getElementById('task-difficulty').value = difficultyString;
        
        // Format date for datetime-local input
        if (task.deadline) {
            const deadlineDate = new Date(task.deadline);
            const formattedDate = deadlineDate.toISOString().slice(0, 16);
            document.getElementById('task-deadline').value = formattedDate;
        } else {
            document.getElementById('task-deadline').value = '';
        }
        
        this.taskModalTitle.textContent = 'Edit Task';
        this.taskModal.classList.add('active');
    }
    
    openAddTaskModal() {
        // Clear the form
        this.taskIdField.value = '';
        this.taskForm.reset();
        
        // Set default deadline to tomorrow
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        const formattedDate = tomorrow.toISOString().slice(0, 16); // Format: YYYY-MM-DDTHH:MM
        document.getElementById('task-deadline').value = formattedDate;
        
        this.taskModalTitle.textContent = 'Add New Task';
        this.taskModal.classList.add('active');
    }
    
    async handleFormSubmit(e) {
        e.preventDefault();
        console.log("Processing form submission");

        // Check if logged in
        const token = localStorage.getItem('token');
        if (!token) {
            alert("You need to be logged in to save tasks");
            window.location.href = 'login.html';
            return;
        }
        
        const taskId = this.taskIdField.value;
        const deadlineInput = document.getElementById('task-deadline').value;
        
        // Properly format the deadline
        let formattedDeadline = null;
        if (deadlineInput) {
            formattedDeadline = new Date(deadlineInput).toISOString();
        }
        
        // Get difficulty as number value (1-5)
        let difficultyValue = 3; // Default medium
        const difficultySelect = document.getElementById('task-difficulty');
        if (difficultySelect) {
            const selectedValue = difficultySelect.value;
            switch (selectedValue) {
                case 'easy': difficultyValue = 1; break;
                case 'medium': difficultyValue = 3; break;
                case 'hard': difficultyValue = 5; break;
            }
        }
        
        // Create task data matching the backend DTO
        const taskData = {
            name: document.getElementById('task-name').value,
            description: document.getElementById('task-description').value,
            subject: document.getElementById('task-subject').value,
            duration_minutes: parseInt(document.getElementById('task-duration').value) || 30,
            difficulty: difficultyValue,
            deadline: formattedDeadline
        };
        
        console.log("Submitting task data:", taskData);
        
        try {
            if (taskId) {
                await this.taskService.updateTask(parseInt(taskId), taskData);
                console.log("Task updated successfully");
            } else {
                await this.taskService.createTask(taskData);
                console.log("Task created successfully");
            }
            
            this.renderTasks();
            this.updateProgressSummary();
            this.taskModal.classList.remove('active');
        } catch (error) {
            console.error('Failed to save task:', error);
        }
    }

    // Convert difficulty string to number
    convertDifficultyToNumber(difficulty) {
        switch(difficulty) {
            case 'easy': return 1;
            case 'medium': return 3;
            case 'hard': return 5;
            default: return 3;
        }
    }
    
    // Convert difficulty number to string
    convertDifficultyToString(difficulty) {
        if (difficulty <= 2) return 'easy';
        if (difficulty <= 4) return 'medium';
        return 'hard';
    }
    
    updateProgressSummary() {
        const stats = this.taskService.getTaskStats();

        if (this.tasksCompletedEl) this.tasksCompletedEl.textContent = stats.completedTasks;
        if (this.tasksTotalEl) this.tasksTotalEl.textContent = stats.totalTasks;
        if (this.productivityScoreEl) this.productivityScoreEl.textContent = `${stats.productivityScore}%`;
    }
}