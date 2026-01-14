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

    if (error.response && error.response.status === 401) {
        // Exclude /protected/api/me from redirecting to login, allowing guests to stay on the page
        // Only exclude if it exactly matches the auth check, not sub-resources like /profile
        if (!error.config.url.endsWith('/protected/api/me')) {
            window.location.href = 'https://auth.bookommerce.com:8282/page/login?session_expired';
        }
    }

    return Promise.reject(error);
});

export default axiosInstance;
