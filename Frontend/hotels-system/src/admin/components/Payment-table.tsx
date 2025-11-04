import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { CreditCard, DollarSign, Calendar } from "lucide-react"
import { use, useEffect, useState } from "react"
import type { PaymentMethod, StatusPayment, PaymentResponse } from "@/reservations/data/payment.response"
import { UserContext } from "@/context/UserContext"


export function PaymentsTable() {

    const getStatusBadge = (status: StatusPayment) => {
        switch (status) {
            case "approved":
                return <Badge className="bg-green-500 hover:bg-green-600">Aprobado</Badge>
            case "manual":
                return <Badge className="bg-yellow-500 hover:bg-yellow-600">Manual</Badge>
        }
    }

    const [loading, setLoading] = useState(true);
    const [payments, setPayments] = useState<PaymentResponse[]>([]);

    const BASE_URL = import.meta.env.VITE_API_URL_PAYMENT;


    const { user } = use(UserContext);


    useEffect(() => {
        fecthPayments();
    }, [])




    const fecthPayments = async () => {
        try {
            setLoading(true);
            const response = await fetch(`${BASE_URL}`, {
                headers: {
                    'Authorization': `Bearer ${user ? user.token : 0}`,
                    "ngrok-skip-browser-warning": "true"
                }
            })
            if (response.ok) {
                const data = await response.json()
                setPayments(data)
            }
        } catch (error) {
            console.log(error);
        }
        finally {
            setLoading(false);
        }
    }

    if (loading) {
        return <div className="text-center py-8">Cargando pagos...</div>
    }

    const getPaymentMethodBadge = (method: PaymentMethod) => {
        switch (method) {
            case "efectivo":
                return (
                    <Badge variant="outline" className="gap-1">
                        <DollarSign className="w-3 h-3" />
                        Efectivo
                    </Badge>
                )
            case "master":
                return (
                    <Badge variant="outline" className="gap-1 bg-orange-50">
                        <CreditCard className="w-3 h-3" />
                        Mastercard
                    </Badge>
                )
            case "visa":
                return (
                    <Badge variant="outline" className="gap-1 bg-blue-50">
                        <CreditCard className="w-3 h-3" />
                        Visa
                    </Badge>
                )
        }
    }

    const formatDate = (date: Date) => {
        return new Date(date).toLocaleDateString("es-ES", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
        })
    }

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat("es-ES", {
            style: "currency",
            currency: "ARS",
        }).format(amount)
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-2xl font-bold">Pagos</CardTitle>
            </CardHeader>
            <CardContent>
                <div className="rounded-md border">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>ID</TableHead>
                                <TableHead>MP Payment ID</TableHead>
                                <TableHead>User ID</TableHead>
                                <TableHead>Reservation ID</TableHead>
                                <TableHead>Orden reserva</TableHead>
                                <TableHead>Monto</TableHead>
                                <TableHead>Estado</TableHead>
                                <TableHead>Método de Pago</TableHead>
                                <TableHead>Fecha de Registro</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {payments.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={8} className="text-center text-muted-foreground">
                                        No hay pagos registrados
                                    </TableCell>
                                </TableRow>
                            ) : (
                                payments.map((payment) => (
                                    <TableRow key={payment.id}>
                                        <TableCell className="font-medium">{payment.id}</TableCell>
                                        <TableCell>{payment.mpPaymentId ? payment.mpPaymentId : "-"}</TableCell>
                                        <TableCell>{payment.userId}</TableCell>
                                        <TableCell className="font-medium text-sm">{payment.reservationId}</TableCell>
                                        <TableCell className="font-medium text-sm">#{payment.orderReservation}</TableCell>
                                        <TableCell className="font-semibold">{formatCurrency(payment.amount)}</TableCell>
                                        <TableCell>{getStatusBadge(payment.status)}</TableCell>
                                        <TableCell>{getPaymentMethodBadge(payment.paymentMethod)}</TableCell>
                                        <TableCell>
                                            <div className="flex items-center gap-2">
                                                <Calendar className="w-4 h-4 text-muted-foreground" />
                                                {formatDate(payment.registrationDate)}
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </div>
            </CardContent>
        </Card>
    )
}