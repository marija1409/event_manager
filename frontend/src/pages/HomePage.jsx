import React, { useEffect, useState } from "react";
import { Container, Row, Col, Card, Button, Pagination } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";
import _axios from "../axiosInstance.js";
import { formatDate } from "../utils/formatDate.ts";
import TopEvents from "../components/TopEvents.jsx";

const Home = () => {
    const navigate = useNavigate();
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(1);
    const [hasNextPage, setHasNextPage] = useState(false);
    const limit = 10;
    const [searchParams] = useSearchParams();
    const searchQuery = searchParams.get("query") || "";

    useEffect(() => {
        const fetchEvents = async () => {
            setLoading(true);
            try {
                const endpoint = searchQuery
                    ? `/api/events/search?query=${encodeURIComponent(searchQuery)}&page=${page}&limit=${limit}`
                    : `/api/events?page=${page}&limit=${limit}`;

                const res = await _axios.get(endpoint);
                const data = Array.isArray(res.data) ? res.data : [];
                setEvents(data);
                setHasNextPage(data.length === limit);
            } catch (err) {
                console.error("Error fetching events:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchEvents();
    }, [page, searchQuery]);

    const truncateDescription = (desc, length = 100) =>
        desc.length > length ? desc.slice(0, length) + "..." : desc;

    return (
        <Container className="my-5">
            <TopEvents />
            {!searchQuery ? (
                page > 1 ? (
                    <h1 className="mb-4 text-center">Events</h1>
                ) : (
                    <h1 className="mb-4 text-center">Newest Events</h1>
                )
            ) : (
                <p className="text-muted text-center mb-4">
                    Showing results for "<strong>{searchQuery}</strong>"
                </p>
            )}

            {loading ? (
                <p className="text-center">Loading events...</p>
            ) : events.length === 0 ? (
                <p className="text-center">
                    {searchQuery ? `No events found for "${searchQuery}".` : "No events found."}
                </p>
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

export default Home;
