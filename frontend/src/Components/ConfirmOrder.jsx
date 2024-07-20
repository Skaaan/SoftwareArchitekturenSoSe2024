import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './ConfirmOrder.css';
import { getBasketItems, getAllProducts, checkout } from './apiService';
import Header from './Header';

const ConfirmOrder = () => {
  const [books, setBooks] = useState([]);
  const [subTotal, setSubTotal] = useState(0);
  const [deliveryFee, setDeliveryFee] = useState(4.00);
  const navigate = useNavigate();
  const location = useLocation();
  const [paypalLoaded, setPaypalLoaded] = useState(false);

  useEffect(() => {
    const fetchBasketItems = async () => {
      try {
        const basket = await getBasketItems();
        const allProducts = await getAllProducts();

        if (basket.items && Array.isArray(basket.items)) {
          const transformedBooks = basket.items.map(item => {
            const product = allProducts.find(p => p.isbn === item.isbn) || {};
            return {
              id: item.id,
              isbn: item.isbn,
              quantity: item.quantity,
              name: product.name || 'Unknown Book',
              author: product.author || 'Unknown Author',
              price: product.price || 0,
              imageLink: product.imageLink || 'https://via.placeholder.com/150',
            };
          });

          setBooks(transformedBooks);
          const calculatedSubTotal = transformedBooks.reduce((total, item) => total + (item.price * item.quantity), 0);
          setSubTotal(calculatedSubTotal);
          console.log('Sub-total calculated:', calculatedSubTotal);
        } else {
          console.error('Basket items are not an array:', basket.items);
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchBasketItems();
  }, []);

  useEffect(() => {
    if (books.length > 0 && subTotal > 0 && !paypalLoaded) {
      console.log('Books loaded:', books);
      console.log('Sub-total:', subTotal);
      console.log('Total including delivery:', (subTotal + deliveryFee).toFixed(2));
      
      window.paypal.Buttons({
        createOrder: function (data, actions) {
          return actions.order.create({
            purchase_units: [{
              amount: {
                value: (subTotal + deliveryFee).toFixed(2)
              }
            }]
          });
        },
        onApprove: async function (data, actions) {
          return actions.order.capture().then(async function (details) {
            try {
              alert('Transaction completed by ' + details.payer.name.given_name);
              // Call the checkout API to finalize the order
              const orderDetails = await checkout();
              console.log('Order details:', orderDetails);
              // Navigate to the order confirmation page
              navigate('/orderconfirmation');
            } catch (error) {
              console.error('Error during checkout:', error);
            }
          });
        }
      }).render('#paypal-button-container');
      setPaypalLoaded(true);
    }
  }, [books, subTotal, deliveryFee, navigate, paypalLoaded]);

  const shippingAddress = location.state?.formShippingData || {
    name: "Unknown",
    phone: "Unknown",
    email: "Unknown",
    street: "Unknown",
    city: "Unknown",
    zip: "Unknown",
    shippingCountry: "Unknown",
    shippingFirstName: "Unknown",
    shippingLastName: "Unknown",
    shippingStreet: "Unknown",
    shippingCity: "Unknown",
    shippingPlz: "Unknown",
    method: "Standard"
  };

  const handleGoBack = () => {
    navigate(-1);
  };

  return (
    <div className="ConfirmOrder_confirm-order-page">
      <Header />
      <main className="ConfirmOrder_main">
        <h2>Confirm Order</h2>
        <div className="ConfirmOrder_cart-items">
          {books.map((book, index) => (
            <div key={index} className="ConfirmOrder_cart-item">
              <img src={book.imageLink} alt={book.name} />
              <div className="ConfirmOrder_items-details">
                <h3>{book.name}</h3>
                <p><strong>Author:</strong> {book.author}</p>
                <p><strong>Price:</strong> {book.price}€</p>
                <p><strong>Quantity:</strong> {book.quantity}</p>
              </div>
            </div>
          ))}
        </div>
        <div className="ConfirmOrder_summary">
          <p><strong>Sub-total:</strong> {subTotal.toFixed(2)}€</p>
          <p><strong>Delivery:</strong> {deliveryFee.toFixed(2)}€</p>
          <p><strong>Total:</strong> {(subTotal + deliveryFee).toFixed(2)}€</p>
        </div>
        <div className="ConfirmOrder_shipping-details">
          <h3>Shipping Details ({shippingAddress.method}) :</h3>
          <p>{shippingAddress.shippingFirstName} {shippingAddress.shippingLastName},</p>
          <p>{shippingAddress.shippingStreet}</p>
          <p>{shippingAddress.shippingCity}, {shippingAddress.shippingPlz}</p>
          <p>{shippingAddress.shippingCountry}</p>
        </div>
        <div className="ConfirmOrder_payment-methods">
          <h2>Payment Method</h2>
          <div id="paypal-button-container"></div>
        </div>
        <button className="ConfirmOrder_back-button" onClick={handleGoBack}>Back</button>
      </main>
    </div>
  );
};

export default ConfirmOrder;
