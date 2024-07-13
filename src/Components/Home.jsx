import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardMedia, Typography, Box, Button } from '@mui/material';
import './Home.css';
import Footer from './Footer';
import { getAllProducts, deleteProduct, addToBasket } from './apiService';

const Home = () => {
  const [books, setBooks] = useState([]);
  const [filteredBooks, setFilteredBooks] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBooks = async () => {
      try {
        const data = await getAllProducts();
        setBooks(data);
        setFilteredBooks(data);
      } catch (error) {
        console.error('Error fetching books:', error);
      }
    };

    fetchBooks();
  }, []);

  const handleAddToCart = async (book) => {
    try {
      const orderLineItemsDto = {
        isbn: book.isbn,
        price: book.price,
        quantity: 1, // Assuming quantity is 1 for simplicity
      };
      await addToBasket(orderLineItemsDto);
      console.log(`Added ${book.name} to cart`);
    } catch (error) {
      console.error('Error adding item to cart:', error);
    }
  };

  const handleEdit = (book) => {
    navigate(`/edit/${book.id}`);
  };

  const handleDelete = async (bookId) => {
    try {
      await deleteProduct(bookId);
      setBooks(books.filter(book => book.id !== bookId));
      setFilteredBooks(filteredBooks.filter(book => book.id !== bookId));
    } catch (error) {
      console.error('Error deleting book:', error);
    }
  };

  const handleDescription = (book) => {
    navigate(`/description/${book.id}`, { state: { book } });
  };

  return (
    <Box className="app" sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', bgcolor: '#f5f7fa' }}>
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Box className="book-list" sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 2, p: 2 }}>
          {filteredBooks.map(book => (
            <Card key={book.id} className="book-card" sx={{ position: 'relative', cursor: 'pointer', transition: 'transform 0.2s, box-shadow 0.3s', '&:hover': { transform: 'translateY(-5px)', boxShadow: '0 10px 15px rgba(0, 0, 0, 0.15)' } }}>
              <CardMedia
                component="img"
                height="180"
                image={book.imageLink} // Assuming imageLink is the field for the image URL
                alt={book.name} // Assuming name is the field for the book title
                sx={{ objectFit: 'cover' }}
              />
              <CardContent sx={{ textAlign: 'center' }}>
                <Typography variant="h6">{book.name}</Typography>
                <Typography variant="body2" color="textSecondary">Author: {book.author}</Typography>
                <Typography variant="body2" color="textSecondary">Genre: {book.genre}</Typography>
                <Typography variant="body2" color="textSecondary">Published Year: {book.publishedYear}</Typography>
                <Typography variant="body2" color="textSecondary">ISBN: {book.isbn}</Typography>
              </CardContent>
              <Box className="overlay" sx={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', bgcolor: 'rgba(51, 51, 51, 0.8)', color: '#fff', fontSize: '18px', opacity: 0, transition: 'opacity 0.3s', boxShadow: 'inset 0 0 10px rgba(0, 0, 0, 0.5)', '&:hover': { opacity: 1 } }}>
                <Button variant="contained" color="primary" onClick={() => handleDescription(book)} sx={{ mb: 1 }}>Read Description</Button>
                <Button variant="contained" color="secondary" onClick={() => handleAddToCart(book)}>Add to Cart</Button>
              </Box>
              <Box className="admin-actions" sx={{ display: 'flex', justifyContent: 'space-between', p: 1 }}>
                <Button variant="outlined" color="primary" onClick={() => handleEdit(book)}>Edit</Button>
                <Button variant="outlined" color="secondary" onClick={() => handleDelete(book.id)}>Delete</Button>
              </Box>
            </Card>
          ))}
        </Box>
      </Box>
      <Footer />
    </Box>
  );
};

export default Home;
