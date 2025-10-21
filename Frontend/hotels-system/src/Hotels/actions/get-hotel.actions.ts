
import { hotelApi } from "../hotelApi";
import type { Hotel } from "../types/hotel.interface";


export const getHotels = async (): Promise<Hotel[]> => {
    const { data } = await hotelApi.get('');
    return data;
};