import {
    CheckCircleOutlined,
    ClockCircleOutlined,
    CloseCircleOutlined,
    DeleteOutlined,
    EyeOutlined,
    MessageOutlined,
    SearchOutlined,
    StarFilled,
    UserOutlined
} from '@ant-design/icons';
import {
    Avatar,
    Badge,
    Button,
    Card,
    Col,
    Input,
    message,
    Modal,
    Popconfirm,
    Rate,
    Row,
    Space,
    Statistic,
    Table,
    Tag,
    theme,
    Tooltip,
    Typography
} from 'antd';
import { useCallback, useEffect, useState } from 'react';
import { approveRating, deleteRating, getRatings, rejectRating } from '../../services/ratingService';

const { Title, Text, Paragraph } = Typography;
const { Search } = Input;

const RatingPage = () => {
    const { token } = theme.useToken();

    const [ratingsContent, setRatingsContent] = useState([]);
    const [loading, setLoading] = useState(false);
    const [selectedRatingEntry, setSelectedRatingEntry] = useState(null);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [searchText, setSearchText] = useState('');
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 5,
        total: 0
    });
    const [sorter, setSorter] = useState({
        field: 'CREATED_AT',
        order: 'DESC'
    });

    const fetchRatings = useCallback(async (page = 0, size = 5, sort = 'CREATED_AT', order = 'DESC') => {
        setLoading(true);
        try {
            const params = {
                page,
                size,
                sort,
                order,
                // Add more filters if needed
            };
            const response = await getRatings(params);

            if (response.status === 200) {
                const { data: responseBody } = response;
                setRatingsContent(responseBody.data || []);
                if (responseBody.meta) {
                    setPagination({
                        current: responseBody.meta.page + 1,
                        pageSize: responseBody.meta.size,
                        total: responseBody.meta.totalElements || responseBody.meta.total
                    });
                }
            } else {
                Modal.error({
                    title: 'Đã xảy ra lỗi không mong muốn!',
                    content: response.data?.message || 'Có lỗi xảy ra khi tải đánh giá.'
                });
            }
        } catch (error) {
            if (error.response) {
                const { status, data } = error.response;
                if (status === 400) {
                    const { fieldErrors = {}, globalErrors = [] } = data.errors || {};

                    // Global errors to toast
                    globalErrors.forEach(err => message.error(err));

                    // Field errors to modal list
                    const fieldErrorList = Object.entries(fieldErrors).flatMap(([field, msgs]) =>
                        msgs.map(m => `${field}: ${m}`)
                    );

                    if (fieldErrorList.length > 0) {
                        Modal.error({
                            title: 'Lỗi xác thực',
                            content: (
                                <ul>
                                    {fieldErrorList.map((err, idx) => <li key={idx}>{err}</li>)}
                                </ul>
                            )
                        });
                    }
                } else if ([401, 403, 500].includes(status)) {
                    message.error(data?.message || `Error ${status}: Server issue`);
                } else {
                    Modal.error({
                        title: 'Unexpected Error Occur!',
                        content: data?.message || 'An unhandled error occurred.'
                    });
                }
            } else {
                Modal.error({
                    title: 'Unexpected Error Occur!',
                    content: 'Network error or server is unreachable.'
                });
            }
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchRatings(pagination.current - 1, pagination.pageSize, sorter.field, sorter.order);
    }, [fetchRatings, pagination.current, pagination.pageSize, sorter]);

    const handleTableChange = (newPagination, filters, newSorter) => {
        const fieldMap = {
            'point': 'POINT',
            'createdAt': 'CREATED_AT'
        };

        const newSortField = fieldMap[newSorter.field] || 'CREATED_AT';
        const newSortOrder = newSorter.order === 'ascend' ? 'ASC' : 'DESC';

        setSorter({ field: newSortField, order: newSortOrder });
        setPagination(prev => ({
            ...prev,
            current: newPagination.current,
            pageSize: newPagination.pageSize
        }));
    };

    const handleApprove = async (id) => {
        setLoading(true);
        try {
            const response = await approveRating(id);
            if (response.status === 200) {
                message.success(response.data?.message || 'Đã duyệt đánh giá thành công');
                fetchRatings(pagination.current - 1, pagination.pageSize, sorter.field, sorter.order);
            }
        } catch (error) {
            if (error.response) {
                const { status, data } = error.response;
                if (status === 400) {
                    const { fieldErrors = {}, globalErrors = [] } = data.errors || {};

                    // Global errors to toast
                    globalErrors.forEach(err => message.error(err));

                    // Field errors to modal list
                    const fieldErrorList = Object.entries(fieldErrors).flatMap(([field, msgs]) =>
                        msgs.map(m => `${field}: ${m}`)
                    );

                    if (fieldErrorList.length > 0) {
                        Modal.error({
                            title: 'Lỗi xác thực',
                            content: (
                                <ul>
                                    {fieldErrorList.map((err, idx) => <li key={idx}>{err}</li>)}
                                </ul>
                            )
                        });
                    }
                } else if ([401, 403, 500].includes(status)) {
                    message.error(data?.message || `Error ${status}: Server issue`);
                } else {
                    Modal.error({
                        title: 'Unexpected Error Occur!',
                        content: data?.message || 'An unhandled error occurred.'
                    });
                }
            } else {
                Modal.error({
                    title: 'Unexpected Error Occur!',
                    content: 'Network error or server is unreachable.'
                });
            }
        } finally {
            setLoading(false);
        }
    };

    const handleReject = async (id) => {
        setLoading(true);
        try {
            const response = await rejectRating(id);
            if (response.status === 200) {
                message.success(response.data?.message || 'Đã từ chối đánh giá thành công');
                fetchRatings(pagination.current - 1, pagination.pageSize, sorter.field, sorter.order);
            }
        } catch (error) {
            if (error.response) {
                const { status, data } = error.response;
                if (status === 400) {
                    const { fieldErrors = {}, globalErrors = [] } = data.errors || {};
                    globalErrors.forEach(err => message.error(err));
                    const fieldErrorList = Object.entries(fieldErrors).flatMap(([field, msgs]) =>
                        msgs.map(m => `${field}: ${m}`)
                    );
                    if (fieldErrorList.length > 0) {
                        Modal.error({
                            title: 'Lỗi xác thực',
                            content: (<ul>{fieldErrorList.map((err, idx) => <li key={idx}>{err}</li>)}</ul>)
                        });
                    }
                } else if ([401, 403, 500].includes(status)) {
                    message.error(data?.message || `Error ${status}: Server issue`);
                } else {
                    Modal.error({
                        title: 'Unexpected Error Occur!',
                        content: data?.message || 'An unhandled error occurred.'
                    });
                }
            } else {
                Modal.error({
                    title: 'Unexpected Error Occur!',
                    content: 'Network error or server is unreachable.'
                });
            }
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        setLoading(true);
        try {
            const response = await deleteRating(id);
            if (response.status === 200) {
                message.success(response.data?.message || 'Đã xóa đánh giá thành công');
                fetchRatings(pagination.current - 1, pagination.pageSize, sorter.field, sorter.order);
            }
        } catch (error) {
            if (error.response) {
                const { status, data } = error.response;
                if (status === 400) {
                    const { globalErrors = [] } = data.errors || {};
                    globalErrors.forEach(err => message.error(err));
                } else if ([401, 403, 500].includes(status)) {
                    message.error(data?.message || `Error ${status}: Server issue`);
                } else {
                    Modal.error({
                        title: 'Unexpected Error Occur!',
                        content: data?.message || 'An unhandled error occurred.'
                    });
                }
            } else {
                Modal.error({
                    title: 'Unexpected Error Occur!',
                    content: 'Network error or server is unreachable.'
                });
            }
        } finally {
            setLoading(false);
        }
    };

    const showDetail = (record) => {
        setSelectedRatingEntry(record);
        setIsDetailModalOpen(true);
    };

    const columns = [
        {
            title: 'Thông tin sách',
            key: 'bookInfo',
            width: 250,
            render: (_, record) => (
                <Space direction="vertical" size={0}>
                    <Text strong>{record.book.title}</Text>
                    <Text type="secondary" style={{ fontSize: '12px' }}>ID: {record.book.id}</Text>
                </Space>
            ),
        },
        {
            title: 'Người đánh giá',
            key: 'rater',
            render: (_, record) => (
                <Space>
                    <Avatar icon={<UserOutlined />} size="small" />
                    <Text>{record.rating.rater}</Text>
                </Space>
            ),
        },
        {
            title: 'Đánh giá',
            key: 'point',
            dataIndex: ['rating', 'point'],
            render: (point) => (
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Rate disabled defaultValue={point} style={{ fontSize: 12 }} />
                    <Text type="secondary">({point})</Text>
                </div>
            ),
            sorter: true,
        },
        {
            title: 'Bình luận',
            key: 'comment',
            render: (_, record) => (
                <Tooltip title={record.rating.comment}>
                    <Text type="secondary" ellipsis style={{ maxWidth: 200 }}>"{record.rating.comment}"</Text>
                </Tooltip>
            ),
        },
        {
            title: 'Trạng thái',
            key: 'approved',
            render: (_, record) => (
                <Badge
                    status={record.rating.approved ? 'success' : 'processing'}
                    text={record.rating.approved ? 'Đã duyệt' : 'Chờ duyệt'}
                />
            ),
        },
        {
            title: 'Hành động',
            key: 'action',
            fixed: 'right',
            width: 150,
            render: (_, record) => (
                <Space size="middle">
                    <Tooltip title="Xem chi tiết">
                        <Button
                            type="text"
                            icon={<EyeOutlined style={{ color: '#1890ff' }} />}
                            onClick={() => showDetail(record)}
                        />
                    </Tooltip>

                    {!record.rating.approved ? (
                        <Tooltip title="Duyệt">
                            <Button
                                type="text"
                                icon={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
                                onClick={() => handleApprove(record.rating.id)}
                            />
                        </Tooltip>
                    ) : (
                        <Tooltip title="Chuyển về chờ duyệt">
                            <Button
                                type="text"
                                icon={<CloseCircleOutlined style={{ color: '#faad14' }} />}
                                onClick={() => handleReject(record.rating.id)}
                            />
                        </Tooltip>
                    )}

                    <Popconfirm
                        title="Xóa đánh giá"
                        description="Bạn có chắc chắn muốn xóa đánh giá này không? Hành động này không thể hoàn tác."
                        onConfirm={() => handleDelete(record.rating.id)}
                        okText="Xóa"
                        cancelText="Hủy"
                        okButtonProps={{ danger: true }}
                    >
                        <Tooltip title="Xóa">
                            <Button
                                type="text"
                                danger
                                icon={<DeleteOutlined />}
                            />
                        </Tooltip>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div style={{ background: 'transparent' }}>
            {/* Header Section */}
            <div style={{ marginBottom: 24 }}>
                <Title level={2}>Quản lý đánh giá</Title>
                <Text type="secondary">Theo dõi và quản lý phản hồi của khách hàng về sách của bạn.</Text>
            </div>

            {/* Statistics Cards - Note: These might need a dedicated API or total counts from meta */}
            <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
                <Col xs={24} sm={12} lg={6}>
                    <Card bordered={false} className="stat-card">
                        <Statistic
                            title="Tổng số đánh giá"
                            value={pagination.total}
                            prefix={<MessageOutlined style={{ color: '#1890ff' }} />}
                        />
                    </Card>
                </Col>
                {/* Pending count, average rating, and 5-star count are not available from basic page response. 
                    They would ideally come from another endpoint. For now, placeholders based on page. */}
                <Col xs={24} sm={12} lg={6}>
                    <Card bordered={false} className="stat-card" style={{ opacity: 0.8 }}>
                        <Statistic
                            title="Trạng thái"
                            value="API Hoạt động"
                            valueStyle={{ fontSize: '18px', color: '#52c41a' }}
                            prefix={<ClockCircleOutlined />}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card bordered={false} className="stat-card" style={{ opacity: 0.8 }}>
                        <Statistic
                            title="Thông tin trang"
                            value={`${pagination.current} / ${Math.ceil(pagination.total / pagination.pageSize) || 1}`}
                            prefix={<StarFilled style={{ color: '#fadb14' }} />}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card bordered={false} className="stat-card" style={{ opacity: 0.8 }}>
                        <Statistic
                            title="Sức khỏe hệ thống"
                            value="Hoạt động"
                            valueStyle={{ color: '#52c41a' }}
                            prefix={<CheckCircleOutlined />}
                        />
                    </Card>
                </Col>
            </Row>

            {/* Table and Filter Section */}
            <Card bordered={false}>
                <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Search
                        placeholder="Tính năng tìm kiếm sắp ra mắt..."
                        allowClear
                        enterButton={<SearchOutlined />}
                        size="large"
                        disabled
                        style={{ maxWidth: 400 }}
                    />
                    <Text type="secondary">Tổng {pagination.total} đánh giá</Text>
                </div>

                <Table
                    columns={columns}
                    dataSource={ratingsContent}
                    rowKey={(record) => record.rating.id}
                    loading={loading}
                    pagination={{
                        current: pagination.current,
                        pageSize: pagination.pageSize,
                        total: pagination.total,
                        showSizeChanger: true,
                        pageSizeOptions: ['5', '10'],
                    }}
                    onChange={handleTableChange}
                    scroll={{ x: 1000 }}
                />
            </Card>

            {/* Detail Modal */}
            <Modal
                title={
                    <Space>
                        <MessageOutlined />
                        <span>Chi tiết đánh giá</span>
                    </Space>
                }
                open={isDetailModalOpen}
                onCancel={() => setIsDetailModalOpen(false)}
                footer={[
                    <Button key="close" onClick={() => setIsDetailModalOpen(false)}>
                        Đóng
                    </Button>,
                    <Button
                        key="delete"
                        danger
                        onClick={() => {
                            handleDelete(selectedRatingEntry.rating.id);
                            setIsDetailModalOpen(false);
                        }}
                    >
                        Xóa đánh giá
                    </Button>,
                    selectedRatingEntry?.rating?.approved ? (
                        <Button
                            key="reject"
                            type="default"
                            onClick={() => {
                                handleReject(selectedRatingEntry.rating.id);
                                setIsDetailModalOpen(false);
                            }}
                        >
                            Chuyển về chờ duyệt
                        </Button>
                    ) : (
                        <Button
                            key="approve"
                            type="primary"
                            onClick={() => {
                                handleApprove(selectedRatingEntry.rating.id);
                                setIsDetailModalOpen(false);
                            }}
                        >
                            Duyệt ngay
                        </Button>
                    )
                ]}
                width={700}
                centered
            >
                {selectedRatingEntry && (
                    <div style={{ padding: '0 10px' }}>
                        <Row gutter={[24, 24]}>
                            <Col span={16}>
                                <div style={{ marginBottom: 20 }}>
                                    <Text type="secondary">Người đánh giá</Text>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginTop: '4px' }}>
                                        <Avatar icon={<UserOutlined />} size="large" />
                                        <div>
                                            <Text strong style={{ fontSize: '16px', display: 'block' }}>{selectedRatingEntry.rating.rater}</Text>
                                            <Text type="secondary" style={{ fontSize: '12px' }}>Đánh giá lúc {new Date(selectedRatingEntry.rating.createdAt).toLocaleString()}</Text>
                                        </div>
                                    </div>
                                </div>

                                <div style={{ marginBottom: 20 }}>
                                    <Text type="secondary">Đánh giá</Text>
                                    <div style={{ marginTop: '4px' }}>
                                        <Rate disabled defaultValue={selectedRatingEntry.rating.point} />
                                        <Text strong style={{ marginLeft: '8px', fontSize: '18px' }}>{selectedRatingEntry.rating.point}/5</Text>
                                    </div>
                                </div>

                                <div>
                                    <Text type="secondary">Bình luận</Text>
                                    <Paragraph style={{
                                        marginTop: '8px',
                                        padding: '16px',
                                        background: token.colorFillAlter,
                                        borderRadius: token.borderRadiusLG,
                                        fontSize: '15px',
                                        lineHeight: '1.6',
                                        fontStyle: 'italic'
                                    }}>
                                        "{selectedRatingEntry.rating.comment}"
                                    </Paragraph>
                                </div>
                            </Col>

                            <Col span={8} style={{ borderLeft: `1px solid ${token.colorBorderSecondary}` }}>
                                <div style={{ marginBottom: 20 }}>
                                    <Text type="secondary">Sách</Text>
                                    <Card
                                        size="small"
                                        style={{ marginTop: '8px', background: token.colorFillAlter }}
                                        bodyStyle={{ padding: '12px' }}
                                    >
                                        <Text strong style={{ display: 'block', marginBottom: 4 }}>{selectedRatingEntry.book.title}</Text>
                                        <Text type="secondary" style={{ fontSize: '12px' }}>Book ID: {selectedRatingEntry.book.id}</Text>
                                    </Card>
                                </div>

                                <div>
                                    <Text type="secondary">Trạng thái</Text>
                                    <div style={{ marginTop: '8px' }}>
                                        <Tag color={selectedRatingEntry.rating.approved ? 'green' : 'gold'} style={{ padding: '4px 12px', fontSize: '14px' }}>
                                            {selectedRatingEntry.rating.approved ? 'Đã duyệt' : 'Chờ duyệt'}
                                        </Tag>
                                    </div>
                                </div>
                            </Col>
                        </Row>
                    </div>
                )}
            </Modal>

            <style>{`
                .stat-card {
                    box-shadow: ${token.boxShadowTertiary};
                    transition: all 0.3s ease;
                }
                .stat-card:hover {
                    transform: translateY(-5px);
                    box-shadow: ${token.boxShadow};
                }
                .ant-table-wrapper {
                    border-radius: ${token.borderRadiusLG}px;
                    overflow: hidden;
                }
            `}</style>
        </div>
    );
};

export default RatingPage;
