import React, { useEffect, useState } from "react";
import AuthContext from "./AuthContext";

export const AuthProvider = ({ children }) => {
    const [jwt, setJwt] = useState(() => localStorage.getItem("jwt"));
    const [role, setRole] = useState(() => localStorage.getItem("role"));
    const [name, setName] = useState(() => localStorage.getItem("name"));
    const [email, setEmail] = useState(() => localStorage.getItem("email"));

    const [isPublicTab, setIsPublicTab] = useState(false);

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        if (params.get("public") === "true") {
            setIsPublicTab(true);
        }
    }, []);

    useEffect(() => {
        if (isPublicTab) return;
        if (jwt) localStorage.setItem("jwt", jwt);
        else localStorage.removeItem("jwt");
    }, [jwt, isPublicTab]);

    useEffect(() => {
        if (isPublicTab) return;
        if (role) localStorage.setItem("role", role);
        else localStorage.removeItem("role");
    }, [role, isPublicTab]);

    useEffect(() => {
        if (isPublicTab) return;
        if (name) localStorage.setItem("name", name);
        else localStorage.removeItem("name");
    }, [name, isPublicTab]);

    useEffect(() => {
        if (isPublicTab) return;
        if (email) localStorage.setItem("email", email);
        else localStorage.removeItem("email");
    }, [email, isPublicTab]);

    const login = (jwtToken, userRole, name, email) => {
        if (!isPublicTab) {
            setJwt(jwtToken);
            setRole(userRole);
            setName(name);
            setEmail(email);
        }
    };

    const logout = () => {
        if (!isPublicTab) {
            setJwt(null);
            setRole(null);
            setName(null);
            setEmail(null);
        }
    };

    const isTokenValid = () => {
        if (!jwt) return false;
        try {
            const parts = jwt.split(".");
            if (parts.length !== 3) return false;
            const payload = JSON.parse(atob(parts[1]));
            if (!payload.exp) return false;
            return Date.now() < payload.exp * 1000;
        } catch (error) {
            console.error("Error decoding JWT:", error);
            return false;
        }
    };

    const isAuthenticated = !isPublicTab && jwt && isTokenValid();

    return (
        <AuthContext.Provider
            value={{ jwt, role, login, logout, isAuthenticated, name, email, isPublicTab }}
        >
            {children}
        </AuthContext.Provider>
    );
};
