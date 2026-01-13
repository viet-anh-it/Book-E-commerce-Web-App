import { ArrowLeftOutlined, CloseOutlined } from '@ant-design/icons';
import { Button, Col, Empty, Row, Spin, Typography, message, notification, theme } from 'antd';
import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import BackToTopButton from '../components/BackToTopButton';
import CartItem from '../components/CartItem';
import CartSummary from '../components/CartSummary';

import { getCart, removeCartItem, updateCartItem } from '../api/cart';
import ToastProgressBar from '../components/common/ToastProgressBar';

const { Title } = Typography;

const CartPage = () => {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [subtotal, setSubtotal] = useState(0);
    const [total, setTotal] = useState(0);
    const [api, contextHolder] = notification.useNotification();
    const {
        token: { colorText },
    } = theme.useToken();



    // WAIT, I should just extract it.

    const fetchCart = async (showLoading = true) => {
        try {
            if (showLoading) setLoading(true);
            const response = await getCart();
            if (response.status === 200) {
                const mappedItems = response.data.cartItems.map(item => ({
                    id: item.id, // Cart Item ID
                    bookId: item.book.id,
                    title: item.book.title,
                    author: item.book.author,
                    price: item.book.price,
                    quantity: item.quantity,
                    rating: item.book.rating,
                    image: item.book.thumbnailUrlPath,
                }));
                setCartItems(mappedItems);
                setSubtotal(response.data.totalPrice);
                setTotal(response.data.totalPrice);
            }
        } catch (error) {
            console.error("Failed to fetch cart:", error);
            message.error("Failed to load cart data.");
        } finally {
            if (showLoading) setLoading(false);
        }
    };

    useEffect(() => {
        fetchCart(true);
    }, []);

    const handleUpdateQuantity = async (id, quantity) => {
        // Optimistic UI update or wait for server?
        // We will wait/try and then update state to be safe, or we can update state then revert.
        // Given the requirement is to "notify success", we likely should do the API call.
        // However, input number usually feels better with optimistic update.
        // But let's stick to the flow: User action -> API -> Toast -> Update UI.
        // Since InputNumber in CartItem calls this on change, we might want to debounce or accept it. 
        // For now, let's implement direct call as requested by "click ... or enter".

        // Actually, InputNumber onChange might fire frequently. But standard behaviour is okay.

        try {


            await updateCartItem(id, quantity);

            // Re-fetch cart data without full page loader to avoid flicker
            await fetchCart(false);

            const key = `update-success-${Date.now()}`;
            const duration = 5;

            api.success({
                message: 'Cập nhật số lượng thành công',
                description: (
                    <div style={{ position: 'relative', paddingBottom: 10 }}>
                        <ToastProgressBar duration={duration} onClose={() => api.destroy(key)} />
                    </div>
                ),
                key,
                duration: 0,
                placement: 'topRight',
                closeIcon: <CloseOutlined style={{ color: colorText }} />,
            });

        } catch (error) {
            console.error("Update quantity error:", error);
            const key = `update-error-${Date.now()}`;
            const duration = 5;

            let messageStr = 'Validation failed';
            let description = 'Check your input';

            if (error.response) {
                const { status, data } = error.response;
                if (status === 200) {
                    // Success
                } else if (status === 400) {
                    if (data.errors && data.errors.fieldErrors) {
                        const fieldErrors = Object.values(data.errors.fieldErrors).flat();
                        if (fieldErrors.length > 0) {
                            description = fieldErrors[0].replace(/^\d+:/, '');
                        }
                    }
                } else if (status === 401) {
                    messageStr = 'Unauthorized';
                    description = 'Please login to continue.';
                } else if (status === 403) {
                    messageStr = 'Forbidden';
                    description = 'You are not allowed to do this.';
                } else {
                    messageStr = 'Unexpected Error Occur!';
                    description = data.message || 'Something went wrong.';
                }
            } else {
                messageStr = 'Unexpected Error Occur!';
                description = 'Network error or server unreachable.';
            }

            api.error({
                message: messageStr,
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

            // Revert or re-fetch cart to ensure consistency if it failed
            // For now, maybe just fetchCart is easiest to ensure truth, but we don't have easy access to fetchCart inside here without prop drilling or moving function out.
            // But we didn't update state yet, so UI is still at old value (except InputNumber internal state might differ).
            // CartItem input is controlled by `item.quantity` from `cartItems`. If we didn't update state, it should revert on re-render.
            // However, Antd InputNumber might need force re-render or we assume user will try again.
        }
    };

    const handleRemoveItem = async (id) => {
        try {

            await removeCartItem(id);
            await fetchCart(false); // Re-fetch cart data

            const key = `remove-success-${Date.now()}`;
            const duration = 5;

            api.success({
                message: 'Xóa sản phẩm thành công',
                description: (
                    <div style={{ position: 'relative', paddingBottom: 10 }}>
                        <ToastProgressBar duration={duration} onClose={() => api.destroy(key)} />
                    </div>
                ),
                key,
                duration: 0,
                placement: 'topRight',
                closeIcon: <CloseOutlined style={{ color: colorText }} />,
            });

        } catch (error) {
            console.error("Remove item error:", error);
            const key = `remove-error-${Date.now()}`;
            const duration = 5;

            let messageStr = 'Validation failed';
            let description = 'Check your input';

            if (error.response) {
                const { status, data } = error.response;
                if (status === 200) {
                    // Should be success
                } else if (status === 400) {
                    if (data.errors && data.errors.fieldErrors) {
                        const fieldErrors = Object.values(data.errors.fieldErrors).flat();
                        if (fieldErrors.length > 0) {
                            // "3:Found no item..." -> "Found no item..."
                            description = fieldErrors[0].replace(/^\d+:/, '');
                        }
                    }
                } else if (status === 401) {
                    messageStr = 'Unauthorized';
                    description = 'Please login to continue.';
                } else if (status === 403) {
                    messageStr = 'Forbidden';
                    description = 'You are not allowed to do this.';
                } else {
                    messageStr = 'Unexpected Error Occur!';
                    description = data.message || 'Something went wrong.';
                }
            } else {
                messageStr = 'Unexpected Error Occur!';
                description = 'Network error or server unreachable.';
            }

            api.error({
                message: messageStr,
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

    const handleCheckout = () => {
        console.log('Proceeding to checkout...');
        // Implement checkout logic here
    };

    const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);

    if (loading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', padding: '24px' }}>
                <Spin size="large" tip="Đang tải giỏ hàng..." />
            </div>
        );
    }

    return (
        <div style={{ padding: '24px' }}>
            {contextHolder}
            <Button
                type="text"
                className="back-button"
                icon={<ArrowLeftOutlined />}
                onClick={() => navigate(-1)}
                style={{ marginBottom: '16px', paddingLeft: '12px', paddingRight: '12px' }}
            >
                Quay lại
            </Button>
            <Title level={2} style={{ marginBottom: '24px', marginTop: 0 }}>
                Giỏ hàng ({totalItems} sản phẩm)
            </Title>

            {cartItems.length === 0 ? (
                <Empty
                    description="Giỏ hàng trống"
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                >
                    <Link to="/">
                        <Button type="primary">Tiếp tục mua sắm</Button>
                    </Link>
                </Empty>
            ) : (
                <Row gutter={[24, 24]}>
                    {/* Cart Items List */}
                    <Col xs={24} lg={16}>
                        {cartItems.map(item => (
                            <CartItem
                                key={item.id}
                                item={item}
                                onUpdateQuantity={handleUpdateQuantity}
                                onRemove={handleRemoveItem}
                            />
                        ))}
                    </Col>

                    {/* Cart Summary */}
                    <Col xs={24} lg={8}>
                        <div style={{ position: 'sticky', top: 88 }}>
                            <CartSummary
                                subtotal={subtotal}
                                total={total}
                                onCheckout={handleCheckout}
                            />
                        </div>
                    </Col>
                </Row>
            )}
            <BackToTopButton />
        </div>
    );
};

export default CartPage;
