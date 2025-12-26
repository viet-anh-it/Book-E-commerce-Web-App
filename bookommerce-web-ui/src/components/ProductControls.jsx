import React from 'react';
import { Button, Select, Typography, Space, Input, theme } from 'antd';
import { FilterOutlined } from '@ant-design/icons';
import { useSearchParams } from 'react-router-dom';

const { Text } = Typography;
const { Search } = Input;

const ProductControls = ({ totalItems, onOpenFilter }) => {
    const [searchParams, setSearchParams] = useSearchParams();
    const sortOption = searchParams.get('sort') || 'TITLE&order=ASC';
    const pageSize = Number.parseInt(searchParams.get('size') || '10', 10);
    const search = searchParams.get('search');
    const { token } = theme.useToken();

    const handleSortChange = (value) => {
        const currentParams = Object.fromEntries(searchParams);
        setSearchParams({ ...currentParams, page: 0, sort: value });
    };

    const handlePageSizeChange = (value) => {
        const currentParams = Object.fromEntries(searchParams);
        setSearchParams({ ...currentParams, page: 0, size: value });
    };

    const handleSearch = (value) => {
        const currentParams = Object.fromEntries(searchParams);
        if (value) {
            setSearchParams({ ...currentParams, page: 0, search: value });
        } else {
            const { search, ...rest } = currentParams;
            setSearchParams({ ...rest, page: 0 });
        }
    };

    const isDefaultSort = !searchParams.get('sort') || searchParams.get('sort') === 'TITLE&order=ASC';
    const isDefaultSize = !searchParams.get('size') || searchParams.get('size') === '10';
    const isDefaultSearch = !searchParams.get('search');
    const isDefault = isDefaultSort && isDefaultSize && isDefaultSearch;

    const handleReset = () => {
        if (isDefault) return;

        const currentParams = Object.fromEntries(searchParams);
        const newParams = { ...currentParams };
        delete newParams.search;
        newParams.sort = 'TITLE&order=ASC';
        newParams.size = '10';
        newParams.page = 0;
        setSearchParams(newParams);
    };

    return (
        <div style={{
            backgroundColor: token.colorBgContainer,
            padding: 16,
            height: '100%',
            display: 'flex',
            flexDirection: 'column',
            gap: 16
        }}>
            <div style={{ borderBottom: `1px solid ${token.colorBorderSecondary}`, paddingBottom: 16 }}>
                <Text strong style={{ fontSize: 16 }}>Controls</Text>
            </div>

            <Space direction="vertical" style={{ width: '100%' }}>
                <Text strong>Search</Text>
                <Search
                    key={search || 'empty'}
                    placeholder="Search..."
                    allowClear
                    onSearch={handleSearch}
                    defaultValue={search}
                />
            </Space>

            <Space direction="vertical" style={{ width: '100%' }}>
                <Text strong>Show</Text>
                <Select
                    defaultValue={10}
                    value={pageSize}
                    style={{ width: '100%' }}
                    onChange={handlePageSizeChange}
                    options={[
                        { value: 10, label: '10 items' },
                        { value: 20, label: '20 items' },
                    ]}
                />
            </Space>

            <Space direction="vertical" style={{ width: '100%' }}>
                <Text strong>Sort by</Text>
                <Select
                    defaultValue="TITLE&order=ASC"
                    value={sortOption}
                    style={{ width: '100%' }}
                    onChange={handleSortChange}
                    options={[
                        { value: 'PRICE&order=ASC', label: 'Price: Low to High' },
                        { value: 'PRICE&order=DESC', label: 'Price: High to Low' },
                        { value: 'TITLE&order=ASC', label: 'Title: A to Z' },
                        { value: 'TITLE&order=DESC', label: 'Title: Z to A' },
                        { value: 'RATING&order=DESC', label: 'Rating: High to Low' },
                        { value: 'RATING&order=ASC', label: 'Rating: Low to High' },
                    ]}
                />
            </Space>

            <div style={{ marginTop: 'auto', paddingTop: 16, borderTop: `1px solid ${token.colorBorderSecondary}` }}>
                <Button block onClick={handleReset} style={{ marginBottom: 16 }} disabled={isDefault}>
                    Reset
                </Button>
                <Text type="secondary">{totalItems} Items found</Text>
            </div>
        </div>
    );
};

export default ProductControls;
