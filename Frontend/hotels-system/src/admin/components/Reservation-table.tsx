import { useState, useEffect, use } from "react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { UserContext } from "@/context/UserContext"
import type { ReservationResponse } from "@/reservations/data/reservation.response"
import { Button } from "@/components/ui/button"
import { PaymentModal } from "./PaymentModal"
import dayjs from "dayjs"
import "dayjs/locale/es";

dayjs.locale("es");

export function ReservationsTable() {
    const [reservations, setReservations] = useState<ReservationResponse[]>([])
    const [loading, setLoading] = useState(true)
    const [isPaymentModalOpen, setIsPaymentModalOpen] = useState(false);
    const [selectedReservation, setSelectedReservation] = useState<ReservationResponse | null>(null);


    const BASE_URL = import.meta.env.VITE_API_URL_RESERVATION;

    const { user } = use(UserContext);

    useEffect(() => {
        fetchReservations()
    }, [])

    const fetchReservations = async () => {
        try {
            setLoading(true)
            const response = await fetch(`${BASE_URL}`, {
                headers: {
                    Authorization: `Bearer ${user?.token}`,
                    "ngrok-skip-browser-warning": "true"
                },
            })
            if (response.ok) {
                const data = await response.json()
                setReservations(data)
            }
        } catch (error) {
            console.error("Error fetching reservations:", error)
        } finally {
            setLoading(false)
        }
    }

    const handlePaymentSuccess = (reservationId: string) => {
        setReservations(prev =>
            prev.map(r =>
                r.id === reservationId ? { ...r, state: "PAYMENT" } : r
            )
        );
    };

    const handlePay = (reservation: ReservationResponse) => {
        setSelectedReservation(reservation);
        setIsPaymentModalOpen(true);
    };

    const formatDate = (dateString: string) => {
        return dayjs(dateString).format("DD [de] MMMM [de] YYYY")
    }

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat("es-ES", {
            style: "currency",
            currency: "ARS",
        }).format(price)
    }


    const getStateBadge = (state: string) => {
        const stateConfig = {
            PAYMENT: { variant: "default" as const, className: "bg-emerald-600 text-white border border-emerald-700 shadow-sm" },
            PENDING: { variant: "secondary" as const, className: "bg-red-500 text-white border border-red-600 shadow-sm" },
        }

        const config = stateConfig[state as keyof typeof stateConfig] || { variant: "outline" as const, className: "" }

        return (
            <Badge variant={config.variant} className={config.className}>
                {state === 'PAYMENT'
                    ? 'Pagada'
                    : state === 'PENDING'
                        ? 'Pendiente de pago'
                        : state}
            </Badge>
        )
    }

    if (loading) {
        return <div className="text-center py-8">Cargando reservas...</div>
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>Reservas</CardTitle>
            </CardHeader>
            <CardContent>
                <div className="rounded-md border">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Orden</TableHead>
                                <TableHead>Hotel</TableHead>
                                <TableHead>Habitación</TableHead>
                                <TableHead>Usuario</TableHead>
                                <TableHead>fecha de entrada</TableHead>
                                <TableHead>fecha de salida</TableHead>
                                <TableHead>Estado</TableHead>
                                <TableHead className="text-right">Precio</TableHead>
                                <TableHead>Fecha Registro</TableHead>
                                <TableHead></TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {reservations.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={9} className="text-center py-8 text-muted-foreground">
                                        No hay reservas registradas
                                    </TableCell>
                                </TableRow>
                            ) : (
                                reservations.map((reservation) => (
                                    <TableRow key={reservation.id}>
                                        <TableCell className="font-medium">#{reservation.orderNumber}</TableCell>
                                        <TableCell>{reservation.hotelName}</TableCell>
                                        <TableCell>{reservation.roomNumber}</TableCell>
                                        <TableCell>{reservation.username}</TableCell>
                                        <TableCell>{formatDate(reservation.checkInDate)}</TableCell>
                                        <TableCell>{formatDate(reservation.checkOutDate)}</TableCell>
                                        <TableCell>{getStateBadge(reservation.state)}</TableCell>
                                        <TableCell className="text-right font-medium">{formatPrice(reservation.price)}</TableCell>
                                        <TableCell className="text-muted-foreground">{formatDate(reservation.registrationDate)}</TableCell>
                                        <TableCell className="text-muted-foreground">
                                            {reservation.state === "PENDING" && (
                                                <Button
                                                    onClick={() => handlePay(reservation)}
                                                    className="bg-primary text-white px-3 py-1 rounded-md"
                                                >
                                                    Registrar pago
                                                </Button>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                    {selectedReservation && (
                        <PaymentModal
                            isOpen={isPaymentModalOpen}
                            onClose={() => setIsPaymentModalOpen(false)}
                            reservationId={selectedReservation.id}
                            orderReservation={selectedReservation.orderNumber}
                            amount={selectedReservation.price}
                            userId={selectedReservation.userId}
                            onPaymentSuccess={() => handlePaymentSuccess(selectedReservation.id)}
                        />
                    )}
                </div>
            </CardContent>
        </Card>
    )
}
