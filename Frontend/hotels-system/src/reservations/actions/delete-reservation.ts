import { reservationApi } from "../reservationApi"

export const deleteReservation = async (id: string, token: string) => {
    await reservationApi.delete(`${id}`, {
        headers: {
            Authorization: `Bearer ${token}`,
            "ngrok-skip-browser-warning": "true"
        }
    });
};
