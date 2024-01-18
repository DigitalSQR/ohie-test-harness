import { useNavigate } from "react-router-dom";
import tool_icon from "../styles/images/tool-icon.png";
import { useEffect, useState } from "react";
import { UserAPI } from "../api/UserAPI";
import { USER_ROLES } from "../constants/role_constants";
export default function Landing() {
  const navigate = useNavigate();
  const [user, setUser] = useState();

  useEffect(() => {
    UserAPI.viewUser().then((user) => {
      setUser(user);
    })
  }, [])

  return (
    <div id="wrapper">
        <div className="col-lg-6 mx-auto text-center pt-5">
            <img src={tool_icon} />
            <h4 className="mt-2">Testing Harness Tool</h4>
            <p className="font-size-16 mt-4">
                Register your application to open-source testing harness and complete test framework that will
                facilitate testing how well technologies align
                to the OpenHIE Architecture specification and health and data content, as specified by WHO SMART
                Guidelines.
            </p>
            <p className="my-4"><a className="text-blue font-weight-500" target="_blank"
                    href="https://guides.ohie.org/arch-spec/openhie-component-specifications-1">View OpenHIE Component
                    Specifications</a></p>
            {
              user?.roleIds?.includes(USER_ROLES.ROLE_ID_ASSESSEE) ?
              <>
                <div className="my-4"> <button className="btn btn-primary btn-blue mt-2" onClick={() => { navigate("/dashboard/register-application") }}><i className="bi bi-pencil-square"></i>
                    Register application</button></div>
              </>
              : null 
            }
        </div>
    </div>
  )
}