import React, { useEffect, useState } from "react";
import { Container, Row, Col, Card, Button, Pagination } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import _axios from "../axiosInstance.js";
import { formatDate } from "../utils/formatDate.ts";
import TopEvents from "../components/TopEvents.jsx";

const Popular = () => {
    const navigate = useNavigate();
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchEvents = async () => {
            setLoading(true);
            try {
                const res = await _axios.get(`/api/events/top`);
                const data = Array.isArray(res.data) ? res.data : [];
                setEvents(data);
            } catch (err) {
                console.error("Error fetching events:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchEvents();
    }, []);

    const truncateDescription = (desc, length = 100) =>
        desc.length > length ? desc.slice(0, length) + "..." : desc;


    return (
        <Container className="my-5">
            <TopEvents />
            <h1 className="mb-4 text-center">Top 10 Most Popular Events</h1>

            {loading ? (
                <p className="text-center">Loading events...</p>
            ) : events.length === 0 ? (
                <p className="text-center">No events found.</p>
            ) : (
                <>
                    <Row xs={1} md={2} lg={3} className="g-4">
                        {events.map((event) => (
                            <Col key={event.eventId}>
                                <Card className="h-100 shadow-sm">
                                    <Card.Body>
                                        <Card.Title>
                                            {event.title}
                                        </Card.Title>
                                        <Card.Text>{truncateDescription(event.description)}</Card.Text>
                                    </Card.Body>
                                    <Card.Footer className="d-flex justify-content-between align-items-center">
                                        <small className="text-muted">
                                            Created: {formatDate(event.createdAt)}
                                        </small>
                                        <small className="badge bg-primary">
                                            {event.category?.name || "No Category"}
                                        </small>
                                    </Card.Footer>
                                    <Card.Body>
                                        <Button
                                            variant="outline-primary"
                                            onClick={() => navigate(`/events/${event.eventId}`)}
                                        >
                                            View Details
                                        </Button>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                </>
            )}
        </Container>
    );
};

export default Popular;
