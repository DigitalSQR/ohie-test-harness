
import { Fragment } from "react"
import { useNavigate } from "react-router-dom";

export default function Register(){
    const navigate = useNavigate();
    const ClickHandler = () => {
        
        navigate("/login");}
    
    return(
    <Fragment>
        <button onClick={ClickHandler}>Return to Login</button>
        <p>the Register Page</p>
        </Fragment>)
}