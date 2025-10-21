export interface Hotel {
    id: string
    name: string
    address: string
    country: string
    location: string
    description: string
    image: string // renamed from imageUrl to image for consistency
    pricePerNight: number // added pricePerNight to Hotel type
    rating: number
    email: string
    phone: string
}

export interface Room {
    id: string
    roomNumber: string
    hotelId: string
    capacity: number
    available: boolean
    rating: number
    pricePerNight: number
    description: string
}

export interface Reservation {
    id: string
    hotelName: string
    roomNumber: string
    orderNumber: string
    username: string
    checkInDate: string
    checkOutDate: string
    price: number
    registrationDate: string
}

export type UserRole = "ADMIN" | "USER"

export interface User {
    username: string
    role: UserRole
}