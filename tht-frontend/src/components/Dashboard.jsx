import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../api/authActions';
import axios from 'axios';

const Dashboard = () => {
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);

  const handleLogout = async () => {
    try {
      // Make an authenticated API call
      await axios.get('/api/logout');

      // Dispatch the logout action to clear the token from Redux
      dispatch(logout());

      // Redirect or navigate to the login page
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <div>
      <h1>Dashboard</h1>
      <p>Welcome to the secured dashboard!</p>
      <button onClick={handleLogout}>Logout</button>
    </div>
  );
};

export default Dashboard;
