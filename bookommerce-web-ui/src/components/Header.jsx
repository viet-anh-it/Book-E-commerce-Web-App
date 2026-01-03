import React, { useState } from 'react';
import { Layout, Menu, Badge, Avatar, Button, Space, Typography, theme, Switch, Grid, Drawer } from 'antd';
import { ShoppingCartOutlined, UserOutlined, BookOutlined, MenuOutlined, SunOutlined, MoonOutlined } from '@ant-design/icons';
import { useAuth } from '../contexts/AuthContext';

const { Header: AntHeader } = Layout;
const { Title } = Typography;
const { useBreakpoint } = Grid;

import { useNavigate, useLocation } from 'react-router-dom';
import PropTypes from 'prop-types';

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
        } else if (key === 'profile') {
            navigate('/profile');
        }
    };

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

    const menuItems = [
        ...(location.pathname === '/' ? [{ key: 'home', label: 'Shopping' }] : []),
        ...(user ? [{ key: 'profile', label: 'Hồ sơ' }] : []),
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

                    {screens.md && (
                        <Space>
                            {user ? (
                                <>
                                    <span style={{ fontWeight: 500 }}>Welcome, {user.username}!</span>
                                    <Button type="link" onClick={() => navigate('/profile')}>
                                        Hồ sơ
                                    </Button>
                                    <Button type="default" href='https://bff.bookommerce.com:8181/confirm-logout'>
                                        Logout
                                    </Button>
                                </>
                            ) : (
                                <>
                                    <Button type="text" onClick={() => window.location.href = 'https://bff.bookommerce.com:8181/oauth2/authorization/bff'}>
                                        Log in
                                    </Button>
                                    <Button type="primary" onClick={() => window.location.href = 'https://auth.bookommerce.com:8282/page/signup'}>
                                        Sign up
                                    </Button>
                                </>
                            )}
                        </Space>
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