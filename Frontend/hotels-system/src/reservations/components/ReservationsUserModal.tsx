
import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Calendar, DollarSign, Hash, Hotel, User, X } from "lucide-react"
import type { ReservationResponse } from "../data/reservation.response"
import { useDeleteReservation } from "../hooks/useDeleteReservation"
import dayjs from "dayjs";


interface MyReservationsModalProps {
    isOpen: boolean
    onClose: () => void
    reservations: ReservationResponse[]
}

export function MyReservationsModal({ isOpen, onClose, reservations }: MyReservationsModalProps) {
    const [cancelModalOpen, setCancelModalOpen] = useState(false)
    const [selectedReservation, setSelectedReservation] = useState<ReservationResponse | null>(null)

    const { mutate: deleteReservation } = useDeleteReservation();

    const formatDate = (dateString: string) => {
        return dayjs(dateString).format("DD [de] MMMM [de] YYYY");
    };

    const formatDateTime = (dateString: string) => {
        const date = new Date(dateString)
        return date.toLocaleDateString("es-ES", {
            year: "numeric",
            month: "short",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        })
    }

    const handleCancelClick = (reservation: ReservationResponse) => {
        setSelectedReservation(reservation)
        setCancelModalOpen(true)
    }

    const handleConfirmCancel = () => {
        if (selectedReservation) {
            deleteReservation(selectedReservation.id);
        }
        setCancelModalOpen(false)
        setSelectedReservation(null)
    }

    return (
        <>
            <Dialog open={isOpen} onOpenChange={onClose}>
                <DialogContent className="max-w-5xl max-h-[85vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle className="text-2xl">Mis Reservas</DialogTitle>
                    </DialogHeader>

                    <div className="space-y-4 mt-4">
                        {reservations.length === 0 ? (
                            <div className="text-center py-12 text-muted-foreground">
                                <p>No tienes reservas registradas</p>
                            </div>
                        ) : (
                            reservations.map((reservation) => (
                                <Card key={reservation.id} className="overflow-hidden hover:shadow-md transition-shadow">
                                    <CardContent className="p-6">
                                        <div className="space-y-4">
                                            {/* Información principal */}
                                            <div className="space-y-3">
                                                <div className="flex items-start justify-between">
                                                    <div>
                                                        <div className="flex items-center gap-2 mb-1">
                                                            <Hotel className="w-5 h-5 text-primary" />
                                                            <h3 className="text-lg font-semibold text-foreground">{reservation.hotelName}</h3>
                                                        </div>
                                                        <div className="flex items-center gap-2 text-sm text-muted-foreground">
                                                            <Hash className="w-4 h-4" />
                                                            <span>Habitación {reservation.roomNumber}</span>
                                                        </div>
                                                    </div>
                                                    <Badge variant="outline" className="text-xs">
                                                        Orden #{reservation.orderNumber}
                                                    </Badge>
                                                </div>

                                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pt-2">
                                                    <div className="flex items-center gap-2 text-sm">
                                                        <User className="w-4 h-4 text-muted-foreground" />
                                                        <span className="text-muted-foreground">Usuario:</span>
                                                        <span className="font-medium text-foreground">{reservation.username}</span>
                                                    </div>

                                                    <div className="flex items-center gap-2 text-sm">
                                                        <DollarSign className="w-4 h-4 text-muted-foreground" />
                                                        <span className="text-muted-foreground">Total:</span>
                                                        <span className="font-bold text-lg text-foreground">${reservation.price.toFixed(2)}</span>
                                                    </div>
                                                </div>

                                                <div className="flex flex-col gap-2 pt-2 border-t border-border">
                                                    <div className="flex items-center gap-2 text-sm">
                                                        <Calendar className="w-4 h-4 text-green-600" />
                                                        <span className="text-muted-foreground">Check-in:</span>
                                                        <span className="font-medium text-foreground">{formatDate(reservation.checkInDate)}</span>
                                                    </div>

                                                    <div className="flex items-center gap-2 text-sm">
                                                        <Calendar className="w-4 h-4 text-red-600" />
                                                        <span className="text-muted-foreground">Check-out:</span>
                                                        <span className="font-medium text-foreground">{formatDate(reservation.checkOutDate)}</span>
                                                    </div>
                                                </div>

                                                <div className="text-xs text-muted-foreground pt-2">
                                                    Registrado: {formatDateTime(reservation.registrationDate)}
                                                </div>
                                            </div>

                                            <div className="pt-2 border-t border-border">
                                                <Button
                                                    variant="destructive"
                                                    size="lg"
                                                    onClick={() => handleCancelClick(reservation)}
                                                    className="w-full"
                                                >
                                                    <X className="w-4 h-4 mr-2" />
                                                    Cancelar Reserva
                                                </Button>
                                            </div>
                                        </div>
                                    </CardContent>
                                </Card>
                            ))
                        )}
                    </div>
                </DialogContent>
            </Dialog>

            <Dialog open={cancelModalOpen} onOpenChange={setCancelModalOpen}>
                <DialogContent className="max-w-sm">
                    <DialogHeader>
                        <DialogTitle className="text-lg">Confirmar Cancelación</DialogTitle>
                    </DialogHeader>

                    <div className="py-4">
                        <p className="text-center text-muted-foreground">¿Estás seguro que quieres cancelar la reserva?</p>
                    </div>

                    <div className="flex gap-3 justify-center">
                        <Button variant="outline" onClick={() => setCancelModalOpen(false)}>
                            No, mantener
                        </Button>
                        <Button variant="destructive" onClick={handleConfirmCancel}>
                            Sí, cancelar
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>
        </>
    )
}
