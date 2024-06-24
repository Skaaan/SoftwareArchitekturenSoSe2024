import React, { useState } from 'react';
import Home from './Components/Home';
import SignIn from './Components/signin';
import SignUp from './Components/SignUp';
import Description from './Components/Description';
import Contact from './Components/Contact';
import Checkout from './Components/Checkout';
import ShippingAddress from './Components/ShippingAddress';
import ConfirmOrder from './Components/ConfirmOrder';
import OrderConfirmation from './Components/OrderConfirmation';
import './App.css'; // Combined CSS for global styles

const App = () => {
  const [currentPage, setCurrentPage] = useState('home');
  const [selectedBook, setSelectedBook] = useState(null);
  const [history, setHistory] = useState([]);

  const navigate = (page, book = null) => {
    setHistory([...history, currentPage]);
    setCurrentPage(page);
    setSelectedBook(book);
  };

  const goBack = () => {
    const newHistory = [...history];
    const previousPage = newHistory.pop();
    setHistory(newHistory);
    setCurrentPage(previousPage);
  };

  return (
    <div>
      {currentPage === 'home' && <Home navigate={navigate} goBack={goBack} />}
      {currentPage === 'signin' && <SignIn navigate={navigate} goBack={goBack} />}
      {currentPage === 'signup' && <SignUp navigate={navigate} goBack={goBack} />}
      {currentPage === 'description' && <Description book={selectedBook} navigate={navigate} goBack={goBack} />}
      {currentPage === 'contact' && <Contact navigate={navigate} goBack={goBack} />}
      {currentPage === 'checkout' && <Checkout navigate={navigate} goBack={goBack} />}
      {currentPage === 'shippingaddress' && <ShippingAddress navigate={navigate} goBack={goBack} />}
      {currentPage === 'confirmorder' && <ConfirmOrder navigate={navigate} goBack={goBack} />}
      {currentPage === 'orderconfirmation' && <OrderConfirmation navigate={navigate} goBack={goBack} />}
    </div>
  );
};

export default App;
