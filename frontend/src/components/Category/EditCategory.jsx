import React, { useState, useEffect } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import _axios from "../../axiosInstance.js";
import useAuth from "../../auth.js";

const EditCategory = ({ show, onHide, category, onCategoryUpdated }) => {
    const { jwt } = useAuth();
    const [formData, setFormData] = useState({
        name: "",
        description: "",
    });

    useEffect(() => {
        if (category) {
            setFormData({
                name: category.name || "",
                description: category.description || "",
            });
        }
    }, [category]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await _axios.patch(
                `/api/categories/${category.categoryId}`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                }
            );
            onCategoryUpdated(response.data);  // Update parent with new user data
        } catch (error) {
            console.error("Failed to update category:", error);
            alert("Failed to update category.");
        }
    };

    const handleDelete = async (e) => {
        e.preventDefault();
        try {
            await _axios.delete(`/api/categories/${category.categoryId}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            });

            // Optionally notify the parent that this user is gone
            onCategoryUpdated(null); // or create a separate onUserDeleted()

            // Close the modal after successful deletion
            onHide();
        } catch (error) {
            console.error("Failed to delete category:", error);
            alert("Failed to delete category.");
        }
    };

    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header closeButton>
                <Modal.Title>Edit Category</Modal.Title>
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
                        Save Changes
                    </Button>
                    <Button variant="secondary" className="ms-2" onClick={onHide}>
                        Cancel
                    </Button>
                    <Button variant="danger" className="ms-2" onClick={handleDelete}>
                        Delete
                    </Button>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default EditCategory;
