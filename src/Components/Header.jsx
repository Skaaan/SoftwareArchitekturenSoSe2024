import React from 'react';
import { Box, Typography, Button, IconButton } from '@mui/material';
import { useKeycloak } from '@react-keycloak/web';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import logo from '../assets/logo.png';
import './Header.css';

const Header = ({ navigate }) => {
  const { keycloak } = useKeycloak();

  return (
    <Box className="header" sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', p: 2, bgcolor: '#e3dac9', borderBottom: 1, borderColor: '#ccc' }}>
      <Box className="logo-container" onClick={() => navigate('home')} sx={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
        <img src={logo} alt="Logo" className="logo-img" style={{ width: 40, height: 40, marginRight: 10 }} />
        <Typography className="logo-text" variant="h5" sx={{ color: '#333', transition: 'color 0.3s', '&:hover': { color: '#555' } }}>Reader’s Insel</Typography>
      </Box>
      <Box className="nav">
        {!keycloak.authenticated && (
          <Button onClick={() => keycloak.login()} sx={{ mx: 1, border: 2, borderColor: '#333', color: '#333', '&:hover': { bgcolor: '#333', color: '#fff' } }}>Log in</Button>
        )}
        {!!keycloak.authenticated && (
          <>
            <Button onClick={() => keycloak.logout()} sx={{ mx: 1, border: 2, borderColor: '#333', color: '#333', '&:hover': { bgcolor: '#333', color: '#fff' } }}>Log out</Button>
            <Button onClick={() => navigate('profile')} sx={{ mx: 1, border: 2, borderColor: '#333', color: '#333', '&:hover': { bgcolor: '#333', color: '#fff' } }}>Profile</Button>
          </>
        )}
        <Button onClick={() => navigate('contact')} sx={{ mx: 1, border: 2, borderColor: '#333', color: '#333', '&:hover': { bgcolor: '#333', color: '#fff' } }}>Contact</Button>
        <IconButton onClick={() => navigate('checkout')} sx={{ mx: 1 }}>
          <ShoppingCartIcon style={{ color: '#333' }} />
        </IconButton>
      </Box>
    </Box>
  );
};

export default Header;
