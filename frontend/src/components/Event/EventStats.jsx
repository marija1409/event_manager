import React from "react";
import { Badge, Button } from "react-bootstrap";
import { Eye, HandThumbsDown, Heart } from "react-bootstrap-icons";

const EventStats = ({ event, onLike, onDislike, onRSVP, canRSVP }) => {
    return (
        <div className="d-flex flex-column align-items-end">
            <div className="mb-3">
                <Eye className="me-1" />
                <span>{event.views || 0} views</span>
            </div>
            
            <div className="d-flex gap-2 mb-3">
                <Button
                    size="sm"
                    variant="outline-success"
                    onClick={() => onLike("event")}
                >
                    <Heart size={20} /> {event.engagement?.like || 0}
                </Button>
                <Button
                    size="sm"
                    variant="outline-danger"
                    onClick={() => onDislike("event")}
                >
                    <HandThumbsDown size={20} /> {event.engagement?.dislike || 0}
                </Button>
            </div>
            
            {event.maxCapacity && event.maxCapacity > 0 && (
                <>
                    <div className="mb-2">
                        <strong>Capacity:</strong> 
                        <span className={`ms-1 ${
                            event.currentCapacity <= 0 ? 'text-danger' : 
                            event.currentCapacity <= event.maxCapacity * 0.2 ? 'text-warning' : 
                            'text-success'
                        }`}>
                            {event.currentCapacity}/{event.maxCapacity}
                        </span>
                    </div>
                    <div className="mb-2">
                        {canRSVP ? (
                            <Button
                                size="sm"
                                variant="success"
                                onClick={onRSVP}
                            >
                                RSVP
                            </Button>
                        ) : (
                            <Badge bg="warning">Event Full</Badge>
                        )}
                    </div>
                </>
            )}
        </div>
    );
};

export default EventStats;
