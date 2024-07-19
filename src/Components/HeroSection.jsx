import React, { useState, useEffect } from 'react';
import { Box, Button, Typography, keyframes } from '@mui/material';
import backgroundImage from './Background.png';

const fadeIn = keyframes`
  0% { opacity: 0; transform: translateY(20px); }
  100% { opacity: 1; transform: translateY(0); }
`;

const fadeOut = keyframes`
  0% { opacity: 1; transform: translateY(0); }
  100% { opacity: 0; transform: translateY(-20px); }
`;

const HeroSection = ({ navigateToLogin }) => {
  const [showTitle, setShowTitle] = useState(false);
  const [showSubtitle, setShowSubtitle] = useState(false);
  const [showButton, setShowButton] = useState(false);

  const handleGetStarted = () => {
    navigateToLogin(); 
  };

  useEffect(() => {
    const titleTimeout = setTimeout(() => setShowTitle(true), 500);
    const subtitleTimeout = setTimeout(() => {
      setShowTitle(false);
      setShowSubtitle(true);
    }, 4000); // Increased timing for smoother transition
    const buttonTimeout = setTimeout(() => {
      setShowSubtitle(false);
      setShowButton(true);
    }, 8000); // Increased timing for smoother transition

    return () => {
      clearTimeout(titleTimeout);
      clearTimeout(subtitleTimeout);
      clearTimeout(buttonTimeout);
    };
  }, []);

  
  return (
    <Box
      sx={{
        height: '100vh',
        background: `linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url(${backgroundImage})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        color: '#fff',
        textAlign: 'center',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        position: 'relative',
      }}
    >
      <div>
        {showTitle && (
          <Typography
            variant="h2"
            sx={{
              animation: `${fadeIn} 2s ease-in-out, ${fadeOut} 2s ease-in-out 3s forwards`,
              fontWeight: 'bold',
            }}
          >
            Welcome to Readers_Insel!
          </Typography>
        )}
        {showSubtitle && (
          <Typography
            variant="h3"
            sx={{
              animation: `${fadeIn} 2s ease-in-out, ${fadeOut} 2s ease-in-out 3s forwards`,
              fontWeight: 'bold',
            }}
          >
            Explore a vast collection of literary treasures.
          </Typography>
        )}
      </div>
      {showButton && (
        <Button
          variant="contained"
          sx={{
            animation: `${fadeIn} 2s ease-in-out`,
            backgroundColor: '#3E2723', 
            '&:hover': { backgroundColor: '#3E2723' },
            fontWeight: 'bold',
            fontSize: '1.2rem',
            padding: '10px 20px',
            marginTop: '20px',
            color: '#fff',
          }}
          onClick={handleGetStarted}
        >
          Get Started
        </Button>
      )}
    </Box>
  );
};

export default HeroSection;
