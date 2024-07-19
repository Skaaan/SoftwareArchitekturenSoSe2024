import React, { useEffect } from 'react';
import * as firebaseui from 'firebaseui';
import { getAuth, EmailAuthProvider, GoogleAuthProvider } from 'firebase/auth'; 
import 'firebaseui/dist/firebaseui.css';
import { auth } from './firebase-config'; // Adjust the path if necessary

const FirebaseAuth = () => {
  useEffect(() => {
    const uiConfig = {
      signInSuccessUrl: '/home', // URL to redirect to after sign-in
      signInOptions: [
        EmailAuthProvider.PROVIDER_ID,
        GoogleAuthProvider.PROVIDER_ID,
      ],
      credentialHelper: firebaseui.auth.CredentialHelper.NONE // Optional: To disable account chooser
    };

    const ui = firebaseui.auth.AuthUI.getInstance() || new firebaseui.auth.AuthUI(auth);
    ui.start('#firebaseui-auth-container', uiConfig);

    return () => {
      if (ui.isPendingRedirect()) {
        ui.reset();
      }
      ui.delete();
    }; 
  }, []);

  return <div id="firebaseui-auth-container"></div>;
};

export default FirebaseAuth;
