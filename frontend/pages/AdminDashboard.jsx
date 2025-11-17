import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import adminService from '../services/adminService';

const AdminDashboard = () => {
    const { logout } = useAuth();
    const [stats, setStats] = useState({ totalStudents: 0, totalTeachers: 0 });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const data = await adminService.getStats();
                setStats(data);
                setLoading(false);
            } catch (err) {
                // If the user's token is invalid or they get a 403 Forbidden
                console.error("Error fetching admin stats:", err);
                setError("Failed to load statistics. Access denied or server error.");
                setLoading(false);
            }
        };

        fetchStats();
    }, []);

    if (loading) {
        return <div style={{ padding: '20px' }}>Loading Admin Analytics...</div>;
    }

    if (error) {
        return <div style={{ padding: '20px', color: 'red' }}>Error: {error}</div>;
    }

    return (
        <div style={{ padding: '30px', maxWidth: '900px', margin: 'auto', border: '1px solid #ddd' }}>
            <h1>🛡 Admin Panel Dashboard</h1>
            <p>Welcome, Administrator. You have full management privileges.</p>
            
            <div style={{ display: 'flex', gap: '20px', margin: '20px 0' }}>
                {/* Analytics Display (Data fetched from GET /api/admin/stats) */}
                <div style={statCardStyle}>
                    <h2>Total Students</h2>
                    <p style={statCountStyle}>{stats.totalStudents}</p>
                </div>
                
                <div style={statCardStyle}>
                    <h2>Total Teachers</h2>
                    <p style={statCountStyle}>{stats.totalTeachers}</p>
                </div>
            </div>

            <h2>User Management Actions</h2>
            <p>Admin CRUD APIs are functional:</p>
            <ul>
                <li>POST /api/admin/teachers (Create New Teacher)</li>
                <li>DELETE /api/admin/teachers/{'{id}'} (Delete Teacher)</li>
                {/* In a real app, these would be forms that call adminService.createTeacher */}
            </ul>

            <button 
                onClick={logout} 
                style={{ marginTop: '20px', backgroundColor: 'darkred', color: 'white', padding: '10px' }}
            >
                Secure Logout
            </button>
        </div>
    );
};

// Simple inline styles for the dashboard cards
const statCardStyle = {
    border: '1px solid #ccc',
    padding: '20px',
    borderRadius: '5px',
    flex: '1',
    textAlign: 'center',
    backgroundColor: '#f9f9f9',
};

const statCountStyle = {
    fontSize: '2.5em',
    fontWeight: 'bold',
    color: '#007bff',
    marginTop: '5px',
};

export default AdminDashboard;