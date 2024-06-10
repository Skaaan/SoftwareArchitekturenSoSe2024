import React, { useState } from 'react';
import './ShippingAddress.css';
import dhlStandardLogo from '../assets/dhl-1.svg';
import dhlExpressLogo from '../assets/DHL_Express_logo.svg.png';
import hermesLogo from '../assets/hermes-vector-logo.png';

const ShippingAddress = ({ navigate, goBack }) => {
  const [sameAsDelivery, setSameAsDelivery] = useState(false);

  const handleCheckboxChange = () => {
    setSameAsDelivery(!sameAsDelivery);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    navigate('confirmorder');
  };

  return (
    <div className="shipping-address-page">
      <header className="header">
        <div className="logo">📚 Reader’s Insel</div>
      </header>
      <main>
        <h2>Delivery address</h2>
        <form className="address-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="country">Land*</label>
            <input type="text" id="country" required />
          </div>
          <div className="form-group">
            <label htmlFor="firstName">First name*</label>
            <input type="text" id="firstName" required />
          </div>
          <div className="form-group">
            <label htmlFor="lastName">Last name*</label>
            <input type="text" id="lastName" required />
          </div>
          <div className="form-group">
            <label htmlFor="street">Street*</label>
            <input type="text" id="street" required />
          </div>
          <div className="form-group">
            <label htmlFor="plz">PLZ*</label>
            <input type="text" id="plz" required />
          </div>
          <div className="form-group">
            <label htmlFor="city">City*</label>
            <input type="text" id="city" required />
          </div>
          <div className="form-group checkbox">
            <input
              type="checkbox"
              id="sameAsBilling"
              checked={sameAsDelivery}
              onChange={handleCheckboxChange}
            />
            <label htmlFor="sameAsBilling">Billing address same as delivery address.</label>
          </div>

          {!sameAsDelivery && (
            <>
              <h2>Billing address</h2>
              <div className="form-group">
                <label htmlFor="billingCountry">Land*</label>
                <input type="text" id="billingCountry" required />
              </div>
              <div className="form-group">
                <label htmlFor="billingFirstName">First name*</label>
                <input type="text" id="billingFirstName" required />
              </div>
              <div className="form-group">
                <label htmlFor="billingLastName">Last name*</label>
                <input type="text" id="billingLastName" required />
              </div>
              <div className="form-group">
                <label htmlFor="billingStreet">Street*</label>
                <input type="text" id="billingStreet" required />
              </div>
              <div className="form-group">
                <label htmlFor="billingPlz">PLZ*</label>
                <input type="text" id="billingPlz" required />
              </div>
              <div className="form-group">
                <label htmlFor="billingCity">City*</label>
                <input type="text" id="billingCity" required />
              </div>
            </>
          )}

          <h2>Shipping Method</h2>
          <div className="shipping-methods">
            <div className="method">
              <input type="radio" id="hermes" name="shippingMethod" value="Hermes" defaultChecked />
              <label htmlFor="hermes">Hermes Standard (Free)</label>
              <img src={hermesLogo} alt="Hermes" />
            </div>
            <div className="method">
              <input type="radio" id="dhlStandard" name="shippingMethod" value="DHL Standard" />
              <label htmlFor="dhlStandard">DHL Standard (Free)</label>
              <img src={dhlStandardLogo} alt="DHL Standard" />
            </div>
            <div className="method">
              <input type="radio" id="dhlExpress" name="shippingMethod" value="DHL Express" />
              <label htmlFor="dhlExpress">DHL Express (4,99 €)</label>
              <img src={dhlExpressLogo} alt="DHL Express" />
            </div>
          </div>
          <button type="submit" className="continue-button">Continue</button>
        </form>
        <button className="back-button" onClick={goBack}>Back</button>
      </main>
      <footer>
        <p>Copyright © 2024 Reader’s Insel®. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default ShippingAddress;
