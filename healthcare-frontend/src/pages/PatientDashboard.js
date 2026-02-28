import React, { useState, useEffect, useCallback } from 'react';
import { Container, Paper, Typography, Button, TextField, Box,
         Alert, Card, CardContent, Chip, Grid } from '@mui/material';
import { createAppointment, getPatientAppointments } from '../services/api';

function PatientDashboard() {
  const user = JSON.parse(localStorage.getItem('user'));
  const [appointments, setAppointments] = useState([]);
  const [form, setForm] = useState({ doctorId: '', doctorEmail: '', appointmentDateTime: '', reason: '', notes: '' });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadAppointments = useCallback(async () => {
    try {
      const response = await getPatientAppointments(user.id || 1);
      setAppointments(response.data);
    } catch (err) {
      console.error('Failed to load appointments');
    }
  }, [user.id]);

  useEffect(() => {
    loadAppointments();
  }, [loadAppointments]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      await createAppointment({
        patientId: user.id || 1,
        patientEmail: user.email,
        doctorId: parseInt(form.doctorId),
        doctorEmail: form.doctorEmail,
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

      {/* Book Appointment Form */}
      <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
        <Typography variant="h6" gutterBottom fontWeight="bold">
          Book New Appointment
        </Typography>
        {message && <Alert severity="success" sx={{ mb: 2 }}>{message}</Alert>}
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Box component="form" onSubmit={handleSubmit}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField fullWidth label="Doctor ID" name="doctorId"
                value={form.doctorId} onChange={handleChange} required />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Doctor Email" name="doctorEmail"
                value={form.doctorEmail} onChange={handleChange} required />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Appointment Date & Time" name="appointmentDateTime"
                type="datetime-local" value={form.appointmentDateTime}
                onChange={handleChange} InputLabelProps={{ shrink: true }} required />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Reason" name="reason"
                value={form.reason} onChange={handleChange} required />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Notes" name="notes" multiline rows={2}
                value={form.notes} onChange={handleChange} />
            </Grid>
          </Grid>
          <Button variant="contained" type="submit" sx={{ mt: 2 }}>
            Book Appointment
          </Button>
        </Box>
      </Paper>

      {/* Appointments List */}
      <Typography variant="h6" gutterBottom fontWeight="bold">
        My Appointments
      </Typography>
      {appointments.length === 0 ? (
        <Typography color="text.secondary">No appointments yet.</Typography>
      ) : (
        appointments.map(apt => (
          <Card key={apt.id} sx={{ mb: 2 }}>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h6">Appointment #{apt.id}</Typography>
                <Chip label={apt.status} color={getStatusColor(apt.status)} />
              </Box>
              <Typography>Doctor: {apt.doctorEmail}</Typography>
              <Typography>Date: {new Date(apt.appointmentDateTime).toLocaleString()}</Typography>
              <Typography>Reason: {apt.reason}</Typography>
            </CardContent>
          </Card>
        ))
      )}
    </Container>
  );
}

export default PatientDashboard;