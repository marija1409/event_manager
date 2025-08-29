import React from "react";
import { Button, Form, Modal } from "react-bootstrap";

const RSVPModal = ({ 
    show, 
    onHide, 
    eventTitle, 
    rsvpEmail, 
    setRsvpEmail, 
    onConfirmRSVP 
}) => {
    return (
        <Modal show={show} onHide={onHide}>
            <Modal.Header closeButton>
                <Modal.Title>RSVP for {eventTitle}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group className="mb-3">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            type="email"
                            value={rsvpEmail}
                            onChange={(e) => setRsvpEmail(e.target.value)}
                            placeholder="Enter your email"
                            required
                        />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onHide}>
                    Cancel
                </Button>
                <Button variant="success" onClick={onConfirmRSVP}>
                    Confirm RSVP
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default RSVPModal;
