const API_BASE_URL = 'http://localhost:8081/api/auth'

// Theme Toggle
function toggleTheme() {
    const body = document.body;
    const themeIcon = document.getElementById('theme-icon');
    
    body.classList.toggle('dark-theme');
    
    if (body.classList.contains('dark-theme')) {
        themeIcon.classList.remove('fa-moon');
        themeIcon.classList.add('fa-sun');
        localStorage.setItem('darkTheme', 'true');
    } else {
        themeIcon.classList.remove('fa-sun');
        themeIcon.classList.add('fa-moon');
        localStorage.setItem('darkTheme', 'false');
    }
}

// Password Toggle
function togglePassword() {
    const passwordInput = document.getElementById('password');
    const passwordIcon = document.getElementById('password-icon');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        passwordIcon.classList.remove('fa-eye');
        passwordIcon.classList.add('fa-eye-slash');
    } else {
        passwordInput.type = 'password';
        passwordIcon.classList.remove('fa-eye-slash');
        passwordIcon.classList.add('fa-eye');
    }
}

// Form Submission with API Integration
document.querySelector('.login-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const loginBtn = document.getElementById('loginBtn');
    const messageDiv = document.getElementById('message');
    
    // Add loading state
    loginBtn.classList.add('loading');
    loginBtn.disabled = true;
    
    // Hide existing messages
    messageDiv.classList.remove('show');
    
    // Get form data
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    console.log('Attempting login with:', { username: email, password: '***' }); // Debug log
    
    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: email, // Backend expects username field
                password: password
            })
        });
        
        console.log('Login response status:', response.status); // Debug log
        
        if (response.ok) {
            const data = await response.json();
            console.log('Login successful:', data); // Debug log
            
            // Store JWT token
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('email', data.email);
            
            messageDiv.textContent = 'Login successful! Redirecting to dashboard...';
            messageDiv.className = 'message success show';
            
            // Redirect to dashboard (home.html, not index.html)
            setTimeout(() => {
                window.location.href = 'home.html';
            }, 1500);
        } else {
            const errorText = await response.text();
            console.error('Login failed:', errorText); // Debug log
            messageDiv.textContent = errorText || 'Invalid credentials. Please try again.';
            messageDiv.className = 'message error show';
        }
    } catch (error) {
        console.error('Login error:', error);
        messageDiv.textContent = 'Connection error. Please try again.';
        messageDiv.className = 'message error show';
    } finally {
        // Remove loading state
        loginBtn.classList.remove('loading');
        loginBtn.disabled = false;
    }
});

// ...existing code...

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is already logged in
    const token = localStorage.getItem('token');
    if (token) {
        window.location.href = 'home.html'; // Also update this redirect
    }
});

// Input Focus Effects
document.querySelectorAll('.form-input').forEach(input => {
    input.addEventListener('focus', function() {
        this.parentElement.style.transform = 'scale(1.02)';
    });
    
    input.addEventListener('blur', function() {
        this.parentElement.style.transform = 'scale(1)';
    });
});

// Social Login Handlers
document.querySelectorAll('.social-button').forEach(button => {
    button.addEventListener('click', function(e) {
        e.preventDefault();
        const provider = this.textContent.trim();
        alert(`${provider} login would be implemented here`);
    });
});

// Check saved theme preference
if (localStorage.getItem('darkTheme') === 'true') {
    document.body.classList.add('dark-theme');
    document.getElementById('theme-icon').classList.remove('fa-moon');
    document.getElementById('theme-icon').classList.add('fa-sun');
}
