import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import _axios from '../axiosInstance';
import useAuth from "../auth.js"; // Axios instanca

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleLogin = async (event) => {
        event.preventDefault();
        try {
            const response = await _axios.post('/api/users/login', {
                email,
                password,
            });
            console.log("HERE IS RESPONSE DATA" + response.data.active);
            console.log("ROLE:" + response.data.role);
            console.log("Name:" + response.data.name);
            console.log("Email:" + response.data.email);
            if (response.data.active === 'true') {
                login(response.data.jwt, response.data.role, response.data.name, response.data.email);
                navigate('/');
            }else{
                throw response;
            }
        } catch (error) {
            setErrorMessage('Wrong credentials.');
            console.error('Login failed', error);
        }
    };

    return (
        <div className="container d-flex justify-content-center align-items-center vh-100">
            <div className="col-md-4">
                <div className="card p-4 shadow">
                    <h2 className="text-center mb-4">Prijava na sistem</h2>

                    {errorMessage && <div className="alert alert-danger">{errorMessage}</div>}

                    <form onSubmit={handleLogin}>
                        <div className="mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Unesite korisničko ime"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Unesite lozinku"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <button type="submit" className="btn btn-primary w-100">Prijavi se</button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;
