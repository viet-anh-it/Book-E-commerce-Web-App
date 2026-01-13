import { CloseOutlined, DownOutlined, ShoppingCartOutlined, UpOutlined } from '@ant-design/icons';
import { Breadcrumb, Button, Card, Col, Image, InputNumber, Progress, Rate, Row, Spin, Typography, notification, theme } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { getBookById, getRatings } from '../api/book';
import { addToCart } from '../api/cart';
import BackToTopButton from '../components/BackToTopButton';
import ToastProgressBar from '../components/common/ToastProgressBar';
import ProductReviews from '../components/ProductReviews';
import { useAuth } from '../contexts/AuthContext';

const { Title, Text } = Typography;

const ProductDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const { user } = useAuth();
    const [quantity, setQuantity] = useState(1);
    const [api, contextHolder] = notification.useNotification();
    const {
        token: { colorText },
    } = theme.useToken();

    const handleAddToCart = async () => {
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
                duration: 0,
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
                const { status, data } = error.response;
                if (status === 200) {

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
                } else if (status === 401) {
                    message = 'Unauthorized';
                    description = 'Please login to continue.';
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

    // State for product data
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);

    // State for reviews
    const [reviews, setReviews] = useState([]);
    const [reviewsMeta, setReviewsMeta] = useState({});
    const [sortOption, setSortOption] = useState('newest');
    const [filterOption, setFilterOption] = useState('all');
    const [limitOption, setLimitOption] = useState(5);
    const [currentPage, setCurrentPage] = useState(0);

    // Refs for height calculation
    const leftColumnRef = useRef(null);
    const detailsPanelRef = useRef(null);
    const descriptionPanelRef = useRef(null);
    const reviewsRef = useRef(null);

    // State for collapse logic
    const [isExpanded, setIsExpanded] = useState(false);
    const [showViewMore, setShowViewMore] = useState(false);
    const [collapsedHeight, setCollapsedHeight] = useState('auto');
    const [expandedHeight, setExpandedHeight] = useState('auto');
    const [descriptionMinHeight, setDescriptionMinHeight] = useState('auto');

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                setLoading(true);
                const data = await getBookById(id);
                if (data && data.data) {
                    setProduct({
                        id: data.data.id,
                        title: data.data.title,
                        author: data.data.author,
                        rating: data.data.ratingStatistic?.averagePoint || 0,
                        ratingCount: data.data.ratingStatistic?.ratingCount || 0,
                        ratingStatistic: data.data.ratingStatistic || {
                            averagePoint: 0,
                            ratingCount: 0,
                            _1PointCount: 0,
                            _2PointCount: 0,
                            _3PointCount: 0,
                            _4PointCount: 0,
                            _5PointCount: 0
                        },
                        price: data.data.price,
                        imageUrl: data.data.thumbnailUrlPath,
                        description: data.data.description
                    });

                    // Initial reviews fetch from product data if available, otherwise fetch separately
                    if (data.data.ratings) {
                        setReviews(data.data.ratings.data || []);
                        setReviewsMeta(data.data.ratings.meta || {});
                    } else {
                        fetchReviews(0, 5, 'newest', 'all');
                    }
                }
            } catch (error) {
                console.error('Failed to fetch product:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchProduct();
    }, [id]);

    const fetchReviews = async (page, size, sort, filter, append = false) => {
        try {
            const params = {
                page,
                size,
            };

            // Map sort option to API params
            if (sort === 'newest') {
                params.sort = 'CREATED_AT';
                params.order = 'DESC';
            } else if (sort === 'oldest') {
                params.sort = 'CREATED_AT';
                params.order = 'ASC';
            } else if (sort === 'rating_desc') {
                params.sort = 'POINT';
                params.order = 'DESC';
            } else if (sort === 'rating_asc') {
                params.sort = 'POINT';
                params.order = 'ASC';
            }

            // Map filter option to API params
            if (filter !== 'all') {
                params.point = parseInt(filter);
            }

            const data = await getRatings(id, params);
            if (data && data.data) {
                setReviews(prev => append ? [...prev, ...data.data] : data.data);
                setReviewsMeta(data.meta || {});
            }
        } catch (error) {
            console.error('Failed to fetch reviews:', error);
        }
    };

    const handleReviewSuccess = async () => {
        // Refresh reviews and product data to update statistics
        fetchReviews(0, limitOption, sortOption, filterOption);
        // Re-fetch product to get updated rating statistics
        const data = await getBookById(id);
        if (data && data.data) {
            setProduct(prev => ({
                ...prev,
                rating: data.data.ratingStatistic?.averagePoint || 0,
                ratingCount: data.data.ratingStatistic?.ratingCount || 0,
                ratingStatistic: data.data.ratingStatistic
            }));
        }
    };

    useEffect(() => {
        if (!product) return;

        const calculateHeight = () => {
            if (leftColumnRef.current && detailsPanelRef.current && descriptionPanelRef.current) {
                const leftHeight = leftColumnRef.current.offsetHeight;
                const detailsHeight = detailsPanelRef.current.offsetHeight;
                const descriptionContentHeight = descriptionPanelRef.current.scrollHeight;

                setExpandedHeight(descriptionContentHeight);

                // Calculate minHeight for Description Panel
                // Space available = Left Height - (Details Card Top + Details Card Height + Gap)
                // Details Card Top = 56 (marginTop). Gap = 24 (marginTop of Description Card).
                const topOffset = 56 + detailsHeight + 24;

                const availableHeight = leftHeight - topOffset;

                if (availableHeight > 0) {
                    setDescriptionMinHeight(availableHeight);
                } else {
                    setDescriptionMinHeight('auto');
                }

                const rightBottom = topOffset + descriptionContentHeight;
                const leftBottom = leftHeight;

                if (rightBottom > leftBottom) {
                    setShowViewMore(true);

                    // Calculate precise content height to align card bottom with left column bottom
                    // Card Height = Header + Body Padding + Content + View More Area
                    // We want Card Height = availableHeight
                    // So Content = availableHeight - Header - Body Padding - View More Area

                    const card = descriptionPanelRef.current.closest('.ant-card');
                    const header = card?.querySelector('.ant-card-head');
                    const headerHeight = header ? header.offsetHeight : 58; // Default approx if not found
                    const bodyPadding = 48; // 24px top + 24px bottom
                    const viewMoreArea = 48; // Button height + margin approx

                    const overhead = headerHeight + bodyPadding + viewMoreArea;
                    const availableForContent = availableHeight - overhead;

                    // Ensure we don't set a negative or too small height
                    if (availableForContent > 100) { // Minimum reasonable content height
                        setCollapsedHeight(availableForContent);
                    } else {
                        setCollapsedHeight(100); // Fallback min height
                    }
                } else {
                    setShowViewMore(false);
                    setIsExpanded(false); // Reset expansion if not needed
                    setCollapsedHeight('auto');
                }
            }
        };

        const observer = new ResizeObserver(() => {
            // Wrap in requestAnimationFrame to avoid "ResizeObserver loop limit exceeded"
            requestAnimationFrame(calculateHeight);
        });

        if (leftColumnRef.current) observer.observe(leftColumnRef.current);
        if (detailsPanelRef.current) observer.observe(detailsPanelRef.current);
        // Also observe the description panel itself to handle content changes or expansion
        if (descriptionPanelRef.current) observer.observe(descriptionPanelRef.current);

        // Initial calculation
        calculateHeight();

        return () => {
            observer.disconnect();
        };
    }, [product, isExpanded]);

    // Custom smooth scroll function with cubic easing
    const animateScroll = (targetY, duration = 800) => {
        const startY = window.scrollY;
        const difference = targetY - startY;
        const startTime = performance.now();

        const easeInOutCubic = (t) => {
            return t < 0.5
                ? 4 * t * t * t
                : 1 - Math.pow(-2 * t + 2, 3) / 2;
        };

        const step = (currentTime) => {
            const elapsed = currentTime - startTime;
            let progress = elapsed / duration;

            if (progress > 1) progress = 1;

            const val = easeInOutCubic(progress);
            window.scrollTo(0, startY + difference * val);

            if (progress < 1) {
                requestAnimationFrame(step);
            }
        };

        requestAnimationFrame(step);
    };

    // Save scroll position to sessionStorage
    useEffect(() => {
        // Disable browser's default scroll restoration
        if ('scrollRestoration' in window.history) {
            window.history.scrollRestoration = 'manual';
        }

        const handleScroll = () => {
            if (location.key) {
                sessionStorage.setItem(`scroll_${location.key}`, window.scrollY.toString());
            }
        };

        // Debounce the scroll handler
        let timeoutId;
        const debouncedScroll = () => {
            if (timeoutId) clearTimeout(timeoutId);
            timeoutId = setTimeout(handleScroll, 100);
        };

        window.addEventListener('scroll', debouncedScroll);
        return () => {
            window.removeEventListener('scroll', debouncedScroll);
            if (timeoutId) clearTimeout(timeoutId);
        };
    }, [location.key]);

    // Restore scroll position with retry mechanism
    const hasRestoredScroll = useRef(false);
    useEffect(() => {
        if (!hasRestoredScroll.current && product) {
            const savedScroll = sessionStorage.getItem(`scroll_${location.key}`);
            if (savedScroll) {
                const targetY = parseInt(savedScroll, 10);

                // Attempt to scroll with retries to handle dynamic content loading
                let attempts = 0;
                const maxAttempts = 10;

                const tryScroll = () => {
                    const currentHeight = document.documentElement.scrollHeight;
                    const windowHeight = window.innerHeight;

                    // If document is long enough to scroll to target (or at least close to it)
                    if (currentHeight >= targetY + windowHeight || attempts >= maxAttempts) {
                        animateScroll(targetY);
                        hasRestoredScroll.current = true;
                    } else {
                        attempts++;
                        setTimeout(tryScroll, 100); // Retry every 100ms
                    }
                };

                // Initial delay to allow layout to settle
                setTimeout(tryScroll, 100);
            }
        }
    }, [product, location.key]);

    const getPercentage = (count) => {
        if (!product || product.ratingStatistic.ratingCount === 0) return 0;
        return Math.round((count / product.ratingStatistic.ratingCount) * 100);
    };



    if (loading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <Spin size="large" />
            </div>
        );
    }

    if (!product) {
        return (
            <div style={{ padding: '24px', textAlign: 'center' }}>
                <Title level={3}>Không tìm thấy sản phẩm</Title>
                <Button onClick={() => navigate(-1)}>Quay lại</Button>
            </div>
        );
    }

    const ratingData = [
        { star: 5, count: product.ratingStatistic._5PointCount || 0 },
        { star: 4, count: product.ratingStatistic._4PointCount || 0 },
        { star: 3, count: product.ratingStatistic._3PointCount || 0 },
        { star: 2, count: product.ratingStatistic._2PointCount || 0 },
        { star: 1, count: product.ratingStatistic._1PointCount || 0 },
    ].map(item => ({
        ...item,
        percent: getPercentage(item.count)
    }));

    const scrollToReviews = () => {
        if (reviewsRef.current) {
            const headerOffset = 100;
            const elementPosition = reviewsRef.current.getBoundingClientRect().top;
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

    const handleSortChange = (value) => {
        setSortOption(value);
        setCurrentPage(0);
        fetchReviews(0, limitOption, value, filterOption);
        scrollToReviews();
    };

    const handleFilterChange = (value) => {
        setFilterOption(value);
        setCurrentPage(0);
        fetchReviews(0, limitOption, sortOption, value);
        scrollToReviews();
    };

    const handleLimitChange = (value) => {
        setLimitOption(value);
        setCurrentPage(0);
        fetchReviews(0, value, sortOption, filterOption);
        scrollToReviews();
    };

    const handleLoadMore = () => {
        const nextPage = currentPage + 1;
        setCurrentPage(nextPage);
        fetchReviews(nextPage, limitOption, sortOption, filterOption, true);
    };

    const isDefault = sortOption === 'newest' && filterOption === 'all' && limitOption === 5;

    const handleReset = () => {
        if (isDefault) return;
        setSortOption('newest');
        setFilterOption('all');
        setLimitOption(5);
        setCurrentPage(0);
        fetchReviews(0, 5, 'newest', 'all');
        scrollToReviews();
    };

    return (
        <div style={{ padding: '24px', width: '100%' }}>
            {contextHolder}
            <Row gutter={[48, 32]}>
                {/* Left Panel - Sticky Wrapper */}
                <Col xs={24} md={10} lg={8}>
                    <div ref={leftColumnRef} style={{ position: 'sticky', top: 88 }}>
                        <Breadcrumb
                            style={{ marginBottom: '16px' }}
                            items={[
                                {
                                    title: <Link to="/">Trang chủ</Link>,
                                },
                                {
                                    title: 'Chi tiết sách',
                                },
                            ]}
                        />

                        <Card bordered={false} bodyStyle={{ padding: 24, display: 'flex', justifyContent: 'center' }}>
                            <Image
                                src={`https://bff.bookommerce.com:8181${product.imageUrl}`}
                                alt={product.title}
                                fallback="https://via.placeholder.com/400x600?text=No+Image"
                                style={{ maxWidth: '100%', height: 'auto', objectFit: 'contain' }}
                            />
                        </Card>

                        <Card bordered={false} title={<Title level={3}>Đánh giá</Title>} bodyStyle={{ padding: 24 }} style={{ marginTop: 24 }}>
                            <div style={{ display: 'flex', gap: 24 }}>
                                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minWidth: 120 }}>
                                    <span style={{ fontSize: 48, fontWeight: 'bold' }}>{product.rating}<span style={{ fontSize: 24, color: '#888' }}>/5</span></span>
                                    <Rate disabled allowHalf value={product.rating} />
                                    <Text type="secondary">({product.ratingCount} đánh giá)</Text>
                                </div>
                                <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 8 }}>
                                    {ratingData.map(item => (
                                        <div key={item.star} style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                                            <Text style={{ whiteSpace: 'nowrap' }}>{item.star} sao</Text>
                                            <Progress percent={item.percent} strokeColor="#faad14" showInfo={true} format={percent => `${percent}%`} />
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </Card>
                    </div>
                </Col>

                {/* Right Panel - Details & Description */}
                <Col xs={24} md={14} lg={16}>
                    <div ref={detailsPanelRef}>
                        <Card bordered={false} bodyStyle={{ padding: 24 }} style={{ marginTop: 56 }}>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                                <Title level={1} style={{ margin: 0 }}>{product.title}</Title>

                                <Text style={{ fontSize: '18px', color: 'text.secondary' }}>
                                    Tác giả: <Text strong>{product.author}</Text>
                                </Text>

                                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                    <Rate disabled allowHalf value={product.rating} />
                                    <Text style={{ fontSize: '16px' }}>({product.rating})</Text>
                                </div>

                                <Title level={2} style={{ color: '#fa541c', margin: 0 }}>
                                    {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.price)}
                                </Title>

                                <div style={{ marginTop: 24, display: 'flex', gap: 16, alignItems: 'center' }}>
                                    {user ? (
                                        <>
                                            <Text>Số lượng:</Text>
                                            <InputNumber
                                                min={1}
                                                defaultValue={1}
                                                value={quantity}
                                                onChange={setQuantity}
                                                onPressEnter={handleAddToCart}
                                            />
                                            <Button type="primary" icon={<ShoppingCartOutlined />} size="large" onClick={handleAddToCart}>
                                                Thêm vào giỏ
                                            </Button>
                                        </>
                                    ) : (
                                        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                                            <Text style={{ fontSize: '16px' }}>Bạn muốn mua sách này?</Text>
                                            <a href="https://auth.bookommerce.com:8282/page/login" className="auth-link" style={{ fontSize: '16px', fontWeight: 'bold', color: '#1890ff' }}>
                                                Đăng nhập để mua sắm
                                            </a>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </Card>
                    </div>

                    <Card
                        bordered={false}
                        title={<Title level={3}>Mô tả</Title>}
                        bodyStyle={{ padding: 24 }}
                        style={{ marginTop: 24, minHeight: descriptionMinHeight }}
                    >
                        <div
                            ref={descriptionPanelRef}
                            style={{
                                display: 'flex',
                                flexDirection: 'column',
                                gap: '16px',
                                height: showViewMore ? (isExpanded ? expandedHeight : collapsedHeight) : 'auto',
                                overflow: 'hidden',
                                transition: 'height 0.3s ease',
                                position: 'relative',
                                maskImage: showViewMore && !isExpanded ? 'linear-gradient(to bottom, black 60%, transparent 100%)' : 'none',
                                WebkitMaskImage: showViewMore && !isExpanded ? 'linear-gradient(to bottom, black 60%, transparent 100%)' : 'none',
                            }}
                        >
                            <Text style={{ whiteSpace: 'pre-wrap', textAlign: 'justify', display: 'block' }}>{product.description}</Text>
                        </div>
                        {showViewMore && (
                            <div style={{ textAlign: 'center', marginTop: 16 }}>
                                <Button
                                    type="link"
                                    onClick={() => setIsExpanded(!isExpanded)}
                                    icon={isExpanded ? <UpOutlined /> : <DownOutlined />}
                                >
                                    {isExpanded ? 'Thu gọn' : 'Xem thêm'}
                                </Button>
                            </div>
                        )}
                    </Card>
                </Col>
            </Row>

            <Row style={{ marginTop: 24 }} ref={reviewsRef}>
                <Col span={24}>
                    <Card
                        bordered={false}
                        title={<Title level={3}>Đánh giá</Title>}
                        bodyStyle={{ padding: 24 }}
                    >
                        <ProductReviews
                            user={user}
                            bookId={id}
                            reviews={reviews}
                            meta={reviewsMeta}
                            sortOption={sortOption}
                            filterOption={filterOption}
                            limitOption={limitOption}
                            onSortChange={handleSortChange}
                            onFilterChange={handleFilterChange}
                            onLoadMore={handleLoadMore}
                            onLimitChange={handleLimitChange}
                            onReset={handleReset}
                            onReviewSuccess={handleReviewSuccess}
                            disabled={isDefault}
                        />
                    </Card>
                </Col>
            </Row>

            <BackToTopButton />
        </div>
    );
};

export default ProductDetailPage;