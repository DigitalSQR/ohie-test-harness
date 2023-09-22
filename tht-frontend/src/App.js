import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom'; // Import the necessary components and hooks
import { Provider } from 'react-redux';
import store from './store/store';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Land from './components/Landing';

function App() {
  return (
    <Provider store={store}>
      <Router>
        <Routes> {/* Use Routes instead of Route */}
        <Route
            path="/"
            element={<Login />} // Use Navigate instead of Redirect
          />
          <Route
            path="/login"
            element={store.getState().auth.token ? <Navigate to="/dashboard" /> : <Login />} // Use Navigate instead of Redirect
          />
          <Route
            path="/dashboard"
            element={store.getState().auth.token ? <Dashboard /> : <Navigate to="/login" />} // Use Navigate instead of Redirect
          />
          <Route
          path='/landing' element={<Land/>}/>
          {/* Additional secured routes can be added here */}
        </Routes>
      </Router>
    </Provider>
  );
}

export default App;
