import React, { useEffect, useState } from "react";
import { Card, ListGroup } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import _axios from "../axiosInstance.js";

const TopEvents = () => {
    const [topEvents, setTopEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchTopEvents = async () => {
            try {
                const response = await _axios.get("/api/events/engagement/top");
                if (response.data && Array.isArray(response.data)) {
                    setTopEvents(response.data);
                }
            } catch (err) {
                console.error("Error fetching top events:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchTopEvents();
    }, []);

    const handleEventClick = (eventId) => {
        navigate(`/events/${eventId}`);
    };

    if (loading) {
        return (
            <div className="d-flex justify-content-center mb-4">
                <div className="badge bg-light text-dark p-3">
                    <small>Loading Top Events...</small>
                </div>
            </div>
        );
    }

    if (topEvents.length === 0) {
        return null;
    }

    return (
        <div className="d-flex justify-content-center gap-2 mb-4 flex-wrap">
            {topEvents.map((event) => (
                <div
                    key={event.eventId || event.id}
                    onClick={() => handleEventClick(event.eventId || event.id)}
                    className="badge bg-warning text-white p-2"
                    style={{ 
                        cursor: 'pointer',
                        fontSize: '0.8rem',
                        width: '160px',
                        height: '60px',
                        whiteSpace: 'normal',
                        textAlign: 'center',
                        lineHeight: '1.2',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}
                >
                    🔥 {event.title}
                </div>
            ))}
        </div>
    );
};

export default TopEvents;
