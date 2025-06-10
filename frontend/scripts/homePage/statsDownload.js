class StatsDownloadService {
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

    async downloadStats() {
        try {
            const response = await fetch(`${this.apiUrl}/download`, {
                method: 'GET',
                headers: this.getAuthHeader()
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const textContent = await response.text();
            this.triggerDownload(textContent, 'study_stats.txt');
            
            return true;
        } catch (error) {
            console.error('Error downloading stats:', error);
            throw error;
        }
    }

    triggerDownload(content, filename) {
        const blob = new Blob([content], { type: 'text/plain' });
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
}

// Make it globally available
window.StatsDownloadService = StatsDownloadService;