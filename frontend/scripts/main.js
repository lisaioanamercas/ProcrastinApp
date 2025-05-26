// Global variables
const API_URL = 'http://localhost:8081/api';
let currentUser = null;
let tasks = [];
let habits = [];
let currentFilter = 'all';

// DOM Elements
const welcomeTitleEl = document.getElementById('welcome-title');
const welcomeSubtitleEl = document.getElementById('welcome-subtitle');
const userNameEl = document.getElementById('user-name');
const userAvatarEl = document.getElementById('user-avatar');
const tasksCompletedEl = document.getElementById('tasks-completed');
const tasksTotalEl = document.getElementById('tasks-total');
const habitsCompletedEl = document.getElementById('habits-completed');
const productivityScoreEl = document.getElementById('productivity-score');
const taskListEl = document.getElementById('task-list');
const habitListEl = document.getElementById('habit-list');
const filterButtons = document.querySelectorAll('.filter-btn');

// Theme toggle
const themeBtn = document.getElementById('theme-btn');
if (themeBtn) {
    themeBtn.addEventListener('click', toggleTheme);
}

function toggleTheme() {
    document.body.classList.toggle('dark-theme');
    const isDarkTheme = document.body.classList.contains('dark-theme');
    if (themeBtn) {
        themeBtn.innerHTML = isDarkTheme ? '<i class="fas fa-sun"></i>' : '<i class="fas fa-moon"></i>';
    }
    localStorage.setItem('darkTheme', isDarkTheme);
}

// Check saved theme preference
if (localStorage.getItem('darkTheme') === 'true') {
    document.body.classList.add('dark-theme');
    if (themeBtn) {
        themeBtn.innerHTML = '<i class="fas fa-sun"></i>';
    }
}

// Authentication Check - Simplified
function checkAuth() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const email = localStorage.getItem('email');
    
    if (!token) {
        // No token found, redirect to login
        window.location.href = 'login.html';
        return;
    }
    
    // Use stored user data
    currentUser = {
        username: username,
        email: email,
        name: username // Use username as name
    };
    
    updateUserInterface();
    loadDashboard();
}

// Update UI with user data
function updateUserInterface() {
    if (!currentUser) return;
    
    const displayName = currentUser.name || currentUser.username;
    const firstName = displayName.split('.')[0] || displayName.split(' ')[0] || displayName;
    
    if (welcomeTitleEl) {
        welcomeTitleEl.textContent = `Welcome back, ${firstName.charAt(0).toUpperCase() + firstName.slice(1)}!`;
    }
    if (userNameEl) {
        userNameEl.textContent = displayName;
    }
    if (userAvatarEl) {
        userAvatarEl.textContent = displayName.charAt(0).toUpperCase();
    }
}

// Load all dashboard data
function loadDashboard() {
    loadTasks();
    loadHabits();
    updateStats();
}

// Task Management - Use mock data for now
function loadTasks() {
    console.log('Loading tasks...');
    
    // Use mock data since backend endpoints don't exist yet
    tasks = [
        {
            id: 1,
            name: 'Complete project proposal',
            description: 'Finish the requirements document',
            subject: 'Computer Science',
            duration: 120,
            difficulty: 'hard',
            deadline: new Date(new Date().getTime() + 86400000).toISOString(),
            completed: false
        },
        {
            id: 2,
            name: 'Study for exam',
            description: 'Review chapters 1-5',
            subject: 'Mathematics',
            duration: 90,
            difficulty: 'medium',
            deadline: new Date(new Date().getTime() + 172800000).toISOString(),
            completed: true
        }
    ];
    
    renderTasks();
    updateProgressSummary();
}

