import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'react-toastify';
import { deleteReservation } from '../actions/delete-reservation';

export const useDeleteReservation = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: deleteReservation,
        onSuccess: () => {
            toast.success('Reserva cancelada correctamente');
            queryClient.invalidateQueries({ queryKey: ['reservations'] });
        },
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        onError: (error: any) => {
            const status = error.response?.status;
            if (status === 404) {
                toast.warn('La reserva no existe o ya fue eliminada');
            } else {
                toast.error('Error al cancelar la reserva');
            }
        },
    });
};