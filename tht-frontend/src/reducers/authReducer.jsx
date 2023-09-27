import { createSlice } from "@reduxjs/toolkit";
const initialState = {
  token: null,
  error: null,
  access_token: null,
  expires_in: null,
  refresh_token: null,
  scope: null,
  token_type: null,
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
      state.error = null;
    },
  },
});
const authreducers = authSlice.reducer;
export default authreducers;
export const { login_success, login_failure, log_out } = authSlice.actions;
