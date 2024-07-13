import React, { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import './Description.css';
import Footer from './Footer';

const Description = () => {
  const location = useLocation();
  const { book } = location.state || {};

  useEffect(() => {
  }, [book]);

  if (!book) return <div>No book selected</div>;

  return (
    <div className="description-page">
      <main>
        <div className="description-book-details">
          <img src={book.imageLink} alt={book.name} className="book-image" />
          <div className="description-book-info">
            <h2>{book.name}</h2>
            <p><strong>Author:</strong> {book.author}</p>
            <p><strong>Genre:</strong> {book.genre}</p>
            <p><strong>Published Year:</strong> {book.publishedYear}</p>
            <p><strong>ISBN:</strong> {book.isbn}</p>
            <p><strong>Description:</strong> {book.description}</p>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default Description;
