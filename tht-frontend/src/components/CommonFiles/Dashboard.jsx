import { Fragment, useEffect, useState } from "react";
import Header from "./Header/Header";
import Sidebar from "./Sidebar/Sidebar";
import { Outlet } from "react-router-dom";
import Footer from "./Footer/Footer";
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
