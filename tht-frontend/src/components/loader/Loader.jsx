// components/Loader.js
import React from 'react';
import { useLoader } from './LoaderContext';
import './Loader.css'; 

const Loader = () => {
  const { isLoading } = useLoader();

  return (
    <div className={`loader ${isLoading ? 'visible' : 'hidden'}`}>
      <div className="spinner"></div>
      <p>Loading...</p>
    </div>
  );
};

export default Loader;
