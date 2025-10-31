export interface PaymentResponse {
    id: number;
    mpPaymentId: number | null;
    userId: number;
    reservationId: string;
    amount: number;
    status: StatusPayment;
    paymentMethod: PaymentMethod;
    registrationDate: Date;
}

export type PaymentMethod = "efectivo" | "master" | "visa"


export type StatusPayment = "approved" | "manual"

