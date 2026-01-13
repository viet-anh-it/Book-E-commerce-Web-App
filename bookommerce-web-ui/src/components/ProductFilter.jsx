import { Button, Checkbox, Collapse, InputNumber, Radio, Rate, Slider, Space, Tag, theme, Typography } from 'antd';
import React, { useEffect, useState } from 'react';

import { ReloadOutlined } from '@ant-design/icons';
import { useSearchParams } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';

const { Text } = Typography;

const ProductFilter = ({ activeSections = ['1'], onActiveSectionsChange }) => {
    const { token } = theme.useToken();
    const [searchParams, setSearchParams] = useSearchParams();
    const [priceRange, setPriceRange] = useState([0, 1000000]);
    const [rating, setRating] = useState(null);
    const [genres, setGenres] = useState([]);
    const [genreOptions, setGenreOptions] = useState([]);

    useEffect(() => {
        const fetchGenres = async () => {
            try {
                const response = await axiosInstance.get('/api/genres');
                if (response.data && response.data.data) {
                    const options = response.data.data.map(genre => ({
                        label: genre.name,
                        value: genre.id
                    }));
                    setGenreOptions(options);
                }
            } catch (error) {
                console.error('Failed to fetch genres:', error);
            }
        };

        fetchGenres();
    }, []);

    useEffect(() => {
        const minPrice = searchParams.get('minPrice');
        const maxPrice = searchParams.get('maxPrice');
        const genresParam = searchParams.get('genres');
        const ratingParam = searchParams.get('rating');

        if (minPrice && maxPrice) {
            setPriceRange([Number.parseInt(minPrice, 10), Number.parseInt(maxPrice, 10)]);
        }

        if (genresParam) {
            // Convert string IDs to numbers to match the value type in genreOptions
            setGenres(genresParam.split(',').map(id => Number(id)));
        } else {
            setGenres([]);
        }

        if (ratingParam) {
            setRating(Number.parseInt(ratingParam, 10));
        } else {
            setRating(null);
        }
    }, [searchParams]);

    // Calculate if there are active filters (URL params exist)
    const hasActiveFilters = !!(
        searchParams.get('minPrice') ||
        searchParams.get('maxPrice') ||
        searchParams.get('genres') ||
        searchParams.get('rating')
    );

    // Calculate if there are pending changes (Local state differs from URL params)
    const currentMinPrice = searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : 0;
    const currentMaxPrice = searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : 1000000;
    const currentGenres = searchParams.get('genres') ? searchParams.get('genres').split(',').map(Number).sort().join(',') : '';
    const currentRating = searchParams.get('rating') ? Number(searchParams.get('rating')) : null;

    const stateMinPrice = priceRange[0];
    const stateMaxPrice = priceRange[1];
    const stateGenres = [...genres].sort().join(',');
    const stateRating = rating;

    const isPriceChanged = stateMinPrice !== currentMinPrice || stateMaxPrice !== currentMaxPrice;
    const isGenresChanged = stateGenres !== currentGenres;
    const isRatingChanged = stateRating !== currentRating;

    const hasPendingChanges = isPriceChanged || isGenresChanged || isRatingChanged;


    const handleApply = () => {
        const currentParams = Object.fromEntries(searchParams);
        const newParams = {
            ...currentParams,
            minPrice: priceRange[0],
            maxPrice: priceRange[1],
            page: 0,
        };

        if (genres.length > 0) {
            newParams.genres = genres.join(',');
        } else {
            delete newParams.genres;
        }

        if (rating) {
            newParams.rating = rating;
        } else {
            delete newParams.rating;
        }

        setSearchParams(newParams);
    };

    const handleApplyPrice = () => {
        const currentParams = Object.fromEntries(searchParams);
        setSearchParams({
            ...currentParams,
            minPrice: priceRange[0],
            maxPrice: priceRange[1],
            page: 0,
        });
    };

    const handleApplyGenres = () => {
        const currentParams = Object.fromEntries(searchParams);
        const newParams = { ...currentParams, page: 0 };
        if (genres.length > 0) {
            newParams.genres = genres.join(',');
        } else {
            delete newParams.genres;
        }
        setSearchParams(newParams);
    };

    const handleApplyRating = () => {
        const currentParams = Object.fromEntries(searchParams);
        const newParams = { ...currentParams, page: 0 };
        if (rating) {
            newParams.rating = rating;
        } else {
            delete newParams.rating;
        }
        setSearchParams(newParams);
    };

    const handleReset = () => {
        const currentParams = Object.fromEntries(searchParams);
        const hasFilters = currentParams.minPrice || currentParams.maxPrice || currentParams.genres || currentParams.rating;

        if (!hasFilters) return;

        delete currentParams.minPrice;
        delete currentParams.maxPrice;
        delete currentParams.genres;
        delete currentParams.rating;
        setSearchParams({ ...currentParams, page: 0 });
        setPriceRange([0, 1000000]);
        setRating(null);
        setGenres([]);
    };

    const handleMinPriceChange = (value) => {
        setPriceRange([value, priceRange[1]]);
    };

    const handleMaxPriceChange = (value) => {
        setPriceRange([priceRange[0], value]);
    };

    const handleResetPrice = (e) => {
        e.stopPropagation();
        const currentParams = Object.fromEntries(searchParams);
        delete currentParams.minPrice;
        delete currentParams.maxPrice;
        setSearchParams({ ...currentParams, page: 0 });
        setPriceRange([0, 1000000]);
    };

    const handleResetGenres = (e) => {
        e.stopPropagation();
        const currentParams = Object.fromEntries(searchParams);
        delete currentParams.genres;
        setSearchParams({ ...currentParams, page: 0 });
        setGenres([]);
    };

    const handleResetRating = (e) => {
        e.stopPropagation();
        const currentParams = Object.fromEntries(searchParams);
        delete currentParams.rating;
        setSearchParams({ ...currentParams, page: 0 });
        setRating(null);
    };

    const handleRemovePrice = () => {
        const currentParams = Object.fromEntries(searchParams);
        delete currentParams.minPrice;
        delete currentParams.maxPrice;
        setSearchParams({ ...currentParams, page: 0 });
    };

    const handleRemoveRating = () => {
        const currentParams = Object.fromEntries(searchParams);
        delete currentParams.rating;
        setSearchParams({ ...currentParams, page: 0 });
    };

    const handleRemoveGenre = (genreId) => {
        const currentGenres = searchParams.get('genres')?.split(',') || [];
        const newGenres = currentGenres.filter(id => id !== String(genreId));
        const currentParams = Object.fromEntries(searchParams);

        if (newGenres.length > 0) {
            currentParams.genres = newGenres.join(',');
        } else {
            delete currentParams.genres;
        }

        setSearchParams({ ...currentParams, page: 0 });
    };

    const renderActiveFilters = () => {
        const tags = [];

        // Price Tag
        const minPrice = searchParams.get('minPrice');
        const maxPrice = searchParams.get('maxPrice');
        if (minPrice && maxPrice) {
            tags.push(
                <Tag
                    key="price"
                    closable
                    onClose={(e) => {
                        e.preventDefault();
                        handleRemovePrice();
                    }}
                    style={{ margin: '4px' }}
                >
                    Giá: {Number(minPrice).toLocaleString()} - {Number(maxPrice).toLocaleString()}
                </Tag>
            );
        }

        // Rating Tag
        const ratingParam = searchParams.get('rating');
        if (ratingParam) {
            tags.push(
                <Tag
                    key="rating"
                    closable
                    onClose={(e) => {
                        e.preventDefault();
                        handleRemoveRating();
                    }}
                    style={{ margin: '4px' }}
                >
                    Đánh giá: {ratingParam} Sao trở lên
                </Tag>
            );
        }

        // Genre Tags
        const genresParam = searchParams.get('genres');
        if (genresParam) {
            const genreIds = genresParam.split(',');
            genreIds.forEach(id => {
                const genre = genreOptions.find(g => String(g.value) === id);
                const label = genre ? genre.label : `Thể loại ${id}`;
                tags.push(
                    <Tag
                        key={`genre-${id}`}
                        closable
                        onClose={(e) => {
                            e.preventDefault();
                            handleRemoveGenre(id);
                        }}
                        style={{ margin: '4px' }}
                    >
                        {label}
                    </Tag>
                );
            });
        }

        if (tags.length === 0) return null;

        return (
            <div style={{ marginBottom: 16, display: 'flex', flexWrap: 'wrap' }}>
                {tags}
            </div>
        );
    };

    const genExtra = (onReset, disabled) => (
        <ReloadOutlined
            className={disabled ? '' : 'reset-icon'}
            onClick={(e) => {
                e.stopPropagation();
                if (!disabled) onReset(e);
            }}
            style={{
                fontSize: 16,
                color: disabled ? token.colorTextDisabled : token.colorTextSecondary,
                cursor: disabled ? 'not-allowed' : 'pointer',
            }}
        />
    );

    const items = [
        {
            key: '1',
            label: 'Khoảng giá',
            extra: genExtra(handleResetPrice, !searchParams.get('minPrice') && !searchParams.get('maxPrice')),
            children: (
                <div>
                    <Slider
                        range
                        min={0}
                        max={1000000}
                        step={1000}
                        defaultValue={[0, 1000000]}
                        value={priceRange}
                        onChange={setPriceRange}
                    />
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 8 }}>
                        <InputNumber
                            min={0}
                            max={1000000}
                            style={{ width: '100%' }}
                            value={priceRange[0]}
                            onChange={handleMinPriceChange}
                        />
                        <Text>-</Text>
                        <InputNumber
                            min={0}
                            max={1000000}
                            style={{ width: '100%' }}
                            value={priceRange[1]}
                            onChange={handleMaxPriceChange}
                        />
                    </div>
                    <Button type="primary" size="small" style={{ marginTop: 8, width: '100%' }} onClick={handleApplyPrice}>
                        Áp dụng
                    </Button>
                </div>
            ),
        },
        {
            key: '2',
            label: 'Thể loại',
            extra: genExtra(handleResetGenres, !searchParams.get('genres')),
            children: (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                    <Checkbox.Group
                        options={genreOptions}
                        value={genres}
                        onChange={setGenres}
                        style={{ display: 'flex', flexDirection: 'column', gap: 8 }}
                    />
                    <Button type="primary" size="small" style={{ marginTop: 8 }} onClick={handleApplyGenres}>
                        Áp dụng
                    </Button>
                </div>
            ),
        },
        {
            key: '3',
            label: 'Đánh giá',
            extra: genExtra(handleResetRating, !searchParams.get('rating')),
            children: (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                    <Radio.Group value={rating} onChange={(e) => setRating(e.target.value)}>
                        <Space direction="vertical">
                            <Radio value={4}>
                                <Space>
                                    <Rate disabled defaultValue={4} style={{ fontSize: 14 }} />
                                    <Text>trở lên</Text>
                                </Space>
                            </Radio>
                            <Radio value={3}>
                                <Space>
                                    <Rate disabled defaultValue={3} style={{ fontSize: 14 }} />
                                    <Text>trở lên</Text>
                                </Space>
                            </Radio>
                            <Radio value={2}>
                                <Space>
                                    <Rate disabled defaultValue={2} style={{ fontSize: 14 }} />
                                    <Text>trở lên</Text>
                                </Space>
                            </Radio>
                            <Radio value={1}>
                                <Space>
                                    <Rate disabled defaultValue={1} style={{ fontSize: 14 }} />
                                    <Text>trở lên</Text>
                                </Space>
                            </Radio>
                        </Space>
                    </Radio.Group>
                    <Button type="primary" size="small" style={{ marginTop: 8 }} onClick={handleApplyRating}>
                        Áp dụng
                    </Button>
                </div>
            ),
        },
    ];

    return (
        <div style={{
            backgroundColor: token.colorBgContainer,
            height: '100%',
            display: 'flex',
            flexDirection: 'column'
        }}>
            <div style={{ padding: '16px', borderBottom: `1px solid ${token.colorBorderSecondary}` }}>
                <Text strong style={{ fontSize: 16 }}>Bộ lọc</Text>
            </div>

            <Collapse
                accordion
                activeKey={activeSections}
                onChange={onActiveSectionsChange}
                ghost
                items={items}
                expandIconPosition="end"
            />

            <div style={{ marginTop: 'auto', padding: '16px' }}>
                {renderActiveFilters()}
                <Space style={{ width: '100%' }} direction="vertical">
                    <Button
                        type="primary"
                        block
                        onClick={handleApply}
                        disabled={!hasPendingChanges}
                    >
                        Áp dụng bộ lọc
                    </Button>
                    <Button
                        block
                        onClick={handleReset}
                        disabled={!hasActiveFilters}
                    >
                        Đặt lại
                    </Button>
                </Space>
            </div>
        </div>
    );
};

export default React.memo(ProductFilter);
