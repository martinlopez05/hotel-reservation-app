import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL_ROOMS;

export const roomApi = axios.create({
    baseURL: `${BASE_URL}/rooms`,
});