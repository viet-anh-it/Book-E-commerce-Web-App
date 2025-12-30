import axiosInstance from '../api/axiosConfig';

export const getBooks = async (params) => {
    try {
        const response = await axiosInstance.get('/api/books', { params });
        return response.data; // This returns the full response object { data: [], message: "", status: 200 }
    } catch (error) {
        console.error('Error fetching books:', error);
        throw error;
    }
};

export const createBook = async (formData) => {
    try {
        const response = await axiosInstance.post('/api/books', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    } catch (error) {
        console.error('Error creating book:', error);
        throw error;
    }
};

export const updateBook = async (id, formData) => {
    try {
        const response = await axiosInstance.put(`/api/books/${id}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    } catch (error) {
        console.error('Error updating book:', error);
        throw error;
    }
};

export const deleteBook = async (id) => {
    try {
        const response = await axiosInstance.delete(`/api/books/${id}`);
        return response.data;
    } catch (error) {
        console.error('Error deleting book:', error);
        throw error;
    }
};

export const getBookById = async (id) => {
    try {
        const response = await axiosInstance.get(`/api/books/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching book with id ${id}:`, error);
        throw error;
    }
};
