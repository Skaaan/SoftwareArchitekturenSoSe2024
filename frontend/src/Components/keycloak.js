// keycloak.js
import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8180/',
  realm: 'bookStore',
  clientId: 'react-client',
  enableLogging: true,
  checkLoginIframe: false, 
});

export default keycloak;
