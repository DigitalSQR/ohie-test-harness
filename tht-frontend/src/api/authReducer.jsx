// authReducer.js
//toolkit code
import  { createSlice } from '@reduxjs/toolkit';
const initialState = {
  token: null,
  error: null,
};

const authSlice = createSlice({
  name:"authreducer",
  initialState:initialState,
  reducers:{
    login_success:(state,action)=>{state.token = action.payload},
    login_failure:(state,action)=>{state.error = action.payload},
    log_out:(state)=>{state.token = null; state.error=null}
  }
})
const authreducers = authSlice.reducer;
export default authreducers;
export const {login_success,login_failure,log_out} = authSlice.actions;

// const authReducer = (state = initialState, action) => {
//   switch (action.type) {
//     case 'LOGIN_SUCCESS':
//       return {
//         ...state,
//         token: action.payload,
//         error: null,
//       };
//     case 'LOGIN_FAILURE':
//       return {
//         ...state,
//         token: null,
//         error: action.payload,
//       };
//     case 'LOGOUT':
//       return {
//         ...state,
//         token: null,
//         error: null,
//       };
//     default:
//       return state;
//   }
// };

// export default authReducer;


