import axiosInstance from './axiosInstance';

export const getBookById = async (id) => {
    try {
        const response = await axiosInstance.get(`/api/books/${id}`);
        return response.data;
    } catch (error) {
        console.error('Error fetching book details:', error);
        throw error;
    }
};

export const getRatings = async (bookId, params) => {
    try {
        const response = await axiosInstance.get('/api/ratings', {
            params: {
                bookId,
                ...params
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching ratings:', error);
        throw error;
    }
};
