import React, { useEffect, useState } from "react";
import { Container, Row, Col, Card, Button, Pagination, Badge } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import _axios from "../axiosInstance.js";
import { formatDate } from "../utils/formatDate.ts";

const TagEvents = () => {
    const navigate = useNavigate();
    const { tag } = useParams();
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(1);
    const [hasNextPage, setHasNextPage] = useState(false);
    const limit = 10;

    useEffect(() => {
        const fetchTagEvents = async () => {
            setLoading(true);
            try {
                const endpoint = `/api/events/tag?page=${page}&limit=${limit}&tag=${encodeURIComponent(tag)}`;
                const res = await _axios.get(endpoint);
                const data = Array.isArray(res.data) ? res.data : [];
                setEvents(data);
                setHasNextPage(data.length === limit);
            } catch (err) {
                console.error("Error fetching tag events:", err);
            } finally {
                setLoading(false);
            }
        };

        if (tag) {
            fetchTagEvents();
        }
    }, [tag, page]);

    const truncateDescription = (desc, length = 100) =>
        desc.length > length ? desc.slice(0, length) + "..." : desc;

    return (
        <Container className="my-5">
            {/* Page Title */}
            <div className="text-center mb-4">
                <h1>Events with tag: <Badge bg="primary" style={{ fontSize: '0.4em', verticalAlign: 'middle' }}>{tag}</Badge></h1>
            </div>

            {loading ? (
                <p className="text-center">Loading events...</p>
            ) : events.length === 0 ? (
                <div className="text-center">
                    <p>No events found for tag "{tag}".</p>
                    <Button 
                        variant="outline-primary" 
                        onClick={() => navigate('/')}
                    >
                        Back to All Events
                    </Button>
                </div>
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

                    <Pagination className="justify-content-center mt-4">
                        <Pagination.Prev
                            onClick={() => setPage((prev) => Math.max(prev - 1, 1))}
                            disabled={page === 1}
                        />
                        <Pagination.Item active>{page}</Pagination.Item>
                        <Pagination.Next
                            onClick={() => hasNextPage && setPage((prev) => prev + 1)}
                            disabled={!hasNextPage}
                        />
                    </Pagination>
                </>
            )}
        </Container>
    );
};

export default TagEvents;
