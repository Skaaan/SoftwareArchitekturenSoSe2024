import React, { useEffect, useState } from 'react';
import { TextField, IconButton, Card, CardContent, CardMedia, Typography, Box, InputAdornment, Button } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { useKeycloak } from '@react-keycloak/web';
import './Home.css';
import basket from '../assets/Basket.png';
import logo from '../assets/logo.png';

const Home = ({ navigate }) => {
  const { keycloak, initialized } = useKeycloak();
  const [books, setBooks] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [filteredBooks, setFilteredBooks] = useState([]);
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    fetch('http://localhost:4002/books')
      .then(response => response.json())
      .then(data => {
        setBooks(data);
        setFilteredBooks(data);
      })
      .catch(error => console.error('Error fetching books:', error));
  }, []);

  const handleSearch = () => {
    if (!searchQuery.trim()) {
      setIsError(true);
      return;
    }
    setIsError(false);
    const query = searchQuery.toLowerCase();
    const filtered = books.filter(book => 
      book.title.toLowerCase().includes(query) || 
      book.isbn.toLowerCase().includes(query) ||
      book.author.toLowerCase().includes(query)
    );
    setFilteredBooks(filtered);
  };

  const handleEdit = (book) => {
    navigate('edit', book);
  };

  const handleDelete = async (bookId) => {
    try {
      const response = await fetch(`http://localhost:4002/books/${bookId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${keycloak.token}`
        }
      });
      if (response.ok) {
        setBooks(books.filter(book => book.id !== bookId));
        setFilteredBooks(filteredBooks.filter(book => book.id !== bookId));
      } else {
        console.error('Failed to delete book');
      }
    } catch (error) {
      console.error('Error deleting book:', error);
    }
  };

  if (!initialized) {
    return <div>Loading...</div>;
  }

  return (
    <Box className="app" sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', bgcolor: '#f5f7fa' }}>
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
            <img src={basket} alt="Checkout" className='basket' style={{ width: 30, height: 30 }} />
          </IconButton>
        </Box>
      </Box>
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Box className="search-bar" sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', my: 2 }}>
          <TextField 
            variant="outlined" 
            placeholder="Search by Title, Author, or ISBN" 
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
              if (isError) setIsError(false);
            }}
            error={isError}
            helperText={isError ? "Search input is required" : ""}
            sx={{ width: 300 }}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton 
                    onClick={handleSearch} 
                    sx={{ padding: '0 12px', height: '100%', borderRadius: '0 5px 5px 0', transition: 'transform 0.2s, background-color 0.3s', '&:hover': { transform: 'scale(1.1)', backgroundColor: '#f0f0f0' } }}>
                    <SearchIcon style={{ color: isError ? 'red' : '#3f51b5' }} />
                  </IconButton>
                </InputAdornment>
              ),
              style: {
                borderTopRightRadius: 5,
                borderBottomRightRadius: 5,
              },
            }}
          />
        </Box>
        <Box className="book-list" sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 2, p: 2 }}>
          {filteredBooks.map(book => (
            <Card key={book.id} className="book-card" onClick={() => navigate('description', book)} sx={{ position: 'relative', cursor: 'pointer', transition: 'transform 0.2s, box-shadow 0.3s', '&:hover': { transform: 'translateY(-5px)', boxShadow: '0 10px 15px rgba(0, 0, 0, 0.15)' } }}>
              <CardMedia
                component="img"
                height="180"
                image={book.image_url}
                alt={book.title}
                sx={{ objectFit: 'cover' }}
              />
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="h6">{book.title}</Typography>
                <Typography variant="body2" color="textSecondary">Author: {book.author}</Typography>
                <Typography variant="body2" color="textSecondary">Genre: {book.genre}</Typography>
                <Typography variant="body2" color="textSecondary">Published Year: {book.published_year}</Typography>
                <Typography variant="body2" color="textSecondary">ISBN: {book.isbn}</Typography>
              </CardContent>
              <Box className="read-description" sx={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, display: 'flex', justifyContent: 'center', alignItems: 'center', bgcolor: 'rgba(51, 51, 51, 0.8)', color: '#fff', fontSize: '18px', opacity: 0, transition: 'opacity 0.3s', boxShadow: 'inset 0 0 10px rgba(0, 0, 0, 0.5)', '&:hover': { opacity: 1 } }}>
                Read Description
              </Box>
              {keycloak.hasRealmRole('admin') && (
                <Box className="admin-actions" sx={{ display: 'flex', justifyContent: 'space-between', p: 1 }}>
                  <Button variant="outlined" color="primary" onClick={() => handleEdit(book)}>Edit</Button>
                  <Button variant="outlined" color="secondary" onClick={() => handleDelete(book.id)}>Delete</Button>
                </Box>
              )}
            </Card>
          ))}
        </Box>
      </Box>
      <Box component="footer" className="footer" sx={{ textAlign: 'center', p: 2, bgcolor: '#e3dac9', borderTop: 1, borderColor: '#ccc', mt: 'auto', fontSize: '14px', color: '#666' }}>
        &copy; 2024 Reader’s Insel. All rights reserved.
      </Box>
    </Box>
  );
};

export default Home;
