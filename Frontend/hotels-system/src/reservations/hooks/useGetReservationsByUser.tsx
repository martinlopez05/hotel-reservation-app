import { useQuery } from '@tanstack/react-query'
import { getReservationsByUser } from '../actions/get-reservations-by-user'

const useGetReservationsByUser = (idUser: number) => {
    return useQuery(
        {
            queryKey: ['reservations', idUser],
            queryFn: () => getReservationsByUser(idUser),
            staleTime: 1000 * 60 * 5,
        }
    )
}

export default useGetReservationsByUser
