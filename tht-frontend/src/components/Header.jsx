import { Fragment } from "react";
import avatar from "../styles/images/avatar.jpg";
import "../scss/_header.scss"
export default function Header(){
    return(
      //   <nav className="navbar navbar-light openhie-navbar">
      //   <div className="container-fluid">
      //     <a className="navbar-brand" href="index.html">
      //       <img src={openhie_logo} alt="" height="50" />
      //     </a>
      //   </div>
      // </nav>
      <Fragment>
        <header>
      <div class="pd-left-240 ps-30" id="header-col-1"></div>
      <div class="d-flex align-items-center">
          <div class="bell-icon"><i class="bi bi-bell side-nav-toggle"></i></div>
          <div class="dropdown">
              <div class="user-dropdown" data-bs-toggle="dropdown" aria-expanded="false">
                  <span class="user-pic"><img src={avatar}/></span>
                  <span class="user-name">John Doe<i class="bi bi-chevron-down"></i></span>
                  <span class="font-size-12">Assessee</span>
              </div>
              <ul class="dropdown-menu">
                  <li><a class="dropdown-item" href="#">Action</a></li>
                  <li><a class="dropdown-item" href="#">Another action</a></li>
                  <li><a class="dropdown-item" href="#">Something else here</a></li>
              </ul>
          </div>
      </div>
      </header>
      </Fragment>
    )
}