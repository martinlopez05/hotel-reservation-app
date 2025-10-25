
import { hotelApi } from "../hotelApi";

export const getHotelById = async (id: number, token: string) => {
    const { data } = await hotelApi.get(`/${id}`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
    return data;
};