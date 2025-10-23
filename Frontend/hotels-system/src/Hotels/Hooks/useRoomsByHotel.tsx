import { useQuery } from '@tanstack/react-query';
import { getRoomsByHotel } from '../actions/get-rooms-by-hotel';

const useRoomsByHotel = (idHotel: number) => {
    return useQuery({
        queryKey: ['roomsHotels', idHotel],
        queryFn: () => getRoomsByHotel(idHotel),
        staleTime: 1000 * 60 * 5, // 5 minutos
    });
}

export default useRoomsByHotel
