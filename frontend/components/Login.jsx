import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; 

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();
    // const { setUser } = useAuth(); // optional: if you store user info globally

    // 🧩 Your backend login API (via ngrok)
    const BASE_URL = "https://unquerulous-chae-uncharitably.ngrok-free.dev/api/auth/login";

    const handleLogin = async (e) => {
    e.preventDefault();
    setError(''); // Clear error on new attempt
    
    // NOTE: You can remove the 'success' state entirely as it's redundant.

    try {
        const userData = await login(email, password);
        
        // 1. Success Message (Optional: console log instead of state)
        console.log("Login successful. Navigating to dashboard.");
        
        // 2. --- CRITICAL NAVIGATION ---
        if (userData && userData.role) {
            let dashboardPath = '/';
            switch (userData.role) {
                case 'ROLE_STUDENT':
                    dashboardPath = '/student/dashboard';
                    break;
                case 'ROLE_TEACHER':
                    dashboardPath = '/teacher/dashboard';
                    break;
                case 'ROLE_ADMIN':
                    dashboardPath = '/admin/dashboard';
                    break;
                default:
                    dashboardPath = '/'; 
            }
            
            // Navigate the user to the correct secure path
            navigate(dashboardPath); 
            
        } else {
             // Failsafe if token is received but role is empty
             setError('Login success but user role is missing.');
        }
        
    } catch (err) {
        // If login fails (401/400 status from backend)
        setError('Login failed. Please check your email and password.');
        console.error("Login attempt error:", err);
    }
    };

    return (
        <div style={{
            padding: '20px',
            maxWidth: '400px',
            margin: 'auto',
            border: '1px solid #ddd',
            borderRadius: '8px',
            marginTop: '60px',
            boxShadow: '0 4px 10px rgba(0,0,0,0.1)'
        }}>
            <h2 style={{ textAlign: 'center' }}>🔐 Secure Login</h2>

            <form onSubmit={handleLogin}>
                {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}
                {success && <p style={{ color: 'green', textAlign: 'center' }}>✅ Login successful!</p>}

                <div style={{ marginBottom: '15px' }}>
                    <label>Email (Username):</label>
                    <input 
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        style={{
                            width: '100%',
                            padding: '8px',
                            marginTop: '5px',
                            borderRadius: '4px',
                            border: '1px solid #ccc'
                        }}
                    />
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <label>Password:</label>
                    <input 
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        style={{
                            width: '100%',
                            padding: '8px',
                            marginTop: '5px',
                            borderRadius: '4px',
                            border: '1px solid #ccc'
                        }}
                    />
                </div>

                <button 
                    type="submit"
                    style={{
                        width: '100%',
                        padding: '10px',
                        backgroundColor: '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer'
                    }}
                >
                    Log In
                </button>
            </form>
        </div>
    );
};

export default Login;