// Authentication and API utilities
class AuthService {
    constructor() {
        this.baseURL = 'http://localhost:9091';
        this.token = localStorage.getItem('token');
        this.user = JSON.parse(localStorage.getItem('user') || 'null');
    }

    async login(username, password) {
        try {
            const response = await fetch(`${this.baseURL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });

            const data = await response.json();

            if (response.ok) {
                this.token = data.token;
                this.user = {
                    username: data.username,
                    fullName: data.fullName,
                    role: data.role
                };
                
                localStorage.setItem('token', this.token);
                localStorage.setItem('user', JSON.stringify(this.user));
                
                return { success: true, data };
            } else {
                return { success: false, error: data.error || 'Login failed' };
            }
        } catch (error) {
            return { success: false, error: 'Network error. Please try again.' };
        }
    }

    async register(username, email, fullName, password) {
        try {
            const response = await fetch(`${this.baseURL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, email, fullName, password }),
            });

            const data = await response.json();

            if (response.ok) {
                return { success: true, data };
            } else {
                return { success: false, error: data.error || 'Registration failed' };
            }
        } catch (error) {
            return { success: false, error: 'Network error. Please try again.' };
        }
    }

    logout() {
        this.token = null;
        this.user = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'index.html';
    }

    isAuthenticated() {
        return this.token && this.user;
    }

    isHR() {
        return this.user && this.user.role === 'HR';
    }

    isEmployee() {
        return this.user && this.user.role === 'EMPLOYEE';
    }

    getAuthHeaders() {
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.token}`,
        };
    }

    async makeRequest(url, options = {}) {
        const defaultOptions = {
            headers: this.getAuthHeaders(),
        };

        const mergedOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers,
            },
        };

        try {
            const response = await fetch(`${this.baseURL}${url}`, mergedOptions);
            
            if (response.status === 401) {
                this.logout();
                return { success: false, error: 'Session expired. Please login again.' };
            }

            const data = await response.json();
            
            if (response.ok) {
                return { success: true, data };
            } else {
                return { success: false, error: data.error || 'Request failed' };
            }
        } catch (error) {
            return { success: false, error: 'Network error. Please try again.' };
        }
    }
}

// Initialize auth service
const authService = new AuthService();

// Login form handling
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const registerLink = document.getElementById('registerLink');
    const registerModal = document.getElementById('registerModal');
    const closeModal = document.querySelector('.close');
    const logoutBtn = document.getElementById('logoutBtn');

    // Handle login form submission
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const errorMessage = document.getElementById('errorMessage');
            
            // Show loading state
            const submitBtn = loginForm.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Logging in...';
            submitBtn.disabled = true;

            const result = await authService.login(username, password);
            
            // Reset button state
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;

            if (result.success) {
                // Redirect based on user role
                if (result.data.role === 'HR') {
                    window.location.href = 'hr-dashboard.html';
                } else {
                    window.location.href = 'employee-dashboard.html';
                }
            } else {
                errorMessage.textContent = result.error;
                errorMessage.style.display = 'block';
            }
        });
    }

    // Handle register link click
    if (registerLink) {
        registerLink.addEventListener('click', function(e) {
            e.preventDefault();
            registerModal.style.display = 'flex';
        });
    }

    // Handle register form submission
    if (registerForm) {
        registerForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('regUsername').value;
            const email = document.getElementById('regEmail').value;
            const fullName = document.getElementById('regFullName').value;
            const password = document.getElementById('regPassword').value;
            
            // Show loading state
            const submitBtn = registerForm.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Registering...';
            submitBtn.disabled = true;

            const result = await authService.register(username, email, fullName, password);
            
            // Reset button state
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;

            if (result.success) {
                alert('Registration successful! Please login with your credentials.');
                registerModal.style.display = 'none';
                registerForm.reset();
            } else {
                alert('Registration failed: ' + result.error);
            }
        });
    }

    // Handle modal close
    if (closeModal) {
        closeModal.addEventListener('click', function() {
            registerModal.style.display = 'none';
            registerForm.reset();
        });
    }

    // Close modal when clicking outside
    if (registerModal) {
        registerModal.addEventListener('click', function(e) {
            if (e.target === registerModal) {
                registerModal.style.display = 'none';
                registerForm.reset();
            }
        });
    }

    // Handle logout
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            if (confirm('Are you sure you want to logout?')) {
                authService.logout();
            }
        });
    }

    // Check authentication on dashboard pages
    if (window.location.pathname.includes('dashboard')) {
        if (!authService.isAuthenticated()) {
            window.location.href = 'index.html';
            return;
        }

        // Update user info in header
        const userNameElement = document.getElementById('userName');
        if (userNameElement && authService.user) {
            userNameElement.textContent = `Welcome, ${authService.user.fullName}`;
        }
    }
});

// Utility functions
function showError(message, elementId = null) {
    if (elementId) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    } else {
        alert(message);
    }
}

function showSuccess(message) {
    alert(message);
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function getMonthName(monthNumber) {
    const months = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return months[monthNumber - 1] || 'Unknown';
}

// Modal utility functions
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'flex';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
        // Reset form if exists
        const form = modal.querySelector('form');
        if (form) {
            form.reset();
        }
    }
}

// Add event listeners for modal close buttons
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.close').forEach(closeBtn => {
        closeBtn.addEventListener('click', function() {
            const modal = this.closest('.modal');
            if (modal) {
                modal.style.display = 'none';
                const form = modal.querySelector('form');
                if (form) {
                    form.reset();
                }
            }
        });
    });

    // Close modal when clicking outside
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                modal.style.display = 'none';
                const form = modal.querySelector('form');
                if (form) {
                    form.reset();
                }
            }
        });
    });
});
