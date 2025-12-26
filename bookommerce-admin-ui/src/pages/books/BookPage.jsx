import React, { useEffect, useState } from 'react';
import { Table, Button, Space, Input, Modal, message, Typography, Popconfirm, Image } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import { getBooks, deleteBook, createBook, updateBook } from '../../services/bookService';
import { getGenres } from '../../services/genreService';
import BookFormModal from './BookFormModal';

const { Title } = Typography;

const BookPage = () => {
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

    const fetchBooks = async () => {
        setLoading(true);
        try {
            const response = await getBooks();
            if (response && response.status === 200) {
                setBooks(response.data || []);
            } else {
                setBooks([]);
                message.error('Failed to fetch books');
            }
        } catch (error) {
            setBooks([]);
            message.error('Failed to fetch books');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBooks();
    }, []);

    const handleDelete = async (id) => {
        try {
            await deleteBook(id);
            message.success('Book deleted successfully');
            fetchBooks();
        } catch (error) {
            message.error('Failed to delete book');
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
        setEditingBook(record);
        setFormErrors([]);
        const success = await fetchGenres();
        if (success) {
            setIsModalOpen(true);
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
                await updateBook(editingBook._id, values);
                message.success('Book updated successfully');
            } else {
                await createBook(values);
                message.success('Book added successfully');
            }
            setIsModalOpen(false);
            fetchBooks();
        } catch (error) {
            if (error.response) {
                const status = error.response.status;
                if (status === 400) {
                    const fieldErrors = error.response.data.errors?.fieldErrors;
                    if (fieldErrors) {
                        const antErrors = Object.keys(fieldErrors).map(key => {
                            // Map API field names to UI field names
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
                    }
                } else if (status === 401) {
                    message.error('Please login to continue!');
                } else if (status === 403) {
                    message.error('Forbidden!');
                } else if (status === 500) {
                    message.error('Unexpected Error Occur!');
                } else {
                    message.error('Failed to save book');
                }
            } else {
                message.error('Failed to save book');
            }
        } finally {
            setModalLoading(false);
        }
    };

    const columns = [
        {
            title: 'Thumbnail',
            dataIndex: 'thumbnail',
            key: 'thumbnail',
            render: (text) => <Image width={50} src={text} fallback="https://via.placeholder.com/50" />,
        },
        {
            title: 'Title',
            dataIndex: 'title',
            key: 'title',
            filterable: true,
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
            render: (price) => `$${price.toFixed(2)}`,
            sorter: (a, b) => a.price - b.price,
        },
        {
            title: 'Quantity',
            dataIndex: 'quantity',
            key: 'quantity',
        },
        {
            title: 'Category',
            dataIndex: 'category',
            key: 'category',
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button
                        type="primary"
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record)}
                    />
                    <Popconfirm
                        title="Delete the book"
                        description="Are you sure to delete this book?"
                        onConfirm={() => handleDelete(record._id)}
                        okText="Yes"
                        cancelText="No"
                    >
                        <Button type="primary" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    const filteredBooks = Array.isArray(books) ? books.filter(book =>
        book.title?.toLowerCase().includes(searchText.toLowerCase()) ||
        book.author?.toLowerCase().includes(searchText.toLowerCase())
    ) : [];

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
                onChange={(e) => setSearchText(e.target.value)}
            />

            <Table
                columns={columns}
                dataSource={filteredBooks}
                rowKey="_id"
                loading={loading}
                pagination={{ pageSize: 5 }}
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
