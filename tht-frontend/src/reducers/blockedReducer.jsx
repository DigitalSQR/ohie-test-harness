import { createSlice } from "@reduxjs/toolkit";
const initialState = {
  isBlocked: "unblocked",
  dynamicDescription: "",
};

const blockedSlice = createSlice({
  name: "blockreducer",
  initialState: initialState,
  reducers: {
    set_blocker: (state, action) => {
      const payload = action.payload;
      state.isBlocked = payload;
    },
    set_dynamic_description: (state, action) => {
      state.dynamicDescription = action.payload;
    },
  },
});

const blockreducer = blockedSlice.reducer;
export default blockreducer;
export const { set_blocker, set_dynamic_description } = blockedSlice.actions;
