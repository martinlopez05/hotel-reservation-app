import { use, useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { CreditCard, DollarSign, Banknote } from "lucide-react"
import { Card } from "@/components/ui/card"
import { toast } from "react-toastify"
import { UserContext } from "@/context/UserContext"

interface PaymentModalProps {
    isOpen: boolean
    onClose: () => void
    reservationId: string
    orderReservation: number
    amount: number
    userId: number
    onPaymentSuccess?: () => void
}

type PaymentMethod = "efectivo" | "master" | "visa"

export function PaymentModal({ isOpen, onClose, reservationId, orderReservation, amount, userId, onPaymentSuccess }: PaymentModalProps) {

    const [selectedMethod, setSelectedMethod] = useState<PaymentMethod | null>(null)
    const { user } = use(UserContext);

    const handleConfirmPayment = async () => {
        if (!selectedMethod) {
            toast.error("Por favor selecciona un método de pago")
            return
        }

        const paymentRequest = {
            userId,
            reservationId,
            amount,
            paymentMethod: selectedMethod,
        }

        const BASE_URL = import.meta.env.VITE_API_URL_PAYMENT;

        try {
            await fetch(`${BASE_URL}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${user?.token}`,
                    'Content-Type': 'application/json',
                    "ngrok-skip-browser-warning": "true"

                },
                body: JSON.stringify(paymentRequest)
            });

            toast.success('Pago registrado correctamente')
            if (onPaymentSuccess) {
                onPaymentSuccess();
            }
        } catch (error) {
            toast.error('Error al registrar el pago')
            console.log(error);
        }
        onClose()
        setSelectedMethod(null)
    }

    const paymentMethods = [
        {
            id: "efectivo" as PaymentMethod,
            name: "Efectivo",
            icon: Banknote,
            description: "Pago en efectivo",
        },
        {
            id: "master" as PaymentMethod,
            name: "Mastercard",
            icon: CreditCard,
            description: "Tarjeta Mastercard",
        },
        {
            id: "visa" as PaymentMethod,
            name: "Visa",
            icon: CreditCard,
            description: "Tarjeta Visa",
        },
    ]

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle className="text-2xl font-bold">Registrar Pago</DialogTitle>
                </DialogHeader>

                <div className="space-y-6">
                    {/* Información de la reserva */}
                    <div className="bg-muted p-4 rounded-lg space-y-2">
                        <div className="flex justify-between">
                            <span className="text-muted-foreground">Orden de Reserva:</span>
                            <span className="font-semibold">#{orderReservation}</span>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-muted-foreground">ID de Reserva:</span>
                            <span className="font-mono text-sm">{reservationId}</span>
                        </div>
                        <div className="flex justify-between text-lg">
                            <span className="text-muted-foreground">Monto Total:</span>
                            <span className="font-bold text-primary">${amount.toFixed(2)}</span>
                        </div>
                    </div>

                    {/* Selección de método de pago */}
                    <div className="space-y-3">
                        <h3 className="font-semibold text-lg">Método de Pago</h3>
                        <div className="grid gap-3">
                            {paymentMethods.map((method) => {
                                const Icon = method.icon
                                const isSelected = selectedMethod === method.id

                                return (
                                    <Card
                                        key={method.id}
                                        className={`p-4 cursor-pointer transition-all hover:shadow-md ${isSelected ? "border-primary border-2 bg-primary/5" : "border-border hover:border-primary/50"
                                            }`}
                                        onClick={() => setSelectedMethod(method.id)}
                                    >
                                        <div className="flex items-center gap-3">
                                            <div
                                                className={`p-2 rounded-full ${isSelected ? "bg-primary text-primary-foreground" : "bg-muted"}`}
                                            >
                                                <Icon className="w-5 h-5" />
                                            </div>
                                            <div className="flex-1">
                                                <div className="font-semibold">{method.name}</div>
                                                <div className="text-sm text-muted-foreground">{method.description}</div>
                                            </div>
                                            <div
                                                className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${isSelected ? "border-primary bg-primary" : "border-muted-foreground"
                                                    }`}
                                            >
                                                {isSelected && <div className="w-2 h-2 bg-primary-foreground rounded-full" />}
                                            </div>
                                        </div>
                                    </Card>
                                )
                            })}
                        </div>
                    </div>

                    {/* Botones de acción */}
                    <div className="flex gap-3 pt-4">
                        <Button variant="outline" onClick={onClose} className="flex-1 bg-transparent">
                            Cancelar
                        </Button>
                        <Button onClick={handleConfirmPayment} disabled={!selectedMethod} className="flex-1">
                            <DollarSign className="w-4 h-4 mr-2" />
                            Confirmar Pago
                        </Button>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}
