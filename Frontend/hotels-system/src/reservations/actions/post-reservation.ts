import { reservationApi } from "../reservationApi";
import type { ReservationData } from "../data/reservationData";
import { toast } from 'react-toastify';

export const createReservation = async (reservation: ReservationData) => {
    try {
        const response = await reservationApi.post('', reservation);
        toast.success("reserva creada correctamente");
        return response.data;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (error: any) {
        const status = error.response?.status;

        if (status === 500 || status === 409) {
            toast.error('La habitación ya está reservada en esas fechas');
        } else {
            toast.error('Ocurrió un error inesperado. Por favor, intentá de nuevo.');
        }

    }
};
