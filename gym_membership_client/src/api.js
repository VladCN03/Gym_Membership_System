// src/api.js
import axios from 'axios'

// ATENȚIE: pentru Android emulator, localhost = 10.0.2.2
const BASE = 'http://10.0.2.2:8080/api'

const api = axios.create({
  baseURL: BASE,
  timeout: 30000,
})

// dacă ai nevoie mai târziu de token JWT, îl adăugăm aici la headers

export default api
