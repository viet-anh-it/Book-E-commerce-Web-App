import { Button, Input, Select, Space, theme, Typography } from 'antd';
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
                <Text strong style={{ fontSize: 16 }}>Điều khiển</Text>
            </div>

            <Space direction="vertical" style={{ width: '100%' }}>
                <Text strong>Tìm kiếm</Text>
                <Search
                    key={search || 'empty'}
                    placeholder="Tìm kiếm..."
                    allowClear
                    onSearch={handleSearch}
                    defaultValue={search}
                />
            </Space>

            <Space direction="vertical" style={{ width: '100%' }}>
                <Text strong>Hiển thị</Text>
                <Select
                    defaultValue={10}
                    value={pageSize}
                    style={{ width: '100%' }}
                    onChange={handlePageSizeChange}
                    options={[
                        { value: 10, label: '10 mục' },
                        { value: 20, label: '20 mục' },
                    ]}
                />
            </Space>

            <Space direction="vertical" style={{ width: '100%' }}>
                <Text strong>Sắp xếp theo</Text>
                <Select
                    defaultValue="TITLE&order=ASC"
                    value={sortOption}
                    style={{ width: '100%' }}
                    onChange={handleSortChange}
                    options={[
                        { value: 'PRICE&order=ASC', label: 'Giá: Thấp đến Cao' },
                        { value: 'PRICE&order=DESC', label: 'Giá: Cao đến Thấp' },
                        { value: 'TITLE&order=ASC', label: 'Tên: A đến Z' },
                        { value: 'TITLE&order=DESC', label: 'Tên: Z đến A' },
                        { value: 'RATING&order=DESC', label: 'Đánh giá: Cao đến Thấp' },
                        { value: 'RATING&order=ASC', label: 'Đánh giá: Thấp đến Cao' },
                    ]}
                />
            </Space>

            <div style={{ marginTop: 'auto', paddingTop: 16, borderTop: `1px solid ${token.colorBorderSecondary}` }}>
                <Button block onClick={handleReset} style={{ marginBottom: 16 }} disabled={isDefault}>
                    Đặt lại
                </Button>
                <Text type="secondary">Tìm thấy {totalItems} sản phẩm</Text>
            </div>
        </div>
    );
};

export default ProductControls;
