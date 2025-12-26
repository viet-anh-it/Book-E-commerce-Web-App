import axios from 'axios';

const AUTH_URL = 'https://auth.bookommerce.com:8282';

export const getAuthenticatedUser = async () => {
    try {
        const response = await axios.get(`${AUTH_URL}/api/me`, {
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
