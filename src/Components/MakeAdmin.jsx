import React, { useState, useEffect } from 'react';
import { getFunctions, httpsCallable } from "firebase/functions";
import { getAuth, onAuthStateChanged } from "firebase/auth";

const MakeAdmin = () => {
  const [email, setEmail] = useState(''); // State for email input
  const [message, setMessage] = useState('');
  const [user, setUser] = useState(null);

  useEffect(() => {
    const auth = getAuth();
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      if (currentUser) {
        setUser(currentUser);
      } else {
        setUser(null);
      }
    });

    return () => unsubscribe();
  }, []);

  const makeAdmin = async (e) => {
    e.preventDefault();
    if (!user) {
      setMessage('You must be logged in to make an admin.');
      return;
    }

    const functions = getFunctions();
    const addAdminRole = httpsCallable(functions, 'addAdminRole');
    try {
      const result = await addAdminRole({ email });
      setMessage(result.data.message);
    } catch (error) {
      setMessage('Error adding admin role: ' + error.message);
    }
  };

  return (
    <div>
      <h1>Make Admin</h1>
      <form onSubmit={makeAdmin}>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Enter email to make admin"
          required
        />
        <button type="submit">Make Admin</button>
      </form>
      <p>{message}</p>
    </div>
  );
};

export default MakeAdmin;
