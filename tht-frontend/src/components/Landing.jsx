import { Fragment } from "react";
import openhie_logo from "../styles/img/openhie-logo.png";
import "../styles/Landing.css"
export default function Land() {
  return (
    <Fragment>
      <nav class="navbar navbar-light openhie-navbar">
        <div class="container-fluid">
          <a class="navbar-brand" href="index.html">
            <img src={openhie_logo} alt="" height="50" />
          </a>
        </div>
      </nav>
      <div class="wrapper d-flex">
        <div class="d-inline-flex flex-column flex-shrink-0 px-3 sidebar">
          <div class="menu-toggle text-end">
            <span class="menu-toggle-btn w-30"></span>
          </div>
          <ul class="nav nav-pills flex-column mb-auto">
            <li class="nav-item">
              <a href="#" class="nav-link" aria-current="page">
                <span class="report w-30"></span>
                Reports
              </a>
            </li>
            <li>
              <a href="#" class="nav-link link-dark">
                <span class="history w-30"></span>
                History
              </a>
            </li>
            <li>
              <a href="#" class="nav-link link-dark">
                <span class="profile w-30"></span>
                Profile
              </a>
            </li>
            <li>
              <a href="#" class="nav-link link-dark">
                <span class="settings w-30"></span>
                Settings
              </a>
            </li>
          </ul>
        </div>
      </div>
    </Fragment>
  );
}