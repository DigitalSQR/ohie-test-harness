import { Fragment } from "react";
import Header from "./Header/Header";
import Sidebar from "./Sidebar/Sidebar";
import { Outlet } from "react-router-dom";
import Footer from "./Footer/Footer";
import { useSelector } from "react-redux";

export default function Landing() {
  const header = useSelector((state) => state.homeSlice.header);

  return (
    <Fragment>
      <Header headerContent={header} />
      <div>
        <Sidebar />
        <main>{<Outlet />}</main>
      </div>
      <Footer />
    </Fragment>
  );
}
