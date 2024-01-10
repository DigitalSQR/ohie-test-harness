import { Fragment, useEffect, useState } from "react";
import Header from "./Header";
import Sidebar from "./Sidebar";
import { Outlet } from "react-router-dom";
import Footer from "./Footer";
// import "../styles/Dashboard.css";
export default function Dashboard() {
  return (
    <Fragment>
      <Header />
      <div>
        <Sidebar />
        <main>
          {<Outlet />}
        </main>
      </div>
      <Footer />
    </Fragment>
  );
}
