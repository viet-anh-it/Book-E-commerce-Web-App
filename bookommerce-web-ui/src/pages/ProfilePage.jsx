import React, { useState, useEffect } from 'react';
import {
    Layout,
    Typography,
    Card,
    Form,
    Input,
    Button,
    Radio,
    DatePicker,
    Row,
    Col,
    Avatar,
    message,
    Breadcrumb,
    theme,
    Space,
    Divider,
} from 'antd';
import {
    UserOutlined,
    MailOutlined,
    PhoneOutlined,
    CalendarOutlined,
    ManOutlined,
    WomanOutlined,
    SaveOutlined,
    ArrowLeftOutlined,
} from '@ant-design/icons';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { updateProfile } from '../api/user';

const { Content } = Layout;
const { Title, Text, Paragraph } = Typography;

const ProfilePage = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const {
        token: { colorBgContainer, borderRadiusLG, colorPrimary, colorBgLayout },
    } = theme.useToken();
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (user) {
            form.setFieldsValue({
                firstName: user.firstName || '',
                lastName: user.lastName || '',
                email: user.email || '',
                phoneNumber: user.phoneNumber || '',
                gender: user.gender || 'MALE',
                dateOfBirth: user.dateOfBirth ? dayjs(user.dateOfBirth) : null,
            });
        }
    }, [user, form]);

    const onFinish = async (values) => {
        setLoading(true);
        try {
            const payload = {
                ...values,
                dateOfBirth: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : null,
            };

            await updateProfile(payload);

            message.success('Cập nhật hồ sơ thành công!');
        } catch (error) {
            console.error('Update profile error:', error);
            message.error('Cập nhật hồ sơ thất bại: ' + (error.response?.data?.message || error.message || 'Lỗi không xác định'));
        } finally {
            setLoading(false);
        }
    };

    return (
        <Content style={{ padding: '24px', minHeight: 'calc(100vh - 64px)', background: colorBgLayout }}>
            <div style={{ maxWidth: 1200, margin: '0 auto' }}>
                <Breadcrumb
                    style={{ marginBottom: 16 }}
                    items={[
                        { title: <Button type="link" style={{ padding: 0, height: 'auto' }} onClick={() => navigate('/')}>Trang chủ</Button> },
                        { title: 'Hồ sơ cá nhân' },
                    ]}
                />

                <Row gutter={[24, 24]}>
                    {/* Sidebar / Overview */}
                    <Col xs={24} md={8} lg={6}>
                        <Card
                            variant="borderless"
                            style={{
                                borderRadius: borderRadiusLG,
                                boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
                                textAlign: 'center',
                            }}
                        >
                            <Avatar
                                size={100}
                                icon={<UserOutlined />}
                                src={user?.avatarUrl}
                                style={{
                                    backgroundColor: colorPrimary,
                                    marginBottom: 16,
                                    border: `4px solid ${colorBgContainer}`,
                                    boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                                }}
                            />
                            <Title level={4} style={{ marginBottom: 4 }}>
                                {user?.lastName && user?.firstName
                                    ? `${user.lastName} ${user.firstName}`
                                    : user?.username || 'Người dùng'}
                            </Title>
                            <Text type="secondary">{user?.email || 'Chưa cập nhật email'}</Text>

                            <Divider />

                            <div style={{ textAlign: 'left' }}>
                                <Space vertical style={{ width: '100%' }}>
                                    <Text strong><UserOutlined style={{ marginRight: 8 }} /> Tài khoản</Text>
                                    <Paragraph style={{ paddingLeft: 24, margin: 0 }}>{user?.username}</Paragraph>

                                    <Text strong><CalendarOutlined style={{ marginRight: 8 }} /> Ngày tham gia</Text>
                                    <Paragraph style={{ paddingLeft: 24, margin: 0 }}>
                                        {user?.createdAt ? dayjs(user.createdAt).format('DD/MM/YYYY') : 'N/A'}
                                    </Paragraph>
                                </Space>
                            </div>
                        </Card>
                    </Col>

                    {/* Edit Form */}
                    <Col xs={24} md={16} lg={18}>
                        <Card
                            title={<Title level={3} style={{ margin: 0 }}>Chỉnh sửa hồ sơ</Title>}
                            variant="borderless"
                            style={{
                                borderRadius: borderRadiusLG,
                                boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
                            }}
                            extra={
                                <Button
                                    icon={<ArrowLeftOutlined />}
                                    onClick={() => navigate(-1)}
                                    type="text"
                                >
                                    Quay lại
                                </Button>
                            }
                        >
                            <Form
                                form={form}
                                layout="vertical"
                                onFinish={onFinish}
                                initialValues={{ gender: 'MALE' }}
                                requiredMark="optional"
                            >
                                <Row gutter={16}>
                                    <Col xs={24} sm={12}>
                                        <Form.Item
                                            name="lastName"
                                            label="Họ"
                                            rules={[{ required: true, message: 'Vui lòng nhập họ' }]}
                                        >
                                            <Input prefix={<UserOutlined />} placeholder="Nhập họ" size="large" />
                                        </Form.Item>
                                    </Col>
                                    <Col xs={24} sm={12}>
                                        <Form.Item
                                            name="firstName"
                                            label="Tên"
                                            rules={[{ required: true, message: 'Vui lòng nhập tên' }]}
                                        >
                                            <Input prefix={<UserOutlined />} placeholder="Nhập tên" size="large" />
                                        </Form.Item>
                                    </Col>
                                </Row>

                                <Row gutter={16}>
                                    <Col xs={24} sm={12}>
                                        <Form.Item
                                            name="email"
                                            label="Email"
                                            rules={[
                                                { required: true, message: 'Vui lòng nhập email' },
                                                { type: 'email', message: 'Email không hợp lệ' }
                                            ]}
                                        >
                                            <Input prefix={<MailOutlined />} placeholder="Nhập email" size="large" />
                                        </Form.Item>
                                    </Col>
                                    <Col xs={24} sm={12}>
                                        <Form.Item
                                            name="phoneNumber"
                                            label="Số điện thoại"
                                            rules={[
                                                { required: true, message: 'Vui lòng nhập số điện thoại' },
                                                { pattern: /^[0-9+]{10,12}$/, message: 'Số điện thoại không hợp lệ' }
                                            ]}
                                        >
                                            <Input prefix={<PhoneOutlined />} placeholder="Nhập số điện thoại" size="large" />
                                        </Form.Item>
                                    </Col>
                                </Row>

                                <Row gutter={16}>
                                    <Col xs={24} sm={12}>
                                        <Form.Item
                                            name="gender"
                                            label="Giới tính"
                                        >
                                            <Radio.Group size="large" style={{ width: '100%' }}>
                                                <Radio.Button value="MALE" style={{ width: '33.33%', textAlign: 'center' }}>
                                                    <ManOutlined /> Nam
                                                </Radio.Button>
                                                <Radio.Button value="FEMALE" style={{ width: '33.33%', textAlign: 'center' }}>
                                                    <WomanOutlined /> Nữ
                                                </Radio.Button>
                                                <Radio.Button value="OTHER" style={{ width: '33.33%', textAlign: 'center' }}>
                                                    Khác
                                                </Radio.Button>
                                            </Radio.Group>
                                        </Form.Item>
                                    </Col>
                                    <Col xs={24} sm={12}>
                                        <Form.Item
                                            name="dateOfBirth"
                                            label="Ngày sinh"
                                        >
                                            <DatePicker
                                                style={{ width: '100%' }}
                                                size="large"
                                                format="DD/MM/YYYY"
                                                placeholder="Chọn ngày sinh"
                                            />
                                        </Form.Item>
                                    </Col>
                                </Row>

                                <Divider />

                                <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
                                    <Button
                                        type="primary"
                                        htmlType="submit"
                                        icon={<SaveOutlined />}
                                        loading={loading}
                                        size="large"
                                        style={{ minWidth: 150, borderRadius: 8 }}
                                    >
                                        Lưu thay đổi
                                    </Button>
                                </Form.Item>
                            </Form>
                        </Card>
                    </Col>
                </Row>
            </div>
        </Content>
    );
};

export default ProfilePage;
