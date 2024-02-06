import { Fragment, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import openhie_logo from "../../styles/images/logo.png";
import capture_logo from "../../styles/images/capture-logo.png";
import { AuthenticationAPI } from "../../api/AuthenticationAPI";
import { useLoader } from "../loader/LoaderContext";
import { notification } from "antd";
import ReCAPTCHA from "react-google-recaptcha";
export default function SignUp() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    name: "",
    password: "",
    companyName:""
  });
  const [confirmPassword, setconfirmPassword] = useState("");
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };
  const { showLoader, hideLoader } = useLoader();
  const SignUpHandler = async () => {
    if (formData.password != confirmPassword) {
      notification.error({
        placement: "bottomRight",
        description: "Passwords do not match.",
      });
    } else {
      console.log(formData);
      showLoader();
      AuthenticationAPI.signup(formData)
        .then(
          (result) => {
            hideLoader();
            navigate(`/CongratulationsPage/${result.email}
            `);
          },
          (result) => {
            hideLoader();
            const messages = result.response.data.map((res) => res.message);
            const MessageList = () => (
              <div>
                {messages.map((message, index) => (
                  <li key={index}>{message}</li>
                ))}
              </div>
            );
            
            notification.error({
              placement: 'bottomRight',
              message: 'Error',
              description: <MessageList />,
            });
          }
        )
        .catch((error) => {
          hideLoader();
          notification.error({
            placement: "bottomRight",
            description: `${error.message}`,
          });
        });
    }
  };

  const ClickHandler = () => {
    navigate("/login");
  };

  return (
    <Fragment>
      <div className="container-fluid ps-0">
        <div className="row">
          <div className="col-md-6 col-12 col-sm-12 p-0">
          <div className="login-bg" style={{ height: "max(100vh,100%)", overflowY: "hidden" }}>
              <div className="col-10 col-md-11 col-lg-10 col-xl-8 col-xxl-6">
                <h1>Testing Harness Test Automation</h1>
                <p className="font-size-16 mt-3">
                  Experience streamlined OpenHIE standards compliance testing
                  for healthcare websites. Our tool ensures precision,
                  simplifies complexities, and empowers your projects.
                </p>
              </div>
            </div>
          </div>

          <div className="col-md-6 col-12 col-sm-12">
            <div className="login-form-bg pt-5">
              <div className="text-center">
                <img src={openhie_logo} />
              </div>
              <h4 className="my-4">Sign Up</h4>
              <div>
                <div className="custom-input mb-3">
                  <label htmlFor="exampleFormControlInput1" className="form-label">
                    Name
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-person"></i>
                    </span>
                    <input
                      name="name"
                      type="text"
                      className="form-control border-start-0 ps-0"
                      placeholder="Full name"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
                <div className="custom-input mb-3">
                  <label htmlFor="exampleFormControlInput1" className="form-label">
                    Email
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-envelope"></i>
                    </span>
                    <input
                      name="email"
                      type="text"
                      className="form-control border-start-0 ps-0"
                      placeholder="Email"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      //   className="email"
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
                <div className="custom-input mb-3">
                  <label htmlFor="exampleFormControlInput1" className="form-label">
                    Company
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-envelope"></i>
                    </span>
                    <input
                      name="companyName"
                      type="text"
                      className="form-control border-start-0 ps-0"
                      placeholder="companyName"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
                <div className="custom-input mb-3">
                  <label htmlFor="exampleFormControlInput1" className="form-label">
                    Password
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-lock"></i>
                    </span>
                    <input
                      name="password"
                      type="password"
                      className="form-control border-start-0 ps-0"
                      placeholder="Password"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
                <div className="custom-input mb-3">
                  <label htmlFor="exampleFormControlInput1" className="form-label">
                    Confirm Password
                  </label>
                  <div className="input-group">
                    <span className="input-group-text" id="basic-addon1">
                      <i className="bi bi-lock"></i>
                    </span>
                    <input
                      type="password"
                      className="form-control border-start-0 ps-0"
                      placeholder="Confirm Password"
                      aria-label="Username"
                      aria-describedby="basic-addon1"
                      onChange={(e) => setconfirmPassword(e.target.value)}
                    />
                  </div>
                </div>
                <div>
                  {/* <ReCAPTCHA
                    sitekey="6Lf8OrIoAAAAAAKT2bArym6y1lrkkuoVVpIN0uXf"
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                    }}
                  />{" "} */}
                </div>

                <div className="my-3">
                  <button
                    className="btn btn-primary btn-blue w-100 mt-5"
                    onClick={SignUpHandler}
                  >
                    Sign Up
                  </button>
                </div>

                <div className="text-center mb-5">
                  Already have an account?{" "}
                  <a
                    onClick={ClickHandler}
                    className="font-weight-500 ps-2 text-blue"
                    href="#"
                  >
                    SIGN IN
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  );
}
