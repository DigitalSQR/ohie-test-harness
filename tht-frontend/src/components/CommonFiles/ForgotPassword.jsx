import { Fragment, useState } from "react";
import { AuthenticationAPI } from "../../api/AuthenticationAPI";
import { useLoader } from "../loader/LoaderContext";
import openhie_logo from "../../styles/images/logo.png";
import { notification } from "antd";
import { useNavigate } from "react-router-dom";


export default function ForgotPassword() {
    const { showLoader, hideLoader } = useLoader();
    const [enteredEmail, setEnteredEmail] = useState();
    const navigate = useNavigate();

    const handleInputChange = (e) => {
        setEnteredEmail(e.target.value);
    };

    const VerifyEmail = () => {
        showLoader();
        AuthenticationAPI.forgotpassword(enteredEmail)
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
    };

    const backToLogin = () => {
        navigate("/login");
    }

    return (
        <Fragment>
            <div className="container-fluid ps-0">
                <div className="row">
                    <div className="col-md-6 col-12 col-sm-12 p-0">
                        <div className="login-bg">
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
                            <h2 className="my-4 formTitle">
                                Verify Email
                            </h2>
                            <div className="custom-input mb-3">
                                <label htmlFor="Password" className="form-label">
                                    Registered email
                                </label>
                                <div className="input-group">
                                    <span className="input-group-text">
                                        <i className="bi bi-lock"></i>
                                    </span>
                                    <input
                                        type="email"
                                        className="form-control border-start-0 ps-0"
                                        name="email"
                                        id="email"
                                        autoComplete="off"
                                        value={enteredEmail}
                                        onChange={handleInputChange}
                                        placeholder="example@tht.com"
                                        aria-label="Email"
                                    />
                                </div>
                            </div>

                            <div className="buttonWrapper">
                                <button id="submitButton" onClick={VerifyEmail} className="btn btn-primary">
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