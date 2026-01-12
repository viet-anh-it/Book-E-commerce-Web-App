import axiosInstance from './axiosInstance.js';

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
