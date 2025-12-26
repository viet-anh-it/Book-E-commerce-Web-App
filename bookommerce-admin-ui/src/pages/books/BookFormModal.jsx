import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Select, Row, Col, Upload, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
// import ReactQuill from 'react-quill';
// import 'react-quill/dist/quill.snow.css';

const getBase64 = (file) =>
    new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = (error) => reject(error);
    });

const BookFormModal = ({ open, onCancel, onOk, initialValues, loading, genres, genresLoading, formErrors }) => {
    const [form] = Form.useForm();
    const [fileList, setFileList] = useState([]);

    useEffect(() => {
        if (formErrors) {
            // Clear all current field errors first
            const fieldNames = ['title', 'author', 'category', 'price', 'quantity', 'description', 'thumbnail'];
            const clearErrors = fieldNames.map(name => ({ name, errors: [] }));
            form.setFields(clearErrors);

            // If there are new errors, apply them
            if (formErrors.length > 0) {
                form.setFields(formErrors);
            }
        }
    }, [formErrors, form]);

    useEffect(() => {
        if (open) {
            form.resetFields();
            if (initialValues) {
                form.setFieldsValue(initialValues);
                // Set initial file list for upload if thumbnail exists
                if (initialValues.thumbnail) {
                    setFileList([
                        {
                            uid: '-1',
                            name: 'thumbnail.png',
                            status: 'done',
                            url: initialValues.thumbnail,
                        },
                    ]);
                } else {
                    setFileList([]);
                }
            } else {
                setFileList([]);
            }
        }
    }, [open, initialValues, form]);

    const handleOk = () => {
        const values = form.getFieldsValue();
        const formData = new FormData();
        formData.append('title', values.title || '');
        formData.append('author', values.author || '');
        formData.append('price', values.price ?? 0);
        formData.append('stock', values.quantity ?? 0); // UI uses 'quantity', API uses 'stock'
        formData.append('description', values.description || '');
        formData.append('genreId', values.category ?? 0); // UI uses 'category', API uses 'genreId'

        if (fileList.length > 0 && fileList[0].originFileObj) {
            formData.append('image', fileList[0].originFileObj);
        }

        onOk(formData);
    };

    const handleChange = ({ fileList: newFileList }) => setFileList(newFileList);

    // Dummy request for upload to prevent auto upload action
    const dummyRequest = ({ file, onSuccess }) => {
        setTimeout(() => {
            onSuccess("ok");
        }, 0);
    };

    const uploadButton = (
        <div>
            <PlusOutlined />
            <div style={{ marginTop: 8 }}>Upload</div>
        </div>
    );

    return (
        <Modal
            title={initialValues ? "Edit Book" : "Add New Book"}
            open={open}
            onOk={handleOk}
            onCancel={onCancel}
            confirmLoading={loading}
            width={1000}
            style={{ top: 20 }}
        >
            <Form
                form={form}
                layout="vertical"
                name="book_form"
            >
                <Row gutter={24}>
                    <Col span={8}>
                        <Form.Item
                            label="Cover Image"
                            name="thumbnail" // Although we manage fileList separately, keeping name for validation if needed (but complex with Upload)
                        >
                            <div className="custom-upload-wrapper">
                                <Upload
                                    listType="picture-card"
                                    fileList={fileList}
                                    onPreview={() => { }} // Optional: Handle preview modal
                                    onChange={handleChange}
                                    customRequest={dummyRequest}
                                    maxCount={1}
                                >
                                    {fileList.length >= 1 ? null : uploadButton}
                                </Upload>
                            </div>
                        </Form.Item>
                    </Col>
                    <Col span={16}>
                        <Row gutter={16}>
                            <Col span={24}>
                                <Form.Item
                                    name="title"
                                    label="Title"
                                >
                                    <Input />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item
                                    name="author"
                                    label="Author"
                                >
                                    <Input />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item
                                    name="category"
                                    label="Category"
                                >
                                    <Select
                                        loading={genresLoading}
                                        options={genres.map(genre => ({
                                            value: genre.id,
                                            label: genre.name
                                        }))}
                                        placeholder="Select a category"
                                    />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item
                                    name="price"
                                    label="Price ($)"
                                >
                                    <InputNumber min={0} style={{ width: '100%' }} precision={2} />
                                </Form.Item>
                            </Col>
                            <Col span={12}>
                                <Form.Item
                                    name="quantity"
                                    label="Quantity"
                                >
                                    <InputNumber min={0} style={{ width: '100%' }} precision={0} />
                                </Form.Item>
                            </Col>
                            <Col span={24}>
                                <Form.Item
                                    name="description"
                                    label="Description"
                                >
                                    <Input.TextArea rows={4} />
                                </Form.Item>
                            </Col>
                        </Row>
                    </Col>
                </Row>
            </Form>
        </Modal>
    );
};

export default BookFormModal;
