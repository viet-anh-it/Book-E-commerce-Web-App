import { FacebookFilled, InstagramFilled, LinkedinFilled, TwitterSquareFilled } from '@ant-design/icons';
import { Col, Layout, Row, Space, theme, Typography } from 'antd';

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
                                Điểm đến hàng đầu của bạn để khám phá và mua sách. Chúng tôi cung cấp một bộ sưu tập lớn các đầu sách thuộc mọi thể loại.
                            </Text>
                        </Space>
                    </Col>

                    {/* Quick Links */}
                    <Col xs={24} sm={12} md={6}>
                        <Space direction="vertical" size="middle">
                            <Title level={5} style={{ color: '#fff', margin: 0 }}>Liên kết nhanh</Title>
                            <Space direction="vertical" size="small">
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Trang chủ</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Bán chạy nhất</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Sách mới</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Liên hệ</Link>
                            </Space>
                        </Space>
                    </Col>

                    {/* Customer Service */}
                    <Col xs={24} sm={12} md={6}>
                        <Space direction="vertical" size="middle">
                            <Title level={5} style={{ color: '#fff', margin: 0 }}>Chăm sóc khách hàng</Title>
                            <Space direction="vertical" size="small">
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Câu hỏi thường gặp</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Vận chuyển & Đổi trả</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Theo dõi đơn hàng</Link>
                                <Link href="#" style={{ color: 'rgba(255, 255, 255, 0.65)' }}>Chính sách bảo mật</Link>
                            </Space>
                        </Space>
                    </Col>

                    {/* Stay Connected */}
                    <Col xs={24} sm={12} md={6}>
                        <Space direction="vertical" size="middle">
                            <Title level={5} style={{ color: '#fff', margin: 0 }}>Kết nối với chúng tôi</Title>
                            <Space size="large">
                                <Link href="#" style={{ color: '#fff', fontSize: '24px' }}><FacebookFilled /></Link>
                                <Link href="#" style={{ color: '#fff', fontSize: '24px' }}><TwitterSquareFilled /></Link>
                                <Link href="#" style={{ color: '#fff', fontSize: '24px' }}><InstagramFilled /></Link>
                                <Link href="#" style={{ color: '#fff', fontSize: '24px' }}><LinkedinFilled /></Link>
                            </Space>
                            <Text style={{ color: 'rgba(255, 255, 255, 0.65)' }}>
                                Đăng ký nhận bản tin để cập nhật thông tin và ưu đãi độc quyền.
                            </Text>
                        </Space>
                    </Col>
                </Row>

                <div style={{ marginTop: 48, paddingTop: 24, borderTop: '1px solid rgba(255, 255, 255, 0.1)', textAlign: 'center' }}>
                    <Text style={{ color: 'rgba(255, 255, 255, 0.45)' }}>
                        Bookommerce ©{new Date().getFullYear()} Được tạo bởi Ant Design
                    </Text>
                </div>
            </div>
        </Footer>
    );
};

export default AppFooter;
