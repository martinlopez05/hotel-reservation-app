import { useQuery } from '@tanstack/react-query';
import { getHotels } from '../actions/get-hotel.actions';
import { UserContext } from '@/context/UserContext';
import { use } from 'react';

const useHotels = () => {

    const { user } = use(UserContext);

    return useQuery({
        queryKey: ['hotels'],
        queryFn: () => getHotels(user ? user.token : ''),
        staleTime: 1000 * 60 * 5, // 5 minutos
    });
}

export default useHotels
