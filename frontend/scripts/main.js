// Global variables
let currentUser = null;
const taskService = new TaskService();
const subjectService = new SubjectService();
const habitService = new HabitService();
const heatmapService = new HeatmapService();
const statsDownloadService = new StatsDownloadService();
const subjectStatsService = new SubjectStatsService();

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

// function checkAuth() {
//     const token = localStorage.getItem('token');
//     const username = localStorage.getItem('username');
//     const email = localStorage.getItem('email');
    
//     if (!token) {
//         // Redirect to login if no token
//         window.location.href = 'login.html';
//         return;
//     }
    
//     // Use stored user data
//     currentUser = {
//         username: username,
//         email: email,
//         name: username
//     };
    
//     updateUserInterface();
//     loadDashboard();
// }

function checkAuth() {
    // First check for OAuth parameters in URL
    const urlParams = new URLSearchParams(window.location.search);
    const oauthToken = urlParams.get('token');
    const oauthUsername = urlParams.get('username');
    const oauthEmail = urlParams.get('email');
    const isOAuth = urlParams.get('oauth');
    
    if (isOAuth === 'true' && oauthToken && oauthUsername && oauthEmail) {
        // Store OAuth login data
        localStorage.setItem('token', oauthToken);
        localStorage.setItem('username', oauthUsername);
        localStorage.setItem('email', oauthEmail);
        
        // Clean URL by removing parameters
        const url = new URL(window.location);
        url.search = '';
        window.history.replaceState({}, document.title, url.pathname);
        
        console.log('OAuth login successful for:', oauthUsername);
        
        // Set current user from OAuth data
        currentUser = {
            username: oauthUsername,
            email: oauthEmail,
            name: oauthUsername
        };
        
        updateUserInterface();
        loadDashboard();
        return;
    }
    
    // Existing token check logic
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

async function loadDashboard() {
    console.log('Loading dashboard...');
     
    console.log('HeatmapService created:', heatmapService);
    
    const habitUI = new habitUi(habitService);
    const heatmapUI = new HeatmapUI(heatmapService);
    
    console.log('HeatmapUI created:', heatmapUI);
    
    // Para acces global la submit
    window.habitServiceInstance = habitService;
    window.habitUIInstance = habitUI;
    window.heatmapUIInstance = heatmapUI;

    // Create and initialize UI handlers
    const taskUI = new TaskUI(taskService, subjectService);
    await taskUI.loadTasks();
    await habitUI.loadHabits();
    
    console.log('Initializing heatmap...');
    heatmapUI.init();

    // Update stats
    updateStats();
}



async function updateStats() {
  
    try {
        console.log('Fetching stats...');
        
        const stats = new StudyStats(taskService.tasks, habitService.habits);
        const statsFetch = await stats.fetchStudyStats();
        
        console.log('Stats received:', statsFetch);

        const elements = {
            'weekly-completed': stats.getTasksThisWeek(),
            'avg-difficulty': statsFetch.avgDifficulty.toFixed(1),
            'avg-duration': statsFetch.avgDuration.toFixed(1),
            'current-streak': statsFetch.currentStreak || 0,
            'habit-streak': statsFetch.longestStreak || 0
        };
        
        console.log('Updating elements:', elements);
        
        Object.entries(elements).forEach(([id, value]) => {
            const element = document.getElementById(id);
            if (element) {
                element.textContent = value;
                console.log(`Updated ${id} to ${value}`);
            }
        });
    } catch (error) {
        console.error('Failed to update stats:', error);
        // Don't update with 0 values on error, keep existing values
    }
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
    
    const statsBtn = document.getElementById('stats-btn');
    if (statsBtn) {
        statsBtn.addEventListener('click', async () => {
            try {
                const modal = document.getElementById('stats-modal');
                const loading = document.getElementById('stats-loading');
                const chartsDiv = document.getElementById('stats-charts');
                
                // Show modal
                modal.style.display = 'flex';
                
                // Show loading, hide charts
                loading.style.display = 'block';
                chartsDiv.style.display = 'none';
                
                // Fetch and display subject stats
                const data = await subjectStatsService.fetchSubjectStats();
                subjectStatsService.displaySubjectStats(data);
                
            } catch (error) {
                console.error('Error loading subject stats:', error);
                alert('Failed to load subject statistics. Please try again.');
                
                // Hide modal on error
                document.getElementById('stats-modal').style.display = 'none';
            }
        });
    }
    
    // Add close modal functionality
    const closeStatsModal = document.getElementById('close-stats-modal');
    if (closeStatsModal) {
        closeStatsModal.addEventListener('click', () => {
            document.getElementById('stats-modal').style.display = 'none';
        });
    }
    
    // Close modal when clicking outside
    const statsModal = document.getElementById('stats-modal');
    if (statsModal) {
        statsModal.addEventListener('click', (e) => {
            if (e.target === statsModal) {
                statsModal.style.display = 'none';
            }
        });
    }

    // Add download button event listener
    const downloadBtn = document.getElementById('download-stats-btn');
    if (downloadBtn) {
        downloadBtn.addEventListener('click', async () => {
            try {
                downloadBtn.disabled = true;
                downloadBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Downloading...';
                
                await statsDownloadService.downloadStats();
                
                // Reset button
                downloadBtn.disabled = false;
                downloadBtn.innerHTML = '<i class="fas fa-download"></i> Download';
            } catch (error) {
                alert('Failed to download stats. Please try again.');
                downloadBtn.disabled = false;
                downloadBtn.innerHTML = '<i class="fas fa-download"></i> Download';
            }
        });
    }
});