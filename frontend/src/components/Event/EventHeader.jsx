import React from "react";
import { Badge, Card, Col, Row } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import EventStats from "./EventStats.jsx";

const EventHeader = ({ event, onLike, onDislike, onRSVP, canRSVP }) => {
    const navigate = useNavigate();

    return (
        <Card className="mb-4">
            <Card.Body>
                <Row className="align-items-center">
                    <Col md={8}>
                        <h1 className="mb-3">{event.title}</h1>
                        <p className="lead">{event.description}</p>

                        <div className="mb-3">
                            <Badge bg="primary" className="me-2">
                                {event.category?.name || "No Category"}
                            </Badge>
                            {event.tags && event.tags.map((tag, index) => (
                                <Badge 
                                    key={`tag-${tag.name || tag}-${index}`} 
                                    bg="secondary" 
                                    className="me-1"
                                    style={{ cursor: 'pointer' }}
                                    onClick={() => navigate(`/tag/${tag.name || tag}`)}
                                >
                                    {tag.name || tag}
                                </Badge>
                            ))}
                        </div>
                    </Col>
                    <Col md={4} className="d-flex justify-content-end">
                        <EventStats 
                            event={event}
                            onLike={onLike}
                            onDislike={onDislike}
                            onRSVP={onRSVP}
                            canRSVP={canRSVP}
                        />
                    </Col>
                </Row>
            </Card.Body>
        </Card>
    );
};

export default EventHeader;
