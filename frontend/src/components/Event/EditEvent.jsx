import React, { useState, useEffect } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import _axios from "../../axiosInstance.js";
import useAuth from "../../auth.js";

const EditEvent = ({ show, onHide, event, onEventUpdated }) => {
    const { jwt, email } = useAuth();
    const [categories, setCategories] = useState([]);

    const [formData, setFormData] = useState({
        title: "",
        description: "",
        time: "",
        location: "",
        category: "",
        tags: "",
        maxCapacity: "",
        author: email,
    });

    useEffect(() => {
        if (event) {
            setFormData({
                title: event.title || "",
                description: event.description || "",
                time: formatDateTimeLocal(event.startingAt) || "",
                location: event.location || "",
                category: event.category.name || "",
                tags: event.tags.map(tag => tag.name).join(", ") || "",
                maxCapacity: event.maxCapacity || "",
                author: email || "",
            });
        }
    }, [event, email]);

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

    const formatDateTimeLocal = (dateArray) => {
        if (!Array.isArray(dateArray) || dateArray.length < 5) return "";
        const [year, month, day, hour, minute] = dateArray;

        const pad = (num) => String(num).padStart(2, "0");

        return `${year}-${pad(month)}-${pad(day)}T${pad(hour)}:${pad(minute)}`;
    };


    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await _axios.patch(
                `/api/events/${event.eventId}`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                }
            );
            onEventUpdated(response.data);
            onHide();
        } catch (error) {
            console.error("Failed to update event:", error);
            alert("Failed to update event.");
        }
    };

    const handleDelete = async () => {
        try {
            await _axios.delete(`/api/events/${event.eventId}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            });

            onEventUpdated(null); 
            onHide();
        } catch (error) {
            console.error("Failed to delete event:", error);
            alert("Failed to delete event.");
        }
    };

    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header closeButton>
                <Modal.Title>Edit Event</Modal.Title>
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
                        />
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="formDescription">
                        <Form.Label>Description</Form.Label>
                        <Form.Control
                            type="text"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            required
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
                        />
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="formLocation">
                        <Form.Label>Location</Form.Label>
                        <Form.Control
                            type="text"
                            name="location"
                            value={formData.location}
                            onChange={handleChange}
                            required
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
                            type="text"
                            name="tags"
                            value={formData.tags}
                            onChange={handleChange}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="formMaxCapacity">
                        <Form.Label>Max Capacity</Form.Label>
                        <Form.Control
                            type="number"
                            name="maxCapacity"
                            value={formData.maxCapacity}
                            onChange={handleChange}
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

export default EditEvent;
