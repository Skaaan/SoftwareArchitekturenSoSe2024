import React from 'react';
import { ReactKeycloakProvider } from '@react-keycloak/web';
import keycloak from './keycloak';
import App from '../App';

const eventLogger = (event, error) => {
  console.log('Keycloak event:', event);
  if (error) {
    console.error('Keycloak event error:', error);
  }
};

const tokenLogger = (tokens) => {
  console.log('Keycloak tokens:', tokens);
};

const AppWrapper = () => (
  <ReactKeycloakProvider
    authClient={keycloak}
    initOptions={{ 
      onLoad: 'login-required',
      checkLoginIframe: false,
      pkceMethod: 'S256',
    }}
    onEvent={eventLogger}
    onTokens={tokenLogger}
  >
    <App />
  </ReactKeycloakProvider>
);

export default AppWrapper;
