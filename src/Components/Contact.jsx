import React from 'react';
import Header from './Header';
import Footer from './Footer';
import './Contact.css';

const Contact = ({ navigate }) => {
  return (
    <div className="contact-page">
      <Header navigate={navigate} />
      <main>
        <h2>Contact us</h2>
        <form className="contact-form">
          <div className="form-group">
            <input type="text" placeholder="First name*" required />
          </div>
          <div className="form-group">
            <input type="text" placeholder="Last name*" required />
          </div>
          <div className="form-group">
            <input type="email" placeholder="Email*" required />
          </div>
          <div className="form-group">
            <textarea placeholder="Your message*" required></textarea>
          </div>
          <div className="form-group checkbox">
            <input type="checkbox" id="terms" required />
            <label htmlFor="terms">By clicking here, I state that I have read and understood the terms and conditions.</label>
          </div>
          <div className="form-buttons">
            <button type="submit" className="send-button">Send</button>
            <button type="reset" className="clear-button">Clear form</button>
          </div>
        </form>
      </main>
      <Footer />
    </div>
  );
};

export default Contact;
