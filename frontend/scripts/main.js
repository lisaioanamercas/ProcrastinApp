// Global variables
let currentUser = null;

// DOM Elements
const welcomeTitleEl = document.getElementById('welcome-title');
const userNameEl = document.getElementById('user-name');
const userAvatarEl = document.getElementById('user-avatar');

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

function checkAuth() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const email = localStorage.getItem('email');
    
    if (!token) {
        // Redirect to login if no token
        window.location.href = 'login.html';
        return;
    }
    
    // Use stored user data
    currentUser = {
        username: username,
        email: email,
        name: username
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
    // Create service instances
    const taskService = new TaskService();
    const subjectService = new SubjectService();

    
    // Create and initialize UI handlers
    const taskUI = new TaskUI(taskService, subjectService);
    taskUI.loadTasks();
    
    // Update stats
    updateStats();
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