import { hotelApi } from "../hotelApi";

export const getHotelById = async (id: number) => {
    const { data } = await hotelApi.get(`/${id}`);
    return data;
};