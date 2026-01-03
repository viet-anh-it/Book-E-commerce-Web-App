import axiosInstance from './axiosInstance';

/**
 * Update user profile information
 * @param {Object} userData - Data to update (firstName, lastName, phoneNumber, gender, dateOfBirth)
 * @returns {Promise}
 */
export const updateProfile = async (userData) => {
    // Note: This endpoint is assumed based on standard REST practices.
    // The actual endpoint should be implemented in the Resource Server.
    return axiosInstance.put('/api/me', userData);
};

/**
 * Get user profile details
 * @returns {Promise}
 */
export const getProfile = async () => {
    return axiosInstance.get('/api/me');
};
