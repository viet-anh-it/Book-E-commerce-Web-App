import axiosInstance from './axiosConfig';

/**
 * Fetches the currently authenticated user from the auth server.
 * 
 * @returns {Promise<Object>} The API response containing user info.
 */
export const getAuthenticatedUser = async () => {
    try {
        const response = await axiosInstance.get(`/protected/api/me`, {
            withCredentials: true,
            headers: {
                Accept: 'application/json',
            },
        });
        return response.data;
    } catch (error) {
        throw error;
    }
};

/**
 * Redirects the user to the store login page.
 */
export const redirectToLogin = () => {
    window.location.href = 'https://auth.bookommerce.com:8282/page/login/store';
};
