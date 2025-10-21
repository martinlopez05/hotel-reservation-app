import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL_HOTEL;

{/** creamos nuestor cliente axios de microservicio hotel */ }
export const hotelApi = axios.create({
  baseURL: `${BASE_URL}/hotels`,
});