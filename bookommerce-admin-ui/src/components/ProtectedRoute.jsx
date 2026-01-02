import React, { useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { redirectToLogin } from '../api/auth';

/**
 * ProtectedRoute Component
 * 
 * A wrapper for routes that require authentication.
 * If the user is not authenticated, they are redirected to the login page.
 */
const ProtectedRoute = ({ children }) => {
    const { user, loading } = useAuth();

    useEffect(() => {
        if (!loading && !user) {
            redirectToLogin();
        }
    }, [user, loading]);

    return user ? children : null;
};

export default ProtectedRoute;
