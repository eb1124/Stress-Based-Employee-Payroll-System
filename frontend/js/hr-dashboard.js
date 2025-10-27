// HR Dashboard functionality
class HRDashboard {
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
            'dashboard': 'HR Dashboard',
            'employees': 'Employee Management',
            'payroll': 'Payroll Management',
            'stress-monitoring': 'Stress Monitoring',
            'attendance': 'Attendance Management'
        };
        return titles[sectionName] || 'HR Dashboard';
    }

    async loadSectionData(sectionName) {
        switch (sectionName) {
            case 'dashboard':
                await this.loadDashboardData();
                break;
            case 'employees':
                await this.loadEmployeesData();
                break;
            case 'payroll':
                await this.loadPayrollData();
                break;
            case 'stress-monitoring':
                await this.loadStressMonitoringData();
                break;
            case 'attendance':
                await this.loadAttendanceData();
                break;
        }
    }

    async loadDashboardData() {
        try {
            const result = await authService.makeRequest('/api/hr/dashboard');
            if (result.success) {
                this.updateDashboardStats(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading dashboard data:', error);
            showError('Failed to load dashboard data');
        }
    }

    updateDashboardStats(data) {
        // Update statistics cards
        document.getElementById('totalEmployees').textContent = data.statistics.totalEmployees || '-';
        document.getElementById('attendanceRate').textContent = `${data.statistics.attendanceRate || 0}%`;
        document.getElementById('highStressEmployees').textContent = data.highStressEmployees.length || '-';
        document.getElementById('recentPayslips').textContent = data.recentPayslips.length || '-';

        // Update recent payslips
        this.updateRecentPayslips(data.recentPayslips);

        // Update high stress employees
        this.updateHighStressEmployees(data.highStressEmployees);

        // Create stress chart
        this.createStressChart(data.highStressEmployees);
    }

    updateRecentPayslips(payslips) {
        const recentPayslipsList = document.getElementById('recentPayslipsList');
        if (!recentPayslipsList) return;

        if (payslips.length === 0) {
            recentPayslipsList.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-file-invoice-dollar"></i>
                    <h3>No Recent Payslips</h3>
                    <p>No payslips have been generated recently.</p>
                </div>
            `;
            return;
        }

        recentPayslipsList.innerHTML = payslips.map(payslip => `
            <div class="list-item">
                <div class="payslip-info">
                    <h4>${payslip.employeeName}</h4>
                    <p>${getMonthName(payslip.month)} ${payslip.year}</p>
                    <p class="salary">${formatCurrency(payslip.finalSalary)}</p>
                </div>
                <span class="payslip-date">${formatDate(payslip.generatedAt)}</span>
            </div>
        `).join('');
    }

    updateHighStressEmployees(employees) {
        const highStressList = document.getElementById('highStressList');
        if (!highStressList) return;

        if (employees.length === 0) {
            highStressList.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-heart"></i>
                    <h3>No High Stress Employees</h3>
                    <p>All employees are managing stress well!</p>
                </div>
            `;
            return;
        }

        highStressList.innerHTML = employees.map(employee => `
            <div class="stress-employee">
                <div class="employee-info">
                    <h4>${employee.employeeName}</h4>
                    <p>Stress Level: ${employee.stressLevel}/10</p>
                    <p>Overtime Hours: ${employee.overtimeHours}</p>
                </div>
                <div class="stress-indicator">
                    <div class="stress-bar" style="width: ${(employee.stressLevel / 10) * 100}%"></div>
                </div>
            </div>
        `).join('');
    }

    createStressChart(employees) {
        const ctx = document.getElementById('stressChart');
        if (!ctx) return;

        // Destroy existing chart
        if (this.stressChart) {
            this.stressChart.destroy();
        }

        const labels = employees.map(emp => emp.employeeName);
        const data = employees.map(emp => emp.stressLevel);

        this.stressChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Stress Level',
                    data: data,
                    backgroundColor: data.map(level => 
                        level > 7 ? '#dc3545' : level > 5 ? '#ffc107' : '#28a745'
                    ),
                    borderColor: data.map(level => 
                        level > 7 ? '#dc3545' : level > 5 ? '#ffc107' : '#28a745'
                    ),
                    borderWidth: 1
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

    async loadEmployeesData() {
        try {
            const result = await authService.makeRequest('/api/hr/employees');
            if (result.success) {
                this.updateEmployeesTable(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading employees data:', error);
            showError('Failed to load employees data');
        }
    }

    updateEmployeesTable(employees) {
        const tableBody = document.getElementById('employeesTableBody');
        if (!tableBody) return;

        if (employees.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="empty-state">
                        <i class="fas fa-users"></i>
                        <h3>No Employees Found</h3>
                        <p>No employees are registered in the system.</p>
                    </td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = employees.map(employee => `
            <tr>
                <td>${employee.fullName}</td>
                <td>${employee.email}</td>
                <td>${employee.department || 'N/A'}</td>
                <td>${employee.position || 'N/A'}</td>
                <td>${employee.baseSalary ? formatCurrency(employee.baseSalary) : 'N/A'}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="viewEmployeeDetails(${employee.id})">
                        <i class="fas fa-eye"></i> View
                    </button>
                </td>
            </tr>
        `).join('');
    }

    async loadPayrollData() {
        try {
            // Load employees for dropdown
            const employeesResult = await authService.makeRequest('/api/hr/employees');
            if (employeesResult.success) {
                this.populateEmployeeSelect('payrollEmployeeSelect', employeesResult.data);
            }
        } catch (error) {
            console.error('Error loading payroll data:', error);
        }
    }

    async loadStressMonitoringData() {
        try {
            // Load employees for dropdown
            const employeesResult = await authService.makeRequest('/api/hr/employees');
            if (employeesResult.success) {
                this.populateEmployeeSelect('stressEmployeeSelect', employeesResult.data);
            }
        } catch (error) {
            console.error('Error loading stress monitoring data:', error);
        }
    }

    async loadAttendanceData() {
        try {
            // Load employees for dropdown
            const employeesResult = await authService.makeRequest('/api/hr/employees');
            if (employeesResult.success) {
                this.populateEmployeeSelect('attendanceEmployeeSelect', employeesResult.data);
                this.populateEmployeeSelect('attendanceEmployeeModal', employeesResult.data);
            }
        } catch (error) {
            console.error('Error loading attendance data:', error);
        }
    }

    populateEmployeeSelect(selectId, employees) {
        const select = document.getElementById(selectId);
        if (!select) return;

        // Clear existing options except the first one
        select.innerHTML = '<option value="">Choose an employee...</option>';
        
        employees.forEach(employee => {
            const option = document.createElement('option');
            option.value = employee.id;
            option.textContent = employee.fullName;
            select.appendChild(option);
        });
    }

    setupEventListeners() {
        // Employee selection for payroll
        const payrollEmployeeSelect = document.getElementById('payrollEmployeeSelect');
        if (payrollEmployeeSelect) {
            payrollEmployeeSelect.addEventListener('change', async (e) => {
                const employeeId = e.target.value;
                if (employeeId) {
                    await this.loadEmployeePayslips(employeeId);
                } else {
                    document.getElementById('employeePayslips').innerHTML = '';
                }
            });
        }

        // Employee selection for stress monitoring
        const stressEmployeeSelect = document.getElementById('stressEmployeeSelect');
        if (stressEmployeeSelect) {
            stressEmployeeSelect.addEventListener('change', async (e) => {
                const employeeId = e.target.value;
                if (employeeId) {
                    await this.loadEmployeeStressData(employeeId);
                } else {
                    document.getElementById('employeeStressData').innerHTML = '';
                }
            });
        }

        // Employee selection for attendance
        const attendanceEmployeeSelect = document.getElementById('attendanceEmployeeSelect');
        if (attendanceEmployeeSelect) {
            attendanceEmployeeSelect.addEventListener('change', async (e) => {
                const employeeId = e.target.value;
                if (employeeId) {
                    await this.loadEmployeeAttendance(employeeId);
                } else {
                    document.getElementById('employeeAttendance').innerHTML = '';
                }
            });
        }

        // Attendance filters
        const filterAttendanceBtn = document.getElementById('filterAttendanceBtn');
        if (filterAttendanceBtn) {
            filterAttendanceBtn.addEventListener('click', async () => {
                const employeeId = attendanceEmployeeSelect.value;
                if (employeeId) {
                    await this.loadEmployeeAttendance(employeeId);
                }
            });
        }

        // Add attendance record
        const addAttendanceBtn = document.getElementById('addAttendanceBtn');
        const addAttendanceForm = document.getElementById('addAttendanceForm');

        if (addAttendanceBtn) {
            addAttendanceBtn.addEventListener('click', () => {
                openModal('addAttendanceModal');
            });
        }

        if (addAttendanceForm) {
            addAttendanceForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                await this.addAttendanceRecord();
            });
        }

        // Refresh buttons
        const refreshEmployeesBtn = document.getElementById('refreshEmployeesBtn');
        if (refreshEmployeesBtn) {
            refreshEmployeesBtn.addEventListener('click', () => {
                this.loadEmployeesData();
            });
        }

        const refreshStressDataBtn = document.getElementById('refreshStressDataBtn');
        if (refreshStressDataBtn) {
            refreshStressDataBtn.addEventListener('click', () => {
                this.loadStressMonitoringData();
            });
        }
    }

    async loadEmployeePayslips(employeeId) {
        try {
            const result = await authService.makeRequest(`/api/hr/employee/${employeeId}/payslips`);
            if (result.success) {
                this.updateEmployeePayslips(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading employee payslips:', error);
            showError('Failed to load employee payslips');
        }
    }

    updateEmployeePayslips(payslips) {
        const employeePayslips = document.getElementById('employeePayslips');
        if (!employeePayslips) return;

        if (payslips.length === 0) {
            employeePayslips.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-file-invoice-dollar"></i>
                    <h3>No Payslips Found</h3>
                    <p>This employee has no payslips generated yet.</p>
                </div>
            `;
            return;
        }

        employeePayslips.innerHTML = payslips.map(payslip => `
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
                <span class="payslip-date">${formatDate(payslip.generatedAt)}</span>
            </div>
        `).join('');
    }

    async loadEmployeeStressData(employeeId) {
        try {
            const result = await authService.makeRequest(`/api/hr/employee/${employeeId}/stress-history`);
            if (result.success) {
                this.updateEmployeeStressData(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading employee stress data:', error);
            showError('Failed to load employee stress data');
        }
    }

    updateEmployeeStressData(data) {
        const employeeStressData = document.getElementById('employeeStressData');
        if (!employeeStressData) return;

        employeeStressData.innerHTML = `
            <div class="stress-summary">
                <h3>Stress Statistics for ${data.employee.fullName}</h3>
                <div class="stress-stats">
                    <div class="stat-item">
                        <span class="stat-value">${data.statistics.averageStressLevel}</span>
                        <span class="stat-label">Average Stress Level</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-value">${data.statistics.maxStressLevel}</span>
                        <span class="stat-label">Max Stress Level</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-value">${data.statistics.totalOvertimeHours}</span>
                        <span class="stat-label">Total Overtime Hours</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-value">${data.statistics.totalRecords}</span>
                        <span class="stat-label">Total Records</span>
                    </div>
                </div>
            </div>
            <div class="stress-history">
                <h4>Stress History</h4>
                ${data.stressHistory.length > 0 ? 
                    data.stressHistory.map(record => `
                        <div class="stress-record">
                            <div class="stress-record-header">
                                <h5>${getMonthName(record.month)} ${record.year}</h5>
                                <span class="stress-level-badge stress-level-${record.stressLevel}">
                                    Level ${record.stressLevel}
                                </span>
                            </div>
                            <div class="stress-record-details">
                                <p><strong>Overtime Hours:</strong> ${record.overtimeHours}</p>
                                ${record.overtimeReason ? `<p><strong>Reason:</strong> ${record.overtimeReason}</p>` : ''}
                                <p><strong>Date:</strong> ${formatDate(record.createdAt)}</p>
                            </div>
                        </div>
                    `).join('') :
                    '<div class="empty-state"><i class="fas fa-chart-line"></i><h3>No Stress Records</h3><p>This employee has no stress records.</p></div>'
                }
            </div>
        `;
    }

    async loadEmployeeAttendance(employeeId) {
        try {
            const month = document.getElementById('attendanceMonth').value;
            const year = document.getElementById('attendanceYear').value;
            
            let url = `/api/hr/employee/${employeeId}/attendance`;
            if (month && year) {
                url += `?month=${month}&year=${year}`;
            }

            const result = await authService.makeRequest(url);
            if (result.success) {
                this.updateEmployeeAttendance(result.data);
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error loading employee attendance:', error);
            showError('Failed to load employee attendance');
        }
    }

    updateEmployeeAttendance(data) {
        const employeeAttendance = document.getElementById('employeeAttendance');
        if (!employeeAttendance) return;

        employeeAttendance.innerHTML = `
            <div class="attendance-summary">
                <h3>Attendance Summary for ${data.employee.fullName}</h3>
                <div class="attendance-stats">
                    <div class="stat-item">
                        <span class="stat-value">${data.statistics.presentDays}</span>
                        <span class="stat-label">Present Days</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-value">${data.statistics.paidLeaveDays}</span>
                        <span class="stat-label">Paid Leave Days</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-value">${data.statistics.unpaidLeaveDays}</span>
                        <span class="stat-label">Unpaid Leave Days</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-value">${data.statistics.totalDays}</span>
                        <span class="stat-label">Total Days</span>
                    </div>
                </div>
            </div>
            <div class="attendance-records">
                <h4>Attendance Records</h4>
                ${data.attendanceRecords.length > 0 ? 
                    data.attendanceRecords.map(record => `
                        <div class="attendance-record">
                            <span class="attendance-date">${formatDate(record.date)}</span>
                            <span class="attendance-status status-${record.status.toLowerCase()}">${record.status}</span>
                        </div>
                    `).join('') :
                    '<div class="empty-state"><i class="fas fa-calendar-check"></i><h3>No Attendance Records</h3><p>No attendance records found for the selected period.</p></div>'
                }
            </div>
        `;
    }

    async addAttendanceRecord() {
        try {
            const formData = {
                userId: parseInt(document.getElementById('attendanceEmployeeModal').value),
                date: document.getElementById('attendanceDate').value,
                status: document.getElementById('attendanceStatus').value
            };

            const result = await authService.makeRequest('/api/hr/employee/' + formData.userId + '/attendance', {
                method: 'POST',
                body: JSON.stringify(formData)
            });

            if (result.success) {
                showSuccess('Attendance record added successfully!');
                closeModal('addAttendanceModal');
                // Refresh attendance data if an employee is selected
                const selectedEmployee = document.getElementById('attendanceEmployeeSelect').value;
                if (selectedEmployee) {
                    await this.loadEmployeeAttendance(selectedEmployee);
                }
            } else {
                showError(result.error);
            }
        } catch (error) {
            console.error('Error adding attendance record:', error);
            showError('Failed to add attendance record');
        }
    }
}

// Global function for viewing employee details
async function viewEmployeeDetails(employeeId) {
    try {
        const result = await authService.makeRequest(`/api/hr/employee/${employeeId}/profile`);
        if (result.success) {
            const data = result.data;
            const message = `
Employee Details:
Name: ${data.user.fullName}
Email: ${data.user.email}
Phone: ${data.profile.phone || 'Not provided'}
Department: ${data.profile.department || 'Not provided'}
Position: ${data.profile.position || 'Not provided'}
Base Salary: ${formatCurrency(data.profile.baseSalary)}
Joined: ${formatDate(data.user.createdAt)}
            `;
            alert(message);
        } else {
            showError(result.error);
        }
    } catch (error) {
        console.error('Error loading employee details:', error);
        showError('Failed to load employee details');
    }
}

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    if (window.location.pathname.includes('hr-dashboard')) {
        new HRDashboard();
    }
});
