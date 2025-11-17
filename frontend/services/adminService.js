import api from './api'; // The secure Axios instance

const ADMIN_URL = '/api/admin/';

const getStats = async () => {
    try {
        // API: GET /api/admin/stats
        const response = await api.get(ADMIN_URL + 'stats');
        return response.data; // Returns { totalStudents: N, totalTeachers: M }
    } catch (error) {
        // Axios interceptor should handle 401/403, but we catch generic errors here
        console.error("Failed to fetch admin stats:", error);
        throw error;
    }
};

const adminService = {
    getStats,
    // Add create/delete methods here later if needed
};

export default adminService;