import { configureStore,combineReducers } from '@reduxjs/toolkit';

import authreducers from '../reducers/authReducer';
import storage from 'redux-persist/lib/storage';
import { persistReducer, persistStore } from 'redux-persist';
import thunk from 'redux-thunk';

const persistConfig = {
  key: 'root',
  storage,
}
//const persistedReducer = persistReducer(persistConfig, authreducers);


export const rootReducers = combineReducers({
  authSlice: authreducers
})
const persistedReducer = persistReducer(persistConfig, rootReducers)


export const store = configureStore({
  reducer: persistedReducer,
  devTools: process.env.NODE_ENV !== 'production',
  middleware: [thunk]
})

export const persistor = persistStore(store)