function renderTasks() {
    if (!taskListEl) return;
    
    const filteredTasks = tasks.filter(task => {
        if (currentFilter === 'all') return true;
        if (currentFilter === 'today') {
            const today = new Date().toDateString();
            return new Date(task.deadline).toDateString() === today;
        }
        if (currentFilter === 'week') {
            const weekFromNow = new Date(new Date().getTime() + 7 * 24 * 60 * 60 * 1000);
            return new Date(task.deadline) <= weekFromNow;
        }
        if (currentFilter === 'overdue') {
            return new Date(task.deadline) < new Date() && !task.completed;
        }
        return true;
    });

    taskListEl.innerHTML = filteredTasks.map(task => `
        <div class="task-item ${task.completed ? 'completed' : ''}">
            <div class="task-checkbox ${task.completed ? 'checked' : ''}" data-id="${task.id}"></div>
            <div class="task-details">
                <div class="task-name">${task.name}</div>
                <div class="task-meta">
                    <span><i class="fas fa-book"></i> ${task.subject}</span>
                    <span><i class="fas fa-clock"></i> ${task.duration} min</span>
                    <span><i class="fas fa-calendar"></i> ${new Date(task.deadline).toLocaleDateString()}</span>
                    <span><i class="fas fa-chart-line"></i> ${task.difficulty}</span>
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
        checkbox.addEventListener('click', () => toggleTaskCompletion(checkbox.dataset.id));
    });

    document.querySelectorAll('.edit-task').forEach(btn => {
        btn.addEventListener('click', () => console.log('Edit task:', btn.dataset.id));
    });

    document.querySelectorAll('.delete-task').forEach(btn => {
        btn.addEventListener('click', () => deleteTask(btn.dataset.id));
    });
}

function updateProgressSummary() {
    const completedTasks = tasks.filter(task => task.completed).length;
    const totalTasks = tasks.length;
    const completedHabits = habits.filter(habit => habit.completed).length;
    const productivityScore = totalTasks > 0 ? Math.round((completedTasks / totalTasks) * 100) : 0;

    if (tasksCompletedEl) tasksCompletedEl.textContent = completedTasks;
    if (tasksTotalEl) tasksTotalEl.textContent = totalTasks;
    if (habitsCompletedEl) habitsCompletedEl.textContent = completedHabits;
    if (productivityScoreEl) productivityScoreEl.textContent = `${productivityScore}%`;
}

function toggleTaskCompletion(taskId) {
    const task = tasks.find(t => t.id === parseInt(taskId));
    if (task) {
        task.completed = !task.completed;
        renderTasks();
        updateProgressSummary();
    }
}

function deleteTask(taskId) {
    tasks = tasks.filter(t => t.id !== parseInt(taskId));
    renderTasks();
    updateProgressSummary();
}

// Habit Management
function loadHabits() {
    habits = [
        {
            id: 1,
            name: 'Morning Reading',
            subject: 'Literature',
            time: '08:00',
            daysOfWeek: ['monday', 'wednesday', 'friday'],
            completed: false,
            iconName: 'book'
        },
        {
            id: 2,
            name: 'Exercise',
            subject: 'Health',
            time: '18:00',
            daysOfWeek: ['monday', 'tuesday', 'wednesday', 'thursday', 'friday'],
            completed: true,
            iconName: 'dumbbell'
        }
    ];
    renderHabits();
}

function renderHabits() {
    if (!habitListEl) return;
    
    habitListEl.innerHTML = habits.map(habit => `
        <div class="habit-item">
            <div class="habit-info">
                <div class="habit-icon">
                    <i class="fas fa-${habit.iconName}"></i>
                </div>
                <div class="habit-details">
                    <div class="task-name">${habit.name}</div>
                    <div class="task-meta">
                        <span><i class="fas fa-clock"></i> ${habit.time}</span>
                        <span><i class="fas fa-calendar-day"></i> ${habit.daysOfWeek.join(', ')}</span>
                    </div>
                </div>
            </div>
            <div class="habit-actions">
                <div class="habit-toggle ${habit.completed ? 'checked' : ''}" data-id="${habit.id}"></div>
                <button class="action-icon delete-habit" data-id="${habit.id}"><i class="fas fa-trash-alt"></i></button>
            </div>
        </div>
    `).join('');

    // Add event listeners
    document.querySelectorAll('.habit-toggle').forEach(toggle => {
        toggle.addEventListener('click', () => toggleHabitCompletion(toggle.dataset.id));
    });

    document.querySelectorAll('.delete-habit').forEach(btn => {
        btn.addEventListener('click', () => deleteHabit(btn.dataset.id));
    });
}

function toggleHabitCompletion(habitId) {
    const habit = habits.find(h => h.id === parseInt(habitId));
    if (habit) {
        habit.completed = !habit.completed;
        renderHabits();
        updateProgressSummary();
    }
}

function deleteHabit(habitId) {
    habits = habits.filter(h => h.id !== parseInt(habitId));
    renderHabits();
    updateProgressSummary();
}

// Statistics - Mock data
function updateStats() {
    const elements = {
        'weekly-completed': '12',
        'avg-difficulty': '2.3',
        'avg-duration': '1.5h',
        'habit-streak': '7'
    };
    
    Object.entries(elements).forEach(([id, value]) => {
        const element = document.getElementById(id);
        if (element) element.textContent = value;
    });
}

// Filter buttons
filterButtons.forEach(btn => {
    btn.addEventListener('click', () => {
        filterButtons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        currentFilter = btn.dataset.filter;
        renderTasks();
    });
});

// Logout functionality
const logoutBtn = document.getElementById('logout-btn');
if (logoutBtn) {
    logoutBtn.addEventListener('click', function() {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('email');
        window.location.href = 'login.html';
    });
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    checkAuth();
});