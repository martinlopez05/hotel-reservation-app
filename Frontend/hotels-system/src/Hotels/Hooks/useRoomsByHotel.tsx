import { useQuery } from '@tanstack/react-query';
import { getRoomsByHotel } from '../actions/get-rooms-by-hotel';
import { UserContext } from '@/context/UserContext';
import { use } from 'react';

const useRoomsByHotel = (idHotel: number) => {

    const { user } = use(UserContext);

    return useQuery({
        queryKey: ['roomsHotels', idHotel],
        queryFn: () => getRoomsByHotel(idHotel, user ? user.token : ''),
        staleTime: 1000 * 60 * 5, // 5 minutos
    });
}

export default useRoomsByHotel
