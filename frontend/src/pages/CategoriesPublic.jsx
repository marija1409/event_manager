import React, { useEffect, useState } from "react";
import { Container, Row, Col, Card, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import _axios from "../axiosInstance.js";
import TopEvents from "../components/TopEvents.jsx";

const CategoriesList = () => {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchCategories = async () => {
            setLoading(true);
            try {
                const res = await _axios.get("/api/categories");
                setCategories(Array.isArray(res.data) ? res.data : []);
            } catch (err) {
                console.error("Error fetching categories:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchCategories();
    }, []);

    if (loading) return <p className="text-center mt-5">Loading categories...</p>;
    if (categories.length === 0) return <p className="text-center mt-5">No categories found.</p>;

        return (
        <Container className="my-5">
            <TopEvents />
            <h1 className="mb-4 text-center">Categories</h1>
            <Row xs={1} md={2} lg={3} className="g-4">
                {categories.map((cat) => (
                    <Col key={cat.categoryId}>
                        <Card className="h-100 shadow-sm">
                            <Card.Body>
                                <Card.Title>{cat.name}</Card.Title>
                                <Card.Text>{cat.description}</Card.Text>
                            </Card.Body>
                            <Card.Footer>
                                <Button
                                    variant="primary"
                                    onClick={() => navigate(`/categories/${cat.categoryId}`)}
                                >
                                    View Events
                                </Button>
                            </Card.Footer>
                        </Card>
                    </Col>
                ))}
            </Row>
        </Container>
    );
};

export default CategoriesList;
