import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardMedia, Typography, Box, Button } from '@mui/material';
import { useKeycloak } from '@react-keycloak/web';
import './Home.css';
import Footer from './Footer';
import Header from './Header';

const Home = ({ navigate }) => {
  const { keycloak, initialized } = useKeycloak();
  const [books, setBooks] = useState([]);
  const [filteredBooks, setFilteredBooks] = useState([]);

  useEffect(() => {
    fetch('http://localhost:4002/books')
      .then(response => response.json())
      .then(data => {
        setBooks(data);
        setFilteredBooks(data);
      })
      .catch(error => console.error('Error fetching books:', error));
  }, []);

  const handleAddToCart = (book) => {
    // Add to cart logic here
    console.log(`Added ${book.title} to cart`);
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
      <Header navigate={navigate} />
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Box className="book-list" sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 2, p: 2 }}>
          {filteredBooks.map(book => (
            <Card key={book.id} className="book-card" sx={{ position: 'relative', cursor: 'pointer', transition: 'transform 0.2s, box-shadow 0.3s', '&:hover': { transform: 'translateY(-5px)', boxShadow: '0 10px 15px rgba(0, 0, 0, 0.15)' } }}>
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
              <Box className="overlay" sx={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', bgcolor: 'rgba(51, 51, 51, 0.8)', color: '#fff', fontSize: '18px', opacity: 0, transition: 'opacity 0.3s', boxShadow: 'inset 0 0 10px rgba(0, 0, 0, 0.5)', '&:hover': { opacity: 1 } }}>
                <Button variant="contained" color="primary" onClick={() => navigate('description', book)} sx={{ mb: 1 }}>Read Description</Button>
                <Button variant="contained" color="secondary" onClick={() => handleAddToCart(book)}>Add to Cart</Button>
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
      <Footer />
    </Box>
  );
};

export default Home;
