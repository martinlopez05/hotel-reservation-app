import { appRouter } from './route/app-router'
import { RouterProvider } from 'react-router'
import {
    QueryClient,
    QueryClientProvider,
} from '@tanstack/react-query'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { UserContextProvider } from './context/UserContext';

const queryClient = new QueryClient()

const App = () => {
    return (
        <>
            <UserContextProvider>
                <QueryClientProvider client={queryClient}>
                    <RouterProvider router={appRouter} />
                </QueryClientProvider>
                <ToastContainer
                    position="top-right"
                    autoClose={4000}
                    hideProgressBar={false}
                    newestOnTop={false}
                    closeOnClick
                    pauseOnHover
                    draggable
                />
            </UserContextProvider>
        </>

    )
}

export default App
