import { Fragment, useEffect, useState } from "react";
import argusoft_logo from "../../../styles/images/argusoft-logo.png";
import path_logo from "../../../styles/images/path-logo.png";
import "./_footer.scss";

export default function Footer() {
  const dateTime = new Date();
 
  return (
    <Fragment>
      <footer>
        <div className="pe-2 d-sm-flex d-none">
          Testing Harness Tool © {dateTime.getFullYear()} <span className="ps-2">|</span>{" "}
        </div>
        <div className="pe-2">
          <a href="https://www.argusoft.com/" target="_blank">
          <img src={argusoft_logo}  />
          </a>
        </div>
        <div>
          <img src={path_logo} />
        </div>
      </footer>
    </Fragment>
  );
}
