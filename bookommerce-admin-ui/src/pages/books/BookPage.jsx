import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Table, Button, Space, Input, Modal, message, Typography, Popconfirm, Image } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, EyeOutlined } from '@ant-design/icons';
import { getBooks, deleteBook, createBook, updateBook, getBookById } from '../../services/bookService';
import { getGenres } from '../../services/genreService';
import BookFormModal from './BookFormModal';

const { Title } = Typography;

const BookPage = () => {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchText, setSearchText] = useState('');

    // Modal state
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingBook, setEditingBook] = useState(null);
    const [modalLoading, setModalLoading] = useState(false);
    const [genres, setGenres] = useState([]);
    const [genresLoading, setGenresLoading] = useState(false);
    const [formErrors, setFormErrors] = useState([]);
    const [pagination, setPagination] = useState({
        page: 0,
        size: 10,
        total: 0
    });

    const fetchBooks = async (page = 0, size = 10, search = '') => {
        setLoading(true);
        try {
            const params = {
                page,
                size,
                search: search || undefined
            };
            const response = await getBooks(params);
            if (response?.status === 200) {
                setBooks(response.data || []);
                if (response.meta) {
                    setPagination({
                        page: response.meta.page,
                        size: response.meta.size,
                        total: response.meta.total
                    });
                }
            } else {
                Modal.error({
                    title: 'Unexpected Error Occur!',
                });
            }
        } catch (error) {
            if (error.response?.status === 400) {
                const { globalErrors = [], fieldErrors = {} } = error.response.data.errors || {};
                const errorMessages = [...globalErrors, ...Object.values(fieldErrors).flat()];
                Modal.error({
                    title: 'Validation failed',
                    content: (
                        <ul>
                            {errorMessages.map((msg, index) => (
                                <li key={index}>{msg}</li>
                            ))}
                        </ul>
                    ),
                });
            } else {
                Modal.error({
                    title: 'Unexpected Error Occur!',
                });
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const pageParam = searchParams.get('page');
        const page = pageParam ? Number.parseInt(pageParam) - 1 : 0;

        fetchBooks(page, pagination.size, searchText);
    }, [searchParams]);

    const handleTableChange = (newPagination) => {
        const newPage = newPagination.current;
        setSearchParams({ page: newPage.toString() });
    };

    const handleSearch = (value) => {
        setSearchText(value);
        setSearchParams({ page: '1' }); // Reset to page 1 which triggers the useEffect
    };

    const handleDelete = async (id) => {
        try {
            await deleteBook(id);
            message.success('Book deleted successfully');
            fetchBooks(pagination.page, pagination.size, searchText);
        } catch (error) {
            Modal.error({
                title: 'Unexpected Error Occur!',
            });
        }
    };

    const fetchGenres = async () => {
        setGenresLoading(true);
        try {
            const response = await getGenres();
            if (response.status === 200) {
                setGenres(response.data);
                return true;
            } else {
                Modal.error({
                    title: 'Unexpected Error Occur!',
                });
                return false;
            }
        } catch (error) {
            Modal.error({
                title: 'Unexpected Error Occur!',
            });
            return false;
        } finally {
            setGenresLoading(false);
        }
    };

    const handleAdd = async () => {
        setEditingBook(null);
        setFormErrors([]);
        const success = await fetchGenres();
        if (success) {
            setIsModalOpen(true);
        }
    };

    const handleEdit = async (record) => {
        setLoading(true);
        try {
            const response = await getBookById(record.id);
            if (response && response.status === 200) {
                const bookData = response.data;
                setEditingBook({
                    id: bookData.id,
                    title: bookData.title,
                    author: bookData.author,
                    price: bookData.price,
                    description: bookData.description,
                    quantity: bookData.stock,
                    category: bookData.genreId,
                    thumbnail: bookData.thumbnailUrlPath ? `https://bff.bookommerce.com:8181${bookData.thumbnailUrlPath}` : '',
                    thumbnailUrlPath: bookData.thumbnailUrlPath,
                });
                setFormErrors([]);
                const success = await fetchGenres();
                if (success) {
                    setIsModalOpen(true);
                }
            } else {
                Modal.error({
                    title: 'Unexpected Error Occur!',
                });
            }
        } catch (error) {
            Modal.error({
                title: 'Unexpected Error Occur!',
            });
        } finally {
            setLoading(false);
        }
    };

    const handleModalCancel = () => {
        setIsModalOpen(false);
        setEditingBook(null);
        setFormErrors([]);
    };

    const handleModalOk = async (values) => {
        setModalLoading(true);
        setFormErrors([]);
        try {
            if (editingBook) {
                const response = await updateBook(editingBook.id, values);
                if (response && (response.status === 200 || response.status === 201)) {
                    message.success('Book updated successfully');
                    setIsModalOpen(false);
                    fetchBooks(pagination.page, pagination.size, searchText);
                } else {
                    Modal.error({
                        title: 'Unexpected Error Occur!',
                    });
                }
            } else {
                const response = await createBook(values);
                if (response && (response.status === 200 || response.status === 201)) {
                    message.success('Book added successfully');
                    setIsModalOpen(false);
                    fetchBooks(pagination.page, pagination.size, searchText);
                } else {
                    Modal.error({
                        title: 'Unexpected Error Occur!',
                    });
                }
            }
        } catch (error) {
            if (error.response) {
                const status = error.response.status;
                if (status === 400) {
                    const { globalErrors = [], fieldErrors = {} } = error.response.data.errors || {};

                    // Map field errors to UI fields
                    if (Object.keys(fieldErrors).length > 0) {
                        const antErrors = Object.keys(fieldErrors).map(key => {
                            let name = key;
                            if (key === 'stock') name = 'quantity';
                            if (key === 'genreId') name = 'category';
                            if (key === 'image') name = 'thumbnail';

                            return {
                                name,
                                errors: fieldErrors[key]
                            };
                        });
                        setFormErrors(antErrors);
                    } else {
                        setFormErrors([]);
                    }

                    // Show global errors in modal
                    if (globalErrors.length > 0) {
                        Modal.error({
                            title: 'Validation failed',
                            content: (
                                <ul>
                                    {globalErrors.map((msg, index) => (
                                        <li key={index}>{msg}</li>
                                    ))}
                                </ul>
                            ),
                        });
                    }
                } else {
                    Modal.error({
                        title: 'Unexpected Error Occur!',
                    });
                }
            } else {
                Modal.error({
                    title: 'Unexpected Error Occur!',
                });
            }
        } finally {
            setModalLoading(false);
        }
    };

    const columns = [
        {
            title: 'Thumbnail',
            dataIndex: 'thumbnailUrlPath',
            key: 'thumbnailUrlPath',
            render: (text) => (
                <Image
                    width={50}
                    src={text ? `https://bff.bookommerce.com:8181${text}` : "https://via.placeholder.com/50"}
                    fallback="https://via.placeholder.com/50"
                />
            ),
        },
        {
            title: 'Title',
            dataIndex: 'title',
            key: 'title',
            sorter: (a, b) => a.title.localeCompare(b.title),
        },
        {
            title: 'Author',
            dataIndex: 'author',
            key: 'author',
        },
        {
            title: 'Price',
            dataIndex: 'price',
            key: 'price',
            render: (price) => `đ${price?.toLocaleString()}`,
            sorter: (a, b) => a.price - b.price,
        },
        {
            title: 'Rating',
            dataIndex: 'rating',
            key: 'rating',
            render: (rating) => rating?.toFixed(1) || 'N/A',
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button
                        icon={<EyeOutlined />}
                        onClick={() => navigate(`/books/${record.id}`)}
                    />
                    <Button
                        type="primary"
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record)}
                    />
                    <Popconfirm
                        title="Delete the book"
                        description="Are you sure to delete this book?"
                        onConfirm={() => handleDelete(record.id)}
                        okText="Yes"
                        cancelText="No"
                    >
                        <Button type="primary" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div style={{ padding: '0px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                <Title level={2} style={{ margin: 0 }}>Book Management</Title>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                    Add Book
                </Button>
            </div>

            <Input
                placeholder="Search by title or author"
                prefix={<SearchOutlined />}
                style={{ marginBottom: 16, maxWidth: 300 }}
                onChange={(e) => handleSearch(e.target.value)}
            />

            <Table
                columns={columns}
                dataSource={books}
                rowKey="id"
                loading={loading}
                pagination={{
                    current: pagination.page + 1,
                    pageSize: pagination.size,
                    total: pagination.total,
                    showSizeChanger: true,
                    pageSizeOptions: ['10', '20'],
                }}
                onChange={handleTableChange}
            />

            <BookFormModal
                open={isModalOpen}
                onCancel={handleModalCancel}
                onOk={handleModalOk}
                initialValues={editingBook}
                loading={modalLoading}
                genres={genres}
                genresLoading={genresLoading}
                formErrors={formErrors}
            />
        </div>
    );
};

export default BookPage;
