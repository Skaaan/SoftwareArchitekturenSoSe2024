import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Script/ShippingAddress.css';
import Header from './Header';

const ShippingAddress = () => {
  const [showShippingAddress, setShowShippingAddress] = useState(true); // Show shipping address by default
  const [formShippingData, setFormData] = useState({
    name: '',
    phone: '',
    email: '',
    street: '',
    city: '',
    zip: '',
    shippingCountry: '',
    shippingFirstName: '',
    shippingLastName: '',
    shippingStreet: '',
    shippingCity: '',
    shippingPlz: '',
    method: 'Standard'
  });

  const navigate = useNavigate();

  const handleShowBillingAddress = () => {
    setShowShippingAddress(false);
  };

  const handleShowShippingAddress = () => {
    setShowShippingAddress(true);
  };

  const handleChange = (e) => {
    const { id, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [id]: value
    }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    navigate('/confirmorder', { state: { formShippingData } });
  };

  const goBack = () => {
    navigate(-1);
  };

  return (
    <div>
      <Header />
      <div className="sa_shipping-address-page">
        <div className="sa_modal">
          <div className="sa_modal-header">
            <h2>{showShippingAddress ? 'Add Shipping Address' : 'Add Billing Address'}</h2>
            <button className="sa_close-button" onClick={goBack}>&times;</button>
          </div>
          <form className="sa_address-form" onSubmit={handleSubmit}>
            {showShippingAddress && (
              <>
                <div className="sa_form-group">
                  <label htmlFor="shippingCountry">Country</label>
                  <input type="text" id="shippingCountry" value={formShippingData.shippingCountry} onChange={handleChange} required />
                </div>
                <div className="sa_form-row">
                  <div className="sa_form-group">
                    <label htmlFor="shippingFirstName">First Name</label>
                    <input type="text" id="shippingFirstName" value={formShippingData.shippingFirstName} onChange={handleChange} required />
                  </div>
                  <div className="sa_form-group">
                    <label htmlFor="shippingLastName">Last Name</label>
                    <input type="text" id="shippingLastName" value={formShippingData.shippingLastName} onChange={handleChange} required />
                  </div>
                </div>
                <div className="sa_form-group">
                  <label htmlFor="shippingStreet">Street</label>
                  <input type="text" id="shippingStreet" value={formShippingData.shippingStreet} onChange={handleChange} required />
                </div>
                <div className="sa_form-row">
                  <div className="sa_form-group">
                    <label htmlFor="shippingCity">City</label>
                    <input type="text" id="shippingCity" value={formShippingData.shippingCity} onChange={handleChange} required />
                  </div>
                  <div className="sa_form-group">
                    <label htmlFor="shippingPlz">Zip Code</label>
                    <input type="text" id="shippingPlz" value={formShippingData.shippingPlz} onChange={handleChange} required />
                  </div>
                </div>
                <div className="sa_go-back-to-billing" onClick={handleShowBillingAddress}>
                 + Add Billing Address (Optional)
                </div>
              </>
            )}

            {!showShippingAddress && (
              <>
                <div className="sa_form-group">
                  <label htmlFor="name">Name</label>
                  <input type="text" id="name" value={formShippingData.name} onChange={handleChange} required />
                </div>
                <div className="sa_form-row">
                  <div className="sa_form-group">
                    <label htmlFor="phone">Phone Number</label>
                    <input type="text" id="phone" value={formShippingData.phone} onChange={handleChange} required />
                  </div>
                  <div className="sa_form-group">
                    <label htmlFor="email">Email</label>
                    <input type="email" id="email" value={formShippingData.email} onChange={handleChange} required />
                  </div>
                </div>
                <div className="sa_form-group">
                  <label htmlFor="street">Street</label>
                  <input type="text" id="street" value={formShippingData.street} onChange={handleChange} required />
                </div>
                <div className="sa_form-row">
                  <div className="sa_form-group">
                    <label htmlFor="city">City</label>
                    <input type="text" id="city" value={formShippingData.city} onChange={handleChange} required />
                  </div>
                  <div className="sa_form-group">
                    <label htmlFor="zip">Zip Code</label>
                    <input type="text" id="zip" value={formShippingData.zip} onChange={handleChange} required />
                  </div>
                </div>
                <div className="sa_add-shipping-address" onClick={handleShowShippingAddress}>
                &lt;  Go Back to Shipping Address
                </div>
              </>
            )}

            <div className="sa_terms">
              By proceeding, you agree to our <a href="/terms">Terms of Use</a> and confirm you have read our <a href="/privacy">Privacy</a> and <a href="/cookies">Cookie Statement</a>.
            </div>
            <div className="sa_form-actions">
              <button type="button" className="sa_cancel-button" onClick={goBack}>Back</button>
              <button type="submit" className="sa_save-button">Next</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ShippingAddress;
