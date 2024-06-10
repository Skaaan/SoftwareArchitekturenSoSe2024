import React from 'react';
import './SignIn.css';

const SignIn = ({ navigate }) => {
  return (
    <div className="signin-page">
      <div className="signin-container">
        <header className="header">
          <div className="logo">📚 Reader’s Insel</div>
        </header>
        <main>
          <h2>Sign in</h2>
          <form className="signin-form">
            <div className="form-group">
              <input type="text" placeholder="Username*" required />
            </div>
            <div className="form-group">
              <input type="password" placeholder="Password*" required />
            </div>
            <div className="form-group">
              <a href="#" className="reset-password">Reset password</a>
            </div>
            <button type="submit" className="signin-button">Sign in</button>
          </form>
          <div className="alternative-option">
            <p>Or</p>
            <button className="create-account-button" onClick={() => navigate('signup')}>Create an account</button>
          </div>
        </main>
        <footer>
          <p>Copyright © 2024 Reader’s Insel®. All rights reserved.</p>
        </footer>
        <button onClick={() => navigate('home')}>Go to Home</button>
      </div>
    </div>
  );
};

export default SignIn;
