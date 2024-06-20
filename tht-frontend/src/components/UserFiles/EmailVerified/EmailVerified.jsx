import { useNavigate, useParams } from "react-router-dom";
import { Fragment, useEffect, useState } from "react";
import openhie_logo from "../../../styles/images/logo.png";
import congratulations_icon from "../../../styles/images/congratulations-icon.png";
import { AuthenticationAPI } from "../../../api/AuthenticationAPI";

export default function EmailVerified() {
  const { base64UserEmail } = useParams();
  const { base64TokenId } = useParams();
  const navigate = useNavigate();
  const [isError, setIsError] = useState(false);
  useEffect(() => {
    AuthenticationAPI.verifyEmail(base64UserEmail, base64TokenId)
      .then((response) => {
        if (response.data.level === "ERROR") {
          setIsError(true);
        }
        return response;
      })
      .catch((error) => {
        setIsError(true);
      });
  }, []);

  return (
    <Fragment>
      <div className="container-fluid ps-0">
        <div className="row">
          <div className="col-md-6 col-12 col-sm-12 p-0">
            <div className="login-bg">
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
                {!isError ? (
                  <div>
                    <h2 className="text-green">Congratulations!</h2>
                    <h6 className="my-4">
                      Dear Assessee! Your Email has been verified.
                    </h6>
                    <p className="my-4">
                      Once your account is approved by our admin within 7 days, you will receive
                      an email on the registered email
                    </p>
                  </div>
                ) : (
                  <div className="my-4">
                    Something went Wrong!! Please try verifying it again
                  </div>
                )}

                <button
                  className="btn btn-primary"
                  id="#EmailVerfied-backToLogin"
                  onClick={() => {
                    navigate("/login");
                  }}
                >
                  Back To Login
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Fragment>
  );
}
