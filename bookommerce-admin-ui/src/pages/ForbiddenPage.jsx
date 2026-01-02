import React from 'react';
import { Result, Button, theme } from 'antd';
import { HomeOutlined } from '@ant-design/icons';
import { redirectToLogin } from '../api/auth';

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
                subTitle="Sorry, access to the Store Interface is not allowed for customer accounts."
                extra={
                    <Button
                        type="primary"
                        icon={<HomeOutlined />}
                        onClick={() => {
                            // Redirect to logout then login
                            window.location.href = 'https://bff.bookommerce.com:8181/confirm-logout';
                        }}
                    >
                        Logout & Switch Account
                    </Button>
                }
            />
        </div>
    );
};

export default ForbiddenPage;
