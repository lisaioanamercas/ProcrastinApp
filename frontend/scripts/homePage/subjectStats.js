class SubjectStatsService {
    constructor() {
        this.apiUrl = 'http://localhost:8081/api/stats';
        this.chartColors = [
            '#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', 
            '#ffeaa7', '#dda0dd', '#98d8c8', '#fdcb6e'
        ];
    }

    getAuthHeader() {
        const token = localStorage.getItem('token');
        return {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    }

    async fetchSubjectStats() {
        try {
            const response = await fetch(`${this.apiUrl}/by-subject`, {
                method: 'GET',
                headers: this.getAuthHeader()
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error fetching subject stats:', error);
            throw error;
        }
    }

    async downloadSubjectStats() {
        try {
            const response = await fetch(`${this.apiUrl}/by-subject/download`, {
                method: 'GET',
                headers: this.getAuthHeader()
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const jsonData = await response.json();
            this.triggerJsonDownload(jsonData, 'subject_stats.json');
            
            return true;
        } catch (error) {
            console.error('Error downloading subject stats:', error);
            throw error;
        }
    }

    triggerJsonDownload(data, filename) {
        const jsonString = JSON.stringify(data, null, 2);
        const blob = new Blob([jsonString], { type: 'application/json' });
        const url = window.URL.createObjectURL(blob);
        
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.style.display = 'none';
        
        document.body.appendChild(a);
        a.click();
        
        // Clean up
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    }

    createMainPieChart(subjects) {
        const totalTasks = subjects.reduce((sum, subject) => sum + subject.totalTasks, 0);
        
        if (totalTasks === 0) return '<div class="chart-loading">No data available</div>';
        
        let cumulativePercentage = 0;
        let gradientStops = [];
        let legendHTML = '';
        
        subjects.forEach((subject, index) => {
            const percentage = (subject.totalTasks / totalTasks) * 100;
            const color = this.chartColors[index % this.chartColors.length];
            
            gradientStops.push(`${color} ${cumulativePercentage}% ${cumulativePercentage + percentage}%`);
            cumulativePercentage += percentage;
            
            legendHTML += `
                <div class="legend-item">
                    <div class="legend-color" style="background-color: ${color};"></div>
                    <span class="legend-text">${subject.subjectName}</span>
                    <span class="legend-value">${subject.totalTasks} tasks</span>
                </div>
            `;
        });
        
        const gradientStyle = `background: conic-gradient(${gradientStops.join(', ')});`;
        
        return `
            <div class="main-chart-container">
                <div class="main-pie-chart" style="${gradientStyle}"></div>
                <div class="chart-legend">
                    <h5 style="margin-bottom: 15px; text-align: center;">Tasks by Subject</h5>
                    ${legendHTML}
                </div>
            </div>
        `;
    }

    createCompletionPieChart(subject) {
        const completedTasks = Math.round(subject.totalTasks * (subject.completionRate / 100));
        const incompleteTasks = subject.totalTasks - completedTasks;
        
        if (subject.totalTasks === 0) {
            return `
                <div class="subject-chart-container">
                    <div class="completion-pie-chart" style="background: #f3f4f6;"></div>
                    <div class="completion-chart-info">
                        <div class="completion-percentage">0%</div>
                        <div class="completion-label">No tasks</div>
                    </div>
                </div>
            `;
        }
        
        const completedPercentage = (completedTasks / subject.totalTasks) * 100;
        const gradientStyle = completedPercentage === 100 
            ? 'background: var(--completed-color);'
            : completedPercentage === 0
            ? 'background: var(--incomplete-color);'
            : `background: conic-gradient(var(--completed-color) 0% ${completedPercentage}%, var(--incomplete-color) ${completedPercentage}% 100%);`;
        
        return `
            <div class="subject-chart-container">
                <div class="completion-pie-chart" style="${gradientStyle}"></div>
                <div class="completion-chart-info">
                    <div class="completion-percentage">${subject.completionRate.toFixed(1)}%</div>
                    <div class="completion-label">Completed</div>
                </div>
            </div>
        `;
    }

    displaySubjectStats(data) {
        const modalContent = document.getElementById('stats-modal-content');
        const loading = document.getElementById('stats-loading');
        const chartsDiv = document.getElementById('stats-charts');

        // Hide loading
        loading.style.display = 'none';
        
        // Clear existing content
        chartsDiv.innerHTML = '';
        
        // Create stats display
        const statsContainer = document.createElement('div');
        statsContainer.className = 'subject-stats-container';
        
        let html = '<h4>Statistics by Subject</h4>';
        
        if (data.subjects && data.subjects.length > 0) {
            // Add main pie chart
            html += `
                <div class="charts-section">
                    <h4>Task Distribution Overview</h4>
                    ${this.createMainPieChart(data.subjects)}
                </div>
            `;
            
            html += '<div class="subject-stats-grid">';
            
            data.subjects.forEach(subject => {
                html += `
                    <div class="subject-stat-card">
                        <h5>${subject.subjectName}</h5>
                        
                        ${this.createCompletionPieChart(subject)}
                        
                        <div class="stat-row">
                            <span>Total Tasks:</span>
                            <span>${subject.totalTasks}</span>
                        </div>
                        <div class="stat-row">
                            <span>Avg Difficulty:</span>
                            <span>${subject.avgDifficulty.toFixed(1)}</span>
                        </div>
                        <div class="stat-row">
                            <span>Completion Rate:</span>
                            <span>${subject.completionRate.toFixed(1)}%</span>
                        </div>
                        <div class="stat-row">
                            <span>Total Duration:</span>
                            <span>${Math.floor(subject.totalDuration / 60)}h ${subject.totalDuration % 60}m</span>
                        </div>
                        <div class="stat-row">
                            <span>Incomplete Duration:</span>
                            <span>${Math.floor(subject.incompleteTasksDuration / 60)}h ${subject.incompleteTasksDuration % 60}m</span>
                        </div>
                    </div>
                `;
            });
            
            html += '</div>';
        } else {
            html += '<p>No subject statistics available.</p>';
        }
        
        // Add download button
        html += `
            <div style="margin-top: 30px; text-align: center;">
                <button class="btn btn-secondary" id="download-subject-stats-btn">
                    <i class="fas fa-download"></i> Download Subject Stats
                </button>
            </div>
        `;
        
        statsContainer.innerHTML = html;
        chartsDiv.appendChild(statsContainer);
        
        // Show the charts div
        chartsDiv.style.display = 'block';
        
        // Add event listener for download button
        const downloadBtn = document.getElementById('download-subject-stats-btn');
        if (downloadBtn) {
            downloadBtn.addEventListener('click', () => {
                this.downloadSubjectStats().catch(error => {
                    alert('Failed to download subject stats. Please try again.');
                });
            });
        }
    }
}

// Make it globally available
window.SubjectStatsService = SubjectStatsService;