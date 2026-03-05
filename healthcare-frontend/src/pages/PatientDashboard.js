import React, { useState, useEffect, useCallback } from 'react';
import { Container, Paper, Typography, Button, TextField, Box,
         Alert, Card, CardContent, Chip, MenuItem, Select,
         FormControl, InputLabel } from '@mui/material';
import { createAppointment, getPatientAppointments, getDoctors } from '../services/api';

function PatientDashboard() {
  const user = JSON.parse(localStorage.getItem('user'));
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [form, setForm] = useState({ doctorId: '', doctorEmail: '', appointmentDateTime: '', reason: '', notes: '' });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadAppointments = useCallback(async () => {
    try {
      const response = await getPatientAppointments(user.id);
      setAppointments(response.data);
    } catch (err) {
      console.error('Failed to load appointments');
    }
  }, [user.id]);

  const loadDoctors = useCallback(async () => {
    try {
      const response = await getDoctors();
      setDoctors(response.data);
    } catch (err) {
      console.error('Failed to load doctors');
    }
  }, []);

  useEffect(() => {
    loadAppointments();
    loadDoctors();
  }, [loadAppointments, loadDoctors]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleDoctorSelect = (e) => {
    const selectedDoctor = doctors.find(d => d.id === e.target.value);
    if (selectedDoctor) {
      setForm({ ...form, doctorId: selectedDoctor.id, doctorEmail: selectedDoctor.email, doctorName: selectedDoctor.name });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      await createAppointment({
        patientId: user.id,
        patientEmail: user.email,
        doctorId: parseInt(form.doctorId),
        doctorEmail: form.doctorEmail,
	doctorName: form.doctorName,
        appointmentDateTime: form.appointmentDateTime,
        reason: form.reason,
        notes: form.notes
      });
      setMessage('Appointment booked successfully!');
      setForm({ doctorId: '', doctorEmail: '', appointmentDateTime: '', reason: '', notes: '' });
      loadAppointments();
    } catch (err) {
      setError('Failed to book appointment');
    }
  };

  const getStatusColor = (status) => {
    const colors = { PENDING: 'warning', CONFIRMED: 'success', CANCELLED: 'error', COMPLETED: 'info' };
    return colors[status] || 'default';
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom fontWeight="bold">
        Patient Dashboard
      </Typography>
      <Typography variant="subtitle1" gutterBottom color="text.secondary">
        Welcome, {user?.name}!
      </Typography>

      <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
        <Typography variant="h6" gutterBottom fontWeight="bold">
          Book New Appointment
        </Typography>
        {message && <Alert severity="success" sx={{ mb: 2 }}>{message}</Alert>}
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>

          <FormControl fullWidth required>
            <InputLabel id="doctor-label">Select Doctor</InputLabel>
            <Select
              labelId="doctor-label"
              value={form.doctorId}
              label="Select Doctor"
              onChange={handleDoctorSelect}
              renderValue={(selected) => {
                const doctor = doctors.find(d => d.id === selected);
                return doctor ? `Dr. ${doctor.name}${doctor.specialization ? ` — ${doctor.specialization}` : ''}` : '';
              }}
            >
              {doctors.length === 0 ? (
                <MenuItem disabled>No doctors available</MenuItem>
              ) : (
                doctors.map(doctor => (
                  <MenuItem key={doctor.id} value={doctor.id}>
                    <Box>
                      <Typography variant="body1" fontWeight="medium">
                        Dr. {doctor.name}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {doctor.specialization || 'General'} • {doctor.email}
                      </Typography>
                    </Box>
                  </MenuItem>
                ))
              )}
            </Select>
          </FormControl>
	  
	  {
	  <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
  	  	Debug: {JSON.stringify({doctorId: form.doctorId, doctorEmail: form.doctorEmail, doctorName: form.doctorName})}
	  </Typography>
	  }

          <TextField
            fullWidth
            label="Appointment Date & Time"
            name="appointmentDateTime"
            type="datetime-local"
            value={form.appointmentDateTime}
            onChange={handleChange}
            InputLabelProps={{ shrink: true }}
            required
          />

          <TextField
            fullWidth
            label="Reason for Visit"
            name="reason"
            value={form.reason}
            onChange={handleChange}
            required
            placeholder="e.g. Regular checkup, Fever, Follow-up"
          />

          <TextField
            fullWidth
            label="Additional Notes (optional)"
            name="notes"
            multiline
            rows={2}
            value={form.notes}
            onChange={handleChange}
            placeholder="Any additional information for the doctor"
          />

          <Button variant="contained" type="submit" size="large" sx={{ mt: 1 }}>
            Book Appointment
          </Button>

        </Box>
      </Paper>

      <Typography variant="h6" gutterBottom fontWeight="bold">
        My Appointments
      </Typography>
      {appointments.length === 0 ? (
        <Typography color="text.secondary">No appointments yet.</Typography>
      ) : (
        appointments.map(apt => (
          <Card key={apt.id} sx={{ mb: 2 }}>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                <Typography variant="h6">Appointment #{apt.id}</Typography>
                <Chip label={apt.status} color={getStatusColor(apt.status)} />
              </Box>
              <Typography><strong>Doctor:</strong> Dr. {apt.doctorName}</Typography>
              <Typography><strong>Date:</strong> {new Date(apt.appointmentDateTime).toLocaleString()}</Typography>
              <Typography><strong>Reason:</strong> {apt.reason}</Typography>
              {apt.notes && <Typography color="text.secondary"><strong>Notes:</strong> {apt.notes}</Typography>}
            </CardContent>
          </Card>
        ))
      )}
    </Container>
  );
}

export default PatientDashboard;
