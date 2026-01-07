import React, { createContext, useContext, useState, useEffect } from 'react';
import { getAuthenticatedUser } from '../api/auth';
import { Spin } from 'antd';

const AuthContext = createContext(null);

/**
 * AuthProvider Component
 * 
 * Manages the global authentication state.
 * It checks for the current user on mount and provides the user object,
 * loading state, and logout function to the rest of the application.
 */
export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const response = await getAuthenticatedUser();
                // Check if the response is successful and has data
                if (response && response.status === 200) {
                    setUser(response.data);
                } else {
                    setUser(null);
                }
            } catch (error) {
                // If 401 or network error, assume not logged in
                setUser(null);
            } finally {
                setLoading(false);
            }
        };

        checkAuth();
    }, []);

    const logout = () => {
        // Direct GET request to logout confirmation page
        window.location.href = 'https://bff.bookommerce.com:8181/confirm-logout';
    };

    return (
        <AuthContext.Provider value={{ user, loading, logout }}>
            {loading ? (
                <div style={{
                    height: '100vh',
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center'
                }}>
                    <Spin size="large" tip="Verifying session..." />
                </div>
            ) : (
                children
            )}
        </AuthContext.Provider>
    );
};

/**
 * Custom hook to use the AuthContext.
 */
export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
