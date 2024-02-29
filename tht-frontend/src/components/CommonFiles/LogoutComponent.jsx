import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { log_out } from "../../reducers/authReducer";

const LogoutComponent = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(log_out());
  });
};

export default LogoutComponent;
