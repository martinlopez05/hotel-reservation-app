import { useState, useMemo, use, useContext } from "react"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Calendar } from "lucide-react"
import type { ReservationData } from "../data/reservationData"
import { useCreateReservation } from "../hooks/useCreateReservation"
import { UserContext } from "@/context/UserContext"
import { Navigate, useNavigate } from "react-router"

interface ReservationModalProps {
    isOpen: boolean
    onClose: () => void
    roomId: number
    roomNumber: number
    hotelId: number
    pricePerNight: number
    userId?: number
}



export default function ReservationModal({
    isOpen,
    onClose,
    roomId,
    roomNumber,
    hotelId,
    pricePerNight,
    userId = 0,
}: ReservationModalProps) {
    const [checkInDate, setCheckInDate] = useState("")
    const [checkOutDate, setCheckOutDate] = useState("")

    const { nights, totalPrice } = useMemo(() => {
        if (!checkInDate || !checkOutDate) {
            return { nights: 0, totalPrice: 0 }
        }

        const checkIn = new Date(checkInDate)
        const checkOut = new Date(checkOutDate)
        const diffTime = checkOut.getTime() - checkIn.getTime()
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

        return {
            nights: diffDays > 0 ? diffDays : 0,
            totalPrice: diffDays > 0 ? diffDays * pricePerNight : 0,
        }
    }, [checkInDate, checkOutDate, pricePerNight])


    const { user, authStatus } = useContext(UserContext);

    if (authStatus === "checking") return null;
    if (!user) return <Navigate to="/login" replace />;

    const { mutate: createReservation } = useCreateReservation(user.token);


    const handleConfirm = () => {
        const reservationData: ReservationData = {
            roomId,
            hotelId,
            userId,
            checkInDate,
            checkOutDate,
        }
        createReservation(reservationData);
        setCheckInDate("")
        setCheckOutDate("")
        onClose()
    }

    const isFormValid = checkInDate && checkOutDate && checkInDate < checkOutDate

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle className="text-2xl">Reservar habitación</DialogTitle>
                    <DialogDescription>Habitación número {roomNumber}</DialogDescription>
                </DialogHeader>

                <div className="grid gap-6 py-4">
                    <div className="grid gap-2">
                        <Label htmlFor="checkInDate" className="flex items-center gap-2">
                            <Calendar className="w-4 h-4" />
                            Fecha de entrada
                        </Label>
                        <input
                            id="checkInDate"
                            type="date"
                            value={checkInDate}
                            onChange={(e) => setCheckInDate(e.target.value)}
                            min={new Date().toISOString().split("T")[0]}
                            className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                        />
                    </div>

                    <div className="grid gap-2">
                        <Label htmlFor="checkOutDate" className="flex items-center gap-2">
                            <Calendar className="w-4 h-4" />
                            Fecha de salida
                        </Label>
                        <input
                            id="checkOutDate"
                            type="date"
                            value={checkOutDate}
                            onChange={(e) => setCheckOutDate(e.target.value)}
                            min={checkInDate || new Date().toISOString().split("T")[0]}
                            className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                        />
                    </div>

                    {nights > 0 && (
                        <div className="rounded-lg bg-muted p-4 space-y-2">
                            <div className="flex justify-between text-sm">
                                <span className="text-muted-foreground">Precio por noche:</span>
                                <span className="font-medium">${pricePerNight}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                                <span className="text-muted-foreground">Cantidad de noches:</span>
                                <span className="font-medium">{nights}</span>
                            </div>
                            <div className="border-t border-border pt-2 flex justify-between">
                                <span className="font-semibold">Total:</span>
                                <span className="text-xl font-bold text-primary">${totalPrice}</span>
                            </div>
                            <p className="text-xs text-muted-foreground text-center pt-2 border-t border-border">
                                💳 El pago se abona en efectivo estando en el hotel o puede pagar por mercado pago desde el apartado
                                "Mis reservas"
                            </p>
                        </div>
                    )}
                </div>

                <DialogFooter className="gap-4 sm:gap-1">
                    <Button variant="outline" onClick={onClose}>
                        Cancelar
                    </Button>
                    <Button onClick={handleConfirm} disabled={!isFormValid}>
                        Confirmar reserva
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}
