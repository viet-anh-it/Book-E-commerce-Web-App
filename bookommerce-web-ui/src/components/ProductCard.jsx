import { CloseOutlined, ShoppingCartOutlined } from '@ant-design/icons';
import { Button, Card, InputNumber, notification, Rate, theme, Typography } from 'antd';
import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { addToCart } from '../api/cart';
import { useAuth } from '../contexts/AuthContext';
import ToastProgressBar from './common/ToastProgressBar';

const { Text, Title } = Typography;

const ProductCard = ({ product }) => {
    const {
        token: { colorBgContainer, colorText },
    } = theme.useToken();

    const navigate = useNavigate();
    const location = useLocation();
    const { user } = useAuth();
    const [quantity, setQuantity] = useState(1);
    const [api, contextHolder] = notification.useNotification();

    const handleAddToCart = async (e) => {
        e.stopPropagation();

        try {
            await addToCart({ bookId: product.id, quantity });

            const key = `success-${Date.now()}`;
            const duration = 3;

            api.success({
                message: 'Thêm vào giỏ hàng thành công',
                description: (
                    <div style={{ position: 'relative', paddingBottom: 10 }}>
                        <ToastProgressBar duration={duration} onClose={() => api.destroy(key)} />
                    </div>
                ),
                key,
                duration: 0, // Disable auto-close, let ProgressBar handle it
                placement: 'topRight',
                closeIcon: <CloseOutlined style={{ color: colorText }} />,
            });

        } catch (error) {
            console.error("Add to cart error:", error);
            const key = `error-${Date.now()}`;
            const duration = 4;

            let message = 'Validation failed';
            let description = 'Check your input';

            if (error.response) {
                if (error.response.status === 401) return;
                const { status, data } = error.response;
                if (status === 200) {
                    // Sometimes 200 might cloak an error if backend style is weird, but per spec 200 is success. 
                    // This block is unlikely reachable if axios throws, but just in case.
                } else if (status === 400) {
                    message = data.message || 'Validation failed';
                    if (data.errors && data.errors.fieldErrors) {
                        const fieldErrorMessages = Object.values(data.errors.fieldErrors).flat();
                        description = (
                            <ul style={{ paddingLeft: 20, margin: 0 }}>
                                {fieldErrorMessages.map((msg, idx) => <li key={idx}>{msg}</li>)}
                            </ul>
                        );
                    }
                } else {
                    message = 'Đã xảy ra lỗi!';
                    description = data.message || 'Có lỗi xảy ra.';
                }
            } else {
                message = 'Đã xảy ra lỗi!';
                description = 'Lỗi mạng hoặc không thể kết nối máy chủ.';
            }

            api.error({
                message,
                description: (
                    <div style={{ position: 'relative', paddingBottom: 10 }}>
                        {description}
                        <ToastProgressBar duration={duration} onClose={() => api.destroy(key)} />
                    </div>
                ),
                key,
                duration: 0,
                placement: 'topRight',
                closeIcon: <CloseOutlined style={{ color: colorText }} />,
            });
        }
    };

    const handleQuantityChange = (value) => {
        setQuantity(value);
    };

    return (
        <>
            {contextHolder}
            <Card
                className="product-card"
                hoverable
                onClick={() => navigate(`/books/${product.id}`, { state: { from: location, scrollY: window.scrollY } })}
                style={{ width: '100%', height: '100%', display: 'flex', flexDirection: 'column' }}
                cover={
                    <div style={{
                        position: 'relative',
                        width: '100%',
                        paddingTop: '133.33%', // 3:4 Aspect Ratio (4/3 * 100)
                        overflow: 'hidden',
                        backgroundColor: '#f0f0f0'
                    }}>
                        <img
                            className="product-image"
                            alt={product.title}
                            src={`https://bff.bookommerce.com:8181${product.thumbnailUrlPath}`}
                            style={{
                                position: 'absolute',
                                top: 0,
                                left: 0,
                                width: '100%',
                                height: '100%',
                                objectFit: 'cover',
                            }}
                        />
                    </div>
                }
                bodyStyle={{ flex: 1, display: 'flex', flexDirection: 'column', padding: 12 }}
                actions={[
                    user ? (
                        <div key="add-to-cart" style={{ padding: '0 12px', display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 8, width: '100%' }} onClick={e => e.stopPropagation()}>
                            <InputNumber
                                min={1}
                                max={99}
                                value={quantity}
                                onChange={handleQuantityChange}
                                size="middle"
                                style={{ width: 60 }}
                                onPressEnter={handleAddToCart}
                            />
                            <Button type="primary" icon={<ShoppingCartOutlined />} size="middle" onClick={handleAddToCart}>
                                Thêm
                            </Button>
                        </div>
                    ) : (
                        <div key="login-link" style={{ padding: '0 12px', display: 'flex', alignItems: 'center', justifyContent: 'flex-end', width: '100%' }} onClick={e => e.stopPropagation()}>
                            <a href="https://auth.bookommerce.com:8282/page/login/customer" className="auth-link" style={{ fontSize: '14px', fontWeight: '500', color: '#1890ff' }}>
                                Đăng nhập để mua sắm
                            </a>
                        </div>
                    )
                ]}
            >
                <div style={{ flex: 1 }}>
                    <Title level={5} ellipsis={{ rows: 2 }} style={{ marginBottom: 8, fontSize: 16 }}>
                        {product.title}
                    </Title>
                    <Text type="secondary" style={{ fontSize: 12, display: 'block', marginBottom: 4 }}>
                        {product.author}
                    </Text>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <Rate disabled allowHalf value={product.rating} style={{ fontSize: 12 }} />
                        <Text type="secondary" style={{ fontSize: 12 }}>({product.rating})</Text>
                    </div>
                </div>

                <div style={{ marginTop: 12 }}>
                    <Text strong style={{ fontSize: 18, color: '#fa541c' }}>
                        {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.price)}
                    </Text>
                </div>
            </Card>
        </>
    );
};

export default React.memo(ProductCard);
