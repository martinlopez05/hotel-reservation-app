
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Mail, Phone, MapPin, Star, Users, DollarSign } from "lucide-react"
import useHotelById from "../Hooks/useHotelById"
import { useParams } from "react-router"
import useRoomsByHotel from "../Hooks/useRoomsByHotel"
import { use, useState } from "react"
import { Button } from "@/components/ui/button"
import ReservationModal from "@/reservations/components/ReservationModal"
import { UserContext } from "@/context/UserContext"
import { HotelReviews } from "../components/HotelReviews"






export default function HotelPage() {

    const { idHotel = '' } = useParams();

    const { data: hotel, isLoading: loadingHotel, isError: errorHotel } = useHotelById(+idHotel);

    const { data: rooms, isLoading: loadingRoom, isError: errorRoom } = useRoomsByHotel(+idHotel);

    const { user } = use(UserContext);

    const [isModalOpen, setIsModalOpen] = useState(false)
    const [selectedRoom, setSelectedRoom] = useState<{ id: number; roomNumber: number; pricePerNight: number } | null>(
        null,
    )

    const handleRoomClick = (roomId: number, roomNumber: number, pricePerNight: number, available: boolean) => {
        if (available) {
            setSelectedRoom({ id: roomId, roomNumber, pricePerNight })
            setIsModalOpen(true)
        }
    }

    if (loadingHotel || loadingRoom) {
        return <p>Cargando...</p>
    }

    if (errorHotel || errorRoom) {
        return <p>Error...</p>
    }


    return (
        <div className="min-h-screen bg-background">

            <main className="container mx-auto px-4 py-8">
                {/* Imagen principal del hotel */}
                <div className="relative w-full h-[400px] md:h-[500px] rounded-xl overflow-hidden shadow-lg mb-8">
                    <img
                        src={hotel.imageUrl || "/placeholder.svg"}
                        alt={hotel.name}
                        className="w-full h-full object-cover object-center"
                    />
                </div>
                {/* Detalles del hotel */}
                <div className="grid gap-8 md:grid-cols-3 mb-12">
                    <div className="md:col-span-2 space-y-6">
                        <div>
                            <div className="flex items-start justify-between mb-2">
                                <h1 className="text-4xl font-bold text-foreground text-balance">{hotel.name}</h1>
                                <div className="flex items-center gap-1 bg-primary text-primary-foreground px-3 py-1 rounded-lg">
                                    <Star className="w-4 h-4 fill-current" />
                                    <span className="font-semibold">{hotel.rating}</span>
                                </div>
                            </div>

                            <div className="flex items-center gap-2 text-muted-foreground mb-4">
                                <MapPin className="w-4 h-4" />
                                <span className="text-sm">
                                    {hotel.address}, {hotel.location}, {hotel.country}
                                </span>
                            </div>

                            <p className="text-foreground leading-relaxed">{hotel.description}</p>
                        </div>
                    </div>

                    <Card className="h-fit">
                        <CardHeader>
                            <CardTitle>Información de contacto</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="flex items-center gap-3">
                                <div className="bg-muted p-2 rounded-lg">
                                    <Mail className="w-5 h-5 text-muted-foreground" />
                                </div>
                                <div>
                                    <p className="text-xs text-muted-foreground">Email</p>
                                    <a
                                        href={`mailto:${hotel.email}`}
                                        className="text-sm font-medium text-foreground hover:text-primary transition-colors"
                                    >
                                        {hotel.email}
                                    </a>
                                </div>
                            </div>

                            <div className="flex items-center gap-3">
                                <div className="bg-muted p-2 rounded-lg">
                                    <Phone className="w-5 h-5 text-muted-foreground" />
                                </div>
                                <div>
                                    <p className="text-xs text-muted-foreground">Teléfono  {hotel.phone}</p>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                {/* Sección de habitaciones disponibles */}
                <div>
                    <h2 className="text-3xl font-bold text-foreground mb-6">Habitaciones disponibles</h2>

                    <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                        {rooms?.map((room) => (
                            <Card key={room.id} className="overflow-hidden hover:shadow-lg transition-shadow">
                                {/* Imagen como header visual */}
                                <img
                                    src={room.imageUrl || "/placeholder.svg?height=200&width=400&query=hotel room"}
                                    alt={`Habitación ${room.roomNumber}`}
                                    className="w-full h-56 object-cover object-center"
                                />

                                <CardHeader className="pb-4">
                                    <div className="flex items-start justify-between">
                                        <div>
                                            <CardTitle className="text-xl">Habitación {room.roomNumber}</CardTitle>
                                            <CardDescription className="mt-1">{room.description}</CardDescription>
                                        </div>
                                        <Badge
                                            variant={room.available ? "default" : "secondary"}
                                            className={room.available ? "bg-green-500 hover:bg-green-600" : ""}
                                        >
                                            {room.available ? "Disponible" : "No disponible"}
                                        </Badge>
                                    </div>
                                </CardHeader>

                                <CardContent className="space-y-4">
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center gap-2 text-muted-foreground">
                                            <Users className="w-4 h-4" />
                                            <span className="text-sm">Capacidad: {room.capacity} personas</span>
                                        </div>
                                        <div className="flex items-center gap-1">
                                            <Star className="w-4 h-4 fill-yellow-400 text-yellow-400" />
                                            <span className="text-sm font-medium">{room.rating}</span>
                                        </div>
                                    </div>

                                    <div className="pt-4 border-t border-border">
                                        <div className="flex items-center justify-between">
                                            <div className="flex items-center gap-1 text-muted-foreground">
                                                <DollarSign className="w-4 h-4" />
                                                <span className="text-sm">Por noche</span>
                                            </div>
                                            <span className="text-2xl font-bold text-foreground">${room.pricePerNight}</span>
                                        </div>
                                        <Button
                                            className="w-full mt-3"
                                            onClick={() => handleRoomClick(room.id, room.roomNumber, room.pricePerNight, room.available)}
                                            disabled={!room.available}
                                        >
                                            {room.available ? "Reservar ahora" : "No disponible"}
                                        </Button>
                                    </div>
                                </CardContent>
                            </Card>

                        ))}
                    </div>
                </div>
                <div className="mt-50">
                    <HotelReviews hotelId={+idHotel} ></HotelReviews>
                </div>

            </main>
            {selectedRoom && (
                <ReservationModal
                    isOpen={isModalOpen}
                    onClose={() => setIsModalOpen(false)}
                    roomId={selectedRoom.id}
                    roomNumber={selectedRoom.roomNumber}
                    pricePerNight={selectedRoom.pricePerNight}
                    hotelId={hotel.id}
                    userId={user ? user.id : 0}

                ></ReservationModal>
            )}
        </div>
    )
}
