
import { Fragment } from "react"
import { useNavigate } from "react-router-dom";
import openhie_logo from "../styles/images/logo.png";
import capture_logo from "../styles/images/capture-logo.png";

export default function SignUp(){
    const navigate = useNavigate();
    const ClickHandler = () => {
        
        navigate("/login");}
    
    return(
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
                    <div class="login-form-bg pt-5">
                        <div class="text-center"><img src={openhie_logo} /></div>
                        <h4 class="my-4">Sign Up</h4>
                        <div class="custom-scrollbar">
                            <div class="custom-input mb-3">
                                <label for="exampleFormControlInput1" class="form-label">Name</label>
                                <div class="input-group">
                                    <span class="input-group-text" id="basic-addon1"><i class="bi bi-person"></i></span>
                                    <input type="text" class="form-control border-start-0 ps-0" placeholder="Full name" aria-label="Username" aria-describedby="basic-addon1" />
                                </div>
                            </div>
                            <div class="custom-input mb-3">
                                <label for="exampleFormControlInput1" class="form-label">Email</label>
                                <div class="input-group">
                                    <span class="input-group-text" id="basic-addon1"><i class="bi bi-envelope"></i></span>
                                    <input type="text" class="form-control border-start-0 ps-0" placeholder="Email" aria-label="Username" aria-describedby="basic-addon1" />
                                </div>
                            </div>
                            <div class="custom-input mb-3">
                                <label for="exampleFormControlInput1" class="form-label">Password</label>
                                <div class="input-group">
                                    <span class="input-group-text" id="basic-addon1"><i class="bi bi-lock"></i></span>
                                    <input type="password" class="form-control border-start-0 ps-0" placeholder="Password" aria-label="Username" aria-describedby="basic-addon1" />
                                </div>
                            </div>
                            <div class="custom-input mb-3">
                                <label for="exampleFormControlInput1" class="form-label">Confirm Password</label>
                                <div class="input-group">
                                    <span class="input-group-text" id="basic-addon1"><i class="bi bi-lock"></i></span>
                                    <input type="password" class="form-control border-start-0 ps-0" placeholder="Confirm Password" aria-label="Username" aria-describedby="basic-addon1" />
                                </div>
                            </div>
                            <div class="custom-input mb-3">
                                <label for="exampleFormControlInput1" class="form-label">Company</label>
                                <div class="input-group">
                                    <span class="input-group-text" id="basic-addon1"><i class="bi bi-building"></i></span>
                                    <input type="text" class="form-control border-start-0 ps-0" placeholder="Company" aria-label="Username" aria-describedby="basic-addon1" />
                                </div>
                            </div>

                            <div class="capture">
                                <div>
                                    <label class="custom-checkbox">Im' not a robot
                                        <input type="checkbox" />
                                        <span class="checkmark" ></span>
                                    </label>
                                </div>
                                <div><img src={capture_logo} /></div>
                            </div>

                            <div class="my-3">
                                <button  class="btn btn-primary btn-blue w-100">Sign Up</button>
                            </div>

                            <div class="text-center">Already have an account? <a onClick={ClickHandler} class="font-weight-500 ps-2 text-blue" href="#">SIGN IN</a></div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
        
   </Fragment>)
}