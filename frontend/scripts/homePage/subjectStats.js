class SubjectStatsService {
    constructor() {
        this.apiUrl = 'http://localhost:8081/api/stats';
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
            html += '<div class="subject-stats-grid">';
            
            data.subjects.forEach(subject => {
                html += `
                    <div class="subject-stat-card">
                        <h5>${subject.subjectName}</h5>
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
                            <span>${Math.round(subject.totalDuration / 60)}h ${subject.totalDuration % 60}m</span>
                        </div>
                        <div class="stat-row">
                            <span>Incomplete Duration:</span>
                            <span>${Math.round(subject.incompleteTasksDuration / 60)}h ${subject.incompleteTasksDuration % 60}m</span>
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
            <div style="margin-top: 20px;">
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