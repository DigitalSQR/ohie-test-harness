import { Fragment, useEffect, useState } from "react";
import openhie_logo from "../styles/images/logo.png";
import { useNavigate, useParams } from "react-router-dom";
import { notification } from "antd";
import { AuthenticationAPI } from "../api/AuthenticationAPI";
import { useLoader } from "./loader/LoaderContext";

export default function UpdatePassword() {
  const { base64TokenId } = useParams();
  const { base64UserEmail } = useParams();
  const { showLoader, hideLoader } = useLoader();
  const [confirmPassword, setConfirmPassword] = useState();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    newPassword: "",
    base64UserEmail,
    base64TokenId,
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const clearPasswords = () => {
    setConfirmPassword("");
    setFormData({
      newPassword: "",
      base64UserEmail,
      base64TokenId,
    });
  };

  const updatePassword = () => {
    if (confirmPassword == formData.newPassword) {
      showLoader();
      AuthenticationAPI.resetPassword(formData)
        .then((response) => {
          var data = response.data;
          notification.success({
            placement: "bottomRight",
            description: `${data?.message}`,
          });
          hideLoader();
          navigate("/login");
        })
        .catch((error) => {
          notification.error({
            placement: "bottomRight",
            description: `${error?.data?.message}`,
          });
          hideLoader();
        })
    } else {
      clearPasswords();
      notification.error({
        placement: "bottomRight",
        description: `Passwords do not match`,
      });
    }
  };
  
  const backToLogin = () => {
    navigate("/login");
  };
  
  useEffect(() => {
    console.log('Encrypted string:', base64UserEmail);
    console.log('Last part of the URL:', base64TokenId);
  }, []);

  return (
    <Fragment>
      <div class="container-fluid ps-0">
        <div class="row">
          <div class="col-md-6 col-12 col-sm-12 p-0">
            <div class="login-bg">
              <div class="col-10 col-md-11 col-lg-10 col-xl-8 col-xxl-6">
                <h1>Testing Harness Test Automation</h1>
                <p class="font-size-16 mt-3">
                  Experience streamlined OpenHIE standards compliance testing
                  for healthcare websites. Our tool ensures precision,
                  simplifies complexities, and empowers your projects.
                </p>
              </div>
            </div>
          </div>

          <div class="col-md-6 col-12 col-sm-12">
            <div class="login-form-bg pt-5">
              <div class="text-center">
                <img src={openhie_logo} />
              </div>

              <h2 class="my-4 formTitle">
                Reset Password
              </h2>

              <div class="custom-input mb-3">
                <label for="Password" class="form-label">
                  Password
                </label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="bi bi-lock"></i>
                  </span>
                  <input
                    type="password"
                    class="form-control border-start-0 ps-0"
                    name="newPassword"
                    id="Password"
                    autoComplete="off"
                    value={formData.newPassword}
                    onChange={handleInputChange}
                    placeholder="Password"
                    aria-label="Password"
                  />
                </div>
              </div>

              <div class="custom-input mb-3">
                <label for="confirmPassword" class="form-label">
                  Confirm New Password
                </label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="bi bi-lock"></i>
                  </span>
                  <input
                    type="password"
                    class="form-control border-start-0 ps-0"
                    name="confirmPassword"
                    id="confirmPassword"
                    autoComplete="off"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    placeholder="Confirm New Password"
                    aria-label="Confirm New Password"
                  />
                </div>
              </div>

              <div class="buttonWrapper">
                <button onClick={updatePassword} className="btn btn-primary">
                  <span>Continue</span>
                  <span id="loader"></span>
                </button>
                <button id="submitButton" onClick={backToLogin} className=" mx-2 btn btn-primary">
                  <span>Back to Login</span>
                  <span id="loader"></span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  )
}