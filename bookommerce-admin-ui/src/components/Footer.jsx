import { Layout, Typography } from 'antd';

const { Footer } = Layout;
const { Text } = Typography;

/**
 * AdminFooter Component
 * 
 * Displays the copyright information at the bottom of the layout.
 */
const AdminFooter = () => {
    return (
        <Footer style={{ textAlign: 'center' }}>
            <Text type="secondary">
                Bookommerce Admin ©{new Date().getFullYear()} Được tạo bởi Bookommerce Team
            </Text>
        </Footer>
    );
};

export default AdminFooter;
