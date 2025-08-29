import React, { useState, useEffect } from "react";
import _axios from "../axiosInstance";
import {Container, Row, Col, Table, Button} from "react-bootstrap";
import useAuth from "../auth.js";
import EditUser from "../components/User/EditUser.jsx";
import AddUser from "../components/User/AddUser.jsx";
import Pagination from "../components/Pagination.jsx";
import PaginationControls from "../components/Pagination.jsx";

const Users = () => {
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const { jwt, role } = useAuth();
    const [showPopUp, setShowPopUp] = useState(false);
    const [addPopUp, setAddPopUp] = useState(false);
    const [limit] = useState(10);
    const [page, setPage] = useState(1);
    const [hasNextPage, setHasNextPage] = useState(true);


    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await _axios.get(`/api/users?page=${page}&limit=${limit}`, {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });
                const data = response.data;
                setUsers(data);
                
                setHasNextPage(data.length === limit);
            } catch (error) {
                console.error("Greška prilikom učitavanja korisnika:", error);
            }
        };

        fetchUsers();
    }, [jwt, page, limit]);

    const toggleActive = async (user) => {
        if (!(role === "ADMIN" && user.type === "EVENT_CREATOR")) return;

        const confirmed = window.confirm(
            `Are you sure you want to ${user.active ? "deactivate" : "activate"} this event creator?`
        );

        if (!confirmed) return;

        try {
            await _axios.patch(
                `/api/users/${user.userId}`,
                { active: !user.active },
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                }
            );

            setUsers((prevUsers) =>
                prevUsers.map((u) =>
                    u.userId === user.userId ? { ...u, active: !u.active } : u
                )
            );
        } catch (error) {
            console.error("Error updating user:", error);
            alert("Failed to update user.");
        }
    };

    const handleRowClick = (user) => {
        setSelectedUser(user);
        setShowPopUp(true);
    };

    const handleCloseModal = () => {
        setShowPopUp(false);
        setSelectedUser(null);
    };

    const handleUserUpdated = (updatedUser) => {
        if (!updatedUser) {
            setUsers((prevUsers) =>
                prevUsers.filter((u) => u.userId !== selectedUser.userId)
            );
        } else {
            setUsers((prevUsers) =>
                prevUsers.map((u) =>
                    u.userId === updatedUser.userId ? updatedUser : u
                )
            );
        }

        handleCloseModal();
    };


    return (
        <Container className="mt-4">
            <h1 className="text-center mb-4">All users</h1>

            <Row>
                <Col md={12}>
                    <Table bordered hover responsive>
                        <thead className="table-primary">
                        <tr>
                            <th>Email</th>
                            <th>Name</th>
                            <th>Last name</th>
                            <th>Type</th>
                            <th>Active</th>
                            {role === "ADMIN" && <th>Action</th>}
                        </tr>
                        </thead>
                        <tbody>
                        {users.map((user) => (
                            <tr
                                key={user.userId}
                                onClick={() => handleRowClick(user)}
                                style={{ cursor: "pointer" }}
                            >
                                <td>{user.email}</td>
                                <td>{user.name}</td>
                                <td>{user.lastName}</td>
                                <td>{user.type}</td>
                                <td>{user.active ? "Yes" : "No"}</td>
                                {role === "ADMIN" && (
                                    <td>
                                        {user.type === "EVENT_CREATOR" ? (
                                            <Button
                                                variant={user.active ? "danger" : "success"}
                                                size="sm"
                                                onClick={(e) => {
                                                    e.stopPropagation(); 
                                                    toggleActive(user);
                                                }}
                                            >
                                                {user.active ? "Deactivate" : "Activate"}
                                            </Button>
                                        ) : (
                                            "-"
                                        )}
                                    </td>
                                )}
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                    <PaginationControls page={page} setPage={setPage} hasNextPage={hasNextPage} />
                </Col>
            </Row>

            <Button className="mb-3" onClick={() => setAddPopUp(true)}>Add New User</Button>
            <AddUser
                show={addPopUp}
                onHide={() => setAddPopUp(false)}
                onUserAdded={(newUser) => {
                    setUsers((prev) => [...prev, newUser]);
                    setAddPopUp(false);
                }}
            />

            {selectedUser && (
                <EditUser
                    show={showPopUp}
                    onHide={handleCloseModal}
                    user={selectedUser}
                    onUserUpdated={handleUserUpdated}
                />
            )}

        </Container>
    );


};

export default Users;
