export interface Room {
    id: number;
    roomNumber: number;
    hotelId: number;
    capacity: number;
    imageUrl: string;
    available: boolean;
    rating: number;
    pricePerNight: number;
    description: string;
}
