import { useMutation, useQueryClient } from '@tanstack/react-query';// ajustá la ruta
import { createReservation } from '../actions/post-reservation';

export const useCreateReservation = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createReservation,
        onSuccess: () => {
            // opcional: actualiza la lista de reservas si la tenés en cache
            queryClient.invalidateQueries({ queryKey: ['reservations'] });
        },
    });
};