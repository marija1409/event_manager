import {Navbar, Nav, Container, Button, Form} from "react-bootstrap";
import { Link, useLocation, useNavigate } from "react-router-dom";
import useAuth from "../auth.js";
import {useEffect, useState} from "react";

const NavigationBar = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { isAuthenticated, logout, role, name } = useAuth();
    const [searchInput, setSearchInput] = useState("");

    useEffect(() => {
        const currentParams = new URLSearchParams(location.search);
        setSearchInput(currentParams.get("query") || "");
    }, [location.search]);

    const openPublic = () => {
        window.open("/?public=true", "_blank", "noopener,noreferrer");
    };


    return (
        <Navbar bg="primary" variant="dark" expand="lg">
            <Container>
                {/* Link do početne stranice, Brand je poput Home page-a */}

                <Navbar.Brand as={Link} to="/"  onClick={openPublic}>Event manager</Navbar.Brand>

                {/* Dugme za prikazivanje menija na manjim ekranima */}
                <Navbar.Toggle aria-controls="basic-navbar-nav" />

                {/* Glavni meni */}
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {/* Linkovi ka različitim sekcijama */}
                        {
                            !isAuthenticated && (
                                <>
                                    <Nav.Link as={Link} to="/" className={location.pathname === "/" ? "active" : ""}>
                                        Home
                                    </Nav.Link>
                                    <Nav.Link as={Link} to="/categoriesList" className={location.pathname === "/" ? "active" : ""}>
                                        Categories
                                    </Nav.Link>
                                    <Nav.Link as={Link} to="/popular" className={location.pathname === "/popular" ? "active" : ""}>
                                    Popular
                                    </Nav.Link>
                                </>
                            )
                        }

                        {isAuthenticated && (role === "ADMIN") && (
                            <Nav.Link as={Link} to="/users" className={location.pathname === "/users" ? "active" : ""}>
                            Users
                            </Nav.Link>
                        )}

                        {isAuthenticated && (
                            <>
                                <Nav.Link as={Link} to="/categories" className={location.pathname === "/categories" ? "active" : ""}>
                                    Categories
                                </Nav.Link>
                                <Nav.Link as={Link} to="/events" className={location.pathname === "/events" ? "active" : ""}>
                                    Events
                                </Nav.Link>
                            </>
                        )}



                    </Nav>


                    <div className="d-flex align-items-center gap-5">
                        {(location.pathname === "/events" || location.pathname === "/") && (
                            <>
                                <Form
                                    className="d-flex"
                                    onSubmit={(e) => {
                                        e.preventDefault();
                                        const params = new URLSearchParams();
                                        if (searchInput) {
                                            params.set("query", searchInput);
                                        }
                                        const targetPath = location.pathname === "/events" ? "/events" : "/";
                                        navigate(`${targetPath}?${params.toString()}`);
                                    }}
                                >
                                    <Form.Control
                                        name="search"
                                        type="search"
                                        placeholder="Search"
                                        className="me-2"
                                        aria-label="Search"
                                        value={searchInput}
                                        onChange={(e) => setSearchInput(e.target.value)}
                                        onInput={(e) => {
                                            if (e.currentTarget.value === "") {
                                                setSearchInput("");
                                                const targetPath = location.pathname === "/events" ? "/events" : "/";
                                                navigate(targetPath);
                                            }
                                        }}
                                    />
                                    <Button variant="outline-success" type="submit">Search</Button>
                                </Form>

                            </>
                        )}




                        {isAuthenticated ? (
                            <div>
                                <Button variant="danger" onClick={() => {
                                    logout(navigate);
                                    navigate("/");
                                }}>Logout</Button>
                                <span className="text-white ms-3 fw-semibold">{name}</span>
                            </div>

                        ) : (
                            <Button variant="success" as={Link} to="/login">Login</Button>
                        )}
                    </div>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default NavigationBar;
