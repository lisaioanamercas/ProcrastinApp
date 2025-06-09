class HeatmapUI {
    constructor(heatmapService) {
        this.heatmapService = heatmapService;
        this.currentDate = new Date();
        this.container = document.getElementById('heatmap-container');
        this.monthLabel = document.getElementById('current-month');
        this.tooltip = null;
        
        // Debug logging
        console.log('HeatmapUI constructor called');
        console.log('Container found:', !!this.container);
        console.log('Month label found:', !!this.monthLabel);
        
        this.initEventListeners();
        this.createTooltip();
    }

    initEventListeners() {
        // Previous month button
        const prevBtn = document.getElementById('prev-month-btn');
        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                this.currentDate.setMonth(this.currentDate.getMonth() - 1);
                this.renderHeatmap();
            });
        }

        // Next month button
        const nextBtn = document.getElementById('next-month-btn');
        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                this.currentDate.setMonth(this.currentDate.getMonth() + 1);
                this.renderHeatmap();
            });
        }
    }

    createTooltip() {
        this.tooltip = document.createElement('div');
        this.tooltip.className = 'heatmap-tooltip';
        document.body.appendChild(this.tooltip);
    }

    async renderHeatmap() {
        if (!this.container) return;

        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth() + 1;

        // Update month label
        const monthNames = [
            'January', 'February', 'March', 'April', 'May', 'June',
            'July', 'August', 'September', 'October', 'November', 'December'
        ];
        if (this.monthLabel) {
            this.monthLabel.textContent = `${monthNames[month - 1]} ${year}`;
        }

        try {
            const activityData = await this.heatmapService.getActivityData(year, month);
            this.renderCalendar(year, month, activityData);
        } catch (error) {
            console.error('Failed to render heatmap:', error);
        }
    }

    renderCalendar(year, month, activityData) {
        const firstDay = new Date(year, month - 1, 1);
        const lastDay = new Date(year, month, 0);
        const daysInMonth = lastDay.getDate();
        const startDayOfWeek = firstDay.getDay();

        // Create calendar grid
        let calendarHTML = `
            <div class="heatmap-grid">
                <div class="heatmap-days">
                    <div class="heatmap-day-label">Sun</div>
                    <div class="heatmap-day-label">Mon</div>
                    <div class="heatmap-day-label">Tue</div>
                    <div class="heatmap-day-label">Wed</div>
                    <div class="heatmap-day-label">Thu</div>
                    <div class="heatmap-day-label">Fri</div>
                    <div class="heatmap-day-label">Sat</div>
                </div>
                <div class="heatmap-months">
                    <div class="heatmap-month-labels">
                        ${this.generateWeekHeaders(year, month)}
                    </div>
                    <div class="heatmap-weeks">
                        ${this.generateWeeks(year, month, activityData)}
                    </div>
                </div>
            </div>
        `;

        this.container.innerHTML = calendarHTML;
        this.attachHoverEvents();
    }

    generateWeekHeaders(year, month) {
        const firstDay = new Date(year, month - 1, 1);
        const lastDay = new Date(year, month, 0);
        const totalDays = lastDay.getDate();
        const startDayOfWeek = firstDay.getDay();
        
        const totalCells = Math.ceil((totalDays + startDayOfWeek) / 7) * 7;
        const weeks = Math.ceil(totalCells / 7);
        
        let headers = '';
        for (let week = 0; week < weeks; week++) {
            const weekStart = week * 7 - startDayOfWeek + 1;
            if (weekStart > 0 && weekStart <= totalDays) {
                headers += `<div class="heatmap-month-label">Week ${week + 1}</div>`;
            } else {
                headers += `<div class="heatmap-month-label"></div>`;
            }
        }
        
        return headers;
    }

    generateWeeks(year, month, activityData) {
        const firstDay = new Date(year, month - 1, 1);
        const lastDay = new Date(year, month, 0);
        const daysInMonth = lastDay.getDate();
        const startDayOfWeek = firstDay.getDay();
        
        const totalCells = Math.ceil((daysInMonth + startDayOfWeek) / 7) * 7;
        const weeks = Math.ceil(totalCells / 7);
        
        let weeksHTML = '';
        
        for (let week = 0; week < weeks; week++) {
            weeksHTML += '<div class="heatmap-week">';
            
            for (let day = 0; day < 7; day++) {
                const dayNumber = week * 7 + day - startDayOfWeek + 1;
                
                if (dayNumber > 0 && dayNumber <= daysInMonth) {
                    const date = `${year}-${String(month).padStart(2, '0')}-${String(dayNumber).padStart(2, '0')}`;
                    const activity = activityData[date] || { level: 0, tasks_completed: 0, habits_completed: 0, total_activity: 0 };
                    
                    weeksHTML += `
                        <div class="heatmap-day" 
                             data-level="${activity.level}" 
                             data-date="${date}"
                             data-tasks="${activity.tasks_completed}"
                             data-habits="${activity.habits_completed}"
                             data-total="${activity.total_activity}">
                        </div>
                    `;
                } else {
                    weeksHTML += '<div class="heatmap-day" style="visibility: hidden;"></div>';
                }
            }
            
            weeksHTML += '</div>';
        }
        
        return weeksHTML;
    }

    attachHoverEvents() {
        const heatmapDays = this.container.querySelectorAll('.heatmap-day[data-date]');
        
        heatmapDays.forEach(day => {
            day.addEventListener('mouseenter', (e) => {
                this.showTooltip(e);
            });
            
            day.addEventListener('mouseleave', () => {
                this.hideTooltip();
            });
        });
    }

    showTooltip(event) {
        const element = event.target;
        const date = element.getAttribute('data-date');
        const tasks = element.getAttribute('data-tasks') || 0;
        const habits = element.getAttribute('data-habits') || 0;
        const total = element.getAttribute('data-total') || 0;
        
        const formattedDate = new Date(date).toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
        
        this.tooltip.innerHTML = `
            <div><strong>${formattedDate}</strong></div>
            <div>${tasks} tasks completed</div>
            <div>${habits} habits completed</div>
            <div><strong>Total activity: ${total}</strong></div>
        `;
        
        this.tooltip.classList.add('show');
        
        // Position tooltip
        const rect = element.getBoundingClientRect();
        const tooltipWidth = this.tooltip.offsetWidth || 200; // fallback width
        const tooltipHeight = this.tooltip.offsetHeight || 80; // fallback height
        let left = rect.right + 8;
        let top = rect.top + (rect.height / 2) - (tooltipHeight / 2);

        
            // Check if tooltip goes off the right edge of screen
        if (left + tooltipWidth > window.innerWidth) {
            // Position to the left of the cell instead
            left = rect.left - tooltipWidth - 8;
        }
        
        // Check if tooltip goes off the left edge
        if (left < 0) {
            // Center it above the cell as fallback
            left = rect.left + (rect.width / 2) - (tooltipWidth / 2);
            top = rect.top - tooltipHeight - 8;
        }
        
        // Ensure tooltip doesn't go off top or bottom of screen
        if (top < 0) {
            top = rect.bottom + 8;
        }
        if (top + tooltipHeight > window.innerHeight) {
            top = rect.top - tooltipHeight - 8;
        }
        
        this.tooltip.style.left = left + 'px';
        this.tooltip.style.top = top + 'px';
    }

    hideTooltip() {
        this.tooltip.classList.remove('show');
    }

    // Initialize the heatmap
    async init() {
        await this.renderHeatmap();
    }
}