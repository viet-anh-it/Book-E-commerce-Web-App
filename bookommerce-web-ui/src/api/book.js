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
        const response = await axiosInstance.get(`/api/books/${bookId}/ratings`, {
            params: {
                ...params
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching ratings:', error);
        throw error;
    }
};
export const createRating = async (ratingData) => {
    try {
        const response = await axiosInstance.post('/protected/api/ratings', ratingData);
        return response.data;
    } catch (error) {
        console.error('Error creating rating:', error);
        throw error;
    }
};

export const deleteRating = async (id) => {
    try {
        const response = await axiosInstance.delete(`/protected/api/ratings/${id}`);
        return response.data;
    } catch (error) {
        console.error('Error deleting rating:', error);
        throw error;
    }
};

export const updateRating = async (ratingData) => {
    try {
        const response = await axiosInstance.put('/protected/api/ratings', ratingData);
        return response.data;
    } catch (error) {
        console.error('Error updating rating:', error);
        throw error;
    }
};

