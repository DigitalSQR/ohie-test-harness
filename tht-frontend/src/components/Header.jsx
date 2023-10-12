import openhie_logo from "../styles/img/openhie-logo.png";
export default function Header(){
    return(
        <nav className="navbar navbar-light openhie-navbar">
        <div className="container-fluid">
          <a className="navbar-brand" href="index.html">
            <img src={openhie_logo} alt="" height="50" />
          </a>
        </div>
      </nav>
    )
}