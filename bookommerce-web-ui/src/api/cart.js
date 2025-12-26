import axiosInstance from './axiosInstance';

export const getCart = async () => {
    try {
        const response = await axiosInstance.get('/api/carts');
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const addToCart = async ({ bookId, quantity }) => {
    try {
        const response = await axiosInstance.post('/api/carts/items', {
            bookId,
            quantity
        });
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const removeCartItem = async (id) => {
    try {
        const response = await axiosInstance.delete(`/api/carts/items/${id}`);
        return response.data;
    } catch (error) {
        throw error;
    }
};

export const updateCartItem = async (id, quantity) => {
    try {
        const response = await axiosInstance.patch(`/api/carts/items/${id}`, {
            quantity
        });
        return response.data;
    } catch (error) {
        throw error;
    }
};
