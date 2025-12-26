import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Typography, InputNumber, Button, Image, Rate } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';

const { Text, Title } = Typography;

const CartItem = ({ item, onUpdateQuantity, onRemove }) => {
    const [inputValue, setInputValue] = useState(item.quantity);

    useEffect(() => {
        setInputValue(item.quantity);
    }, [item.quantity]);

    const handleStep = (value) => {
        onUpdateQuantity(item.id, value);
    };

    const handlePressEnter = () => {
        onUpdateQuantity(item.id, inputValue);
    };

    const handleChange = (value) => {
        setInputValue(value);
    };

    const handleBlur = () => {
        setInputValue(item.quantity);
    };
    return (
        <Card
            bordered={false}
            bodyStyle={{ padding: '16px' }}
            style={{
                marginBottom: '16px',
                boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
                borderRadius: '12px'
            }}
        >
            <Row gutter={[16, 16]} align="middle">
                {/* Product Image */}
                <Col xs={24} sm={4} md={3}>
                    <Image
                        src={`https://bff.bookommerce.com:8181${item.image}`}
                        alt={item.title}
                        style={{ width: '100%', borderRadius: '8px', objectFit: 'cover', aspectRatio: '2/3' }}
                        preview={false}
                    />
                </Col>

                {/* Product Details */}
                <Col xs={24} sm={8} md={9}>
                    <Title level={5} style={{ marginBottom: '4px', marginTop: 0 }}>
                        <Link to={`/books/${item.bookId}`} className="cart-item-title-link">
                            {item.title}
                        </Link>
                    </Title>
                    <Text type="secondary" style={{ display: 'block', marginBottom: '4px' }}>
                        {item.author}
                    </Text>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                        <span style={{ fontWeight: 'bold', fontSize: '14px' }}>{item.rating}</span>
                        <Rate disabled allowHalf defaultValue={item.rating} style={{ fontSize: '14px' }} />
                    </div>
                    <Text type="secondary">
                        {item.price.toLocaleString('vi-VN')} đ
                    </Text>
                </Col>

                {/* Quantity Control */}
                <Col xs={12} sm={6} md={5} style={{ textAlign: 'center' }}>
                    <InputNumber
                        min={1}
                        max={99}
                        value={inputValue}
                        onChange={handleChange}
                        onStep={handleStep}
                        onPressEnter={handlePressEnter}
                        onBlur={handleBlur}
                        changeOnWheel
                    />
                </Col>

                {/* Total Price */}
                <Col xs={12} sm={6} md={5} style={{ textAlign: 'center' }}>
                    <Text strong style={{ color: '#faad14', fontSize: '16px' }}>
                        {(item.price * item.quantity).toLocaleString('vi-VN')} đ
                    </Text>
                </Col>

                {/* Remove Button */}
                <Col xs={24} sm={2} md={2} style={{ textAlign: 'right' }}>
                    <Button
                        type="text"
                        danger
                        className="delete-button-active"
                        icon={<DeleteOutlined />}
                        onClick={() => onRemove(item.id)}
                    />
                </Col>
            </Row>
        </Card>
    );
};

export default CartItem;
