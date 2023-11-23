import React from 'react';
import { Provider } from 'react-redux';
import {  store } from './store/store';
import { RouterProvider } from 'react-router-dom';
import routes from './routes/routes';
import { LoaderProvider } from './components/loader/LoaderContext';
import Loader from './components/loader/Loader';
function App() {
  return (
    <LoaderProvider>
    <Provider store={store}>
      <RouterProvider router={routes}></RouterProvider>
      <Loader />
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
    </LoaderProvider>
  );
}

export default App;
