// context/LoaderContext.js
import React, { createContext, useContext, useState } from 'react';

const LoaderContext = createContext();

export const useLoader = () => {
    console.log(1);
  return useContext(LoaderContext);
};

export const LoaderProvider = ({ children }) => {
  const [isLoading, setIsLoading] = useState(false);

  const showLoader = () => {
    setIsLoading(true);
  };

  const hideLoader = () => {
    setIsLoading(false);
  };

  return (
    <LoaderContext.Provider value={{ isLoading, showLoader, hideLoader }}>
      {children}
    </LoaderContext.Provider>
  );
};
