import { BookOutlined, LogoutOutlined, MenuOutlined, MoonOutlined, ShoppingCartOutlined, SunOutlined, UserOutlined } from '@ant-design/icons';
import { Avatar, Badge, Button, Drawer, Dropdown, Grid, Layout, Menu, Space, Switch, Typography, theme } from 'antd';
import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';

const { Header: AntHeader } = Layout;
const { Title } = Typography;
const { useBreakpoint } = Grid;

import PropTypes from 'prop-types';
import { useLocation, useNavigate } from 'react-router-dom';

const Header = ({ isDarkMode, onToggleTheme }) => {
    const { user, logout } = useAuth();
    const {
        token: { colorBgContainer, colorPrimary },
    } = theme.useToken();
    const screens = useBreakpoint();
    const [menuOpen, setMenuOpen] = useState(false);
    const [activeKey, setActiveKey] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    const scrollToProductGrid = () => {
        const element = document.getElementById('product-grid');
        if (element) {
            const headerOffset = 100;
            const elementPosition = element.getBoundingClientRect().top;
            const offsetPosition = elementPosition + window.scrollY - headerOffset;

            const startPosition = window.scrollY;
            const distance = offsetPosition - startPosition;
            const duration = 500;
            let start = null;

            const step = (timestamp) => {
                if (!start) start = timestamp;
                const progress = timestamp - start;
                const percentage = Math.min(progress / duration, 1);
                const ease = 1 - Math.pow(1 - percentage, 3);
                window.scrollTo(0, startPosition + distance * ease);

                if (progress < duration) {
                    window.requestAnimationFrame(step);
                }
            };
            window.requestAnimationFrame(step);
        }
    };

    const handleMenuClick = ({ key }) => {
        if (key === 'home') {
            if (location.pathname !== '/') {
                navigate('/');
            } else {
                scrollToProductGrid();
            }
        }
    };

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    }

    React.useEffect(() => {
        if (location.pathname === '/profile') {
            setActiveKey('profile');
            return;
        }

        const handleScroll = () => {
            const element = document.getElementById('product-grid');
            if (element) {
                const headerOffset = 100;
                const elementPosition = element.getBoundingClientRect().top + window.scrollY;
                if (window.scrollY >= elementPosition - headerOffset - 50) {
                    setActiveKey('home');
                } else {
                    setActiveKey('');
                }
            }
        };

        window.addEventListener('scroll', handleScroll);
        handleScroll();
        return () => window.removeEventListener('scroll', handleScroll);
    }, [location.pathname]);

    const userMenuItems = [
        {
            key: 'header',
            label: (
                <div style={{ padding: '4px 12px', textAlign: 'center' }}>
                    <Avatar
                        size={64}
                        src={user?.avatarUrl || 'https://i.pravatar.cc/150?u=fake'}
                        style={{ marginBottom: 8, backgroundColor: colorPrimary }}
                    />
                    <div style={{ fontWeight: 600, fontSize: '16px' }}>{user?.username}</div>
                </div>
            ),
            disabled: true,
        },
        {
            type: 'divider',
        },
        {
            key: 'profile',
            label: 'Hồ sơ',
            icon: <UserOutlined />,
            onClick: () => navigate('/profile'),
        },
        {
            type: 'divider',
        },
        {
            key: 'logout',
            label: 'Logout',
            icon: <LogoutOutlined />,
            danger: true,
            onClick: () => {
                const csrfToken = getCookie('XSRF-TOKEN');
                window.alert(csrfToken);
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = 'https://bff.bookommerce.com:8181/protected/logout';
                const input = document.createElement('input');
                input.name = '_csrf';
                input.value = csrfToken;
                form.appendChild(input);
                document.body.appendChild(form);
                form.submit();
            },
        },
    ];

    const menuItems = [
        ...(location.pathname === '/' ? [{ key: 'home', label: 'Shopping' }] : []),
    ];

    return (
        <AntHeader
            style={{
                position: 'sticky',
                top: 0,
                zIndex: 1000,
                width: '100%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                backgroundColor: colorBgContainer,
                boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
                padding: '0 24px',
            }}
        >
            {/* Logo Area */}
            <div
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    marginRight: screens.md ? 48 : 16,
                    cursor: 'pointer'
                }}
                onClick={() => navigate('/')}
            >
                {!screens.md && (
                    <Button
                        type="text"
                        icon={<MenuOutlined />}
                        onClick={(e) => {
                            e.stopPropagation();
                            setMenuOpen(true);
                        }}
                        style={{ marginRight: 8 }}
                    />
                )}
                <BookOutlined style={{ fontSize: 24, color: colorPrimary, marginRight: 8 }} />
                <Title level={4} style={{ margin: 0, color: colorPrimary, whiteSpace: 'nowrap' }}>
                    Bookommerce
                </Title>
            </div>

            {/* Navigation Menu (Desktop) */}
            {screens.md && (
                <Menu
                    mode="horizontal"
                    selectedKeys={[activeKey]}
                    onClick={handleMenuClick}
                    items={menuItems}
                    style={{ flex: 1, minWidth: 0, borderBottom: 'none' }}
                />
            )}

            {/* Mobile Menu Drawer */}
            <Drawer
                title="Menu"
                placement="left"
                onClose={() => setMenuOpen(false)}
                open={menuOpen}
                width={250}
            >
                <Menu
                    mode="vertical"
                    selectedKeys={[activeKey]}
                    onClick={(e) => {
                        handleMenuClick(e);
                        setMenuOpen(false);
                    }}
                    items={menuItems}
                    style={{ borderRight: 'none' }}
                />
            </Drawer>

            {/* Actions */}
            <div style={{ display: 'flex', alignItems: 'center', gap: screens.md ? 16 : 8 }}>
                <Space size={screens.md ? "large" : "small"}>
                    <Switch
                        checkedChildren={<MoonOutlined />}
                        unCheckedChildren={<SunOutlined />}
                        checked={isDarkMode}
                        onChange={onToggleTheme}
                    />
                    {user && location.pathname !== '/cart' && (
                        <Badge count={2} showZero>
                            <Button
                                type="text"
                                icon={<ShoppingCartOutlined style={{ fontSize: 20 }} />}
                                onClick={() => navigate('/cart')}
                            />
                        </Badge>
                    )}

                    {user ? (
                        <Dropdown
                            menu={{ items: userMenuItems }}
                            placement="bottomRight"
                            trigger={['hover', 'click']}
                        >
                            <div style={{ cursor: 'pointer', marginLeft: 8 }}>
                                <Avatar
                                    icon={<UserOutlined />}
                                    src={user?.avatarUrl || 'https://i.pravatar.cc/150?u=fake'}
                                    style={{ backgroundColor: colorPrimary }}
                                />
                            </div>
                        </Dropdown>
                    ) : (
                        screens.md && (
                            <Space>
                                <Button type="text" onClick={() => window.location.href = 'https://bff.bookommerce.com:8181/protected/oauth2/authorization/bff'}>
                                    Log in
                                </Button>
                                <Button type="primary" onClick={() => window.location.href = 'https://auth.bookommerce.com:8282/page/signup'}>
                                    Sign up
                                </Button>
                            </Space>
                        )
                    )}
                </Space>
            </div>
        </AntHeader>
    );
};

Header.propTypes = {
    isDarkMode: PropTypes.bool.isRequired,
    onToggleTheme: PropTypes.func.isRequired,
};

export default Header;