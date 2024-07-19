import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Box, TextField, Button, CircularProgress, Typography } from '@mui/material';
import './Script/Edit.css';
import { getAllProducts, updateProduct } from './apiService';
import Header from './Header';

const Edit = () => {
  const { isbn } = useParams();
  console.log('useParams output:', useParams());  // Log the output of useParams
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    author: '',
    genre: '',
    publishedYear: '',
    isbn: '',
    imageLink: '',
    price: ''
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (!isbn) {
      setError('Invalid ISBN');
      return;
    }

    const fetchBook = async () => {
      setLoading(true);
      try {
        const products = await getAllProducts();
        console.log('Fetched products:', products);  // Debugging: Check fetched products
        console.log('Searching for ISBN:', isbn);  // Debugging: Log ISBN being searched for
        const book = products.find(p => String(p.isbn) === String(isbn));
        console.log('Found book:', book);  // Debugging: Check the found book
        if (book) {
          setFormData({
            name: book.name,
            author: book.author,
            genre: book.genre,
            publishedYear: book.publishedYear,
            isbn: book.isbn,
            imageLink: book.imageLink,
            price: book.price
          });
        } else {
          setError('Book not found');
        }
      } catch (error) {
        setError('Error fetching book details');
      } finally {
        setLoading(false);
      }
    };

    fetchBook();
  }, [isbn]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const response = await updateProduct(formData.isbn, formData);
      if (response) {
        setSuccess('Book updated successfully!');
        setTimeout(() => {
          navigate('/');
        }, 2000);
      } else {
        setError('Failed to update book');
      }
    } catch (error) {
      setError('An error occurred while updating the book');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
     <Header />
      <Box className="Edit_edit-page">
        <Box className="Edit_edit-container">
          <Typography variant="h4" className="Edit_edit-header">Edit Book</Typography>
          <form className="Edit_edit-form" onSubmit={handleSubmit}>
            <TextField
              label="Title"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              fullWidth
              margin="normal"
            />
            <TextField
              label="Author"
              name="author"
              value={formData.author}
              onChange={handleChange}
              required
              fullWidth
              margin="normal"
            />
            <TextField
              label="Genre"
              name="genre"
              value={formData.genre}
              onChange={handleChange}
              required
              fullWidth
              margin="normal"
            />
            <TextField
              label="Published Year"
              name="publishedYear"
              value={formData.publishedYear}
              onChange={handleChange}
              required
              fullWidth
              margin="normal"
              type="number"
              inputProps={{ min: 0 }}
            />
            <TextField
              label="ISBN"
              name="isbn"
              value={formData.isbn}
              onChange={handleChange}
              required
              fullWidth
              margin="normal"
              InputProps={{
                readOnly: true,
              }}
            />
            <TextField
              label="Image URL"
              name="imageLink"
              value={formData.imageLink}
              onChange={handleChange}
              required
              fullWidth
              margin="normal"
            />
            <TextField
              label="Price"
              name="price"
              value={formData.price}
              onChange={handleChange}
              required
              fullWidth
              margin="normal"
              type="number"
              inputProps={{ min: 0, step: '0.01' }}
            />
            {formData.imageLink && (
              <Box className="Edit_image-preview">
                <img src={formData.imageLink} alt="Book" />
              </Box>
            )}
            <Box className="Edit_edit-buttons">
              <Button type="submit" variant="contained" color="primary" disabled={loading}>Save</Button>
              <Button type="button" variant="outlined" color="secondary" onClick={() => navigate('/home')}>Cancel</Button>
            </Box>
            {loading && <CircularProgress />}
            {error && <Typography color="error">{error}</Typography>}
            {success && <Typography color="primary">{success}</Typography>}
          </form>
        </Box>
      </Box>
    </div>
  );
};

export default Edit;
