// store.js
import { createStore, combineReducers } from 'redux';
import authReducer from '../api/authReducer';

const rootReducer = combineReducers({
  auth: authReducer,
});

const store = createStore(rootReducer);

export default store;
