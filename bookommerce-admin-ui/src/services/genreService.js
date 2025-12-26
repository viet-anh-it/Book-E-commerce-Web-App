import axiosInstance from '../api/axiosConfig';

export const getGenres = async () => {
    try {
        const response = await axiosInstance.get('/api/genres');
        return response.data;
    } catch (error) {
        console.error('Error fetching genres:', error);
        throw error;
    }
};
