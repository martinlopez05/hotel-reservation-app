import type { User } from "@/Hotels/data/user.interface";
import { jwtDecode } from "jwt-decode";
import { useEffect, useState, createContext, type PropsWithChildren } from "react";

type AuthStatus = 'checking' | 'authenticated' | 'not-authenticated';

interface JwtPayload {
    exp: number;
}

interface UserContextProps {
    authStatus: AuthStatus;
    user: User | null;
    login: (user: User) => void;
    logout: () => void;
}

export const UserContext = createContext({} as UserContextProps);

export const UserContextProvider = ({ children }: PropsWithChildren) => {
    const [user, setUser] = useState<User | null>(null);
    const [authStatus, setAuthStatus] = useState<AuthStatus>('checking');

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        if (storedUser) {
            const parsed = JSON.parse(storedUser);
            const token = parsed?.token;

            if (token && isTokenValid(token)) {
                setUser(parsed);
                setAuthStatus("authenticated");
            } else {
                handleLogout();
            }
        } else {
            setAuthStatus("not-authenticated");
        }
    }, []);

    const isTokenValid = (token: string) => {
        try {
            const decoded: JwtPayload = jwtDecode(token);
            const now = Date.now() / 1000;
            return decoded.exp > now;
        } catch {
            return false;
        }
    };

    const handleLogin = (user: User) => {
        setUser(user);
        setAuthStatus("authenticated");
        localStorage.setItem("user", JSON.stringify(user));
    };

    const handleLogout = () => {
        setUser(null);
        setAuthStatus("not-authenticated");
        localStorage.removeItem("user");
        window.location.href = "/login";
    };

    useEffect(() => {
        if (authStatus === "authenticated" && user?.token) {
            const interval = setInterval(() => {
                if (!isTokenValid(user.token)) {
                    console.warn("Token expirado, redirigiendo al login...");
                    handleLogout();
                }
            }, 60 * 1000);
            return () => clearInterval(interval);
        }
    }, [authStatus, user]);

    if (authStatus === "checking") {
        return (
            <div className="flex items-center justify-center h-screen text-lg text-gray-600">
                Verificando sesión...
            </div>
        );
    }

    return (
        <UserContext.Provider
            value={{
                authStatus,
                user,
                login: handleLogin,
                logout: handleLogout,
            }}
        >
            {children}
        </UserContext.Provider>
    );
};
