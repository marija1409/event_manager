import React from "react";
import { Card, Col, Row } from "react-bootstrap";
import { Calendar, CalendarPlus, Person, Pin } from "react-bootstrap-icons";

const EventInfo = ({ event }) => {
    return (
        <Row className="mb-4">
            <Col md={12}>
                <Card>
                    <Card.Body>
                        <h5>Event Information</h5>
                        <div className="mb-2">
                            <Calendar className="me-2" />
                            <strong>Date & Time:</strong> {event.startingAt}
                        </div>
                        <div className="mb-2">
                            <Pin className="me-2" />
                            <strong>Location:</strong> {event.location || "TBD"}
                        </div>
                        <div className="mb-2">
                            <Person className="me-2" />
                            <strong>Created by:</strong> {event.creator?.name || event.author?.name || "Unknown"}
                        </div>
                        <div className="mb-2">
                            <CalendarPlus className="me-2" />
                            <strong>Created:</strong> {event.createdAt}
                        </div>
                    </Card.Body>
                </Card>
            </Col>
        </Row>
    );
};

export default EventInfo;
