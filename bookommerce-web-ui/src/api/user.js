import axiosInstance from './axiosInstance';

/**
 * Update user profile information
 * @param {Object} userData - Data to update (firstName, lastName, phoneNumber, gender, dateOfBirth)
 * @returns {Promise}
 */
export const updateProfile = async (userData) => {
    // Note: This endpoint is updated to match the profile path
    return axiosInstance.patch('/protected/api/me/profile', userData);
};

/**
 * Get user profile details
 * @returns {Promise}
 */
export const getProfile = async () => {
    return axiosInstance.get('/protected/api/me/profile');
};
/**
 * Change user email
 * @param {string} newEmail
 * @returns {Promise}
 */
export const changeEmail = async (newEmail) => {
    // Note: This endpoint is assumed. Email change often requires verification.
    return axiosInstance.post('/api/me/change-email', { newEmail });
};
/**
 * Change user password
 * @param {Object} passwordData - { currentPassword, newPassword }
 * @returns {Promise}
 */
export const changePassword = async (passwordData) => {
    return axiosInstance.post('/api/me/change-password', passwordData);
};
/**
 * Upload user avatar
 * @param {File} file - The image file to upload
 * @returns {Promise}
 */
export const uploadAvatar = async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return axiosInstance.post('/api/me/avatar', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
};

/**
 * Get user profile avatar
 * @returns {Promise}
 */
export const getProfileAvatar = async () => {
    return axiosInstance.get('/protected/api/me/profile/avatar');
};
