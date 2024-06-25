import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8080',
  realm: 'bookStore',
  clientId: 'react-client',
});

console.log('Keycloak instance created', keycloak);

export default keycloak;
