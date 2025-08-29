import React, { useState, useEffect } from "react";
import _axios from "../axiosInstance";
import {Container, Row, Col, Table, Button} from "react-bootstrap";
import useAuth from "../auth.js";
import PaginationControls from "../components/Pagination.jsx";
import AddCategory from "../components/Category/AddCategory.jsx";
import EditCategory from "../components/Category/EditCategory.jsx";
import AddEvent from "../components/Event/AddEvent.jsx";
import EditEvent from "../components/Event/EditEvent.jsx";
import { useSearchParams } from "react-router-dom";

const Events = () => {
    const [events, setEvents] = useState([]);
    const [selectedEvent, setSelectedEvent] = useState(null);
    const { jwt } = useAuth();
    const [showPopUp, setShowPopUp] = useState(false);
    const [addPopUp, setAddPopUp] = useState(false);
    const [limit] = useState(10);
    const [page, setPage] = useState(1);
    const [hasNextPage, setHasNextPage] = useState(true);
    const [searchParams] = useSearchParams();
    const searchQuery = searchParams.get("query") || "";


    useEffect(() => {
        const fetchEvents = async () => {
            try {
                const endpoint = searchQuery
                    ? `/api/events/search?query=${encodeURIComponent(searchQuery)}&page=${page}&limit=${limit}`
                    : `/api/events?page=${page}&limit=${limit}`;

                const response = await _axios.get(endpoint, {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });

                const data = response.data;
                setEvents(data);
                setHasNextPage(data.length === limit);
            } catch (error) {
                console.error("Cannot load events:", error);
            }
        };

        fetchEvents();
    }, [jwt, page, limit, searchQuery]);


    const handleRowClick = (event) => {
        setSelectedEvent(event);
        setShowPopUp(true);
    };

    const handleCloseModal = () => {
        setShowPopUp(false);
        setSelectedEvent(null);
    };

    const handleEventUpdate = (updatedEvent) => {
        if (!updatedEvent) {
            setEvents((prevEvent) =>
                prevEvent.filter((e) => e.eventId !== selectedEvent.eventId)
            );
        } else {
            setEvents((prevEvent) =>
                prevEvent.map((e) =>
                    e.eventId === updatedEvent.eventId ? updatedEvent : e
                )
            );
        }

        handleCloseModal();
    };

    const showDate = (d) => {
        if (!d || !Array.isArray(d) || d.length < 6) return "Invalid date";

        const [year, month, day, hour, minute, second] = d;
        const date = new Date(year, month - 1, day, hour, minute, second); // month is 0-based!

        if (isNaN(date.getTime())) return "Invalid date";

        return new Intl.DateTimeFormat('en-GB', {
            dateStyle: 'medium',
            timeStyle: 'short',
        }).format(date);
    };


    return (
        <Container className="mt-4">
            {!searchQuery && (
                <h1 className="text-center mb-4">All events</h1>
            )
            }

            {searchQuery && (
                <p className="text-muted">Showing results for "<strong>{searchQuery}</strong>"</p>
            )}

            <Row>
                <Col md={12}>
                    <Table bordered hover responsive>
                        <thead className="table-primary">
                        <tr>
                            <th>Title</th>
                            <th>Author</th>
                            <th>Date of creation</th>
                        </tr>
                        </thead>
                        <tbody>
                        {events.map((event) => (
                            <tr
                                key={event.eventId}
                                onClick={() => handleRowClick(event)}
                                style={{ cursor: "pointer" }}
                            >
                                <td>{event.title}</td>
                                <td>{event.author.name}</td>
                                <td>{showDate(event.createdAt)}</td>
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                    <PaginationControls page={page} setPage={setPage} hasNextPage={hasNextPage} />
                </Col>
            </Row>

            <Button className="mb-3" onClick={() => setAddPopUp(true)}>Add New Event</Button>
            <AddEvent
                show={addPopUp}
                onHide={() => setAddPopUp(false)}
                onEventAdded={(newEvent) => {
                    setEvents((prev) => [...prev, newEvent]);
                    setAddPopUp(false);
                }}
            />

            {selectedEvent && (
                <EditEvent
                    show={showPopUp}
                    onHide={handleCloseModal}
                    event={selectedEvent}
                    onEventUpdated={handleEventUpdate}
                />
            )}

        </Container>
    );


};

export default Events;
