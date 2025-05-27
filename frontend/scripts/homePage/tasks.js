class TaskService {
    constructor() {
        this.apiUrl = 'http://localhost:8081/api/tasks';
        this.tasks = [];
        this.currentFilter = 'all';
        
        // Mock data as fallback
        this.mockTasks = [
            // ...existing mock data
        ];
    }

    // Get auth header with JWT token
    getAuthHeader() {
        const token = localStorage.getItem('token');
        return {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    }

    // Fetch all tasks from API
    async fetchTasks() {
        try {
            console.log("Fetching tasks from API:", this.apiUrl);
            const response = await fetch(this.apiUrl, {
                method: 'GET',
                headers: this.getAuthHeader()
            });

            console.log("API Response status:", response.status);
            
            if (!response.ok) {
                throw new Error(`API Error: ${response.status}`);
            }

            this.tasks = await response.json();
            console.log("Tasks loaded from API:", this.tasks.length);
            return this.tasks;
        } catch (error) {
            console.error('Error loading tasks from API:', error);
            console.log('Using mock data instead');
            
            // Only use mock data during development
            if (window.location.hostname === 'localhost') {
                this.tasks = [...this.mockTasks];
                return this.tasks;
            } else {
                // In production, don't use mock data
                return [];
            }
        }
    }
    
    // Create new task
// Create new task
    async createTask(taskData) {
        console.log("Creating task with data:", taskData);
        
        try {
            console.log("About to send POST request to:", this.apiUrl);
            
            const response = await fetch(this.apiUrl, {
                method: 'POST',
                headers: this.getAuthHeader(),
                body: JSON.stringify(taskData)
            });

            console.log("Response received:", response.status, response.statusText);
            
            if (response.status === 401) {
                console.log("Unauthorized response detected");
                alert("Your session has expired. Please log in again.");
                window.location.href = 'login.html';
                return null;
            }

            if (!response.ok) {
                console.log("Non-OK response:", response.status);
                const errorText = await response.text();
                console.log("Error details:", errorText);
                throw new Error(`Failed to create task: ${response.status}`);
            }
            
            // Get response as text first
            const responseText = await response.text();
            console.log("Response text:", responseText);
            
            // Parse JSON only if we have content
            let newTask;
            if (responseText.trim()) {
                try {
                    newTask = JSON.parse(responseText);
                    console.log("Task created on server:", newTask);
                    this.tasks.push(newTask); // Add to array ONLY ONCE
                    return newTask;
                } catch (e) {
                    console.error("Error parsing response JSON:", e);
                    throw e;
                }
            } else {
                throw new Error("Empty response from server");
            }

        } catch (error) {
            console.error('Network error creating task:', error);
            
            // ONLY create mock task if it's a network error, not auth error
            if (error.message.includes("Failed to fetch")) {
                const mockTask = {
                    id: Math.floor(Math.random() * 1000),
                    name: taskData.name,
                    description: taskData.description, 
                    subject: taskData.subject,
                    duration: taskData.duration_minutes,
                    difficulty: taskData.difficulty,
                    deadline: taskData.deadline,
                    completed: false
                };
                console.log("Creating mock task due to network error:", mockTask);
                this.tasks.push(mockTask);
                return mockTask;
            }
            throw error; // Rethrow other errors
        }
    }

    // Get task by id
    async getTaskById(taskId) {
        try {
            const response = await fetch(`${this.apiUrl}/${taskId}`, {
                method: 'GET',
                headers: this.getAuthHeader()
            });

            if (!response.ok) {
                throw new Error('Failed to fetch task');
            }

            const task = await response.json();
            return task;
        } catch (error) {
            console.error('Error fetching task:', error);
            // Return task from local collection as fallback
            return this.tasks.find(t => t.id === parseInt(taskId));
        }
    }

    // Update Task
    async updateTask(taskId, taskData){
        try{
            const response = await fetch(`${this.apiUrl}/${taskId}`, {
                method: 'PUT',
                headers: this.getAuthHeader(),
                body: JSON.stringify(taskData)
            });

            if (!response.ok) {
                throw new Error('Failed to update task');
            }

            const updatedTask = await response.json();
            const index = this.tasks.findIndex(task => task.id === taskId);
            if (index !== -1) {
                this.tasks[index] = updatedTask;
            }
            return updatedTask;
        } catch (error) {
            console.error('Error updating task:', error);
            console.log('Using mock data instead');
            
            // Update local task as fallback
            const index = this.tasks.findIndex(task => task.id === parseInt(taskId));
            if (index !== -1) {
                this.tasks[index] = {
                    ...this.tasks[index],
                    ...taskData,
                    id: parseInt(taskId)
                };
                return this.tasks[index];
            }
            return null;
        }
    }

    // Delete Task
    async deleteTask(taskId) {
        try {
            const response = await fetch(`${this.apiUrl}/${taskId}`, {
                method: 'DELETE',
                headers: this.getAuthHeader()
            });

            if (!response.ok) {
                throw new Error('Failed to delete task');
            }

            this.tasks = this.tasks.filter(task => task.id !== parseInt(taskId));
            return true;
        } catch (error) {
            console.error('Error deleting task:', error);
            console.log('Using mock data instead');
            
            // Delete from local collection as fallback
            this.tasks = this.tasks.filter(task => task.id !== parseInt(taskId));
            return true;
        }
    }

    // Toggle task completion
    async toggleTaskCompletion(taskId)
    {
        try {
            const taskIdInt = parseInt(taskId);
            const task = this.tasks.find(t => t.id === taskIdInt);
            if (!task) throw new Error('Task not found');
            
            const updatedTask = { ...task, completed: !task.completed };
            
            const response = await fetch(`${this.apiUrl}/${taskIdInt}/toggle`, {
                method: 'PATCH',
                headers: this.getAuthHeader()
            });

            if (!response.ok) {
                throw new Error('Failed to toggle task completion');
            }

            const responseTask = await response.json();
            const index = this.tasks.findIndex(t => t.id === taskIdInt);
            if (index !== -1) {
                this.tasks[index] = responseTask;
            }
            return responseTask;
        } catch (error) {
            console.error('Error toggling task completion:', error);
            console.log('Using mock data instead');
            
            // Toggle locally as fallback
            const index = this.tasks.findIndex(t => t.id === parseInt(taskId));
            if (index !== -1) {
                this.tasks[index].completed = !this.tasks[index].completed;
                return this.tasks[index];
            }
            return null;
        }
    }

    // Get filtered tasks
    getFilteredTasks(filter) {
        this.currentFilter = filter || this.currentFilter;

        return this.tasks.filter(task => {
            if (this.currentFilter === 'all') return true;
            if (this.currentFilter === 'today') {
                const today = new Date().toDateString();
                return new Date(task.deadline).toDateString() === today;
            }
            if (this.currentFilter === 'week') {
                const weekFromNow = new Date(new Date().getTime() + 7 * 24 * 60 * 60 * 1000);
                return new Date(task.deadline).getTime() <= weekFromNow.getTime();
            }
            if (this.currentFilter === 'overdue') {
                return new Date(task.deadline) < new Date() && !task.completed;
            }
            return true;
        });
    }

    // Get task stats
    getTaskStats() {
        const completedTasks = this.tasks.filter(task => task.completed).length;
        const totalTasks = this.tasks.length;
        const productivityScore = totalTasks > 0 ? Math.round((completedTasks / totalTasks) * 100) : 0;

        return {
            completedTasks,
            totalTasks,
            productivityScore
        };
    }
}