import { Fragment } from "react";
import Header from "./Header";
import Sidebar from "./Sidebar";
import { Outlet } from "react-router-dom";
export default function Dashboard() {
  //the below block of code is redundant as component rendering will be done by routing.
  // const [activeComponent,setActiveComponent]=useState(null);

  // const renderActiveComponent=()=>{
    
  //   // console.log(activeComponent)
  //   switch(activeComponent){
  //     case'User':
  //     console.log('rendering user')
  //       //  return <User/>; //rendering based on routes
  //     case'TestCases':
  //       return <TestCases/>;
  //       default:
  //         return null;

  //   }
  // }
  //the redundant block ends here.
  return (
   <Fragment>
      <Header/>
      <div style={{display:'flex'}}>
      {/* <Sidebar onComponentClick={(component)=>{
        setActiveComponent(component)}}/> */}
  <Sidebar/>
    <main style={{flex:1}}>{<Outlet/>}</main>
    </div>
    </Fragment>
  )
      }
  

