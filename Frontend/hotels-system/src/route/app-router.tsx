import { createBrowserRouter } from 'react-router';

import Layout from '../layout/Layout';
import HomePage from '../Hotels/pages/HomePage';
import HotelPage from '../Hotels/pages/HotelPage';
import LoginForm from '../login/components/LoginForm';
import { PrivateRoute } from './PrivateRoute';
import AdminPage from '@/admin/AdminPage';
import RegisterPage from '@/login/components/Register';

export const appRouter = createBrowserRouter([
    {
        path: '/',
        element: <Layout />,
        children: [
            {
                index: true,
                element: (
                    <PrivateRoute>
                        <HomePage />
                    </PrivateRoute>
                ),
            },
            {
                path: 'hotel/:idHotel',
                element: (
                    <PrivateRoute>
                        <HotelPage />
                    </PrivateRoute>
                ),
            },
            {
                path: '/admin',
                element: (
                    <PrivateRoute>
                        <AdminPage />
                    </PrivateRoute>
                ),
            },

        ],
    },

    {
        path: '/login',
        element: <LoginForm></LoginForm>
    },
    {
        path: '/register',
        element: <RegisterPage></RegisterPage>
    },
    {
        path: '/admin',
        element: <PrivateRoute>
            <AdminPage></AdminPage>
        </PrivateRoute>
    }
]);
