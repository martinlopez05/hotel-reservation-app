import { useQuery } from '@tanstack/react-query';
import { getHotels } from '../actions/get-hotel.actions';

const useHotels = () => {
    return useQuery({
        queryKey: ['hotels'],
        queryFn: getHotels,
        staleTime: 1000 * 60 * 5, // 5 minutos
    });
}

export default useHotels
