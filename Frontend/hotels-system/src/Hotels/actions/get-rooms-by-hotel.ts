import { roomApi } from "../roomApi";
import type { Room } from "../data/room.interface";

export const getRoomsByHotel = async (idHotel: number): Promise<Room[]> => {
  const { data } = await roomApi.get(`/hotel/${idHotel}`);
  return data;
};