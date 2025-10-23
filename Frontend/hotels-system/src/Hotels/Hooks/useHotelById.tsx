import { useQuery } from '@tanstack/react-query';
import { getHotelById } from '../actions/get-hotel-by-id.actions';

const useHotelById = (id: number) => {
    return useQuery({
        queryKey: ['hotel', id],
        queryFn: () => getHotelById(id),
        staleTime: 1000 * 60 * 5, // 5 minutos
    });
}

export default useHotelById
