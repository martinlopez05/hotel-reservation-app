import { UserContext } from "@/context/UserContext";
import { Navigate } from "react-router";
import { useContext, type JSX } from "react";


interface Props {
    children: JSX.Element;
}

export const PrivateRoute = ({ children }: Props) => {
    const { authStatus } = useContext(UserContext);

    if (authStatus === "checking") {
        return (
            <div className="loading-container">
                <span className="spinner" />
                <p>Verificando sesión...</p>
            </div>
        );
    }

    if (authStatus === "authenticated") {
        return children;
    }

    return <Navigate to="/login" replace />;
};
