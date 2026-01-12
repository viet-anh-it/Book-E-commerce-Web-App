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
                subTitle="Sorry, you are not authorized to access this page."
                extra={
                    <Button
                        type="primary"
                        icon={<HomeOutlined />}
                        onClick={() => window.location.href = 'https://bff.bookommerce.com:8181/protected/page/confirm-logout'}
                    >
                        Logout & Switch Account
                    </Button>
                }
            />
        </div>
    );
};

export default ForbiddenPage;
