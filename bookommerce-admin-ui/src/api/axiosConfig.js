import axios from 'axios';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

NProgress.configure({ showSpinner: false });

const axiosInstance = axios.create({
    baseURL: 'https://bff.bookommerce.com:8181',
    withCredentials: true,
    headers: {
        'Accept': 'application/json'
    }
});

// Thêm một bộ đón chặn request
axiosInstance.interceptors.request.use(function (config) {
    // Làm gì đó trước khi request dược gửi đi
    NProgress.start();
    return config;
}, function (error) {
    // Làm gì đó với lỗi request
    NProgress.done();
    return Promise.reject(error);
});

// Thêm một bộ đón chặn response
axiosInstance.interceptors.response.use(function (response) {
    // Bất kì mã trạng thái nào nằm trong tầm 2xx đều khiến hàm này được trigger
    // Làm gì đó với dữ liệu response
    NProgress.done();
    return response;
}, function (error) {
    // Bất kì mã trạng thái nào lọt ra ngoài tầm 2xx đều khiến hàm này được trigger\
    // Làm gì đó với lỗi response
    NProgress.done();

    // If the error status is 401 (Unauthorized), let the application handle it
    // e.g. via AuthContext or individual component error handling.

    return Promise.reject(error);
});

export default axiosInstance;
