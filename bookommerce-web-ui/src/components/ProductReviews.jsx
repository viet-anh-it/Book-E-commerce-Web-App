import React, { useState, useRef, useEffect } from 'react';
import { List, Rate, Avatar, Typography, Button, Space, Select, Radio, Row, Col, Card, theme } from 'antd';
import { UpOutlined } from '@ant-design/icons';
import { motion, AnimatePresence } from 'framer-motion';

const { Text, Title, Paragraph } = Typography;
const { Option } = Select;

const styles = `
  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
`;

const ReviewItem = ({ item, index, limitOption, token }) => {
    const [expanded, setExpanded] = useState(false);
    const contentRef = useRef(null);
    const [isOverflowing, setIsOverflowing] = useState(false);

    useEffect(() => {
        if (contentRef.current) {
            setIsOverflowing(contentRef.current.scrollHeight > 72);
        }
    }, [item.comment]);

    return (
        <motion.div
            layout
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, transition: { duration: 0.2 } }}
            transition={{ duration: 0.4, delay: (index % limitOption) * 0.05 }}
        >
            <List.Item
                key={item.id}
                actions={[
                    <Space key="report">
                        <Text type="secondary" style={{ cursor: 'pointer' }}>Báo cáo</Text>
                    </Space>
                ]}
            >
                <List.Item.Meta
                    avatar={<Avatar src={`https://api.dicebear.com/7.x/miniavs/svg?seed=${item.rater}`} size="large" />}
                    title={
                        <Space direction="vertical" size={0}>
                            <Text strong>{item.rater}</Text>
                            <Text type="secondary" style={{ fontSize: '12px' }}>{new Date(item.createdAt).toLocaleDateString('vi-VN')}</Text>
                        </Space>
                    }
                    description={
                        <Space direction="vertical" size={8} style={{ width: '100%' }}>
                            <Rate disabled defaultValue={item.point} style={{ fontSize: '14px', color: '#faad14' }} />
                            <div>
                                <div
                                    ref={contentRef}
                                    style={{
                                        maxHeight: expanded ? contentRef.current?.scrollHeight : 72,
                                        overflow: 'hidden',
                                        transition: 'max-height 0.6s ease',
                                        position: 'relative',
                                    }}
                                >
                                    <Paragraph style={{ margin: 0 }}>
                                        {item.comment}
                                    </Paragraph>
                                    {!expanded && isOverflowing && (
                                        <div style={{
                                            position: 'absolute',
                                            bottom: 0,
                                            left: 0,
                                            width: '100%',
                                            height: '40px',
                                            background: `linear-gradient(transparent, ${token.colorBgContainer})`,
                                            pointerEvents: 'none'
                                        }} />
                                    )}
                                </div>
                                {isOverflowing && (
                                    <Button
                                        type="link"
                                        onClick={() => setExpanded(!expanded)}
                                        style={{ padding: 0, height: 'auto', marginTop: 4 }}
                                    >
                                        {expanded ? 'Thu gọn' : 'Xem thêm'}
                                    </Button>
                                )}
                            </div>
                        </Space>
                    }
                />
            </List.Item>
        </motion.div>
    );
};

