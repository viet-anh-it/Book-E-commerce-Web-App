import React from 'react';
import { Layout, Row, Col, Typography, Space, theme } from 'antd';
import { FacebookFilled, TwitterSquareFilled, InstagramFilled, LinkedinFilled } from '@ant-design/icons';

const { Footer } = Layout;
const { Title, Text, Link } = Typography;

const AppFooter = () => {
    const { token } = theme.useToken();

    return (
        <Footer style={{ background: '#001529', color: '#fff', padding: '48px 24px' }}>
            <div style={{ maxWidth: 1200, margin: '0 auto' }}>
                <Row gutter={[32, 32]}>
                    {/* About Section */}
                    <Col xs={24} sm={12} md={6}>
                        <Space direction="vertical" size="middle">
                            <Title level={4} style={{ color: '#fff', margin: 0 }}>Bookommerce</Title>
                            <Text style={{ color: 'rgba(255, 255, 255, 0.65)' }}>
                                Your premier destination for discovering and buying books. We offer a vast collection of titles across all genres.
                            </Text>
                        </Space>
                    </Col>

                    {/* Quick Links */}
                    <Col xs={24} sm={12} md={6}>
                        <Space direction="vertical" size="middle">
                            <Title level={5} style={{ color: '#fff', margin: 0 }}>Quick Links</Title>
                            <Space direction="vertical" size="small">
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Home</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Best Sellers</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>New Arrivals</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Contact Us</Link>
                            </Space>
                        </Space>
                    </Col>

                    {/* Customer Service */}
                    <Col xs={24} sm={12} md={6}>
                        <Space direction="vertical" size="middle">
                            <Title level={5} style={{ color: '#fff', margin: 0 }}>Customer Service</Title>
                            <Space direction="vertical" size="small">
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>FAQ</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Shipping & Returns</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Order Tracking</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Privacy Policy</Link>
                            </Space>
                        </Space>
                    </Col>

                    {/* Stay Connected */}
                    <Col xs={24} sm={12} md={6}>
                        <Space direction="vertical" size="middle">
                            <Title level={5} style={{ color: '#fff', margin: 0 }}>Stay Connected</Title>
                            <Space size="large">
                                <Link href="#" style={{ color: '#fff', fontSize: '24px' }}><FacebookFilled /></Link>
                                <Link href="#" style={{ color: '#fff', fontSize: '24px' }}><TwitterSquareFilled /></Link>
                                <Link href="#" style={{ color: '#fff', fontSize: '24px' }}><InstagramFilled /></Link>
                                <Link href="#" style={{ color: '#fff', fontSize: '24px' }}><LinkedinFilled /></Link>
                            </Space>
                            <Text style={{ color: 'rgba(255, 255, 255, 0.65)' }}>
                                Subscribe to our newsletter for updates and exclusive offers.
                            </Text>
                        </Space>
                    </Col>
                </Row>

                <div style={{ marginTop: 48, paddingTop: 24, borderTop: '1px solid rgba(255, 255, 255, 0.1)', textAlign: 'center' }}>
                    <Text style={{ color: 'rgba(255, 255, 255, 0.45)' }}>
                        Bookommerce ©{new Date().getFullYear()} Created with Ant Design
                    </Text>
                </div>
            </div>
        </Footer>
    );
};

export default AppFooter;
