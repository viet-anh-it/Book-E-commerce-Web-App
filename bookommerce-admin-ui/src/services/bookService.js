import axiosInstance from '../api/axiosConfig';

export const getBooks = async () => {
    try {
        const response = await axiosInstance.get('/api/books');
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

export const updateBook = async (id, updatedData) => {
    try {
        const response = await axiosInstance.put(`/api/books/${id}`, updatedData);
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
