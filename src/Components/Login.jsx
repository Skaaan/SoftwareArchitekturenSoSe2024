import React, { useState } from 'react';
import { register, signIn } from './auth';
import { useNavigate } from 'react-router-dom';
import './Script/Login.css';
import Header from './Header';

const SignInComponent = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [isFlipped, setIsFlipped] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async () => {
    try {
      const user = await register(email, password);
      console.log("Registered user:", user);
      navigate('/home');
    } catch (error) {
      setError(error.message);
    }
  };

  const handleSignIn = async () => {
    try {
      const user = await signIn(email, password);
      console.log("Signed in user:", user);
      navigate('/home');
    } catch (error) {
      setError(error.message);
    }
  };

  const toggleFlip = () => {
    setIsFlipped(!isFlipped);
    setError(null);
  };

  return (
    <div>
      <Header />
    <div className="container">
      <div className={`card-container ${isFlipped ? 'flipped' : ''}`}>
        <div className="card front">
          <h2>Sign In</h2>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="input"
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="input"
          />
          <button
            onClick={handleSignIn}
            className="button"
          >
            Sign In
          </button>
          <p className="toggleText">
            Don't have an account? <span onClick={toggleFlip} className="toggleLink">Register</span>
          </p>
        </div>
        <div className="card back">
          <h2>Register</h2>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="input"
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="input"
          />
          <button
            onClick={handleRegister}
            className="button registerButton"
          >
            Register
          </button>
          <p className="toggleText">
            Already have an account? <span onClick={toggleFlip} className="toggleLink">Sign In</span>
          </p>
        </div>
      </div>
      {error && <p className="error">{error}</p>}
    </div>
    </div>
  );
};

export default SignInComponent;