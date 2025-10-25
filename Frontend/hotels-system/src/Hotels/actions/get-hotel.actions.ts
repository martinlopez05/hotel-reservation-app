
import { hotelApi } from "../hotelApi";
import type { Hotel } from "../data/hotel.interface";


export const getHotels = async (token: string): Promise<Hotel[]> => {
    const { data } = await hotelApi.get('', {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
    return data;
};