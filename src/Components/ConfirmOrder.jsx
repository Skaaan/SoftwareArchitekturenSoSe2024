import React, { useEffect } from 'react';
import './ConfirmOrder.css';

import theKiteRunner from '../assets/TKR.jpg';
import toxic from '../assets/Toxic.jpg';
import Footer from './Footer';
const ConfirmOrder = ({ navigate, goBack }) => {
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

  useEffect(() => {
    window.paypal.Buttons({
      createOrder: function (data, actions) {
        return actions.order.create({
          purchase_units: [{
            amount: {
              value: '44.46' // Replace with your total amount
            }
          }]
        });
      },
      onApprove: function (data, actions) {
        return actions.order.capture().then(function (details) {
          alert('Transaction completed by ' + details.payer.name.given_name);
          navigate('orderconfirmation');
        });
      }
    }).render('#paypal-button-container');
  }, [navigate]);

  return (
    <div className="confirm-order-page">
      <header className="header">
        <div className="logo" onClick={() => navigate('home')}>📚 Reader’s Insel</div>
      </header>
      <main>
        <h2>Confirm Order</h2>
        <div className="cart-items">
          {books.map((book, index) => (
            <div key={index} className="cart-item">
              <img src={book.imgSrc} alt={book.title} />
              <div className="item-details">
                <h3>{book.title}</h3>
                <p><strong>Author:</strong> {book.author}</p>
                <p><strong>Price:</strong> {book.price}</p>
              </div>
            </div>
          ))}
        </div>
        <div className="summary">
          <p><strong>Sub-total:</strong> 40,46€</p>
          <p><strong>Delivery:</strong> 4,00€</p>
          <p><strong>Total:</strong> 44,46€</p>
        </div>
        <div className="payment-methods">
          <h2>Payment Method</h2>
            <div id="paypal-button-container"></div>
          </div>
        <button className="back-button" onClick={goBack}>Back</button>
      </main>
      <Footer />
    </div>
  );
};

export default ConfirmOrder;
