import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

const useRefresh = (interval = 30000, skipPaths = []) => {
  const location = useLocation();

  useEffect(() => {
    if (skipPaths.includes(location.pathname)) return;

    const refreshInterval = setInterval(() => {
      window.location.reload();
    }, interval);

    return () => clearInterval(refreshInterval);
  }, [interval, location.pathname, skipPaths]);
};

export default useRefresh;
