import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL_RESERVATION;

export const reservationApi = axios.create({
    baseURL: `${BASE_URL}/reservations`,
});