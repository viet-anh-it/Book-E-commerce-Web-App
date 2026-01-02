import React from 'react';
import { Layout, Switch, Flex, Typography, theme as antTheme, Button } from 'antd';
import { SunOutlined, MoonOutlined, MenuFoldOutlined, MenuUnfoldOutlined, LogoutOutlined } from '@ant-design/icons';
import { useAuth } from '../contexts/AuthContext';

const { Header } = Layout;
const { Title } = Typography;

/**
 * AdminHeader Component
 * 
 * Displays the brand logo, theme toggle switch, and logout button.
 * 
 * @param {Object} props
 * @param {boolean} props.isDarkMode - Current theme mode status (true if dark)
 * @param {Function} props.onThemeChange - Function to toggle theme
 * @param {boolean} props.collapsed - Current sider collapse state
 * @param {Function} props.setCollapsed - Function to set sider collapse state
 */
const AdminHeader = ({ isDarkMode, onThemeChange, collapsed, setCollapsed }) => {
    // Access standard design tokens to ensure consistent spacing/colors
    const {
        token: { colorBgContainer, paddingInline },
    } = antTheme.useToken();

    const { logout } = useAuth();

    return (
        <Header
            style={{
                padding: 0,
                background: colorBgContainer,
                paddingLeft: paddingInline,
                paddingRight: paddingInline,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
            }}
        >
            <Flex align="center" gap="middle">
                <Button
                    type="text"
                    icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                    onClick={() => setCollapsed(!collapsed)}
                    style={{
                        fontSize: '16px',
                        width: 64,
                        height: 64,
                    }}
                />

                {/* Brand Name / Logo */}
                <Title level={3} style={{ margin: 0 }}>
                    Bookommerce
                </Title>
            </Flex>

            {/* Actions - Theme Toggle and Logout */}
            <Flex gap="middle" align="center">
                <Switch
                    checkedChildren={<MoonOutlined />}
                    unCheckedChildren={<SunOutlined />}
                    checked={isDarkMode}
                    onChange={onThemeChange}
                />

                <Button
                    type="primary"
                    danger
                    icon={<LogoutOutlined />}
                    onClick={logout}
                >
                    Logout
                </Button>
            </Flex>
        </Header>
    );
};

export default AdminHeader;
