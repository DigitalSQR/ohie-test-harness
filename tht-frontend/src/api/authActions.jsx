// authActions.js
//This file is replaced by actions exported from authReducer file
export const loginSuccess = (accessToken) => ({
  type: 'LOGIN_SUCCESS',
  payload: accessToken,
});

export const loginFailure = (error) => ({
  type: 'LOGIN_FAILURE',
  payload: error,
});

export const logout = () => ({
  type: 'LOGOUT',
});
