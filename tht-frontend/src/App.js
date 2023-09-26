import React from 'react';
import { Provider } from 'react-redux';
import store from './store/store'
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Land from './components/Landing';
import { BrowserRouter as Router, Route, Routes, Navigate, RouterProvider } from 'react-router-dom'; // Import necessary components
import routes from './routes/routes';

// const PrivateRoute = ({ element }) => {
//   const isAuthenticated = !!store.getState().auth.token;
//   console.log(store.getState().auth.token);
//   console.log(store.getState());
//   return isAuthenticated ? element : <Navigate to="/login" />;
// };

function App() {
  return (
    <Provider store={store}>
      <RouterProvider router={routes}></RouterProvider>
      {/* <Router>
        <div className="App">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/dashboard" element={<PrivateRoute element={<Dashboard />} />} />
            <Route index element={<Navigate to="/login" />} />
          </Routes>
        </div> */}
      {/* </Router> */}
    </Provider>
  );
}

export default App;
