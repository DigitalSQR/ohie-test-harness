import { Fragment } from "react";
import openhie_logo from "../styles/images/logo.png";
import congratulations_icon from "../styles/images/congratulations-icon.png";
export default function CongratulationsPage() {

    return (
        <Fragment>
             <div class="container-fluid ps-0">
            <div class="row">
                <div class="col-md-6 col-12 col-sm-12 p-0">
                    <div class="login-bg">
                        <div class="col-10 col-md-11 col-lg-10 col-xl-8 col-xxl-6">
                            <h1>Testing Harness Test Automation</h1>
                            <p class="font-size-16 mt-3">Experience streamlined OpenHIE standards compliance testing for healthcare websites. Our tool ensures precision, simplifies complexities, and empowers your projects.</p>
                        </div>
                    </div>
                </div>


                <div class="col-md-6 col-12 col-sm-12">
                    <div class="col-xxl-7 col-xl-8 col-lg-10 col-md-11 col-11 pt-5 mx-auto text-center">
                        <div class="text-center"><img src={openhie_logo} /></div>
                        <div class="custom-scrollbar">
                            <div class="text-center my-4"><img src={congratulations_icon} /></div>
                            <h2 class="text-green">Congratulations!</h2>
                            <h6 class="my-4">Thank you for signing up for our OpenHIE Compliance Testing Tool!</h6>
                            <p>
                                Your request has been received, and we appreciate your interest in ensuring healthcare systems’ adherence to OpenHIE standards. A verification link has been sent to your registered email address. Click on the link to verify your account. We will process the request as soon as the account is verified.
                            </p>

                            <p class="my-4">
                                Once your account is approved by our admin, you will receive another email on the registered email <span class="fw-bold">johnsmith@gmail.com</span>
                            </p>

                            <p><a class="text-blue fw-bold" href="#">RESEND</a> Verification link.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        </Fragment>)
}