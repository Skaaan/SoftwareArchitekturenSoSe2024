import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Button, IconButton, TextField, InputAdornment } from '@mui/material';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import LibraryBooksIcon from '@mui/icons-material/LibraryBooks';
import SearchIcon from '@mui/icons-material/Search';
import { useKeycloak } from '@react-keycloak/web';
import './Header.css';
import { getBasketItems } from './apiService';

const Header = ({ handleSearch }) => {
  const { keycloak } = useKeycloak();
  const [searchQuery, setSearchQuery] = useState('');
  const [isError, setIsError] = useState(false);
  const navigate = useNavigate();

  const onSearch = () => {
    if (!searchQuery.trim()) {
      setIsError(true);
      return;
    }
    setIsError(false);
    handleSearch(searchQuery);
  };

  const handleBasketClick = async () => {
    try {
      const basketItems = await getBasketItems();
      console.log('Basket items:', basketItems);
      navigate('/checkout', { state: { basketItems } });
    } catch (error) {
      console.error('Error fetching basket items:', error);
    }
  };

  const handleLogout = () => {
    keycloak.logout();
  };

  return (
    <Box className="header" sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', p: 2, bgcolor: '#e3dac9', borderBottom: 1, borderColor: '#ccc' }}>
      <Box className="logo-container" onClick={() => navigate('/')} sx={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
        <LibraryBooksIcon 
          sx={{ 
            color: '#333', 
            width: 40, 
            height: 40, 
            marginRight: 1, 
            transition: 'color 0.3s, transform 0.3s', 
            '&:hover': { color: '#555', transform: 'scale(1.1)' } 
          }} 
        />
        <Typography className="logo-text" variant="h5" sx={{ color: '#333', transition: 'color 0.3s', '&:hover': { color: '#555' } }}>Reader’s Insel</Typography>
      </Box>
      <Box className="search-bar" sx={{ width: '300px', mx: 2 }}>
        <TextField 
          fullWidth 
          variant="outlined" 
          placeholder="Search by Title, Author, or ISBN" 
          value={searchQuery}
          onChange={(e) => {
            setSearchQuery(e.target.value);
            if (isError) setIsError(false);
          }}
          error={isError}
          helperText={isError ? "Search input is required" : ""}
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton onClick={onSearch}>
                  <SearchIcon sx={{ color: '#666', transition: 'color 0.3s', '&:hover': { color: '#333' } }} />
                </IconButton>
              </InputAdornment>
            ),
          }}
          sx={{
            bgcolor: '#f8f0dc',  // A lighter shade of the header color
            '& .MuiOutlinedInput-root': {
              '& fieldset': {
                borderColor: '#333',
                transition: 'border-color 0.3s',
              },
              '&:hover fieldset': {
                borderColor: '#555',
              },
              '&.Mui-focused fieldset': {
                borderColor: '#333',
              },
            },
            '& .MuiInputBase-input': {
              color: '#333',
            },
          }} 
        />
      </Box>
      <Box className="nav">
        {keycloak.authenticated && (
          <Button onClick={handleLogout} sx={{ mx: 1, border: 2, borderColor: '#333', color: '#333', transition: 'background-color 0.3s, color 0.3s', '&:hover': { bgcolor: '#333', color: '#fff' } }}>Logout</Button>
        )}
        <Button onClick={() => navigate('/profile')} sx={{ mx: 1, border: 2, borderColor: '#333', color: '#333', transition: 'background-color 0.3s, color 0.3s', '&:hover': { bgcolor: '#333', color: '#fff' } }}>Profile</Button>
        <Button onClick={() => navigate('/contact')} sx={{ mx: 1, border: 2, borderColor: '#333', color: '#333', transition: 'background-color 0.3s, color 0.3s', '&:hover': { bgcolor: '#333', color: '#fff' } }}>Contact</Button>
        <IconButton onClick={handleBasketClick} sx={{ mx: 1 }}>
          <ShoppingCartIcon sx={{ color: '#333', transition: 'color 0.3s', '&:hover': { color: '#555' } }} />
        </IconButton>
      </Box>
    </Box>
  );
};

export default Header;
