import type { User } from "@/Hotels/data/user.interface";
import { createContext, useState, type PropsWithChildren } from "react";





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
    const storedUser = localStorage.getItem('user');
    const initialUser = storedUser ? JSON.parse(storedUser) : null;

    const [user, setUser] = useState<User | null>(initialUser);
    const [authStatus, setAuthStatus] = useState<AuthStatus>(
        initialUser ? 'authenticated' : 'not-authenticated'
    );

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
