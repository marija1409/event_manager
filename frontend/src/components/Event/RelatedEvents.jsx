import React from "react";
import { Card, Col } from "react-bootstrap";

const RelatedEvents = ({ relatedEvents, onNavigateToEvent }) => {
    return (
        <Col md={4}>
            <Card>
                <Card.Body>
                    <h5>Related Events</h5>
                    {relatedEvents.length === 0 ? (
                        <p className="text-muted">No related events found</p>
                    ) : (
                        <div>
                            {relatedEvents.map((relatedEvent) => {
                                return (
                                <Card key={relatedEvent.id} className="mb-3">
                                    <Card.Body>
                                        <h6 className="mb-2">
                                            <a 
                                                href="#" 
                                                onClick={(e) => {
                                                    e.preventDefault();
                                                    onNavigateToEvent(relatedEvent.id);
                                                }}
                                                style={{ textDecoration: 'none', color: 'inherit' }}
                                            >
                                                {relatedEvent.title}
                                            </a>
                                        </h6>
                                        <p className="text-muted small mb-0">
                                            {relatedEvent.description && relatedEvent.description.length > 100 
                                                ? `${relatedEvent.description.substring(0, 100)}...` 
                                                : relatedEvent.description}
                                        </p>
                                    </Card.Body>
                                </Card>
                                );
                            })}
                        </div>
                    )}
                </Card.Body>
            </Card>
        </Col>
    );
};

export default RelatedEvents;
