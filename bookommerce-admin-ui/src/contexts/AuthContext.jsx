import { Spin } from 'antd';
import { createContext, useContext, useEffect, useState } from 'react';
import { getAuthenticatedUser } from '../api/auth';

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

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    }

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
        const csrfToken = getCookie('XSRF-TOKEN');
        window.alert(csrfToken);
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'https://bff.bookommerce.com:8181/protected/logout';
        const input = document.createElement('input');
        input.name = '_csrf';
        input.value = csrfToken;
        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    };

    // const logout = async () => {
    //     const csrfToken = getCookie('XSRF-TOKEN');
    //     window.alert(`csrfToken: ${csrfToken}`);
    //     const response = await fetch('https://bff.bookommerce.com:8181/protected/logout', {
    //         method: 'POST',
    //         headers: {
    //             'X-XSRF-TOKEN': csrfToken,
    //             'Content-Type': 'application/json',
    //             'Accept': 'application/json'
    //         },
    //         credentials: 'include'
    //     });
    //     const json = await response.json();
    //     window.alert(`body: ${json}`);
    //     if (json.status === 200) {
    //         window.location.href = json.data.redirectUrl;
    //     }
    // }

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
