import React, { useState, useEffect } from "react";
import _axios from "../axiosInstance";
import { Container, Row, Col, Table, Button } from "react-bootstrap";
import useAuth from "../auth.js";
import PaginationControls from "../components/Pagination.jsx";
import AddCategory from "../components/Category/AddCategory.jsx";
import EditCategory from "../components/Category/EditCategory.jsx";

const Categories = () => {
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const { jwt } = useAuth();
    const [showPopUp, setShowPopUp] = useState(false);
    const [addPopUp, setAddPopUp] = useState(false);
    const [limit] = useState(10);
    const [page, setPage] = useState(1);
    const [hasNextPage, setHasNextPage] = useState(true);


    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await _axios.get(`/api/categories?page=${page}&limit=${limit}`, {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });
                const data = response.data;
                setCategories(data);

                setHasNextPage(data.length === limit);
            } catch (error) {
                console.error("Cannot load categories:", error);
            }
        };

        fetchCategories();
    }, [jwt, page, limit]);

    const handleRowClick = (category) => {
        setSelectedCategory(category);
        setShowPopUp(true);
    };

    const handleCloseModal = () => {
        setShowPopUp(false);
        setSelectedCategory(null);
    };

    const handleCategoryUpdate = (updatedCategory) => {
        if (!updatedCategory) {

            setCategories((prevCategory) =>
                prevCategory.filter((c) => c.categoryId !== selectedCategory.categoryId)
            );
        } else {
            setCategories((prevCategory) =>
                prevCategory.map((c) =>
                    c.categoryId === updatedCategory.categoryId ? updatedCategory : c
                )
            );
        }

        handleCloseModal();
    };


    return (
        <Container className="mt-4">
            <h1 className="text-center mb-4">All categories</h1>

            <Row>
                <Col md={12}>
                    <Table bordered hover responsive>
                        <thead className="table-primary">
                            <tr>
                                <th>Name</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            {categories.map((category) => (
                                <tr
                                    key={category.categoryId}
                                    onClick={() => handleRowClick(category)}
                                    style={{ cursor: "pointer" }}
                                >
                                    <td>{category.name}</td>
                                    <td>{category.description}</td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                    <PaginationControls page={page} setPage={setPage} hasNextPage={hasNextPage} />
                </Col>
            </Row>

            <Button className="mb-3" onClick={() => setAddPopUp(true)}>Add New Category</Button>
            <AddCategory
                show={addPopUp}
                onHide={() => setAddPopUp(false)}
                onCategoryAdded={(newCategory) => {
                    setCategories((prev) => [...prev, newCategory]);
                    setAddPopUp(false);
                }}
            />

            {selectedCategory && (
                <EditCategory
                    show={showPopUp}
                    onHide={handleCloseModal}
                    category={selectedCategory}
                    onCategoryUpdated={handleCategoryUpdate}
                />
            )}

        </Container>
    );


};

export default Categories;
