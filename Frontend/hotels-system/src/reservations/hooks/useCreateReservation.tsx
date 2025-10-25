import { useMutation, useQueryClient } from '@tanstack/react-query';// ajustá la ruta
import { createReservation } from '../actions/post-reservation';
import type { ReservationData } from '../data/reservationData';

export const useCreateReservation = (token: string) => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (reservation: ReservationData) => createReservation(reservation, token),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['reservations'] });
        },
    });
};