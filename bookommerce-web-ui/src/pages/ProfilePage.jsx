import {
    ArrowLeftOutlined,
    CalendarOutlined,
    CameraOutlined,
    EditOutlined,
    LockOutlined,
    MailOutlined,
    ManOutlined,
    PhoneOutlined,
    SaveOutlined,
    UserOutlined,
    WomanOutlined
} from '@ant-design/icons';
import {
    Avatar,
    Breadcrumb,
    Button,
    Card,
    Col,
    DatePicker,
    Divider,
    Form,
    Input,
    Layout,
    message,
    Modal,
    Radio,
    Row,
    Space,
    theme,
    Typography,
    Upload,
} from 'antd';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { changeEmail, changePassword, getProfile, updateProfile, uploadAvatar } from '../api/user';
import { useAuth } from '../contexts/AuthContext';

const { Content } = Layout;
const { Title, Text, Paragraph } = Typography;

const ProfilePage = () => {
    const { user, refreshUser } = useAuth();
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const {
        token: { colorBgContainer, borderRadiusLG, colorPrimary, colorBgLayout },
    } = theme.useToken();
    const [loading, setLoading] = useState(false);
    const [isEmailModalVisible, setIsEmailModalVisible] = useState(false);
    const [emailLoading, setEmailLoading] = useState(false);
    const [emailForm] = Form.useForm();

    const [isPasswordModalVisible, setIsPasswordModalVisible] = useState(false);
    const [passwordLoading, setPasswordLoading] = useState(false);
    const [passwordForm] = Form.useForm();

    const [avatarLoading, setAvatarLoading] = useState(false);
    const [isAvatarModalVisible, setIsAvatarModalVisible] = useState(false);
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);

    const [profileData, setProfileData] = useState(null);

    const fetchProfile = async () => {
        form.setFields(Object.keys(form.getFieldsValue()).map(key => ({ name: key, errors: [] })));
        setLoading(true);
        try {
            const response = await getProfile();
            const data = response.data.data;
            setProfileData(data);
            form.setFieldsValue({
                firstName: data.firstName || '',
                lastName: data.lastName || '',
                phoneNumber: data.phone || '',
                gender: data.gender || 'MALE',
                dateOfBirth: data.dob ? dayjs(data.dob) : null,
            });
        } catch (error) {
            console.error('Fetch profile error:', error);
            const status = error.response?.status;
            const errorMsg = error.response?.data?.message || error.message || 'Lỗi không xác định';

            if ([401, 403, 500].includes(status)) {
                message.error(errorMsg);
            } else {
                Modal.error({
                    title: 'Lỗi không xác định',
                    content: errorMsg,
                    centered: true,
                });
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProfile();
    }, [form]);

    const onFinish = async (values) => {
        form.setFields(Object.keys(form.getFieldsValue()).map(key => ({ name: key, errors: [] })));
        setLoading(true);
        try {
            const payload = {
                firstName: values.firstName,
                lastName: values.lastName,
                phone: values.phoneNumber,
                gender: values.gender,
                dob: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : null,
            };

            await updateProfile(payload);

            message.success('Cập nhật hồ sơ thành công!');
            await fetchProfile();
        } catch (error) {
            console.error('Update profile error:', error);
            const status = error.response?.status;
            const errorData = error.response?.data;
            const errorMsg = errorData?.message || error.message || 'Lỗi không xác định';

            if (status === 400 && errorData?.errors?.fieldErrors) {
                const fieldErrors = errorData.errors.fieldErrors;
                const formErrors = [];

                // Map backend field names to frontend form names
                const fieldMapping = {
                    phone: 'phoneNumber',
                    dob: 'dateOfBirth'
                };

                Object.keys(fieldErrors).forEach(backendField => {
                    const frontendField = fieldMapping[backendField] || backendField;
                    formErrors.push({
                        name: frontendField,
                        errors: fieldErrors[backendField],
                    });
                });

                form.setFields(formErrors);
                message.error('Vui lòng kiểm tra lại thông tin nhập vào!');
            } else if ([401, 403, 500].includes(status)) {
                message.error(errorMsg);
            } else {
                Modal.error({
                    title: 'Cập nhật hồ sơ thất bại',
                    content: errorMsg,
                    centered: true,
                });
            }
        } finally {
            setLoading(false);
        }
    };

    const handleEmailChange = async (values) => {
        setEmailLoading(true);
        try {
            await changeEmail(values.newEmail);
            message.success('Yêu cầu thay đổi email đã được gửi. Vui lòng kiểm tra hộp thư của bạn!');
            setIsEmailModalVisible(false);
            emailForm.resetFields();
        } catch (error) {
            console.error('Change email error:', error);
            message.error('Thay đổi email thất bại: ' + (error.response?.data?.message || error.message || 'Lỗi không xác định'));
        } finally {
            setEmailLoading(false);
        }
    };

    const handlePasswordChange = async (values) => {
        setPasswordLoading(true);
        try {
            await changePassword({
                currentPassword: values.currentPassword,
                newPassword: values.newPassword,
            });
            message.success('Mật khẩu đã được thay đổi thành công!');
            setIsPasswordModalVisible(false);
            passwordForm.resetFields();
        } catch (error) {
            console.error('Change password error:', error);
            message.error('Thay đổi mật khẩu thất bại: ' + (error.response?.data?.message || error.message || 'Lỗi không xác định'));
        } finally {
            setPasswordLoading(false);
        }
    };

    const handleAvatarSelect = (info) => {
        const file = info.file.originFileObj || info.file;
        if (file) {
            setSelectedFile(file);
            const reader = new FileReader();
            reader.onload = (e) => setPreviewUrl(e.target.result);
            reader.readAsDataURL(file);
        }
    };

    const handleAvatarSubmit = async () => {
        if (!selectedFile) {
            message.error('Vui lòng chọn một tầm ảnh!');
            return;
        }

        setAvatarLoading(true);
        try {
            await uploadAvatar(selectedFile);
            message.success('Cập nhật ảnh đại diện thành công!');
            await refreshUser();
            setIsAvatarModalVisible(false);
            setSelectedFile(null);
            setPreviewUrl(null);
        } catch (error) {
            console.error('Upload avatar error:', error);
            message.error('Cập nhật ảnh đại diện thất bại: ' + (error.response?.data?.message || error.message || 'Lỗi không xác định'));
        } finally {
            setAvatarLoading(false);
        }
    };

    const beforeAvatarUpload = (file) => {
        return false; // Prevent auto upload
    };

    return (
        <Content style={{ padding: '24px', minHeight: 'calc(100vh - 64px)', background: colorBgLayout }}>
            <div style={{ width: '100%', margin: '0 auto' }}>
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
                            <div style={{ position: 'relative', display: 'inline-block', marginBottom: 16 }}>
                                <div
                                    style={{ cursor: 'pointer', position: 'relative' }}
                                    onClick={() => setIsAvatarModalVisible(true)}
                                >
                                    <Avatar
                                        size={100}
                                        icon={<UserOutlined />}
                                        src={user?.avatarUrl || 'https://i.pravatar.cc/150?u=fake'}
                                        style={{
                                            backgroundColor: colorPrimary,
                                            border: `4px solid ${colorBgContainer}`,
                                            boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                                        }}
                                    />
                                    <div
                                        style={{
                                            position: 'absolute',
                                            bottom: 4,
                                            right: 4,
                                            width: 28,
                                            height: 28,
                                            borderRadius: '50%',
                                            backgroundColor: colorBgContainer,
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                                        }}
                                    >
                                        <CameraOutlined style={{ color: colorPrimary, fontSize: 14 }} />
                                    </div>
                                </div>
                            </div>
                            <Title level={4} style={{ marginBottom: 4 }}>
                                {profileData?.lastName && profileData?.firstName
                                    ? `${profileData.lastName} ${profileData.firstName}`
                                    : user?.username || 'Người dùng'}
                            </Title>
                            <div style={{ marginBottom: 16 }}>
                                <Text type="secondary" style={{ display: 'block', marginBottom: 8 }}>
                                    {user?.email || user?.username}
                                </Text>
                                <Space direction="vertical" style={{ width: '100%' }}>
                                    <Button
                                        type="primary"
                                        ghost
                                        size="small"
                                        icon={<EditOutlined />}
                                        onClick={() => setIsEmailModalVisible(true)}
                                        style={{ borderRadius: 6, width: '100%' }}
                                    >
                                        Thay đổi email
                                    </Button>
                                    <Button
                                        type="default"
                                        size="small"
                                        icon={<LockOutlined />}
                                        onClick={() => setIsPasswordModalVisible(true)}
                                        style={{ borderRadius: 6, width: '100%' }}
                                    >
                                        Đổi mật khẩu
                                    </Button>
                                </Space>
                            </div>

                            <Divider />

                            <div style={{ textAlign: 'left' }}>
                                <Space direction="vertical" style={{ width: '100%' }}>
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
                                        >
                                            <Input prefix={<UserOutlined />} placeholder="Nhập họ" size="large" />
                                        </Form.Item>
                                    </Col>
                                    <Col xs={24} sm={12}>
                                        <Form.Item
                                            name="firstName"
                                            label="Tên"
                                        >
                                            <Input prefix={<UserOutlined />} placeholder="Nhập tên" size="large" />
                                        </Form.Item>
                                    </Col>
                                </Row>

                                <Row gutter={16}>
                                    <Col xs={24} sm={12}>
                                        <Form.Item
                                            name="phoneNumber"
                                            label="Số điện thoại"
                                        >
                                            <Input prefix={<PhoneOutlined />} placeholder="Nhập số điện thoại" size="large" />
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

                                <Row gutter={16}>
                                    <Col xs={24} sm={24}>
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

            <Modal
                title="Thay đổi địa chỉ email"
                open={isEmailModalVisible}
                onCancel={() => {
                    setIsEmailModalVisible(false);
                    emailForm.resetFields();
                }}
                footer={null}
                centered
                destroyOnClose
                transitionName=""
                maskTransitionName=""
            >
                <div style={{ marginBottom: 20 }}>
                    <Text type="secondary">
                        Hệ thống sẽ gửi mã xác thực hoặc liên kết xác nhận đến email mới của bạn.
                    </Text>
                </div>
                <Form
                    form={emailForm}
                    layout="vertical"
                    onFinish={handleEmailChange}
                    requiredMark={false}
                >
                    <Form.Item
                        name="newEmail"
                        label="Email mới"
                    >
                        <Input prefix={<MailOutlined />} placeholder="Nhập địa chỉ email mới" size="large" />
                    </Form.Item>

                    <Form.Item style={{ marginBottom: 0, textAlign: 'right', marginTop: 24 }}>
                        <Space>
                            <Button onClick={() => setIsEmailModalVisible(false)}>
                                Hủy
                            </Button>
                            <Button
                                type="primary"
                                htmlType="submit"
                                loading={emailLoading}
                                icon={<SaveOutlined />}
                            >
                                Xác nhận thay đổi
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title="Đổi mật khẩu"
                open={isPasswordModalVisible}
                onCancel={() => {
                    setIsPasswordModalVisible(false);
                    passwordForm.resetFields();
                }}
                footer={null}
                centered
                destroyOnClose
                transitionName=""
                maskTransitionName=""
            >
                <Form
                    form={passwordForm}
                    layout="vertical"
                    onFinish={handlePasswordChange}
                    requiredMark={false}
                >
                    <Form.Item
                        name="currentPassword"
                        label="Mật khẩu hiện tại"
                    >
                        <Input.Password prefix={<LockOutlined />} placeholder="Nhập mật khẩu hiện tại" size="large" />
                    </Form.Item>

                    <Form.Item
                        name="newPassword"
                        label="Mật khẩu mới"
                    >
                        <Input.Password prefix={<LockOutlined />} placeholder="Nhập mật khẩu mới" size="large" />
                    </Form.Item>

                    <Form.Item
                        name="confirmPassword"
                        label="Xác nhận mật khẩu mới"
                        dependencies={['newPassword']}
                    >
                        <Input.Password prefix={<LockOutlined />} placeholder="Xác nhận mật khẩu mới" size="large" />
                    </Form.Item>

                    <Form.Item style={{ marginBottom: 0, textAlign: 'right', marginTop: 24 }}>
                        <Space>
                            <Button onClick={() => setIsPasswordModalVisible(false)}>
                                Hủy
                            </Button>
                            <Button
                                type="primary"
                                htmlType="submit"
                                loading={passwordLoading}
                                icon={<SaveOutlined />}
                            >
                                Đổi mật khẩu
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title="Thay đổi ảnh đại diện"
                open={isAvatarModalVisible}
                onCancel={() => {
                    setIsAvatarModalVisible(false);
                    setSelectedFile(null);
                    setPreviewUrl(null);
                }}
                onOk={handleAvatarSubmit}
                confirmLoading={avatarLoading}
                okText="Xác nhận"
                cancelText="Hủy"
                centered
                destroyOnClose
                width={800}
                transitionName=""
                maskTransitionName=""
            >
                <div style={{ textAlign: 'center', padding: '40px 0' }}>
                    <div style={{ marginBottom: 32 }}>
                        <Avatar
                            size={300}
                            src={previewUrl || user?.avatarUrl || 'https://i.pravatar.cc/150?u=fake'}
                            icon={<UserOutlined style={{ fontSize: 100 }} />}
                            style={{
                                border: `6px solid ${colorPrimary}`,
                                boxShadow: '0 8px 24px rgba(0,0,0,0.12)'
                            }}
                        />
                    </div>

                    <Upload
                        showUploadList={false}
                        beforeUpload={() => false}
                        onChange={handleAvatarSelect}
                        accept="image/*"
                    >
                        <Button icon={<CameraOutlined />} size="large" style={{ minWidth: 220 }}>
                            Chọn ảnh từ máy tính
                        </Button>
                    </Upload>

                    {selectedFile && (
                        <div style={{ marginTop: 20 }}>
                            <Text strong style={{ fontSize: 16 }}>Đã chọn: </Text>
                            <Text type="secondary" style={{ fontSize: 16 }}>{selectedFile.name}</Text>
                        </div>
                    )}
                </div>
            </Modal>
        </Content>
    );
};

export default ProfilePage;
