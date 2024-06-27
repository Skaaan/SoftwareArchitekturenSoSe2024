import React from 'react';
import './SignUp.css';
import Footer from './Footer';

const SignUp = ({ navigate }) => {
  return (
    <div className="signup-page">
      <div className="signup-container">
        <header className="header">
          <div className="logo">📚 Reader’s Insel</div>
        </header>
        <main>
          <h2>Create an account</h2>
          <form className="signup-form">
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
              <input type="password" placeholder="Password*" required />
            </div>
            <div className="form-group">
              <input type="text" placeholder="Address*" required />
            </div>
            <div className="form-group checkbox">
              <input type="checkbox" id="terms" required />
              <label htmlFor="terms">By clicking here, I state that I have read and understood the terms and conditions.</label>
            </div>
            <div className="form-group checkbox">
              <input type="checkbox" id="newsletter" />
              <label htmlFor="newsletter">Always stay up-to-date. Subscribe to our newsletter.</label>
            </div>
            <button type="submit" className="signup-button">Sign Up</button>
          </form>
        </main>
        <button onClick={() => navigate('home')}>Go to Home</button>
        <Footer />
      </div>
    </div>
  );
};

export default SignUp;
