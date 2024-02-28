// PageNotFound.js

import React from "react";
import { Link } from "react-router-dom";
import "./PageNotFound.scss";

export default function PageNotFound() {
  return (
    <div className="container">
      <div className="content">
        <h1 className="heading">404 - Page Not Found</h1>
        <p className="description">
          Oops! The page you are looking for might be in another galaxy.
        </p>
        <Link to="/dashboard" className="link">
          Go back to Home
        </Link>
      </div>
    </div>
  );
}
