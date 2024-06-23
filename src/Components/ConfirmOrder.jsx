import React from 'react';
import './ConfirmOrder.css';

import theKiteRunner from '../assets/TKR.jpg';
import toxic from '../assets/Toxic.jpg';
import paypalLogo from '../assets/Paypal.png';
import creditCardLogo from '../assets/CC.png';

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

  const handlePayment = () => {
    // Handle payment logic here, then navigate to the OrderConfirmation page
    navigate('orderconfirmation');
  };

  return (
    <div className="confirm-order-page">
      <header className="header">
        <div className="logo">📚 Reader’s Insel</div>
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
          <div className="method">
            <input type="radio" id="paypal" name="paymentMethod" value="PayPal" defaultChecked />
            <label htmlFor="paypal">PayPal</label>
            <img src={paypalLogo} alt="PayPal" />
          </div>
          <div className="method">
            <input type="radio" id="invoice" name="paymentMethod" value="Invoice" />
            <label htmlFor="invoice">Pay by Invoice</label>
          </div>
          <div className="method">
            <input type="radio" id="creditCard" name="paymentMethod" value="CreditCard" />
            <label htmlFor="creditCard">Credit/Debit Card</label>
            <img src={creditCardLogo} alt="Credit/Debit Card" />
          </div>
        </div>
        <button className="pay-button" onClick={handlePayment}>Pay</button>
        <button className="back-button" onClick={goBack}>Back</button>
      </main>
      <footer>
        <p>Copyright © 2024 Reader’s Insel®. All rights reserved.</p>
      </footer>
      <button onClick={() => navigate('home')}>Go to Home</button>
    </div>
  );
};

export default ConfirmOrder;
