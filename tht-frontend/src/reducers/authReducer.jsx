import { createSlice } from "@reduxjs/toolkit";
const initialState = {
  token: null,
  error: null,
  access_token: null,
  expires_in: null,
  refresh_token: null,
  scope: null,
  token_type: null,
  isKeepLogin: null
};

const authSlice = createSlice({
  name: "authreducer",
  initialState: initialState,
  reducers: {
    login_success: (state, action) => {
      const payload = action.payload;
      state.token = payload.token;
      state.access_token = payload.access_token;
      state.expires_in = payload.expires_in;
      state.refresh_token = payload.refresh_token;
      state.scope = payload.scope;
      state.token_type = payload.token_type;
    },
    login_failure: (state, action) => {
      state.error = action.payload;
    },
    log_out: (state) => {
      state.access_token = null;
      state.expires_in = null;
      state.refresh_token = null;
      state.scope = null;
      state.token_type = null;
    },
    refreshTokenSuccess: (state, action) => {
      const payload = action.payload;
      state.token = payload.token;
      state.access_token = payload.access_token;
      state.expires_in = payload.expires_in;
      state.refresh_token = payload.refresh_token;
      state.scope = payload.scope;
      state.token_type = payload.token_type;
    },
    // Action for token refresh failure
    refreshTokenFailure: (state) => {
      state.access_token = null;
      state.expires_in = null;
      state.refresh_token = null;
      state.scope = null;
      state.token_type = null;
    },
    setIsKeepLoginState:(state,action) => {
      console.log("State=",state);
      console.log("isKeepLogin=",action);
      state.isKeepLogin = action.payload;
    }
  },
});
const authreducers = authSlice.reducer;
export default authreducers;
export const { login_success, login_failure, log_out,refreshTokenSuccess,refreshTokenFailure,setIsKeepLoginState } = authSlice.actions;
