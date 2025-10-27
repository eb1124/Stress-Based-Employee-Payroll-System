// Employee Dashboard functionality
class EmployeeDashboard {
    constructor() {
        this.currentSection = 'dashboard';
        this.stressChart = null;
        this.init();
    }

    init() {
        this.setupNavigation();
        this.loadDashboardData();
        this.setupEventListeners();
    }

    setupNavigation() {
        const menuItems = document.querySelectorAll('.sidebar-menu a[data-section]');
        menuItems.forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const section = e.currentTarget.getAttribute('data-section');
                this.showSection(section);
            });
        });
    }

    showSection(sectionName) {
        // Hide all sections
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });

        // Remove active class from menu items
        document.querySelectorAll('.sidebar-menu a').forEach(item => {
            item.classList.remove('active');
        });

        // Show selected section
        const targetSection = document.getElementById(sectionName);
        if (targetSection) {
            targetSection.classList.add('active');
        }

        // Add active class to menu item
        const activeMenuItem = document.querySelector(`[data-section="${sectionName}"]`);
        if (activeMenuItem) {
            activeMenuItem.classList.add('active');
        }

        // Update page title
        const pageTitle = document.getElementById('pageTitle');
        if (pageTitle) {
            pageTitle.textContent = this.getSectionTitle(sectionName);
        }

        this.currentSection = sectionName;

        // Load section-specific data
        this.loadSectionData(sectionName);
    }

    getSectionTitle(sectionName) {
        const titles = {
            'dashboard': 'Dashboard',
            'profile': 'My Profile',
            'payslips': 'My Payslips',
            'stress-tracking': 'Stress Tracking',
            'wellness': 'Wellness Tips',
            'reminders': 'My Reminders'
        };
        return titles[sectionName] || 'Dashboard';
    }

    async loadSectionData(sectionName) {
        switch (sectionName) {
            case 'dashboard':
                await this.loadDashboardData();
                break;
            case 'profile':
                await this.loadProfileData();
                break;
            case 'payslips':
                await this.loadPayslipsData();
                break;
            case 'stress-tracking':
                await this.loadStressTrackingData();
                break;
            case 'wellness':
                await this.loadWellnessData();
                break;
            case 'reminders':
                await this.loadRemindersData();
                break;
        }
    }

    async loadDashboardData() {
        try {
            // Load stress dashboard data
            const stressResult = await authService.makeRequest('/api/employee/stress-dashboard');
            if (stressResult.success) {
                this.updateStressDashboard(stressResult.data);
            }

            // Load recent activity (mock data for now)
            this.updateRecentActivity();

        } catch (error) {
            console.error('Error loading dashboard data:', error);
        }
    }

    updateStressDashboard(data) {
        // Update current stress level
        const currentStressElement = document.getElementById('currentStressLevel');
        if (currentStressElement) {
            currentStressElement.textContent = data.currentStressLevel || '-';
        }

        // Update stress chart
        this.createStressChart(data.stressHistory || []);

        // Update stress bar
        const stressBar = document.getElementById('stressBar');
        const stressLevelText = document.getElementById('stressLevelText');
        if (stressBar && stressLevelText) {
            const stressLevel = data.currentStressLevel || 0;
            const percentage = (stressLevel / 10) * 100;
            stressBar.style.width = `${percentage}%`;
            stressLevelText.textContent = stressLevel;
        }
    }

    createStressChart(stressHistory) {
        const ctx = document.getElementById('stressChart');
        if (!ctx) return;

        // Destroy existing chart
        if (this.stressChart) {
            this.stressChart.destroy();
        }

        const labels = stressHistory.map(record => 
            `${getMonthName(record.month)} ${record.year}`
        );
        const data = stressHistory.map(record => record.stressLevel);

        this.stressChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Stress Level',
                    data: data,
                    borderColor: '#667eea',
                    backgroundColor: 'rgba(102, 126, 234, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 10,
                        ticks: {
                            stepSize: 1
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    }

    updateRecentActivity() {
        const activityList = document.getElementById('recentActivity');
        if (!activityList) return;

        // Mock recent activity data
        const activities = [
            { icon: 'fas fa-file-invoice-dollar', text: 'Generated payslip for January 2024', time: '2 hours ago' },
            { icon: 'fas fa-chart-line', text: 'Updated stress level to 6', time: '1 day ago' },
            { icon: 'fas fa-bell', text: 'Completed reminder: Submit monthly report', time: '2 days ago' },
            { icon: 'fas fa-heart', text: 'Viewed wellness tips', time: '3 days ago' }
        ];

        activityList.innerHTML = activities.map(activity => `
            <div class="list-item">
                <div class="activity-content">
                    <i class="${activity.icon}"></i>
                    <span>${activity.text}</span>
                </div>
                <span class="activity-time">${activity.time}</span>
            </div>
        `).join('');
    }

    async loadProfileData() {
        try {
            const result = await authService.makeRequest('/api/employee/profile');
            if (result.success) {
                this.updateProfileDisplay(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading profile data:', error);
            showError('Failed to load profile data');
        }
    }

    updateProfileDisplay(data) {
        const profile = data.profile;
        const user = data.user;

        document.getElementById('profileFullName').textContent = user.fullName;
        document.getElementById('profileEmail').textContent = user.email;
        document.getElementById('profilePhone').textContent = profile.phone || 'Not provided';
        document.getElementById('profileDepartment').textContent = profile.department || 'Not provided';
        document.getElementById('profilePosition').textContent = profile.position || 'Not provided';
        document.getElementById('profileSalary').textContent = formatCurrency(profile.baseSalary);
    }

    async loadPayslipsData() {
        try {
            const result = await authService.makeRequest('/api/employee/payslips');
            if (result.success) {
                this.updatePayslipsDisplay(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading payslips data:', error);
            showError('Failed to load payslips data');
        }
    }

    updatePayslipsDisplay(payslips) {
        const payslipsList = document.getElementById('payslipsList');
        if (!payslipsList) return;

        if (payslips.length === 0) {
            payslipsList.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-file-invoice-dollar"></i>
                    <h3>No Payslips Found</h3>
                    <p>Generate your first payslip to get started.</p>
                </div>
            `;
            return;
        }

        payslipsList.innerHTML = payslips.map(payslip => `
            <div class="list-item">
                <div class="payslip-info">
                    <h4>${getMonthName(payslip.month)} ${payslip.year}</h4>
                    <p>Base Salary: ${formatCurrency(payslip.baseSalary)}</p>
                    <p>Final Salary: ${formatCurrency(payslip.finalSalary)}</p>
                    ${payslip.unpaidLeaveDeductions > 0 ? 
                        `<p class="deduction">Deductions: ${formatCurrency(payslip.unpaidLeaveDeductions)}</p>` : 
                        ''
                    }
                </div>
                <div class="payslip-actions">
                    <span class="payslip-date">${formatDate(payslip.generatedAt)}</span>
                </div>
            </div>
        `).join('');
    }

    async loadStressTrackingData() {
        try {
            const result = await authService.makeRequest('/api/employee/stress-dashboard');
            if (result.success) {
                this.updateStressTrackingDisplay(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading stress tracking data:', error);
            showError('Failed to load stress tracking data');
        }
    }

    updateStressTrackingDisplay(data) {
        // Update stress level display
        const stressLevelText = document.getElementById('stressLevelText');
        const avgStressLevel = document.getElementById('avgStressLevel');
        const totalOvertimeHours = document.getElementById('totalOvertimeHours');

        if (stressLevelText) {
            stressLevelText.textContent = data.currentStressLevel || '-';
        }

        if (avgStressLevel) {
            avgStressLevel.textContent = data.averageStressLevel || '-';
        }

        if (totalOvertimeHours) {
            const totalHours = data.stressHistory?.reduce((sum, record) => sum + record.overtimeHours, 0) || 0;
            totalOvertimeHours.textContent = totalHours;
        }

        // Update stress history
        const stressHistory = document.getElementById('stressHistory');
        if (stressHistory) {
            if (data.stressHistory && data.stressHistory.length > 0) {
                stressHistory.innerHTML = data.stressHistory.map(record => `
                    <div class="stress-record">
                        <div class="stress-record-header">
                            <h4>${getMonthName(record.month)} ${record.year}</h4>
                            <span class="stress-level-badge stress-level-${record.stressLevel}">
                                Level ${record.stressLevel}
                            </span>
                        </div>
                        <div class="stress-record-details">
                            <p><strong>Overtime Hours:</strong> ${record.overtimeHours}</p>
                            ${record.overtimeReason ? `<p><strong>Reason:</strong> ${record.overtimeReason}</p>` : ''}
                        </div>
                    </div>
                `).join('');
            } else {
                stressHistory.innerHTML = `
                    <div class="empty-state">
                        <i class="fas fa-chart-line"></i>
                        <h3>No Stress Records</h3>
                        <p>Add your first stress record to start tracking.</p>
                    </div>
                `;
            }
        }
    }

    async loadWellnessData() {
        try {
            const result = await authService.makeRequest('/api/employee/wellness-tips');
            if (result.success) {
                this.updateWellnessDisplay(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading wellness data:', error);
            showError('Failed to load wellness tips');
        }
    }

    updateWellnessDisplay(tips) {
        const wellnessTips = document.getElementById('wellnessTips');
        if (!wellnessTips) return;

        wellnessTips.innerHTML = tips.map(tip => `
            <div class="wellness-tip">
                <h4>${tip.title}</h4>
                <p>${tip.description}</p>
            </div>
        `).join('');
    }

    async loadRemindersData() {
        try {
            const result = await authService.makeRequest('/api/employee/reminders');
            if (result.success) {
                this.updateRemindersDisplay(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading reminders data:', error);
            showError('Failed to load reminders');
        }
    }

    updateRemindersDisplay(reminders) {
        const remindersList = document.getElementById('remindersList');
        if (!remindersList) return;

        if (reminders.length === 0) {
            remindersList.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-bell"></i>
                    <h3>No Reminders</h3>
                    <p>Add your first reminder to stay organized.</p>
                </div>
            `;
            return;
        }

        remindersList.innerHTML = reminders.map(reminder => `
            <div class="list-item">
                <div class="reminder-content">
                    <input type="checkbox" ${reminder.isCompleted ? 'checked' : ''} 
                           onchange="updateReminder(${reminder.id}, this.checked)">
                    <span class="${reminder.isCompleted ? 'completed' : ''}">${reminder.reminderText}</span>
                </div>
                <span class="reminder-date">${formatDate(reminder.createdAt)}</span>
            </div>
        `).join('');
    }

    setupEventListeners() {
        // Profile edit functionality
        const editProfileBtn = document.getElementById('editProfileBtn');
        const editProfileForm = document.getElementById('editProfileForm');
        const cancelEditBtn = document.getElementById('cancelEditBtn');
        const profileForm = document.getElementById('profileForm');

        if (editProfileBtn) {
            editProfileBtn.addEventListener('click', () => {
                editProfileForm.style.display = 'block';
                this.populateEditForm();
            });
        }

        if (cancelEditBtn) {
            cancelEditBtn.addEventListener('click', () => {
                editProfileForm.style.display = 'none';
            });
        }

        if (profileForm) {
            profileForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                await this.updateProfile();
            });
        }

        // Payslip generation
        const generatePayslipBtn = document.getElementById('generatePayslipBtn');
        const generatePayslipForm = document.getElementById('generatePayslipForm');

        if (generatePayslipBtn) {
            generatePayslipBtn.addEventListener('click', () => {
                openModal('generatePayslipModal');
            });
        }

        if (generatePayslipForm) {
            generatePayslipForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                await this.generatePayslip();
            });
        }

        // Stress record addition
        const addStressRecordBtn = document.getElementById('addStressRecordBtn');
        const addStressRecordForm = document.getElementById('addStressRecordForm');

        if (addStressRecordBtn) {
            addStressRecordBtn.addEventListener('click', () => {
                openModal('addStressRecordModal');
            });
        }

        if (addStressRecordForm) {
            addStressRecordForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                await this.addStressRecord();
            });
        }

        // Reminder addition
        const addReminderBtn = document.getElementById('addReminderBtn');
        const addReminderForm = document.getElementById('addReminderForm');

        if (addReminderBtn) {
            addReminderBtn.addEventListener('click', () => {
                openModal('addReminderModal');
            });
        }

        if (addReminderForm) {
            addReminderForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                await this.addReminder();
            });
        }
    }

    async populateEditForm() {
        try {
            const result = await authService.makeRequest('/api/employee/profile');
            if (result.success) {
                const profile = result.data.profile;
                document.getElementById('editPhone').value = profile.phone || '';
                document.getElementById('editDepartment').value = profile.department || '';
                document.getElementById('editPosition').value = profile.position || '';
            }
        } catch (error) {
            console.error('Error populating edit form:', error);
        }
    }

    async updateProfile() {
        try {
            const formData = {
                phone: document.getElementById('editPhone').value,
                department: document.getElementById('editDepartment').value,
                position: document.getElementById('editPosition').value
            };

            const result = await authService.makeRequest('/api/employee/profile', {
                method: 'PUT',
                body: JSON.stringify(formData)
            });

            if (result.success) {
                showSuccess('Profile updated successfully!');
                document.getElementById('editProfileForm').style.display = 'none';
                await this.loadProfileData();
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error updating profile:', error);
            showError('Failed to update profile');
        }
    }

    async generatePayslip() {
        try {
            const formData = {
                month: parseInt(document.getElementById('payslipMonth').value),
                year: parseInt(document.getElementById('payslipYear').value)
            };

            const result = await authService.makeRequest('/api/employee/payslips/generate', {
                method: 'POST',
                body: JSON.stringify(formData)
            });

            if (result.success) {
                showSuccess('Payslip generated successfully!');
                closeModal('generatePayslipModal');
                await this.loadPayslipsData();
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error generating payslip:', error);
            showError('Failed to generate payslip');
        }
    }

    async addStressRecord() {
        try {
            const formData = {
                month: parseInt(document.getElementById('stressMonth').value),
                year: parseInt(document.getElementById('stressYear').value),
                overtimeHours: parseInt(document.getElementById('overtimeHours').value),
                overtimeReason: document.getElementById('overtimeReason').value
            };

            const result = await authService.makeRequest('/api/employee/stress-record', {
                method: 'POST',
                body: JSON.stringify(formData)
            });

            if (result.success) {
                showSuccess('Stress record added successfully!');
                closeModal('addStressRecordModal');
                await this.loadStressTrackingData();
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error adding stress record:', error);
            showError('Failed to add stress record');
        }
    }

    async addReminder() {
        try {
            const formData = {
                reminderText: document.getElementById('reminderText').value
            };

            const result = await authService.makeRequest('/api/employee/reminders', {
                method: 'POST',
                body: JSON.stringify(formData)
            });

            if (result.success) {
                showSuccess('Reminder added successfully!');
                closeModal('addReminderModal');
                await this.loadRemindersData();
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error adding reminder:', error);
            showError('Failed to add reminder');
        }
    }
}

// Global function for updating reminders
async function updateReminder(reminderId, isCompleted) {
    try {
        const result = await authService.makeRequest(`/api/employee/reminders/${reminderId}`, {
            method: 'PUT',
            body: JSON.stringify({ isCompleted })
        });

        if (result.success) {
            showSuccess('Reminder updated successfully!');
        } else {
            showError(result.error);
        }
    } catch (error) {
        console.error('Error updating reminder:', error);
        showError('Failed to update reminder');
    }
}

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    if (window.location.pathname.includes('employee-dashboard')) {
        new EmployeeDashboard();
    }
});
