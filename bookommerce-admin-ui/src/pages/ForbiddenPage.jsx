import { HomeOutlined } from '@ant-design/icons';
import { Button, Result, theme } from 'antd';

const ForbiddenPage = () => {
    const {
        token: { colorBgContainer },
    } = theme.useToken();

    return (
        <div style={{
            height: '100vh',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            backgroundColor: colorBgContainer
        }}>
            <Result
                status="403"
                title="403"
                subTitle="Xin lỗi, tài khoản khách hàng không được phép truy cập Giao diện cửa hàng."
                extra={
                    <Button
                        type="primary"
                        icon={<HomeOutlined />}
                        onClick={() => {
                            // Redirect to logout then login
                            window.location.href = 'https://bff.bookommerce.com:8181/protected/page/confirm-logout';
                        }}
                    >
                        Đăng xuất & Chuyển tài khoản
                    </Button>
                }
            />
        </div>
    );
};

export default ForbiddenPage;
