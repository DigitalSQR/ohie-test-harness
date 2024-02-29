// PageNotFound.js

import React from "react";
import { Link } from "react-router-dom";
import "./PageNotFound.scss";

export default function PageNotFound() {
  return (
    <div className="containerr">
      <div className="contentt">
        <h1 className="headingg">404 - Page Not Found</h1>
        <p className="descriptionn">
          Oops! The page you are looking for might be in another galaxy.
        </p>
        <Link to="/dashboard" className="linkk">
          Go back to Home
        </Link>
      </div>
    </div>
  );
}
