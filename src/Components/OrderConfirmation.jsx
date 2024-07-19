import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Script/OrderConfirmation.css';
import Header from './Header';
import { Box, TextField, Button, CircularProgress, Typography } from '@mui/material';

const OrderConfirmation = () => {
  const navigate = useNavigate();

  return (
    <div className="oc_order-confirmation-page">
      <Header />
      <Box className="Edit_edit-page">
        <Box className="Edit_edit-container">
          <Typography variant="h4" className="Edit_edit-header">Thank you for your order</Typography>
          <Typography variant="h6" className="Edit_edit-header">You will receive the order confirmation shortly</Typography>
          <Typography variant="h6" className="Edit_edit-header">For any further queries, feel free to contact us!</Typography>
        </Box>
      </Box>
    </div>
  );
};

export default OrderConfirmation;
