import {
    DashboardOutlined,
    OrderedListOutlined,
    ShoppingOutlined,
    StarOutlined,
    UserOutlined,
} from '@ant-design/icons';
import { Layout, Menu } from 'antd';

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
            <div className="demo-logo-vertical" style={{ height: 64 }} />
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
                        label: 'Bảng điều khiển',
                    },
                    {
                        key: '/books',
                        icon: <ShoppingOutlined />,
                        label: 'Sách',
                    },
                    {
                        key: '/orders',
                        icon: <OrderedListOutlined />,
                        label: 'Đơn hàng',
                    },
                    {
                        key: '/ratings',
                        icon: <StarOutlined />,
                        label: 'Đánh giá',
                    },
                    {
                        key: '/users',
                        icon: <UserOutlined />,
                        label: 'Tài khoản',
                    },
                ]}
            />
        </Sider>
    );
};

export default AdminDrawer;
