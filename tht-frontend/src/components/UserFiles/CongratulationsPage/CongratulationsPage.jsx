import { Fragment } from "react";
import openhie_logo from "../../../styles/images/logo.png";
import congratulations_icon from "../../../styles/images/congratulations-icon.png";
import { notification } from "antd";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";
import { useNavigate, useParams } from "react-router-dom";
import { useLoader } from "../../loader/LoaderContext";
import "./congratulationsPage.scss";
export default function CongratulationsPage() {
  const { showLoader, hideLoader } = useLoader();
  const navigate = useNavigate();
  const params = useParams();
  const {isOauthCreated} = useParams();

  const resendVerification = () => {
    showLoader();
    AuthenticationAPI.resendVerification(params.email)
      .then((response) => {
        var data = response.data;
        notification.success({
          className:"notificationSuccess",
          placement: "top",
          message:"Success",
          description: `${data?.message}`,
        });
        hideLoader();
        navigate("/login");
      })
      .catch((error) => {
        hideLoader();
      });
  };

  return (
      <div id="congratulation">
      <Fragment>
      <div className="container-fluid ps-0">
        <div className="row">
          <div className="col-md-6 col-12 col-sm-12 p-0">
            <div
              className="login-bg"
              style={{ height: "max(100vh,100%)", overflowY: "hidden" }}
            >
              <div className="col-10 col-md-11 col-lg-10 col-xl-8 col-xxl-6">
                <h1>Testing Harness Test Automation</h1>
                <p className="font-size-16 mt-3">
                  Experience streamlined OpenHIE standards compliance verification
                  for healthcare websites. Our tool ensures precision,
                  simplifies complexities, and empowers your projects.
                </p>
              </div>
            </div>
          </div>

          <div className="col-md-6 col-12 col-sm-12">
            <div className="col-xxl-7 col-xl-8 col-lg-10 col-md-11 col-11 pt-5 mx-auto text-center">
              <div className="text-center">
                <img src={openhie_logo} />
              </div>
              <div>
                <div className="text-center my-4">
                  <img src={congratulations_icon} />
                </div>
                <h2 className="text-green">Congratulations!</h2>
                <h6 className="my-4">
                  Thank you for signing up for our OpenHIE Compliance Verification
                  Tool!
                </h6>
                {!isOauthCreated && (
                  <>
                    <p>
                      Your request has been received, and we appreciate your
                      interest in ensuring healthcare systems’ adherence to OpenHIE
                      standards. A verification link has been sent to your
                      registered email address <span className="fw-bold">{ params.email } </span> 
                      . Click on the link to verify your account. 
                      We will process the request as soon as the account is verified.
                    </p>
                    <p>
                      <a className="text-blue fw-bold cursor-pointer" onClick={resendVerification}>
                        RESEND
                      </a>{" "}
                      Verification link.
                    </p>
                  </>
                )}
                {!!isOauthCreated && (
                  <>
                    <p>
                      Your request has been received, and we appreciate your
                      interest in ensuring healthcare systems’ adherence to OpenHIE
                      standards.
                    </p>
                    <p className="my-4">
                      Once your account is approved by our admin within 7 days, you will receive
                      an email on the registered email{" "}
                      <span className="fw-bold">{params.email}</span>
                    </p>
                  </>
                )}
                <button
                  className="btn btn-primary btn-blue w-100 my-4 py-3"
                  onClick={() => navigate("/login")}
                >
                  LOGIN
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Fragment>

      </div>
        );
}
