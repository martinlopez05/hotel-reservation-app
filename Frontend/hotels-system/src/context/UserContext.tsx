import type { User } from "@/Hotels/data/user.interface";
import { createContext, useEffect, useState, type PropsWithChildren } from "react";





type AuthStatus = 'checking' | 'authenticated' | 'not-authenticated';

interface UserContextProps {
    authStatus: AuthStatus;
    isauthenticated: boolean;
    user: User | null;
    login: (user: User) => boolean;
    logout: () => void;
}


export const UserContext = createContext({} as UserContextProps);

export const UserContextProvider = ({ children }: PropsWithChildren) => {
    const [user, setUser] = useState<User | null>(null);
    const [authStatus, setAuthStatus] = useState<AuthStatus>('checking');

    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            const parsed = JSON.parse(storedUser);
            setUser(parsed);
            setAuthStatus('authenticated');
        } else {
            setAuthStatus('not-authenticated');
        }
    }, []);

    const handleLogin = (user: User) => {
        const userWithRole = { ...user, role: user.role || 'ROLE_USER' };
        setUser(userWithRole);
        setAuthStatus('authenticated');
        localStorage.setItem('user', JSON.stringify(userWithRole));
        return true;
    };

    const handleLogout = () => {
        setUser(null);
        setAuthStatus('not-authenticated');
        localStorage.removeItem('user');
    };

    if (authStatus === 'checking') {
        return <div>Cargando sesión...</div>;
    }

    return (
        <UserContext.Provider
            value={{
                authStatus,
                isauthenticated: authStatus === 'authenticated',
                user,
                login: handleLogin,
                logout: handleLogout,
            }}
        >
            {children}
        </UserContext.Provider>
    );
};

