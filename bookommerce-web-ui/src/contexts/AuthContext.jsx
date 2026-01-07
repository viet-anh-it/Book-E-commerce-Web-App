import React, { createContext, useContext, useState, useEffect } from 'react';
import { getAuthenticatedUser } from '../api/auth';
import { Spin } from 'antd';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const checkAuth = async () => {
        try {
            const response = await getAuthenticatedUser();
            if (response.status === 200) {
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

    useEffect(() => {
        checkAuth();
    }, []);

    const logout = async () => {
        try {
            await logoutApi();
        } catch (error) {
            console.error("Logout failed", error);
        } finally {
            setUser(null);
            // Optionally redirect to home to reflect logout state if needed
            // window.location.href = '/';
        }
    };

    return (
        <AuthContext.Provider value={{ user, loading, logout, refreshUser: checkAuth }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
