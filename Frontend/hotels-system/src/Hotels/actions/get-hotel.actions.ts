
import { hotelApi } from "../hotelApi";
import type { Hotel } from "../data/hotel.interface";


export const getHotels = async (): Promise<Hotel[]> => {
    const { data } = await hotelApi.get('');
    return data;
};