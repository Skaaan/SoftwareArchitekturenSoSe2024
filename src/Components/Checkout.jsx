import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Checkout.css';
import Footer from './Footer';
import { getBasketItems, addToBasket, removeFromBasket, updateBasketItemQuantity } from './apiService'; // Import API functions

const Checkout = () => {
  const [books, setBooks] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCartItems = async () => {
      try {
        const basket = await getBasketItems();
        console.log('Fetched basket:', basket);

        // Check if the fetched data needs to be transformed
        if (basket.items) {
          const transformedBooks = basket.items.map(item => ({
            id: item.id,
            isbn: item.isbn,
            quantity: item.quantity,
            // Assuming these fields are present in the item or fetched separately
            name: item.name || 'Unknown Book', 
            author: item.author || 'Unknown Author', 
            price: item.price || 0,
            imageLink: item.imageLink || 'https://via.placeholder.com/150', 
          }));
          setBooks(transformedBooks);
        } else {
          setBooks(basket);
        }
      } catch (error) {
        console.error('Error fetching cart items:', error);
      }
    };

    fetchCartItems();
  }, []);

  const handleWishlist = async (book) => {
    try {
      await addToBasket(book); // Add to wishlist logic can be handled here
      alert('Added to wishlist');
    } catch (error) {
      console.error('Error adding to wishlist:', error);
    }
  };

  const handleRemove = async (book) => {
    try {
      await removeFromBasket(book.id);
      setBooks(books.filter(b => b.id !== book.id));
      alert('Removed from cart');
    } catch (error) {
      console.error('Error removing from cart:', error);
    }
  };

  const handleQuantityChange = async (book, quantity) => {
    try {
      await updateBasketItemQuantity(book.id, quantity);
      setBooks(books.map(b => b.id === book.id ? { ...b, quantity } : b));
    } catch (error) {
      console.error('Error updating quantity:', error);
    }
  };

  const calculateTotal = () => {
    if (!Array.isArray(books)) return { subTotal: 0, delivery: 0, total: 0 };

    const subTotal = books.reduce((acc, book) => acc + (book.price * book.quantity), 0);
    const delivery = 4.00; // Static delivery cost
    const total = subTotal + delivery;
    return { subTotal, delivery, total };
  };

  const { subTotal, delivery, total } = calculateTotal();

  const handleCheckout = () => {
    navigate('/shippingaddress');
  };

  return (
    <div className="checkout-page">
      <main>
        <h2>Checkout</h2>
        <div className="cart-items">
          {Array.isArray(books) && books.map((book, index) => (
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
          <button className="checkout-button" onClick={handleCheckout}>Checkout</button>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default Checkout;