const ProductReviews = ({
    reviews = [],
    meta = {},
    sortOption = 'newest',
    filterOption = 'all',
    limitOption = 5,
    onSortChange,
    onFilterChange,
    onLoadMore,
    onLimitChange,
    onReset,
    disabled = false
}) => {
    const { token } = theme.useToken();
    const handleSortChange = (value) => {
        if (onSortChange) onSortChange(value);
    };

    const handleFilterChange = (e) => {
        const value = e.target.value;
        if (onFilterChange) onFilterChange(value);
    };

    const handleLimitChange = (value) => {
        if (onLimitChange) onLimitChange(value);
    };

    // Back to Top Logic
    const [showBackToTop, setShowBackToTop] = useState(false);
    const reviewsTopRef = useRef(null);

    useEffect(() => {
        const handleScroll = () => {
            if (reviewsTopRef.current) {
                const rect = reviewsTopRef.current.getBoundingClientRect();
                // Show if the top of the reviews section is above the viewport (scrolled past)
                setShowBackToTop(rect.top < 0);
            }
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    const scrollToTop = () => {
        if (reviewsTopRef.current) {
            const headerOffset = 100; // Adjust based on your header height
            const elementPosition = reviewsTopRef.current.getBoundingClientRect().top;
            const startPosition = window.pageYOffset;
            const offsetPosition = elementPosition + startPosition - headerOffset;
            const distance = offsetPosition - startPosition;
            const duration = 800; // ms
            let start = null;

            const easeInOutCubic = (t) => {
                return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
            };

            const animation = (currentTime) => {
                if (start === null) start = currentTime;
                const timeElapsed = currentTime - start;
                const progress = Math.min(timeElapsed / duration, 1);
                const ease = easeInOutCubic(progress);

                window.scrollTo(0, startPosition + distance * ease);

                if (timeElapsed < duration) {
                    requestAnimationFrame(animation);
                }
            };

            requestAnimationFrame(animation);
        }
    };

    return (
        <Row gutter={24}>
            <style>{styles}</style>
            {/* Sidebar - Sticky */}
            <Col xs={24} md={8} lg={6}>
                <div style={{ position: 'sticky', top: 88 }}>
                    <Card title="Bộ lọc & Sắp xếp" size="small">
                        <Space direction="vertical" size="large" style={{ width: '100%' }}>
                            <div>
                                <Text strong style={{ display: 'block', marginBottom: 8 }}>Sắp xếp theo</Text>
                                <Select
                                    defaultValue="newest"
                                    style={{ width: '100%' }}
                                    onChange={handleSortChange}
                                    value={sortOption}
                                >
                                    <Option value="newest">Mới nhất</Option>
                                    <Option value="oldest">Cũ nhất</Option>
                                    <Option value="rating_desc">Điểm cao - thấp</Option>
                                    <Option value="rating_asc">Điểm thấp - cao</Option>
                                </Select>
                            </div>

                            <div>
                                <Text strong style={{ display: 'block', marginBottom: 8 }}>Lọc theo sao</Text>
                                <Radio.Group
                                    value={filterOption}
                                    onChange={handleFilterChange}
                                    style={{ display: 'flex', flexDirection: 'column', gap: 8 }}
                                >
                                    <Radio value="all">Tất cả</Radio>
                                    <Radio value="5">5 Sao</Radio>
                                    <Radio value="4">4 Sao</Radio>
                                    <Radio value="3">3 Sao</Radio>
                                    <Radio value="2">2 Sao</Radio>
                                    <Radio value="1">1 Sao</Radio>
                                </Radio.Group>
                            </div>

                            <div>
                                <Text strong style={{ display: 'block', marginBottom: 8 }}>Số lượng hiển thị</Text>
                                <Select
                                    defaultValue={5}
                                    style={{ width: '100%' }}
                                    onChange={handleLimitChange}
                                    value={limitOption}
                                >
                                    <Option value={5}>5 đánh giá</Option>
                                    <Option value={10}>10 đánh giá</Option>
                                </Select>
                            </div>
                        </Space>
                        <div style={{ marginTop: 24, textAlign: 'center' }}>
                            <Button onClick={onReset} style={{ width: '100%' }} disabled={disabled}>
                                Đặt lại bộ lọc
                            </Button>
                        </div>
                    </Card>
                    {showBackToTop && (
                        <div style={{ marginTop: 16, textAlign: 'center', animation: 'fadeIn 0.3s' }}>
                            <Button type="link" onClick={scrollToTop} icon={<UpOutlined />}>
                                Về đầu danh sách
                            </Button>
                        </div>
                    )}
                </div>
            </Col>

            {/* Main Content - Reviews List */}
            <Col xs={24} md={16} lg={18}>
                <div ref={reviewsTopRef} /> {/* Anchor for scrolling to top */}
                <motion.div
                    layout
                    style={{ overflow: 'hidden' }}
                    transition={{ type: "spring", stiffness: 300, damping: 30 }}
                >
                    <List
                        itemLayout="vertical"
                        size="large"
                        locale={{ emptyText: 'Chưa có đánh giá nào' }}
                    >
                        <AnimatePresence mode="popLayout" initial={false}>
                            {reviews.map((item, index) => (
                                <ReviewItem
                                    key={item.id}
                                    item={item}
                                    index={index}
                                    limitOption={limitOption}
                                    token={token}
                                />
                            ))}
                        </AnimatePresence>
                    </List>

                    {!meta.last ? (
                        <div style={{ textAlign: 'center', marginTop: 12, marginBottom: 12 }}>
                            <Button onClick={onLoadMore}>Xem thêm đánh giá</Button>
                        </div>
                    ) : (
                        reviews.length > 0 && (
                            <div style={{ textAlign: 'center', marginTop: 24, marginBottom: 12 }}>
                                <Text type="secondary">Bạn đã xem hết các đánh giá</Text>
                            </div>
                        )
                    )}
                </motion.div>
            </Col>
        </Row>
    );
};

export default ProductReviews;
