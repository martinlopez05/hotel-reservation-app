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

export const UserContextprovider = ({ children }: PropsWithChildren) => {

    const [authStatus, setAuthStatus] = useState<AuthStatus>('checking');
    const [user, setUser] = useState<User | null>(null);

    const handleLogin = (user: User) => {
        const userWithRole = { ...user, role: user.role || "USER" };
        setUser(userWithRole);
        setAuthStatus('authenticated');
        localStorage.setItem('user', JSON.stringify(user));
        return true;
    }

    const handleLogout = () => {
        setUser(null);
        setAuthStatus('not-authenticated');
        localStorage.removeItem('user');
    }


    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            const parsedUser: User = JSON.parse(storedUser);
            setUser(parsedUser);
            setAuthStatus('authenticated');
        } else {
            setAuthStatus('not-authenticated');
        }
    }, []);

    return (<UserContext value={{
        authStatus: authStatus,
        isauthenticated: authStatus === 'authenticated',
        user: user,
        login: handleLogin,
        logout: handleLogout
    }}>{children}</UserContext>);

}