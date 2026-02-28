import React, { useState } from 'react';
import { Container, Paper, TextField, Button, Typography, Box, Alert } from '@mui/material';
import { useNavigate, Link } from 'react-router-dom';
import { loginUser } from '../services/api';

function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const response = await loginUser(form);
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
      if (response.data.role === 'PATIENT') {
        navigate('/patient-dashboard');
      } else if (response.data.role === 'DOCTOR') {
        navigate('/doctor-dashboard');
      }
    } catch (err) {
      setError('Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h5" align="center" gutterBottom fontWeight="bold">
          Login to Healthcare System
        </Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <Box component="form" onSubmit={handleSubmit}>
          <TextField fullWidth label="Email" name="email" type="email"
            value={form.email} onChange={handleChange} margin="normal" required />
          <TextField fullWidth label="Password" name="password" type="password"
            value={form.password} onChange={handleChange} margin="normal" required />
          <Button fullWidth variant="contained" type="submit"
            sx={{ mt: 3, mb: 2 }} disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </Button>
          <Typography align="center">
            Don't have an account? <Link to="/register">Register here</Link>
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}

export default Login;