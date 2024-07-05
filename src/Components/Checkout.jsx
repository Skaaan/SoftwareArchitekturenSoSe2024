import React from 'react';
import Header from './Header';
import './Checkout.css';

import theKiteRunner from '../assets/TKR.jpg';
import toxic from '../assets/Toxic.jpg';
import Footer from './Footer';

const Checkout = ({ navigate }) => {
  const books = [
    {
      title: "The Kite Runner",
      author: "Khaled Hosseini",
      price: "21,18€",
      imgSrc: theKiteRunner,
    },
    {
      title: "Toxic",
      author: "Nicole Blanchard",
      price: "19,28€",
      imgSrc: toxic,
    },
  ];

  const handleWishlist = (book) => {
    // Add to wishlist logic here
  };

  const handleRemove = (book) => {
    // Remove from cart logic here
  };

  return (
    <div className="checkout-page">
      <Header navigate={navigate} />
      <main>
        <h2>Checkout</h2>
        <div className="cart-items">
          {books.map((book, index) => (
            <div key={index} className="cart-item">
              <img src={book.imgSrc} alt={book.title} />
              <div className="item-details">
                <h3>{book.title}</h3>
                <p><strong>Author:</strong> {book.author}</p>
                <p><strong>Price:</strong> {book.price}</p>
                <div className="item-actions">
                  <button onClick={() => handleWishlist(book)}>❤️</button>
                  <button onClick={() => handleRemove(book)}>🗑️</button>
                </div>
              </div>
              <div className="item-quantity">
                <label htmlFor={`quantity-${index}`}>Quantity:</label>
                <select id={`quantity-${index}`}>
                  <option value="1">1</option>
                  <option value="2">2</option>
                  <option value="3">3</option>
                </select>
              </div>
            </div>
          ))}
        </div>
        <div className="summary">
          <p><strong>Sub-total:</strong> 40,46€</p>
          <p><strong>Delivery:</strong> 4,00€</p>
          <p><strong>Total:</strong> 44,46€</p>
          <button className="checkout-button" onClick={() => navigate('shippingaddress')}>Checkout</button>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default Checkout;
