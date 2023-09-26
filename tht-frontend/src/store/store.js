// store.js

import { createStore, combineReducers } from 'redux';

import { configureStore } from '@reduxjs/toolkit';

import authreducers from '../api/authReducer';

const store = configureStore({
  reducer:{
    authSlice:authreducers
  }
})
export default store;