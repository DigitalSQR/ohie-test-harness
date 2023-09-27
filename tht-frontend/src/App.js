import React from 'react';
import { Provider } from 'react-redux';
import store from './store/store'
import { BrowserRouter as Router, Route, Routes, Navigate, RouterProvider } from 'react-router-dom'; // Import necessary components
import routes from './routes/routes';


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
