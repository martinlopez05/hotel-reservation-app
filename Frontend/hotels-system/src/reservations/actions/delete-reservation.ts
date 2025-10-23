import { toast } from "react-toastify";
import { reservationApi } from "../reservationApi"

export const deleteReservation = async (id: string) => {
    await reservationApi.delete(`${id}`);
};
