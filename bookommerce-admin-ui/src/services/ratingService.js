import axiosInstance from '../api/axiosConfig';

/**
 * Rating Service
 * 
 * Handles all API calls related to ratings/reviews.
 */

export const getRatings = async (params) => {
    return axiosInstance.get('/protected/api/ratings', { params });
};

export const approveRating = async (id) => {
    return axiosInstance.patch(`/api/ratings/${id}/approve`);
};

export const rejectRating = async (id) => {
    return axiosInstance.patch(`/protected/api/ratings/${id}/reject`);
};

export const deleteRating = async (id) => {
    return axiosInstance.delete(`/protected/api/ratings/${id}`);
};
