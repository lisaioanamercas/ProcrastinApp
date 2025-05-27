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
function togglePassword(inputId) {
    const passwordInput = document.getElementById(inputId);
    const iconId = inputId === 'password' ? 'password-icon' : 'confirm-password-icon';
    const passwordIcon = document.getElementById(iconId);
    
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

// Password Strength Checker
function checkPasswordStrength(password) {
    let strength = 0;

    if (password.length >= 8) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;

    const strengthFill = document.getElementById('strength-fill');
    const strengthText = document.getElementById('strength-text');

    switch (strength) {
        case 0:
        case 1:
            strengthFill.className = 'strength-fill weak';
            strengthText.textContent = 'Weak password';
            break;
        case 2:
            strengthFill.className = 'strength-fill fair';
            strengthText.textContent = 'Fair password';
            break;
        case 3:
            strengthFill.className = 'strength-fill good';
            strengthText.textContent = 'Good password';
            break;
        case 4:
        case 5:
            strengthFill.className = 'strength-fill strong';
            strengthText.textContent = 'Strong password';
            break;
    }
    strengthFill.style.width = (strength * 25) + '%';

    if (password.length === 0) {
        strengthFill.style.width = '0%';
        strengthText.textContent = 'Password strength will appear here';
    }
}

// Password validation
function validatePasswords() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (password !== confirmPassword) {
        return false;
    }
    return true;
}

// Form submission
document.querySelector('.register-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const registerBtn = document.getElementById('registerBtn');
    const messageDiv = document.getElementById('message');
    
    // Validate passwords match
    if (!validatePasswords()) {
        messageDiv.textContent = 'Passwords do not match!';
        messageDiv.className = 'message error show';
        return;
    }
    
    // Add loading state
    registerBtn.classList.add('loading');
    registerBtn.disabled = true;
    
    // Hide existing messages
    messageDiv.classList.remove('show');
    
    // Get form data
    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    // Create username from first and last name
    const username = `${firstName.toLowerCase()}.${lastName.toLowerCase()}`;
    
    try {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                email: email,
                password: password,
                firstName: firstName,
                lastName: lastName
            })
        });
        
        const data = await response.text();
        
        if (response.ok) {
            messageDiv.textContent = 'Registration successful! Redirecting to login...';
            messageDiv.className = 'message success show';
            
            // Redirect to login page
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        } else {
            messageDiv.textContent = data || 'Registration failed. Please try again.';
            messageDiv.className = 'message error show';
        }
    } catch (error) {
        console.error('Registration error:', error);
        messageDiv.textContent = 'Connection error. Please try again.';
        messageDiv.className = 'message error show';
    } finally {
        // Remove loading state
        registerBtn.classList.remove('loading');
        registerBtn.disabled = false;
    }
});

// Check saved theme preference
if (localStorage.getItem('darkTheme') === 'true') {
    document.body.classList.add('dark-theme');
    document.getElementById('theme-icon').classList.remove('fa-moon');
    document.getElementById('theme-icon').classList.add('fa-sun');
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('input', function () {
            checkPasswordStrength(passwordInput.value);
        });
    }
    
    // Check if user is already logged in
    const token = localStorage.getItem('token');
    if (token) {
        window.location.href = 'home.html';
    }
});