import React, { useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import _axios from "../../axiosInstance.js";
import useAuth from "../../auth.js";

const AddCategory = ({ show, onHide, onCategoryAdded }) => {
    const { jwt } = useAuth();

    const [formData, setFormData] = useState({
        name: "",
        description: "",
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await _axios.post(
                "/api/categories/add",
                {
                    name: formData.name,
                    description: formData.description,
                },
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                }
            );

            onCategoryAdded(response.data);
            setFormData({
                name: "",
                description: "",
            });
            onHide();
        } catch (error) {
            if (error.response) {
                console.error("Backend error response:", error.response.data);
            } else {
                console.error("Request error:", error.message);
            }
            alert("Failed to add category. See console for details.");
        }

    };

    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header closeButton>
                <Modal.Title>Add New Category</Modal.Title>
            </Modal.Header>

            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3" controlId="formName">
                        <Form.Label>Name</Form.Label>
                        <Form.Control
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            autoComplete="given-name"
                        />
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="formDescription">
                        <Form.Label>Description</Form.Label>
                        <Form.Control
                            type="description"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            required
                            autoComplete="description"
                        />
                    </Form.Group>

                    <Button variant="primary" type="submit">
                        Add Category
                    </Button>
                    <Button variant="secondary" className="ms-2" onClick={onHide}>
                        Cancel
                    </Button>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default AddCategory;
