import React from 'react';
import './Description.css';

const Description = ({ book, navigate }) => {
  if (!book) return <div>No book selected</div>;

  return (
    <div className="description-page">
      <header className="header">
        <div className="logo">📚 Reader’s Insel</div>
        <button onClick={() => navigate('home')}>Back to Home</button>
      </header>
      <main>
        <div className="book-details">
          <img src={book.imgSrc} alt={book.title} />
          <div className="book-info">
            <h2>{book.title}</h2>
            <p><strong>Author:</strong> Khaled Hosseini</p>
            <p><strong>Genre:</strong> Historical Fiction</p>
            <p><strong>Published Year:</strong> 2009</p>
            <p><strong>ISBN:</strong> 9780747573395</p>
            <p><strong>Description:</strong> Twelve-year-old Amir is desperate to win the local kite-fighting tournament and his loyal friend Hassan promises to help him. But neither of the boys can foresee what will happen to Hassan that afternoon, an event that is to shatter their lives. After the Russians invade and the family is forced to flee to America, Amir realizes that one day he must return to Afghanistan under Taliban rule to find the one thing that his new world cannot grant him: redemption.</p>
          </div>
        </div>
      </main>
      <footer>
        <p>Copyright © 2024 Reader’s Insel®. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default Description;
