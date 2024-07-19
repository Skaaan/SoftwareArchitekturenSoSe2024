import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyAfb8d4bn8vsq_aD3bNTXpFve13E2TIPIQ",
  authDomain: "readers-insel.firebaseapp.com",
  projectId: "readers-insel",
  storageBucket: "readers-insel.appspot.com",
  messagingSenderId: "426311017881",
  appId: "1:426311017881:web:bd4477843948ca378d5f08",
  measurementId: "G-DVJ49Z1SXT"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);

export { auth };
export default app;
