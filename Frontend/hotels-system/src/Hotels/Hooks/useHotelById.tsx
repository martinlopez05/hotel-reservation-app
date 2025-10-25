import { useQuery } from '@tanstack/react-query';
import { getHotelById } from '../actions/get-hotel-by-id.actions';
import { UserContext } from '@/context/UserContext';
import { use } from 'react';

const useHotelById = (id: number) => {

    const { user } = use(UserContext);

    return useQuery({
        queryKey: ['hotel', id],
        queryFn: () => getHotelById(id, user.token),
        staleTime: 1000 * 60 * 5, // 5 minutos
    });
}

export default useHotelById
