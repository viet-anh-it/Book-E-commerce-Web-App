import React from 'react';
import { Card, Typography, Button, Divider, Row, Col } from 'antd';

const { Title, Text } = Typography;

const CartSummary = ({ subtotal = 0, total = 0, onCheckout }) => {
    return (
        <Card title="Order Summary" bordered={false} style={{ height: 'fit-content' }}>
            <Row justify="space-between" style={{ marginBottom: '12px' }}>
                <Text>Subtotal</Text>
                <Text strong>{subtotal.toLocaleString('vi-VN')} đ</Text>
            </Row>
            <Row justify="space-between" style={{ marginBottom: '12px' }}>
                <Text>VAT</Text>
                <Text type="secondary">Included</Text>
            </Row>
            <Row justify="space-between" style={{ marginBottom: '12px' }}>
                <Text>Shipping</Text>
                <Text type="secondary">Pay on Delivery</Text>
            </Row>
            <Divider style={{ margin: '12px 0' }} />
            <Row justify="space-between" style={{ marginBottom: '24px' }}>
                <Title level={4} style={{ margin: 0 }}>Total</Title>
                <Title level={4} style={{ margin: 0, color: '#faad14' }}>
                    {total.toLocaleString('vi-VN')} đ
                </Title>
            </Row>
            <Button type="primary" block size="large" onClick={onCheckout}>
                Proceed to Checkout
            </Button>
        </Card>
    );
};

export default CartSummary;
