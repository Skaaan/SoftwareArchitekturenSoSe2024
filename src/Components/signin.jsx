import React from 'react';
import './SignIn.css';
import logo from '../assets/logo.png'; // Ensure this is the correct path to your logo image

const SignIn = ({ navigate }) => {
  const handleFormSubmit = (e) => {
    e.preventDefault();
    // Handle form submission logic here
  };

  const handleResetPassword = () => {
    // Handle reset password logic here
  };

  return (
    <div className="signin-page">
      <header className="header">
        <div className="logo-container" onClick={() => navigate('home')}>
          <img src={logo} alt="Logo" className="logo-img" />
          <div className="logo-text">Reader’s Insel</div>
        </div>
      </header>
      <main>
        <div className="form-container">
          <h2>Sign in</h2>
          <form className="signin-form" onSubmit={handleFormSubmit}>
            <div className="form-group">
              <label htmlFor="username">Username*</label>
              <input type="text" id="username" placeholder="Username" required />
            </div>
            <div className="form-group">
              <label htmlFor="password">Password*</label>
              <input type="password" id="password" placeholder="Password" required />
            </div>
            <div className="form-group">
              <button type="button" className="reset-password" onClick={handleResetPassword}>Reset password</button>
            </div>
            <button type="submit" className="signin-button">Sign in</button>
            <div className="signup-option">
              <p>Or</p>
              <button type="button" className="signup-button" onClick={() => navigate('signup')}>Create an account</button>
            </div>
          </form>
        </div>
      </main>
      <footer>
        <p>Copyright © 2024 Reader’s Insel®. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default SignIn;
