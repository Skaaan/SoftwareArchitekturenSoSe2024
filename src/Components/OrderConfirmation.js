import React from 'react';
import './OrderConfirmation.css';

import theKiteRunner from '../assets/TKR.jpg';
import toxic from '../assets/Toxic.jpg';

const OrderConfirmation = ({ navigate, goBack }) => {
  const orderNumber = "#63718";
  const books = [
    {
      title: "The Kite Runner",
      author: "Khaled Hosseini",
      price: "21,18€",
      quantity: 1,
      imgSrc: theKiteRunner,
    },
    {
      title: "Toxic",
      author: "Nicole Blanchard",
      price: "19,28€",
      quantity: 1,
      imgSrc: toxic,
    },
  ];

  const shippingAddress = {
    name: "Olivia Johnson",
    address: "Washington Street 7C, 1221, Princeton, NewJersey, United States",
    method: "Standard"
  };

  return (
    <div className="order-confirmation-page">
      <header className="header">
        <div className="logo">📚 Reader’s Insel</div>
      </header>
      <main>
        <h2>Order Confirmation</h2>
        <p>Thanks for your purchase. Your order <strong>{orderNumber}</strong> has been confirmed!</p>
        <h3>Order Details:</h3>
        <div className="cart-items">
          {books.map((book, index) => (
            <div key={index} className="cart-item">
              <img src={book.imgSrc} alt={book.title} />
              <div className="item-details">
                <h3>{book.title}</h3>
                <p><strong>Author:</strong> {book.author}</p>
                <p><strong>Price:</strong> {book.price}</p>
                <p><strong>Quantity:</strong> {book.quantity}</p>
              </div>
            </div>
          ))}
        </div>
        <div className="shipping-details">
          <h3>Shipping Details ({shippingAddress.method}) :</h3>
          <p>{shippingAddress.name},</p>
          <p>{shippingAddress.address}</p>
        </div>
        <p>Need help with your order? Reach out to our customer support team via <a href="#" onClick={() => navigate('contact')}>Contact Form</a>.</p>
        <button className="back-button" onClick={goBack}>Back</button>
      </main>
      <footer>
        <p>Copyright © 2024 Reader’s Insel®. All rights reserved.</p>
      </footer>
      <button onClick={() => navigate('home')}>Go to Home</button>
    </div>
  );
};

export default OrderConfirmation;
