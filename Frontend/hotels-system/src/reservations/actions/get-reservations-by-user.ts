import { reservationApi } from "../reservationApi"
import type { ReservationResponse } from "../data/reservation.response";

export const getReservationsByUser = async (idUser: number): Promise<ReservationResponse[]> => {
    const response = await reservationApi.get(`/user/${idUser}`);
    const { data } = await response;
    return data;
}