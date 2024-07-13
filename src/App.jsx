import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './Components/Home';
import SignUp from './Components/SignUp';
import Description from './Components/Description';
import Contact from './Components/Contact';
import Checkout from './Components/Checkout';
import ShippingAddress from './Components/ShippingAddress';
import ConfirmOrder from './Components/ConfirmOrder';
import OrderConfirmation from './Components/OrderConfirmation';
import Edit from './Components/Edit';
import Header from './Components/Header';

import './App.css'; // Combined CSS for global styles

const App = () => {
  const handleSearch = (query) => {
    // Implement your search functionality here
    console.log("Search query:", query);
  };

  return (
    <Router>
      <Header handleSearch={handleSearch} />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/description/:id" element={<Description />} />
        <Route path="/contact" element={<Contact />} />
        <Route path="/checkout" element={<Checkout />} />
        <Route path="/shippingaddress" element={<ShippingAddress />} />
        <Route path="/confirmorder" element={<ConfirmOrder />} />
        <Route path="/orderconfirmation" element={<OrderConfirmation />} />
        <Route path="/edit/:id" element={<Edit />} />
      </Routes>
    </Router>
  );
};

export default App;
