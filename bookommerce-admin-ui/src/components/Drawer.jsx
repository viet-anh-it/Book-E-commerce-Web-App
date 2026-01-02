import React from 'react';
import { Layout, Menu } from 'antd';
import {
    DashboardOutlined,
    UserOutlined,
    ShoppingOutlined,
    OrderedListOutlined,
    StarOutlined,
} from '@ant-design/icons';

const { Sider } = Layout;

/**
 * AdminDrawer Component
 * 
 * Represents the left sidebar navigation (Sider) of the Admin Dashboard.
 * It contains the main navigation menu.
 */
import { useNavigate } from 'react-router-dom';

const AdminDrawer = ({ collapsed }) => {
    const navigate = useNavigate();

    return (
        <Sider
            trigger={null}
            collapsible
            collapsed={collapsed}
            breakpoint="lg"
            collapsedWidth="0"
            onBreakpoint={(broken) => {
                console.log(broken);
            }}
        >
            <div className="demo-logo-vertical" />
            <Menu
                theme="dark"
                mode="inline"
                defaultSelectedKeys={['/']}
                onClick={({ key }) => {
                    navigate(key);
                }}
                items={[
                    {
                        key: '/',
                        icon: <DashboardOutlined />,
                        label: 'Dashboard',
                    },
                    {
                        key: '/books',
                        icon: <ShoppingOutlined />,
                        label: 'Books',
                    },
                    {
                        key: '/orders',
                        icon: <OrderedListOutlined />,
                        label: 'Orders',
                    },
                    {
                        key: '/ratings',
                        icon: <StarOutlined />,
                        label: 'Reviews',
                    },
                    {
                        key: '/users',
                        icon: <UserOutlined />,
                        label: 'Users',
                    },
                ]}
            />
        </Sider>
    );
};

export default AdminDrawer;
