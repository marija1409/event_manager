import React, { useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import _axios from "../../axiosInstance.js";
import useAuth from "../../auth.js";

const AddUser = ({ show, onHide, onUserAdded }) => {
    const { jwt } = useAuth();

    const [formData, setFormData] = useState({
        email: "",
        name: "",
        lastName: "",
        type: "EVENT_CREATOR",
        password: "",
        repeatPassword: "",
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.password || formData.password !== formData.repeatPassword) {
            alert("Passwords must be filled and match.");
            return;
        }

        try {
            const response = await _axios.post(
                "/api/users/register",
                {
                    email: formData.email,
                    name: formData.name,
                    lastName: formData.lastName,
                    type: formData.type,
                    password: formData.password,
                },
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                }
            );

            onUserAdded(response.data);
            setFormData({
                email: "",
                name: "",
                lastName: "",
                type: "EVENT_CREATOR",
                password: "",
                repeatPassword: "",
            });
            onHide();
        } catch (error) {
            if (error.response) {
                console.error("Backend error response:", error.response.data);
            } else {
                console.error("Request error:", error.message);
            }
            alert("Failed to add user. See console for details.");
        }

    };

    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header closeButton>
                <Modal.Title>Add New User</Modal.Title>
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
                            autoComplete="username"
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
                            autoComplete="given-name"
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
                            autoComplete="family-name"
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

                    <Form.Group className="mb-3" controlId="formPassword">
                        <Form.Label>Password</Form.Label>
                        <Form.Control
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            autoComplete="new-password"
                        />
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="formRepeatPassword">
                        <Form.Label>Repeat Password</Form.Label>
                        <Form.Control
                            type="password"
                            name="repeatPassword"
                            value={formData.repeatPassword}
                            onChange={handleChange}
                            required
                            autoComplete="new-password"
                        />
                    </Form.Group>

                    <Button variant="primary" type="submit">
                        Add User
                    </Button>
                    <Button variant="secondary" className="ms-2" onClick={onHide}>
                        Cancel
                    </Button>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default AddUser;
