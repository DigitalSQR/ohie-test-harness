// authActions.js
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
