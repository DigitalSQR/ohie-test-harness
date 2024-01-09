import { Fragment, useState } from "react";
import { AuthenticationAPI } from "../api/AuthenticationAPI";
import { useLoader } from "./loader/LoaderContext";

export default function ForgotPassword(){
    const{showLoader,hideLoader}=useLoader();
    const [enteredEmail,setEnteredEmail] = useState();
    const VerifyEmail = () => {
        // showLoader();
        AuthenticationAPI.forgotpassword(enteredEmail)
        .then((response)=>{console.log(response)})
        .catch((error)=>{throw error})
        
    };
    return(
        <Fragment>
        <span>Please enter your registered email</span>
        <input type="text" onChange={(e)=>{setEnteredEmail(e.target.value)}}/>
        <button onClick={VerifyEmail}>Submit</button>
        </Fragment>
    )
}