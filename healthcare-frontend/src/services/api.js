import axios from 'axios';

const USER_SERVICE = 'http://localhost:8080';
const APPOINTMENT_SERVICE = 'http://localhost:8081';

const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

// Auth APIs
export const registerUser = (data) =>
  axios.post(`${USER_SERVICE}/api/auth/register`, data);

export const loginUser = (data) =>
  axios.post(`${USER_SERVICE}/api/auth/login`, data);

export const getDoctors = () =>
  axios.get(`${USER_SERVICE}/api/users/doctors`, { headers: getAuthHeader() });

// Appointment APIs
export const createAppointment = (data) =>
  axios.post(`${APPOINTMENT_SERVICE}/api/appointments`, data, { headers: getAuthHeader() });

export const getPatientAppointments = (patientId) =>
  axios.get(`${APPOINTMENT_SERVICE}/api/appointments/patient/${patientId}`, { headers: getAuthHeader() });

export const getDoctorAppointments = (doctorId) =>
  axios.get(`${APPOINTMENT_SERVICE}/api/appointments/doctor/${doctorId}`, { headers: getAuthHeader() });

export const updateAppointmentStatus = (id, status) =>
  axios.patch(`${APPOINTMENT_SERVICE}/api/appointments/${id}/status?status=${status}`, {}, { headers: getAuthHeader() });