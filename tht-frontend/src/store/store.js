import { configureStore, combineReducers } from "@reduxjs/toolkit";

import authreducers from "../reducers/authReducer";
import storage from "redux-persist/lib/storage";
import { persistReducer, persistStore } from "redux-persist";
import thunk from "redux-thunk";
import UserInfoReducer from "../reducers/UserInfoReducer";
import HomeReducer from "../reducers/homeReducer";
import blockreducer from "../reducers/blockedReducer";

const persistConfig = {
  key: "root",
  storage,
};
//const persistedReducer = persistReducer(persistConfig, authreducers);

export const rootReducers = combineReducers({
  authSlice: authreducers,
  userInfoSlice: UserInfoReducer,
  homeSlice: HomeReducer,
  blockSlice: blockreducer
});
const persistedReducer = persistReducer(persistConfig, rootReducers);

export const store = configureStore({
  reducer: persistedReducer,
  devTools: process.env.NODE_ENV !== "production",
  middleware: [thunk],
});

export const persistor = persistStore(store);
