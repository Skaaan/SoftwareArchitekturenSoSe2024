import React from 'react';
import './Home.css';

import notesFromUnderground from '../assets/NFU.jpg';
import atomicHabits from '../assets/AH.jpg';
import pillowThoughts from '../assets/PT.jpg';
import theKiteRunner from '../assets/TKR.jpg';
import toxic from '../assets/Toxic.jpg';
import metamorphosis from '../assets/Metamorphosis.jpg';

const books = [
  { title: "Notes from Underground", price: "11,22 €", imgSrc: notesFromUnderground, id: 1 },
  { title: "Atomic Habits", price: "12,78 €", imgSrc: atomicHabits, id: 2 },
  { title: "Pillow Thoughts", price: "17,18 €", imgSrc: pillowThoughts, id: 3 },
  { title: "The Kite Runner", price: "24,18 €", imgSrc: theKiteRunner, id: 4 },
  { title: "Toxic", price: "19,28 €", imgSrc: toxic, id: 5 },
  { title: "Metamorphosis", price: "16,22 €", imgSrc: metamorphosis, id: 6 }
];

const Home = ({ navigate, goBack }) => (
  <div className="app">
    <header className="header">
      <div className="logo">Reader’s Insel</div>
      <div className="nav">
        <button onClick={() => navigate('signin')}>Log in</button>
        <button onClick={() => navigate('signup')}>Sign up</button>
        <button onClick={() => navigate('contact')}>Contact</button>
        <button onClick={() => navigate('checkout')}>Checkout</button>
        <div className="language-select">EN</div>
      </div>
    </header>
    <main>
      <div className="search-bar">
        <input type="text" placeholder="Title, Author, ISBN" />
        <button>🔍</button>
      </div>
      <div className="sort-options">
        <span>Sort by: Price ascending</span>
      </div>
      <div className="book-list">
        {books.map(book => (
          <div className="book-card" key={book.id} onClick={() => navigate('description', book)}>
            <img src={book.imgSrc} alt={book.title} />
            <h3>{book.title}</h3>
            <p>{book.price}</p>
            <div className="icons">
              <a href="#">❤️</a>
              <a href="#">🛒</a>
            </div>
          </div>
        ))}
      </div>
      <button className="back-button" onClick={goBack}>Back</button>
    </main>
    <footer>
      <p>Copyright © 2024 Reader’s Insel®. All rights reserved.</p>
    </footer>
  </div>
);

export default Home;
