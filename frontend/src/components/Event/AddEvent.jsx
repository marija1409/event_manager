import React, {useEffect, useState} from "react";
import { Modal, Button, Form } from "react-bootstrap";
import _axios from "../../axiosInstance.js";
import useAuth from "../../auth.js";

const AddEvent = ({ show, onHide, onEventAdded }) => {
    const { jwt, email } = useAuth();
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        setFormData((prev) => ({ ...prev, author: email }));
    }, [email, show]);


    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await _axios.get("/api/categories", {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });
                setCategories(response.data);
            } catch (error) {
                console.error("Failed to load categories:", error);
            }
        };

        fetchCategories();
    }, [jwt]);

    const [formData, setFormData] = useState({
        title: "",
        description: "",
        author: email,
        time: "",
        location: "",
        category: "",
        tags: "",
        maxCapacity: "",
    });


    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await _axios.post(
                "/api/events/add",
                {
                    title: formData.title,
                    description: formData.description,
                    author: email,
                    time: formData.time,
                    location: formData.location,
                    category: formData.category,
                    tags: formData.tags,
                    maxCapacity: formData.maxCapacity,
                },
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                }
            );

            onEventAdded(response.data);
            setFormData({
                title: "",
                description: "",
                author: email,
                time: "",
                location: "",
                category: "",
                tags: "",
                maxCapacity: "",
            });
            onHide();
        } catch (error) {
            if (error.response) {
                console.error("Backend error response:", error.response.data);
            } else {
                console.error("Request error:", error.message);
            }
            alert("Failed to add event. See console for details.");
        }

    };

    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header closeButton>
                <Modal.Title>Add New Event</Modal.Title>
            </Modal.Header>

            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3" controlId="formTitle">
                        <Form.Label>Title</Form.Label>
                        <Form.Control
                            type="text"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            required
                            autoComplete="title"
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

                    <Form.Group className="mb-3" controlId="formTime">
                        <Form.Label>Date and Time</Form.Label>
                        <Form.Control
                            type="datetime-local"
                            name="time"
                            value={formData.time}
                            onChange={handleChange}
                            required
                            autoComplete="date and time"
                        />
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="formLocation">
                        <Form.Label>Location</Form.Label>
                        <Form.Control
                            type="location"
                            name="location"
                            value={formData.location}
                            onChange={handleChange}
                            required
                            autoComplete="location"
                        />
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="formCategory">
                        <Form.Label>Category</Form.Label>
                        <Form.Select
                            name="category"
                            value={formData.category}
                            onChange={handleChange}
                            required
                        >
                            <option value="">-- Select a category --</option>
                            {categories.map((cat) => (
                                <option key={cat.name} value={cat.name}>
                                    {cat.name}
                                </option>
                            ))}
                        </Form.Select>
                    </Form.Group>

                <Form.Group className="mb-3" controlId="formTags">
                        <Form.Label>Tags</Form.Label>
                        <Form.Control
                            type="tags"
                            name="tags"
                            value={formData.tags}
                            onChange={handleChange}
                            required
                            autoComplete="tags"
                        />
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="formMaxCapacity">
                        <Form.Label>Max capacity</Form.Label>
                        <Form.Control
                            type="maxCapacity"
                            name="maxCapacity"
                            value={formData.maxCapacity}
                            onChange={handleChange}
                            autoComplete="maxCapacity"
                        />
                    </Form.Group>

                    <Button variant="primary" type="submit">
                        Add Event
                    </Button>
                    <Button variant="secondary" className="ms-2" onClick={onHide}>
                        Cancel
                    </Button>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default AddEvent;
