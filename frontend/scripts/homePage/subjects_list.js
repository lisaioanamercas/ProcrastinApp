class SubjectService {
    constructor() {
        this.apiUrl = 'http://localhost:8081/api/subjects';
        this.subjects = [];
    }

    // Get auth header with JWT token
    getAuthHeader() {
        const token = localStorage.getItem('token');
        return {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    }

    // Fetch all subjects
    async fetchSubjects() {
        try {
            console.log("Fetching subjects from API:", this.apiUrl);
            const response = await fetch(this.apiUrl, {
                method: 'GET',
                headers: this.getAuthHeader()
            });

            if (!response.ok) {
                throw new Error('Failed to fetch subjects');
            }

            this.subjects = await response.json();
            return this.subjects;
        } catch (error) {
            console.error('Error fetching subjects:', error);
            // Use mock data as fallback
            this.subjects = [
                { id: 1, name: "Mathematics" },
                { id: 2, name: "Computer Science" },
                { id: 3, name: "Physics" },
                { id: 4, name: "Literature" }
            ];
            return this.subjects;
        }
    }

    // Get subject by name
    getSubjectByName(name) {
        return this.subjects.find(subject => subject.name === name);
    }
}