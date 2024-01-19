import { createSlice } from "@reduxjs/toolkit";
const initialState = {
  id: null,
  state: null,
  name: null,
  email: null,
  roleIds: null
};

const userSlice = createSlice({
    name: "userinforeducer",
    initialState: initialState,
    reducers: {
      userinfo_success: (state, action) => {       
        const payload = action.payload;
        state.id = payload.id;
        state.state = payload.state;
        state.name = payload.name;
        state.email = payload.email;
        state.roleIds = payload.roleIds;

      }
    }
});
const UserInfoReducer = userSlice.reducer;
export default UserInfoReducer;
export const { userinfo_success} = userSlice.actions;
