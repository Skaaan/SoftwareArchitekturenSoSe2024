import React from 'react';
import './Contact.css';
import Footer from './Footer';
const Contact = ({ navigate }) => {
  return (
    <div className="contact-page">
      <header className="header">
        <div className="logo">📚 Reader’s Insel</div>
      </header>
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
      <button onClick={() => navigate('home')}>Go to Home</button>
    </div>
  );
};

export default Contact;
