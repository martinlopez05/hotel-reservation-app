import { UserContext } from "@/context/UserContext";
import { use, type JSX } from "react";
import { Navigate } from "react-router";


interface Props {
    element: JSX.Element; // React.ReactNode
}

export const PrivateRoute = ({ element }: Props) => {
    const { authStatus } = use(UserContext);

    if (authStatus === 'checking') {
        return null;
    }

    if (authStatus === 'authenticated') {
        return element;
    }

    return <Navigate to="/login" replace />;
};
