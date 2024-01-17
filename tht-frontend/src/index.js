import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import 'popper.js/dist/umd/popper.min.js';
import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import "./styles/style.css"; 
import App from './App';
import reportWebVitals from './reportWebVitals';
import { persistor, store } from './store/store';
import { PersistGate } from 'redux-persist/integration/react';
import { Provider } from 'react-redux';
import { setAuthToken,clearAuthInfo } from '../src/api/configs/axiosConfigs'
const root = ReactDOM.createRoot(document.getElementById('root'));
const persistedStateJSON = localStorage.getItem('persist:root');


if (persistedStateJSON) {
  const persistedState = JSON.parse(persistedStateJSON);
 
  const authSlice = JSON.parse(persistedState.authSlice);
  if (authSlice){// && isKeepMeLogin && isKeepMeLogin === true) {
    setAuthToken(authSlice.access_token);
  }else {
    clearAuthInfo();
  }
}
root.render(
  <Provider store={store}>
    <PersistGate loading={null} persistor={persistor}>
      <App />
    </PersistGate>
  </Provider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
