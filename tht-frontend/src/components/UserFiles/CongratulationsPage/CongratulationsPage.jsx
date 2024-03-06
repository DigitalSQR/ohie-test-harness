import { Fragment } from "react";
import openhie_logo from "../../../styles/images/logo.png";
import congratulations_icon from "../../../styles/images/congratulations-icon.png";
import { notification } from "antd";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";
import { useNavigate, useParams } from "react-router-dom";
import { useLoader } from "../../loader/LoaderContext";
export default function CongratulationsPage() {
  const { showLoader, hideLoader } = useLoader();
  const navigate = useNavigate();
  const params = useParams();

  const resendVerification = () => {
    showLoader();
    AuthenticationAPI.resendVerification(params.email)
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
        hideLoader();
      });
  };

  return (
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
                  Experience streamlined OpenHIE standards compliance testing
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
                  Thank you for signing up for our OpenHIE Compliance Testing
                  Tool!
                </h6>
                <p>
                  Your request has been received, and we appreciate your
                  interest in ensuring healthcare systems’ adherence to OpenHIE
                  standards. A verification link has been sent to your
                  registered email address. Click on the link to verify your
                  account. We will process the request as soon as the account is
                  verified.
                </p>

                <p className="my-4">
                  Once your account is approved by our admin, you will receive
                  another email on the registered email{" "}
                  <span className="fw-bold">{params.email}</span>
                </p>

                <p>
                  <a className="text-blue fw-bold" onClick={resendVerification}>
                    RESEND
                  </a>{" "}
                  Verification link.
                </p>
                <button
                  className="btn btn-primary btn-blue w-100 my-4"
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
  );
}
