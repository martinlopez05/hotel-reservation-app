import { useQuery } from '@tanstack/react-query'
import { getReservationsByUser } from '../actions/get-reservations-by-user'
import { UserContext } from '@/context/UserContext'
import { use } from 'react';

const useGetReservationsByUser = (idUser: number) => {

    const { user } = use(UserContext);

    return useQuery(
        {
            queryKey: ['reservations', idUser],
            queryFn: () => getReservationsByUser(idUser, user ? user.token : ''),
            staleTime: 1000 * 60 * 5,
        }
    )
}

export default useGetReservationsByUser
