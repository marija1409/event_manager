import './App.css'
import {lazy, Suspense} from "react";
import {BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom"
import {Spinner} from "react-bootstrap"
import NavigationBar from "./components/NavigationBar.jsx";
import Events from "./pages/Events.jsx";
import CategoriesList from "./pages/CategoriesPublic.jsx";
import CategoryEvents from "./pages/CategoryEvents.jsx";

// Layz-loading je tehnika koja omogucuje da se komponente ucitavaju samo kada su potrebne, npr. kada se pogodi ruta
// Ovo znaci da se komponente ucitavaju asinhrono, sto znaci da se ucitavaju u pozadini
const HomePage = lazy(() => import('./pages/HomePage'));
const LoginPage = lazy(() => import('./pages/LoginPage'));
const Users = lazy(() => import('./pages/Users.jsx'));
const Categories = lazy(() => import('./pages/Categories.jsx'));
const EventDetails = lazy(() => import('./pages/EventDetails.jsx'));
const Popular = lazy(() => import('./pages/Popular.jsx'));
const TagEvents = lazy(() => import('./pages/TagEvents.jsx'));

function App() {
    return (
        <>
            <div className="App">
                <Router>
                    <div className="app-container">
                        <NavigationBar/>
                        <main className="main-content">
                            {/* Suspense je komponenta koja omogucuje da se nesto izvrsi dok se ne ucta strana.
                             Kod nas je to Spinner komponenta (kruzic) */}
                            <Suspense fallback={<Spinner animation="border" role="status">
                                <span className="visually-hidden">Loading...</span>
                            </Spinner>}>
                                <Routes>
                                    {/* Kada se pogodi ruta prikazuje se komponenta (element) koja odgovara ruti */}
                                    <Route path="/" element={<HomePage/>}/>
                                    <Route path="/login" element={<LoginPage/>}/>
                                    <Route path="/users" element={<Users/>}/>
                                    <Route path="/categories" element={<Categories />}/>
                                    <Route path="/events" element={<Events />}/>
                                    <Route path="/events/:id" element={<EventDetails />} />
                                    <Route path="/tag/:tag" element={<TagEvents />} />
                                    <Route path="/categoriesList" element={<CategoriesList />} />
                                    <Route path="/categories/:categoryId" element={<CategoryEvents />} />
                                    <Route path="/popular" element={<Popular />} />
                                </Routes>
                            </Suspense>
                        </main>
                    </div>
                </Router>
            </div>

        </>
    )
}


export default App
