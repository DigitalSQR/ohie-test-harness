import React from 'react';
import { Provider } from 'react-redux';
import {  store } from './store/store';
import { RouterProvider } from 'react-router-dom';
import routes from './routes/routes';
import { LoaderProvider } from './components/loader/LoaderContext';
import Loader from './components/loader/Loader';
import { ConfigProvider } from 'antd';
function App() {
  return (  
    
    // CongigProvider : it is a config provider from antd, 
    <ConfigProvider
    theme={{
      token: {
        fontFamily:"'Inter', sans-serif"
      },
      components:{
        Tabs: {
          inkBarColor:"#009fc8",
          itemActiveColor:"#009fc8"
        },
        Switch:{
          colorPrimary:"#009fc8"
        }
      }
    }}>

    <LoaderProvider>
    <Provider store={store}>
      <RouterProvider router={routes}></RouterProvider>
      <Loader />
    </Provider> 
    </LoaderProvider>
    
    
    </ConfigProvider>
  );
}

export default App;
