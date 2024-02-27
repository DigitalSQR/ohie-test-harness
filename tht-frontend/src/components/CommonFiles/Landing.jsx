import { Fragment, useState } from "react";
import Header from "./Header/Header";
import Sidebar from "./Sidebar/Sidebar";
import { Outlet } from "react-router-dom";
import Footer from "./Footer/Footer";
import { useSelector } from "react-redux";

export default function Landing() {
  const header = useSelector((state) => state.homeSlice.header);
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  return (
    <Fragment>
      <Header headerContent={header} isSidebarOpen={isSidebarOpen} />
      <div>
        <Sidebar
          isSidebarOpen={isSidebarOpen}
          setIsSidebarOpen={setIsSidebarOpen}
        />
        <main>{<Outlet />}</main>
      </div>
      <Footer />
    </Fragment>
  );
}
