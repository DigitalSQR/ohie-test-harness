import { Fragment } from "react";
import argusoft_logo from "../../../styles/images/argusoft-logo.png";
import path_logo from "../../../styles/images/path-logo.png";
import "./_footer.scss";
export default function Footer() {
  return (
    <Fragment>
      <footer>
        <div className="pe-2 d-sm-flex d-none">
          Testing Harness Tool © 2023 <span className="ps-2">|</span>{" "}
        </div>
        <div className="pe-2">
          <img src={argusoft_logo} />
        </div>
        <div>
          <img src={path_logo} />
        </div>
      </footer>
    </Fragment>
  );
}
