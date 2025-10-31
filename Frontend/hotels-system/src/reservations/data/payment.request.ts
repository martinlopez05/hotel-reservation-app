export interface PaymentRequest {
    userId: number;
    reservationId: string;
    amount: number;
    paymentMethod: string;
}