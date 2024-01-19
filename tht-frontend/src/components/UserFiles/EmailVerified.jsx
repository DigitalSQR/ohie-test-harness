import { useNavigate, useParams } from "react-router-dom";
import { Fragment, useEffect } from "react";
import openhie_logo from "../../styles/images/logo.png";
import congratulations_icon from "../../styles/images/congratulations-icon.png";
import { AuthenticationAPI } from "../../api/AuthenticationAPI";

export default function EmailVerified() {
	const { base64UserEmail } = useParams();
	const { base64TokenId } = useParams();
	const navigate = useNavigate();

	useEffect(() => {
		AuthenticationAPI.verifyEmail(base64UserEmail, base64TokenId)
			.then((response) => {
				console.log(response);
				return response;
			})
			.catch((error) => {
				throw error;
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
									Experience streamlined OpenHIE standards
									compliance testing for healthcare websites.
									Our tool ensures precision, simplifies
									complexities, and empowers your projects.
								</p>
							</div>
						</div>
					</div>

					<div className="col-md-6 col-12 col-sm-12">
						<div className="col-xxl-7 col-xl-8 col-lg-10 col-md-11 col-11 pt-5 mx-auto text-center">
							<div className="text-center">
								<img src={openhie_logo} />
							</div>
							<div className="custom-scrollbar">
								<div className="text-center my-4">
									<img src={congratulations_icon} />
								</div>
								<h2 className="text-green">Congratulations!</h2>
								<h6 className="my-4">
									Dear Assesee! Your Email has been verified.
								</h6>
								<p>
									Please wait for Admin's Approval before
									loggin in.
								</p>
								<button
									className="btn btn-primary"
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
