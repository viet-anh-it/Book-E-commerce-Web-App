import axios from 'axios';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import Cookies from 'js-cookie';

NProgress.configure({ showSpinner: false });

const axiosInstance = axios.create({
    baseURL: 'https://bff.bookommerce.com:8181',
    withCredentials: true
});

// Thêm một bộ đón chặn request
axiosInstance.interceptors.request.use(function (config) {
    // Làm gì đó trước khi request dược gửi đi
    const csrfToken = Cookies.get('XSRF-TOKEN');
    const unsafeHttpMethod = ['POST', 'PUT', 'DELETE', 'PATCH'];
    if (csrfToken && unsafeHttpMethod.includes(config.method.toUpperCase())) {
        config.headers['X-CSRF-TOKEN'] = csrfToken;
    }
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
    return Promise.reject(error);
});

export default axiosInstance;
