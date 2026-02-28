import React, { useState, useEffect, useCallback } from 'react';
import { Container, Typography, Card, CardContent,
         Box, Chip, Button, Alert } from '@mui/material';
import { getDoctorAppointments, updateAppointmentStatus } from '../services/api';

function DoctorDashboard() {
  const user = JSON.parse(localStorage.getItem('user'));
  const [appointments, setAppointments] = useState([]);
  const [message, setMessage] = useState('');

  const loadAppointments = useCallback(async () => {
    try {
      const response = await getDoctorAppointments(user.id || 2);
      setAppointments(response.data);
    } catch (err) {
      console.error('Failed to load appointments');
    }
  }, [user.id]);

  useEffect(() => {
    loadAppointments();
  }, [loadAppointments]);

  const handleStatusUpdate = async (id, status) => {
    try {
      await updateAppointmentStatus(id, status);
      setMessage(`Appointment ${status.toLowerCase()} successfully!`);
      loadAppointments();
    } catch (err) {
      console.error('Failed to update status');
    }
  };

  const getStatusColor = (status) => {
    const colors = { PENDING: 'warning', CONFIRMED: 'success', CANCELLED: 'error', COMPLETED: 'info' };
    return colors[status] || 'default';
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom fontWeight="bold">
        Doctor Dashboard
      </Typography>
      <Typography variant="subtitle1" gutterBottom color="text.secondary">
        Welcome, Dr. {user?.name}!
      </Typography>

      {message && <Alert severity="success" sx={{ mb: 2 }}>{message}</Alert>}

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
              <Typography>Patient: {apt.patientEmail}</Typography>
              <Typography>Date: {new Date(apt.appointmentDateTime).toLocaleString()}</Typography>
              <Typography>Reason: {apt.reason}</Typography>
              {apt.status === 'PENDING' && (
                <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
                  <Button variant="contained" color="success" size="small"
                    onClick={() => handleStatusUpdate(apt.id, 'CONFIRMED')}>
                    Confirm
                  </Button>
                  <Button variant="contained" color="error" size="small"
                    onClick={() => handleStatusUpdate(apt.id, 'CANCELLED')}>
                    Cancel
                  </Button>
                </Box>
              )}
              {apt.status === 'CONFIRMED' && (
                <Box sx={{ mt: 2 }}>
                  <Button variant="contained" color="info" size="small"
                    onClick={() => handleStatusUpdate(apt.id, 'COMPLETED')}>
                    Mark Complete
                  </Button>
                </Box>
              )}
            </CardContent>
          </Card>
        ))
      )}
    </Container>
  );
}

export default DoctorDashboard;