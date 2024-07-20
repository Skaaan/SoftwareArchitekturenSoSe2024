import React from 'react';
import { Route, Routes, useNavigate } from 'react-router-dom';
import Home from './Components/Home';
import Description from './Components/Description';
import Contact from './Components/Contact';
import Checkout from './Components/Checkout';
import ShippingAddress from './Components/ShippingAddress';
import ConfirmOrder from './Components/ConfirmOrder';
import OrderConfirmation from './Components/OrderConfirmation';
import Add from './Components/Add';
import Edit from './Components/Edit';
import './App.css';
import Footer from './Components/Footer';
import useRefresh from './Hooks/AutoRefresh';
import HeroSection from './Components/HeroSection';
import Login from './Components/Login';
import PrivateRoute from './Components/PrivateRoute';
import MakeAdmin from './Components/MakeAdmin'; 

const App = () => {
  const navigate = useNavigate();

  useRefresh(60000);

  return (
    <>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/home" element={<PrivateRoute><Home /></PrivateRoute>} />
        <Route path="/description/:id" element={<PrivateRoute><Description /></PrivateRoute>} />
        <Route path="/contact" element={<Contact />} />
        <Route path="/checkout" element={<PrivateRoute><Checkout /></PrivateRoute>} />
        <Route path="/shippingaddress" element={<PrivateRoute><ShippingAddress /></PrivateRoute>} />
        <Route path="/confirmorder" element={<PrivateRoute><ConfirmOrder /></PrivateRoute>} />
        <Route path="/orderconfirmation" element={<PrivateRoute><OrderConfirmation /></PrivateRoute>} />
        <Route path="/add" element={<PrivateRoute><Add /></PrivateRoute>} />
        <Route path="/edit/:isbn" element={<PrivateRoute><Edit /></PrivateRoute>} />
        <Route path="/make-admin" element={<PrivateRoute><MakeAdmin /></PrivateRoute>} />
        <Route path="/" element={<HeroSection navigateToLogin={() => navigate('/login')} />} />
      </Routes>
      <Footer />
    </>
  );
};

export default App;
