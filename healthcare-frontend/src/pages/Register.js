import React, { useState } from 'react';
import { Container, Paper, TextField, Button, Typography, Box, Alert, MenuItem } from '@mui/material';
import { useNavigate, Link } from 'react-router-dom';
import { registerUser } from '../services/api';

function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'PATIENT', phone: '', specialization: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await registerUser(form);
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.error || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h5" align="center" gutterBottom fontWeight="bold">
          Create Account
        </Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Box component="form" onSubmit={handleSubmit}>
          <TextField fullWidth label="Full Name" name="name"
            value={form.name} onChange={handleChange} margin="normal" required />
          <TextField fullWidth label="Email" name="email" type="email"
            value={form.email} onChange={handleChange} margin="normal" required />
          <TextField fullWidth label="Password" name="password" type="password"
            value={form.password} onChange={handleChange} margin="normal" required />
          <TextField fullWidth select label="Role" name="role"
            value={form.role} onChange={handleChange} margin="normal">
            <MenuItem value="PATIENT">Patient</MenuItem>
            <MenuItem value="DOCTOR">Doctor</MenuItem>
          </TextField>
          <TextField fullWidth label="Phone" name="phone"
            value={form.phone} onChange={handleChange} margin="normal" />
          {form.role === 'DOCTOR' && (
            <TextField fullWidth label="Specialization" name="specialization"
              value={form.specialization} onChange={handleChange} margin="normal" />
          )}
          <Button fullWidth variant="contained" type="submit"
            sx={{ mt: 3, mb: 2 }} disabled={loading}>
            {loading ? 'Registering...' : 'Register'}
          </Button>
          <Typography align="center">
            Already have an account? <Link to="/login">Login here</Link>
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}

export default Register;