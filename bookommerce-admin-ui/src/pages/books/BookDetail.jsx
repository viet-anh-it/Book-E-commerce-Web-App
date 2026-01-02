import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Card,
    Row,
    Col,
    Typography,
    Divider,
    Tag,
    Rate,
    Descriptions,
    Table,
    Button,
    Space,
    Progress,
    Avatar,
    Spin,
    Modal,
    Image,
    Breadcrumb
} from 'antd';
import {
    ArrowLeftOutlined,
    UserOutlined
} from '@ant-design/icons';
import { getBookById } from '../../services/bookService';

const { Title, Text, Paragraph } = Typography;

const BookDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [book, setBook] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchBookDetail = async () => {
            setLoading(true);
            try {
                const response = await getBookById(id);
                if (response && response.status === 200) {
                    setBook(response.data);
                } else {
                    Modal.error({
                        title: 'Unexpected Error Occur!',
                    });
                }
            } catch (error) {
                console.error('Error fetching book details:', error);
                Modal.error({
                    title: 'Unexpected Error Occur!',
                });
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchBookDetail();
        }
    }, [id]);

    if (loading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
                <Spin size="large" tip="Loading book details..." />
            </div>
        );
    }

    if (!book) {
        return (
            <div style={{ textAlign: 'center', marginTop: 50 }}>
                <Title level={4}>Book not found</Title>
                <Button type="primary" onClick={() => navigate('/books')}>Back to List</Button>
            </div>
        );
    }

    const ratingDistribution = [
        { star: 5, count: book.ratingStatistic?._5PointCount || 0 },
        { star: 4, count: book.ratingStatistic?._4PointCount || 0 },
        { star: 3, count: book.ratingStatistic?._3PointCount || 0 },
        { star: 2, count: book.ratingStatistic?._2PointCount || 0 },
        { star: 1, count: book.ratingStatistic?._1PointCount || 0 },
    ];

    const ratingColumns = [
        {
            title: 'Rater',
            dataIndex: 'rater',
            key: 'rater',
            render: (text) => (
                <Space>
                    <Avatar icon={<UserOutlined />} />
                    <Text strong>{text || 'Anonymous User'}</Text>
                </Space>
            ),
        },
        {
            title: 'Rating',
            dataIndex: 'point',
            key: 'point',
            render: (point) => <Rate disabled defaultValue={point} style={{ fontSize: 14 }} />,
        },
        {
            title: 'Comment',
            dataIndex: 'comment',
            key: 'comment',
            render: (text) => <Paragraph ellipsis={{ rows: 2, expandable: true }}>{text}</Paragraph>,
        },
        {
            title: 'Date',
            dataIndex: 'createdAt',
            key: 'createdAt',
            render: (date) => new Date(date).toLocaleDateString(),
        },
    ];

    return (
        <div style={{ padding: '0px' }}>
            <div style={{ marginBottom: 16 }}>
                <Breadcrumb
                    items={[
                        { title: <Button type="link" style={{ padding: 0, height: 'auto' }} onClick={() => navigate('/')}>Dashboard</Button> },
                        { title: <Button type="link" style={{ padding: 0, height: 'auto' }} onClick={() => navigate('/books')}>Books</Button> },
                        { title: 'Book Detail' },
                    ]}
                />
            </div>

            <Space direction="vertical" size="large" style={{ width: '100%' }}>
                <Card>
                    <Row gutter={[32, 32]}>
                        <Col xs={24} md={8} lg={6}>
                            <Image
                                width="100%"
                                src={book.thumbnailUrlPath ? `https://bff.bookommerce.com:8181${book.thumbnailUrlPath}` : "https://via.placeholder.com/300x450"}
                                fallback="https://via.placeholder.com/300x450"
                                style={{ borderRadius: 8, boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}
                            />
                        </Col>
                        <Col xs={24} md={16} lg={18}>
                            <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                                    <div>
                                        <Title level={2}>{book.title}</Title>
                                        <Text type="secondary" style={{ fontSize: 16 }}>by </Text>
                                        <Text strong style={{ fontSize: 16 }}>{book.author}</Text>
                                    </div>
                                    <Button
                                        icon={<ArrowLeftOutlined />}
                                        onClick={() => navigate('/books')}
                                        type='primary'
                                    >
                                        Back
                                    </Button>
                                </div>

                                <Row gutter={24}>
                                    <Col span={12}>
                                        <Card type="inner">
                                            <Descriptions column={1}>
                                                <Descriptions.Item label="Price">
                                                    <Text type="danger" style={{ fontSize: 24, fontWeight: 'bold' }}>
                                                        đ{book.price?.toLocaleString()}
                                                    </Text>
                                                </Descriptions.Item>
                                                <Descriptions.Item label="Stock Status">
                                                    {book.stock > 10 ? (
                                                        <Tag color="green">{book.stock} in stock</Tag>
                                                    ) : book.stock > 0 ? (
                                                        <Tag color="orange">{book.stock} low stock</Tag>
                                                    ) : (
                                                        <Tag color="red">Out of stock</Tag>
                                                    )}
                                                </Descriptions.Item>
                                                <Descriptions.Item label="Product ID">
                                                    <Text code>{book.id}</Text>
                                                </Descriptions.Item>
                                            </Descriptions>
                                        </Card>
                                    </Col>
                                    <Col span={12}>
                                        <Card type="inner">
                                            <div style={{ textAlign: 'center' }}>
                                                <Text type="secondary">Average Rating</Text>
                                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8 }}>
                                                    <Title level={1} style={{ margin: 0 }}>{book.ratingStatistic?.averagePoint.toFixed(1)}</Title>
                                                    <Rate disabled allowHalf defaultValue={book.ratingStatistic?.averagePoint} />
                                                </div>
                                                <Text type="secondary">{book.ratingStatistic?.ratingCount} global ratings</Text>
                                            </div>
                                        </Card>
                                    </Col>
                                </Row>

                                <Divider orientation="left">Description</Divider>
                                <Paragraph style={{ fontSize: 16, lineHeight: '1.6' }}>
                                    {book.description}
                                </Paragraph>
                            </Space>
                        </Col>
                    </Row>
                </Card>

                <Row gutter={24}>
                    <Col xs={24} lg={8}>
                        <Card title="Rating Analysis">
                            {ratingDistribution.map((item) => (
                                <div key={item.star} style={{ display: 'flex', alignItems: 'center', marginBottom: 8, gap: 12 }}>
                                    <Text style={{ minWidth: 50 }}>{item.star} Stars</Text>
                                    <Progress
                                        percent={book.ratingStatistic?.ratingCount ? Math.round((item.count / book.ratingStatistic.ratingCount) * 100) : 0}
                                        strokeColor="#fadb14"
                                    />
                                    <Text type="secondary" style={{ minWidth: 40 }}>{item.count}</Text>
                                </div>
                            ))}
                        </Card>
                    </Col>
                    <Col xs={24} lg={16}>
                        <Card title="Latest Customer Reviews">
                            <Table
                                dataSource={book.ratings?.data}
                                columns={ratingColumns}
                                rowKey={(record) => record.createdAt}
                                pagination={false}
                                locale={{ emptyText: 'No reviews yet' }}
                            />
                        </Card>
                    </Col>
                </Row>
            </Space>
        </div>
    );
};

export default BookDetail;
