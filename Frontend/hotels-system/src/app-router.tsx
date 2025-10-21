import { lazy } from 'react';
import { createBrowserRouter, Navigate } from 'react-router';

import Layout from './layout/layout';
import HomePage from './Hotels/pages/HomePage';
import Login from './admin/Login';

export const appRouter = createBrowserRouter([
    {
        path: '/',
        element: <Layout />,
        children: [
            {
                index: true,
                element: <HomePage />,
            },
            {
                path: 'hotels/:idHotel',
                element: <></>,
            },
            {
                path: '*',
                // element: <h1>404</h1>,
                element: <Navigate to="/" />,
            },
        ],
    },

    {
        path: '/admin',
        element: <Login />,
    },
]);
