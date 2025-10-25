import { reservationApi } from "../reservationApi";
import type { ReservationData } from "../data/reservationData";
import { toast } from 'react-toastify';

export const createReservation = async (reservation: ReservationData, token: string) => {
    try {
        const response = await reservationApi.post(
            '',
            reservation,
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            }
        );

        toast.success("Reserva creada correctamente");
        return response.data;
    } catch (error: any) {
        const status = error.response?.status;

        if (status === 500 || status === 409) {
            toast.error('La habitación ya está reservada en esas fechas');
        } else {
            toast.error('Ocurrió un error inesperado. Por favor, intentá de nuevo.');
        }
    }
};

