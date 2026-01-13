import { Button, Card, Divider, Row, Typography } from 'antd';

const { Title, Text } = Typography;

const CartSummary = ({ subtotal = 0, total = 0, onCheckout }) => {
    return (
        <Card title="Thông tin đơn hàng" bordered={false} style={{ height: 'fit-content' }}>
            <Row justify="space-between" style={{ marginBottom: '12px' }}>
                <Text>Tạm tính</Text>
                <Text strong>{subtotal.toLocaleString('vi-VN')} đ</Text>
            </Row>
            <Row justify="space-between" style={{ marginBottom: '12px' }}>
                <Text>VAT</Text>
                <Text type="secondary">Đã bao gồm</Text>
            </Row>
            <Row justify="space-between" style={{ marginBottom: '12px' }}>
                <Text>Vận chuyển</Text>
                <Text type="secondary">Thanh toán khi nhận hàng</Text>
            </Row>
            <Divider style={{ margin: '12px 0' }} />
            <Row justify="space-between" style={{ marginBottom: '24px' }}>
                <Title level={4} style={{ margin: 0 }}>Tổng cộng</Title>
                <Title level={4} style={{ margin: 0, color: '#faad14' }}>
                    {total.toLocaleString('vi-VN')} đ
                </Title>
            </Row>
            <Button type="primary" block size="large" onClick={onCheckout}>
                Tiến hành thanh toán
            </Button>
        </Card>
    );
};

export default CartSummary;
