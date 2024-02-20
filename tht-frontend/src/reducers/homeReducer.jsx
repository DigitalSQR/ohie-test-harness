import { createSlice } from "@reduxjs/toolkit";
const initialState = {
  header: null,
};

const homeSlice = createSlice({
  name: "homereducer",
  initialState,
  reducers: {
    set_header: (state, action) => {
      const payload = action.payload;
      state.header = payload;
    },
  },
});
const HomeReducer = homeSlice.reducer;
export default HomeReducer;
export const { set_header } = homeSlice.actions;
