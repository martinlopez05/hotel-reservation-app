import { appRouter } from './app-router'
import { RouterProvider } from 'react-router'
import {
    QueryClient,
    QueryClientProvider,
} from '@tanstack/react-query'


const queryClient = new QueryClient()

const App = () => {
    return (
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={appRouter} />
        </QueryClientProvider>

    )
}

export default App
