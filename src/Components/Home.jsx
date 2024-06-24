import React, { useEffect, useState } from 'react';
import './Home.css';
import basket from '../assets/Basket.png';
import heart from '../assets/heart.png';
import logo from '../assets/logo.png';

const Home = ({ navigate }) => {
  const [books, setBooks] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
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

  const handleSearch = (event) => {
    const query = event.target.value.toLowerCase();
    setSearchQuery(query);

    const filtered = books.filter(book => 
      book.title.toLowerCase().includes(query) || 
      book.isbn.toLowerCase().includes(query) ||
      book.author.toLowerCase().includes(query)
    );
    setFilteredBooks(filtered);
  };

  return (
    <div className="app">
      <header className="header">
        <div className="logo-container" onClick={() => navigate('home')}>
          <img src={logo} alt="Logo" className="logo-img" />
          <div className="logo-text">Reader’s Insel</div>
        </div>
        <div className="nav">
          <button onClick={() => navigate('signin')}>Log in</button>
          <button onClick={() => navigate('signup')}>Sign up</button>
          <button onClick={() => navigate('contact')}>Contact</button>
          <button onClick={() => navigate('checkout')}>
            <img src={basket} alt="Checkout" className='basket' />
          </button>
        </div>
      </header>
      <main>
        <div className="search-bar">
          <input 
            type="text" 
            placeholder="Search by Title, Author or ISBN" 
            value={searchQuery}
            onChange={handleSearch}
          />
          <button>🔍</button>
        </div>
        <div className="sort-options">
          <span>Sort by: Price ascending</span>
        </div>
        <div className="book-list">
          {filteredBooks.map(book => (
            <div 
              className="book-card" 
              key={book.id} 
              onClick={() => navigate('description', book)}
            >
              <div className="book-image">
                <img src={book.image_url} alt={book.title} />
              </div>
              <div className="book-details">
                <h3>Title: {book.title}</h3>
                <p>Author: {book.author}</p>
                <p>Genre: {book.genre}</p>
                <p>Published Year: {book.published_year}</p>
                <p>ISBN: {book.isbn}</p>
              </div>
              <div className="read-description">
                <span>Show Details</span>
              </div>
            </div>
          ))}
        </div>
      </main>
      <footer>
        <p>&copy; 2024 Reader’s Insel. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default Home;
