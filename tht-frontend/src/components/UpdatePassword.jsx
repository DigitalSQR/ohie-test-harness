import { Fragment, useEffect, useState } from "react";

export default function UpdatePassword(){
  
    
    const [newpassword,setNewPassword] = useState();
    const [registeredemail, setRegisteredEmail] = useState();
    const [newtoken,setAcessToken] = useState();

    useEffect(() => {
        // Get the current URL pathname
        const currentPath = window.location.pathname;
    
        // Split the pathname by '/'
        const pathParts = currentPath.split('/');
    
        // Find the index of 'verify'
        const verifyIndex = pathParts.indexOf('verify');
    
        // Extract the encrypted string from the URL
        if (verifyIndex !== -1 && verifyIndex < pathParts.length - 1) {
          registeredemail = pathParts[verifyIndex + 1]; // Get the string after 'verify/'
          console.log('Encrypted string:', registeredemail);
    
          // Extract the part after the last '/'
           newtoken = currentPath.substring(currentPath.lastIndexOf('/') + 1);
          console.log('Last part of the URL:', newtoken);
    
          
        }
      }, []);
    return(
        <Fragment>
            <span>New Password</span>
            <input type="text"onChange={(e)=>{setNewPassword(e.target.value)}}></input>
            <button>Update Password</button>
        </Fragment>
    )
}