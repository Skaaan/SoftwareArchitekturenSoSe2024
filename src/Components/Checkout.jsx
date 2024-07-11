import React, { useState, useEffect } from 'react';
import Header from './Header';
import './Checkout.css';
import Footer from './Footer';
import { getCartItems, addToCart, removeFromCart, updateCartItemQuantity } from './apiService'; // Import API functions

const Checkout = ({ navigate }) => {
  const [books, setBooks] = useState([]);

  useEffect(() => {
    const fetchCartItems = async () => {
      try {
        const cartItems = await getCartItems();
        setBooks(cartItems);
      } catch (error) {
        console.error('Error fetching cart items:', error);
      }
    };

    fetchCartItems();
  }, []);

  const handleWishlist = async (book) => {
    try {
      await addToCart(book); // Add to wishlist logic can be handled here
      alert('Added to wishlist');
    } catch (error) {
      console.error('Error adding to wishlist:', error);
    }
  };

  const handleRemove = async (book) => {
    try {
      await removeFromCart(book.id);
      setBooks(books.filter(b => b.id !== book.id));
      alert('Removed from cart');
    } catch (error) {
      console.error('Error removing from cart:', error);
    }
  };

  const handleQuantityChange = async (book, quantity) => {
    try {
      await updateCartItemQuantity(book.id, quantity);
      setBooks(books.map(b => b.id === book.id ? { ...b, quantity } : b));
    } catch (error) {
      console.error('Error updating quantity:', error);
    }
  };

  const calculateTotal = () => {
    const subTotal = books.reduce((acc, book) => acc + (book.price * book.quantity), 0);
    const delivery = 4.00; // Static delivery cost
    const total = subTotal + delivery;
    return { subTotal, delivery, total };
  };

  const { subTotal, delivery, total } = calculateTotal();

  return (
    <div className="checkout-page">
      <Header navigate={navigate} />
      <main>
        <h2>Checkout</h2>
        <div className="cart-items">
          {books.map((book, index) => (
            <div key={index} className="cart-item">
              <img src={book.imageLink} alt={book.name} />
              <div className="item-details">
                <h3>{book.name}</h3>
                <p><strong>Author:</strong> {book.author}</p>
                <p><strong>Price:</strong> {book.price}€</p>
                <div className="item-actions">
                  <button onClick={() => handleWishlist(book)}>❤️</button>
                  <button onClick={() => handleRemove(book)}>🗑️</button>
                </div>
              </div>
              <div className="item-quantity">
                <label htmlFor={`quantity-${index}`}>Quantity:</label>
                <select
                  id={`quantity-${index}`}
                  value={book.quantity}
                  onChange={(e) => handleQuantityChange(book, parseInt(e.target.value))}
                >
                  <option value="1">1</option>
                  <option value="2">2</option>
                  <option value="3">3</option>
                </select>
              </div>
            </div>
          ))}
        </div>
        <div className="summary">
          <p><strong>Sub-total:</strong> {subTotal.toFixed(2)}€</p>
          <p><strong>Delivery:</strong> {delivery.toFixed(2)}€</p>
          <p><strong>Total:</strong> {total.toFixed(2)}€</p>
          <button className="checkout-button" onClick={() => navigate('shippingaddress')}>Checkout</button>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default Checkout;
