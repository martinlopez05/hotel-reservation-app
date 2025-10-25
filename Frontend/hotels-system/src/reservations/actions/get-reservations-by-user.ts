import { reservationApi } from "../reservationApi"
import type { ReservationResponse } from "../data/reservation.response";

export const getReservationsByUser = async (idUser: number, token: string): Promise<ReservationResponse[]> => {
    const response = await reservationApi.get(`/user/${idUser}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
    const { data } = await response;
    return data;
}