import React, { useEffect } from 'react';
import Header from './Header';
import './Description.css';

const Description = ({ book, navigate }) => {
  useEffect(() => {
  }, [book]);

  if (!book) return <div>No book selected</div>;

  return (
    <div className="description-page">
      <Header navigate={navigate} />
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
      <footer className="description-footer">
        <p>&copy; 2024 Reader’s Insel. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default Description;
