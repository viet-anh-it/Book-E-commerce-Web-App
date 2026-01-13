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
                subTitle="Xin lỗi, bạn không có quyền truy cập trang này."
                extra={
                    <Button
                        type="primary"
                        icon={<HomeOutlined />}
                        onClick={() => window.location.href = 'https://bff.bookommerce.com:8181/protected/page/confirm-logout'}
                    >
                        Đăng xuất & Chuyển tài khoản
                    </Button>
                }
            />
        </div>
    );
};

export default ForbiddenPage;
