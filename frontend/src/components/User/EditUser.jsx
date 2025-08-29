import React, { useState, useEffect } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import _axios from "../../axiosInstance.js";
import useAuth from "../../auth.js";

const EditUser = ({ show, onHide, user, onUserUpdated }) => {
    const { jwt } = useAuth();
    const [formData, setFormData] = useState({
        email: "",
        name: "",
        lastName: "",
        type: "",
    });

    useEffect(() => {
        if (user) {
            setFormData({
                email: user.email || "",
                name: user.name || "",
                lastName: user.lastName || "",
                type: user.type || "",
            });
        }
    }, [user]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await _axios.patch(
                `/api/users/${user.userId}`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                }
            );
            onUserUpdated(response.data);  // Update parent with new user data
        } catch (error) {
            console.error("Failed to update user:", error);
            alert("Failed to update user.");
        }
    };

    const handleDelete = async (e) => {
        e.preventDefault();
        try {
            await _axios.delete(`/api/users/${user.userId}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            });

            // Optionally notify the parent that this user is gone
            onUserUpdated(null); // or create a separate onUserDeleted()

            // Close the modal after successful deletion
            onHide();
        } catch (error) {
            console.error("Failed to delete user:", error);
            alert("Failed to delete user.");
        }
    };

    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header closeButton>
                <Modal.Title>Edit User</Modal.Title>
            </Modal.Header>

            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3" controlId="formEmail">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </Form.Group>

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

                    <Form.Group className="mb-3" controlId="formLastName">
                        <Form.Label>Last Name</Form.Label>
                        <Form.Control
                            type="text"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="formType">
                        <Form.Label>Type</Form.Label>
                        <Form.Select
                            name="type"
                            value={formData.type}
                            onChange={handleChange}
                            required
                        >
                            <option value="EVENT_CREATOR">EVENT_CREATOR</option>
                            <option value="ADMIN">ADMIN</option>
                        </Form.Select>
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

export default EditUser;
