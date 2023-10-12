import { Fragment, useState } from "react";
import Header from "./Header";
import Sidebar from "./Sidebar";
import User from "./User";
import TestCases from './TestCases';
export default function Dashboard() {
  const [activeComponent,setActiveComponent]=useState(null);

  const renderActiveComponent=()=>{
    
    // console.log(activeComponent)
    switch(activeComponent){
      case'User':
      console.log('rendering user')
        return <User/>;
      case'TestCases':
        return <TestCases/>;
        default:
          return null;
    }
  }
  return (
   <Fragment>
      <Header/>
      <div style={{display:'flex'}}>
      <Sidebar onComponentClick={(component)=>{
        setActiveComponent(component)}}/>
    <main style={{flex:1}}>{renderActiveComponent()}</main>
    </div>
    </Fragment>
  )
      }
  
