import { roomApi } from "../roomApi";
import type { Room } from "../data/room.interface";


export const getRoomsByHotel = async (idHotel: number, token: string): Promise<Room[]> => {
  const { data } = await roomApi.get(`/hotel/${idHotel}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return data;
};