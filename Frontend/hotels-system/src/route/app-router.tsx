import { createBrowserRouter, Navigate } from 'react-router';

import Layout from '../layout/Layout';
import HomePage from '../Hotels/pages/HomePage';
import HotelPage from '../Hotels/pages/HotelPage';
import LoginForm from '../login/components/LoginForm';
import { PrivateRoute } from './PrivateRoute';
import AdminPage from '@/admin/AdminPage';

export const appRouter = createBrowserRouter([
    {
        path: '/',
        element: <Layout />,
        children: [
            {
                index: true,
                element: <PrivateRoute element={<HomePage />}></PrivateRoute>,
            },
            {
                path: 'hotel/:idHotel',
                element: <PrivateRoute element={<HotelPage></HotelPage>}></PrivateRoute>,
            },
            {
                path: '*',
                element: <Navigate to="/" />,
            },
        ],
    },

    {
        path: '/login',
        element: <LoginForm></LoginForm>
    },
    {
        path: '/admin',
        element: <PrivateRoute element={<AdminPage></AdminPage>}></PrivateRoute>
    }
]);
