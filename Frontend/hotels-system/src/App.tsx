import { appRouter } from './route/app-router'
import { RouterProvider } from 'react-router'
import {
    QueryClient,
    QueryClientProvider,
} from '@tanstack/react-query'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { UserContextprovider } from './context/UserContext';

const queryClient = new QueryClient()

const App = () => {
    return (
        <>
            <UserContextprovider>
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
            </UserContextprovider>
        </>

    )
}

export default App
