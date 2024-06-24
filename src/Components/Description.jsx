import React from 'react';
import './Description.css';

const Description = ({ book, navigate }) => {
  if (!book) return <div>No book selected</div>;

  return (
    <div className="description-page">
      <header className="description-header">
        <div className="description-logo">📚 Reader’s Insel</div>
        <button onClick={() => navigate('home')}>Back to Home</button>
      </header>
      <main>
        <div className="description-book-details">
          <img src={book.image_url} alt={book.title} />
          <div className="description-book-info">
            <h2>{book.title}</h2>
            <p><strong>Author:</strong> {book.author}</p>
            <p><strong>Genre:</strong> {book.genre}</p>
            <p><strong>Published Year:</strong> {book.published_year}</p>
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
