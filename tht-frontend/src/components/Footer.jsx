import { Fragment } from "react";
import argusoft_logo from "../styles/images/argusoft-logo.png";
import path_logo from "../styles/images/path-logo.png";
import "../scss/_footer.scss";
export default function Footer() {
  return (
    <Fragment>
      <footer>
        <div class="pe-2 d-sm-flex d-none">
          Testing Harness Tool © 2023 <span class="ps-2">|</span>{" "}
        </div>
        <div class="pe-2">
          <img src={argusoft_logo} />
        </div>
        <div>
          <img src={path_logo} />
        </div>
      </footer>
    </Fragment>
  );
}
