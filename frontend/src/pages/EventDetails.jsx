import React, { useEffect, useState, useCallback } from "react";
import { Alert, Button, Col, Container, Row, Spinner } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import _axios from "../axiosInstance.js";
import { formatDate } from "../utils/formatDate.ts";
import TopEvents from "../components/TopEvents.jsx";
import EventHeader from "../components/Event/EventHeader.jsx";
import EventStats from "../components/Event/EventStats.jsx";
import EventInfo from "../components/Event/EventInfo.jsx";
import CommentsSection from "../components/Event/CommentsSection.jsx";
import RelatedEvents from "../components/Event/RelatedEvents.jsx";
import RSVPModal from "../components/Event/RSVPModal.jsx";

const EventDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [event, setEvent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState({ author: "", content: "" });
    const [showRSVPModal, setShowRSVPModal] = useState(false);
    const [rsvpEmail, setRsvpEmail] = useState("");
    const [relatedEvents, setRelatedEvents] = useState([]);
    const [canRSVP, setCanRSVP] = useState(true);

    const fetchEventDetails = useCallback(async () => {
        try {
            setLoading(true);
            
            const response = await _axios.get(`/api/events/details/${id}`, {
                withCredentials: true
            });
            
            const eventData = response.data;
            // console.log("Event data received:", eventData);
            // console.log("startingAt data:", eventData.startingAt);
            // console.log("startingAt type:", typeof eventData.startingAt);
            // console.log("startingAt is array:", Array.isArray(eventData.startingAt));
            // console.log("Event data ID received:", eventData.eventId);
            
            const transformedEvent = {
                id: eventData.eventId,
                title: eventData.title,
                description: eventData.description,
                location: eventData.location,
                startingAt: formatDate(eventData.startingAt),
                createdAt: formatDate(eventData.createdAt),
                category: { name: eventData.category.name },
                creator: { name: eventData.author.email },
                author: { name: eventData.author.email },
                currentCapacity: eventData.currentCapacity,
                maxCapacity: eventData.maxCapacity,
                views: eventData.views,
                engagement: {
                    like: eventData.engagement.like,
                    dislike: eventData.engagement.dislike
                },
                tags: eventData.tags.map(tag => ({ name: tag.name }))
            };
            
            setEvent(transformedEvent);
            
            const transformedComments = eventData.comments.map(comment => {  
                return {
                    commentId: comment.commentId,
                    author: comment.author,
                    content: comment.content,
                    createdAt: formatDate(comment.createdAt),
                    activity: {
                        like: comment.activity?.like || 0,
                        dislike: comment.activity?.dislike || 0
                    },
                    event: comment.eventId
                };
            });
    
            setComments(transformedComments);
            
            setCanRSVP(eventData.currentCapacity > 0);
            
            
        } catch (err) {
            console.error("Error fetching event details:", err);
            setError("Failed to load event details. Please try again.");
        } finally {
            setLoading(false);
        }
    }, [id]);

    const fetchRelatedEvents = useCallback(async (tags) => {
        if (!tags || tags.length === 0 || !event) return;
        
        try {
            const tagsString = tags.map(tag => tag.name || tag).join(',');
            const response = await _axios.get(`/api/events/tag/related?event=${id}&tags=${encodeURIComponent(tagsString)}`);
            
            if (response.data && Array.isArray(response.data)) {
                const relatedData = response.data.map(event => ({
                    id: event.eventId,
                    title: event.title,
                    description: event.description
                }));
                setRelatedEvents(relatedData);
            }
        } catch (err) {
            console.error("Failed to fetch related events:", err);
        }
    }, [event, id]);

    useEffect(() => {
        if (id) {
            fetchEventDetails();
        }
    }, [id, fetchEventDetails]);

    useEffect(() => {
        if (event && event.tags && event.tags.length > 0) {
            fetchRelatedEvents(event.tags);
        }
    }, [event, fetchRelatedEvents]);

    const handleLike = async (type, targetId = null) => {
        const targetIdToUse = targetId || id;
        try {

            const response = await _axios.post(`/api/engagement/${targetIdToUse}?type=${type}&like=true`, {}, {
                withCredentials: true
            });

            if (response.data) {
                if (type === "event") {
                    setEvent(prev => ({
                        ...prev,
                        engagement: {
                            ...prev.engagement,
                            like: (prev.engagement?.like || 0) + 1
                        }
                    }));
                } else if (type === "comment") {
                    setComments(prev => prev.map(comment => 
                        comment.commentId === targetIdToUse 
                            ? {
                                ...comment,
                                activity: {
                                    ...comment.activity,
                                    like: (comment.activity?.like || 0) + 1
                                }
                            }
                            : comment
                    ));
                }
               await refreshEngagement(targetIdToUse, type);
                
                await new Promise(resolve => setTimeout(resolve, 100));
            }
        } catch (err) {
            console.error("Failed to update like status:", err);
            console.error("Error details:", {
                message: err.message,
                status: err.response?.status,
                data: err.response?.data,
                headers: err.response?.headers
            });
        }
    };

    const handleDislike = async (type, targetId = null) => {
        const targetIdToUse = targetId || id;
        try {
            const response = await _axios.post(`/api/engagement/${targetIdToUse}?type=${type}&like=false`, {}, {
                withCredentials: true
            });
            
            if (response.data) {
                if (type === "event") {
                    setEvent(prev => ({
                        ...prev,
                        engagement: {
                            ...prev.engagement,
                            dislike: (prev.engagement?.dislike || 0) + 1
                        }
                    }));
                } else if (type === "comment") {
                    setComments(prev => prev.map(comment => 
                        comment.commentId === targetIdToUse 
                            ? {
                                ...comment,
                                activity: {
                                    ...comment.activity,
                                    dislike: (comment.activity?.dislike || 0) + 1
                                }
                            }
                            : comment
                    ));
                }
                await refreshEngagement(targetIdToUse, type);
                
                await new Promise(resolve => setTimeout(resolve, 100));
            }
        } catch (err) {
            console.error("Failed to update dislike status:", err);
            console.error("Error details:", {
                message: err.message,
                status: err.response?.status,
                data: err.response?.data,
                headers: err.response?.headers
            });
        }
    };

    const refreshEngagement = async (targetId, type) => {
        try {
            const res = await _axios.get(`/api/engagement/separate/${targetId}?type=${type}`);
            if (type === "event") {
                setEvent(prev => ({ ...prev, engagement: res.data }));
            } else if (type === "comment") {
                setComments(prev => prev.map(c =>
                    c.commentId === targetId ? { ...c, activity: res.data } : c
                ));
            }
        } catch (err) {
            console.error("Failed to refresh engagement:", err);
        }
    };

    const handleSubmitComment = async (e) => {
        e.preventDefault();
        if (!newComment.author.trim() || !newComment.content.trim()) return;

        try {
            const response = await _axios.post(`/api/comments`, {
                author: newComment.author,
                content: newComment.content,
                eventId: id
            });

            if (response.data) {
                setNewComment({ author: "", content: "" });
                await fetchEventDetails();
            }
        } catch (err) {
            console.error("Failed to submit comment:", err);
            console.error("Error details:", {
                message: err.message,
                status: err.response?.status,
                data: err.response?.data
            });
        }
    };

    const handleRSVP = async () => {
        console.log("RSVP email:", rsvpEmail);
        console.log("RSVP eventId:", id);
        if (!rsvpEmail.trim()) return;
    
        try {
            const response = await _axios.post(`/api/events/rsvp`, {
                email: rsvpEmail,
                eventId: Number(id)
            });
    
            if (response.data.success) {
                setRsvpEmail("");
                setShowRSVPModal(false);
                if (response.data.currentCount >= event.currentCapacity) {
                    setCanRSVP(false);
                }
                await fetchEventDetails();
            } else {
                alert("RSVP failed");
            }
        } catch (err) {
            console.error("Failed to submit RSVP:", err);
            alert("RSVP unsuccessful");
        }
    };
    
    const navigateToEvent = (eventId) => {
        navigate(`/events/${eventId}`);
    };

    if (loading) {
        return (
            <Container className="my-5 text-center">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </Container>
        );
    }

    if (error || !event) {
        return (
            <Container className="my-5">
                <Alert variant="danger">
                    {error || "Event not found"}
                </Alert>
                <Button onClick={() => navigate('/')} variant="outline-primary">
                    Back to Home
                </Button>
            </Container>
        );
    }

    return (
        <Container className="my-5">
            <TopEvents />
            <Button
                onClick={() => navigate('/')}
                variant="outline-secondary"
                className="mb-3"
            >
                ← Back to Home
            </Button>

            <Row>
                <Col md={12}>
                    <EventHeader 
                        event={event}
                        onLike={handleLike}
                        onDislike={handleDislike}
                        onRSVP={() => setShowRSVPModal(true)}
                        canRSVP={canRSVP}
                    />
                </Col>
            </Row>

            <EventInfo event={event} />

            <Row>
                <CommentsSection 
                    comments={comments}
                    newComment={newComment}
                    setNewComment={setNewComment}
                    onSubmitComment={handleSubmitComment}
                    onLike={handleLike}
                    onDislike={handleDislike}
                />
                <RelatedEvents 
                    relatedEvents={relatedEvents}
                    onNavigateToEvent={navigateToEvent}
                />
            </Row>

            <RSVPModal 
                show={showRSVPModal}
                onHide={() => setShowRSVPModal(false)}
                eventTitle={event.title}
                rsvpEmail={rsvpEmail}
                setRsvpEmail={setRsvpEmail}
                onConfirmRSVP={handleRSVP}
            />
        </Container>
    );
};

export default EventDetails;
