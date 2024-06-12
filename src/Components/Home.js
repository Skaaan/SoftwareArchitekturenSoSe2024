import React from 'react';
import './Home.css';

import notesFromUnderground from '../assets/NFU.jpg';
import atomicHabits from '../assets/AH.jpg';
import pillowThoughts from '../assets/PT.jpg';
import theKiteRunner from '../assets/TKR.jpg';
import toxic from '../assets/Toxic.jpg';
import metamorphosis from '../assets/Metamorphosis.jpg';
import basket from '../assets/Basket.png';
import heart from '../assets/heart.png';
import addtobasket from '../assets/addtobasket.png';
import logo from '../assets/logo.png';


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
        <input type="text" placeholder="Title, Author, ISBN" />
        <button>🔍</button>
      </div>
      <div className="sort-options">
        <span>Sort by: Price ascending</span>
      </div>
      <div className="book-list">
        {books.map(book => (
          <div className="book-card">
            <img src={book.imgSrc} key={book.id} onClick={() => navigate('description', book)} alt={book.title}/>
            <h3>{book.title}</h3>
            <p>{book.price}</p>
            <div className="icons">
            <img src={heart} alt="Add to wishlist" className='heart'/>
              <a href="#">🛒</a>
            </div>
          </div>
        ))}
      </div>
    </main>
    <footer>
      <p>Copyright © 2024 Reader’s Insel®. All rights reserved.</p>
    </footer>
  </div>
);

export default Home;
