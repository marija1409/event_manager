import React from "react";
import { Button, Card, Col, Form, Row } from "react-bootstrap";
import { Chat, HandThumbsDown, Heart } from "react-bootstrap-icons";

const CommentsSection = ({ 
    comments, 
    newComment, 
    setNewComment, 
    onSubmitComment, 
    onLike, 
    onDislike 
}) => {
    return (
        <Col md={8}>
            <Card>
                <Card.Body>
                    <h5><Chat className="me-2" />Comments</h5>

                    <Form onSubmit={onSubmitComment} className="mb-4">
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Your Name</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={newComment.author}
                                        onChange={(e) => setNewComment(prev => ({ ...prev, author: e.target.value }))}
                                        placeholder="Enter your name"
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Form.Group className="mb-3">
                            <Form.Label>Comment</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                value={newComment.content}
                                onChange={(e) => setNewComment(prev => ({ ...prev, content: e.target.value }))}
                                placeholder="Write your comment..."
                                required
                            />
                        </Form.Group>
                        <Button type="submit" variant="primary">
                            Add Comment
                        </Button>
                    </Form>

                    {comments.length === 0 ? (
                        <p className="text-muted">No comments yet. Be the first to comment!</p>
                    ) : (
                        <div>
                            {comments.map((comment) => (
                                <Card key={comment.commentId} className="mb-3">
                                    <Card.Body>
                                        <div className="d-flex justify-content-between align-items-start mb-2">
                                            <div>
                                                <strong>{comment.author}</strong>
                                                <small className="text-muted ms-2">
                                                    {comment.createdAt}
                                                </small>
                                            </div>
                                            <div className="d-flex gap-2">
                                                <Button
                                                    size="sm"
                                                    variant="outline-success"
                                                    onClick={() => onLike("comment", comment.commentId)}
                                                >
                                                    <Heart size={16} /> {comment.activity?.like || 0}
                                                </Button>
                                                <Button
                                                    size="sm"
                                                    variant="outline-danger"
                                                    onClick={() => onDislike("comment", comment.commentId)}
                                                >
                                                    <HandThumbsDown size={16} /> {comment.activity?.dislike || 0}
                                                </Button>
                                            </div>
                                        </div>
                                        <p className="mb-0">{comment.content}</p>
                                    </Card.Body>
                                </Card>
                            ))}
                        </div>
                    )}
                </Card.Body>
            </Card>
        </Col>
    );
};

export default CommentsSection;
