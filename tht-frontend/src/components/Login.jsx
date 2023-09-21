import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { loginSuccess } from '../api/authActions';
import axios from 'axios';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const dispatch = useDispatch();

  const handleLogin = async () => {
    try {
      // Make a request to your authentication endpoint
      const response = await axios.post('/auth/login', {
        username,
        password,
      });

      const { accessToken } = response.data;

      // Dispatch the loginSuccess action to store the token in Redux
      dispatch(loginSuccess(accessToken));

      // Redirect or navigate to the secured dashboard
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <div>
      <h1>Login</h1>
      <div>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
      </div>
      <div>
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <div>
        <button onClick={handleLogin}>Login</button>
      </div>
    </div>
  );
};

export default Login;
