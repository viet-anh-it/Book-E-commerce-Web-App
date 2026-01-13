import { Button, List, Typography, theme } from 'antd';
import React, { useEffect, useState } from 'react';
import { useLocation, useSearchParams } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';
import ProductCard from './ProductCard';


const ProductList = ({ onProductsChange }) => {
    const [products, setProducts] = useState([]);
    const [hasMore, setHasMore] = useState(true);
    const [searchParams, setSearchParams] = useSearchParams();
    const location = useLocation();
    const sortOption = searchParams.get('sort') || 'TITLE&order=ASC';
    const pageSize = Number.parseInt(searchParams.get('size') || '10', 10);
    const page = Number.parseInt(searchParams.get('page') || '0', 10);
    const minPrice = searchParams.get('minPrice');
    const maxPrice = searchParams.get('maxPrice');
    const genres = searchParams.get('genres');
    const rating = searchParams.get('rating');
    const search = searchParams.get('search');

    const {
        token: { colorBgContainer },
    } = theme.useToken();

    // Keep a ref to products to check length inside useEffect without adding it to dependencies
    const productsRef = React.useRef(products);
    const prevCountRef = React.useRef(0);

    // State for smooth height animation
    const [containerHeight, setContainerHeight] = useState('auto');
    const contentRef = React.useRef(null);

    useEffect(() => {
        prevCountRef.current = productsRef.current.length;
        productsRef.current = products;
    }, [products]);

    // ResizeObserver for smooth height animation
    useEffect(() => {
        if (!contentRef.current) return;

        const observer = new ResizeObserver((entries) => {
            for (const entry of entries) {
                // Use borderBoxSize if available for more accurate height including padding/border
                const height = entry.borderBoxSize?.[0]?.blockSize ?? entry.contentRect.height;
                setContainerHeight(height);
            }
        });

        observer.observe(contentRef.current);
        return () => observer.disconnect();
    }, []);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                // If we are on page > 0 but have no products, it means we just mounted (e.g. back navigation)
                // and need to fetch everything up to the current page to restore the list state.
                const shouldRestore = productsRef.current.length === 0 && page > 0;

                const fetchPage = shouldRestore ? 0 : page;
                const fetchSize = shouldRestore ? (page + 1) * pageSize : pageSize;

                let url = `/api/books?page=${fetchPage}&size=${fetchSize}&sort=${sortOption}`;
                if (minPrice) url += `&minPrice=${minPrice}`;
                if (maxPrice) url += `&maxPrice=${maxPrice}`;
                if (genres) url += `&genres=${genres}`;
                if (rating) url += `&rating=${rating}`;
                if (search) url += `&search=${search}`;

                const response = await axiosInstance.get(url);
                const newProducts = response.data.data || [];
                const isLast = response.data.meta ? response.data.meta.last : true;
                setHasMore(!isLast);

                if (page === 0 || shouldRestore) {
                    setProducts(newProducts);
                } else {
                    setProducts((prev) => [...prev, ...newProducts]);
                }

                if (onProductsChange) {
                    onProductsChange(response.data.meta ? response.data.meta.total : 0);
                }
            } catch (error) {
                console.error('Failed to fetch products:', error);
            }
        };

        fetchProducts();
    }, [page, pageSize, sortOption, minPrice, maxPrice, genres, rating, search, onProductsChange]);

    const listRef = React.useRef(null);
    const isFirstRun = React.useRef(true);
    const hasRestoredScroll = React.useRef(false);

    const scrollToTop = () => {
        if (listRef.current) {
            const headerOffset = 100;
            const elementPosition = listRef.current.getBoundingClientRect().top;
            const offsetPosition = elementPosition + window.scrollY - headerOffset;

            // Custom smooth scroll implementation
            const startPosition = window.scrollY;
            const distance = offsetPosition - startPosition;
            const duration = 500; // ms
            let start = null;

            const step = (timestamp) => {
                if (!start) start = timestamp;
                const progress = timestamp - start;
                const percentage = Math.min(progress / duration, 1);

                // Ease out cubic function for smooth deceleration
                const ease = 1 - Math.pow(1 - percentage, 3);

                window.scrollTo(0, startPosition + distance * ease);

                if (progress < duration) {
                    window.requestAnimationFrame(step);
                }
            };

            window.requestAnimationFrame(step);
        }
    };

    useEffect(() => {
        if (isFirstRun.current) {
            isFirstRun.current = false;
            return;
        }

        // Only scroll to top if we are on the first page (implies filter change or reset)
        if (page !== 0) return;

        scrollToTop();
    }, [minPrice, maxPrice, genres, rating, search, sortOption, pageSize, page]);

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
    useEffect(() => {
        if (!hasRestoredScroll.current && products.length > 0) {
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
    }, [products, location.key]);

    const handleLoadMore = () => {
        const currentParams = Object.fromEntries(searchParams);
        setSearchParams({ ...currentParams, page: page + 1 });
    };

    return (
        <div ref={listRef} id="product-grid">
            {/* product grid */}
            <div style={{
                height: containerHeight,
                overflow: 'hidden',
                transition: 'height 1.2s cubic-bezier(0.25, 0.8, 0.25, 1)' // Smooth ease-out
            }}>
                <div ref={contentRef}>
                    <List
                        grid={{
                            gutter: 16,
                            xs: 1,
                            sm: 2,
                            md: 3,
                            lg: 3,
                            xl: 4,
                            xxl: 4,
                        }}
                        rowKey="id"
                        dataSource={products}
                        renderItem={(item, index) => {
                            const isNew = index >= prevCountRef.current;
                            const delay = isNew ? (index - prevCountRef.current) * 0.05 : 0;
                            return (
                                <List.Item
                                    className={isNew ? "fade-in" : ""}
                                    style={{ animationDelay: `${delay}s` }}
                                >
                                    <ProductCard product={item} />
                                </List.Item>
                            );
                        }}
                        loadMore={
                            products.length > 0 && (
                                <div
                                    style={{
                                        textAlign: 'center',
                                        marginTop: 12,
                                        marginBottom: 24,
                                    }}
                                >
                                    {hasMore ? (
                                        <div style={{ height: 32, lineHeight: '32px' }}>
                                            <Button onClick={handleLoadMore}>Xem thêm</Button>
                                        </div>
                                    ) : (
                                        <Typography.Text type="secondary">Không còn sản phẩm nào</Typography.Text>
                                    )}

                                    {products.length > 4 && (
                                        <div style={{ marginTop: hasMore ? 12 : 8 }}>
                                            <Button
                                                type="link"
                                                onClick={scrollToTop}
                                            >
                                                Về đầu trang
                                            </Button>
                                        </div>
                                    )}
                                </div>
                            )
                        }
                    />
                </div>
            </div>
        </div>
    );
};

export default ProductList;
