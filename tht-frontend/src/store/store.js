import { configureStore } from '@reduxjs/toolkit';

import authreducers from '../reducers/authReducer';

const store = configureStore({
  reducer:{
    authSlice:authreducers
  },
  devTools:process.env.NODE_ENV !== 'production'
})
export default store;