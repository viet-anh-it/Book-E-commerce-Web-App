import React from 'react';
import { Layout, Switch, Flex, Typography, theme as antTheme, Button } from 'antd';
import { SunOutlined, MoonOutlined, MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons';

const { Header } = Layout;
const { Title } = Typography;

/**
 * AdminHeader Component
 * 
 * Displays the brand logo and the theme toggle switch.
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

            {/* Theme Toggle - Light/Dark Mode */}
            <Flex gap="small" align="center">
                <Switch
                    checkedChildren={<MoonOutlined />}
                    unCheckedChildren={<SunOutlined />}
                    checked={isDarkMode}
                    onChange={onThemeChange}
                />
            </Flex>
        </Header>
    );
};

export default AdminHeader;
